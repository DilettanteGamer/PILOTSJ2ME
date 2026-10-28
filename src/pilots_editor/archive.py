"""Read/write an indexed resource overlay without executing or modifying PE code."""
from __future__ import annotations
from dataclasses import dataclass, field
import hashlib
import os
from pathlib import Path
import struct
import tempfile

MAX_BLOB = 16 * 1024 * 1024
MAX_TOTAL = 512 * 1024 * 1024


class FormatError(ValueError):
    pass


class Cursor:
    def __init__(self, data: bytes):
        self.data, self.pos = data, 0

    def take(self, size: int) -> bytes:
        if size < 0 or self.pos + size > len(self.data):
            raise FormatError("Ресурс обрывается раньше объявленной длины")
        value = self.data[self.pos:self.pos + size]
        self.pos += size
        return value

    def integer(self, size=1):
        return int.from_bytes(self.take(size), "little")

    def number(self):
        tag = self.integer()
        if tag < 128:
            return tag
        if not tag & 0x20:
            width = tag & 31
            if not 1 <= width <= 4:
                raise FormatError("Некорректный размер числа")
            return self.integer(width)
        width = 1 + ((tag >> 2) & 3)
        value = self.integer(width) + ((tag & 3) << ((8 * width) % 32))
        # Numeric representation facts, not engine instructions.
        if tag & 0x10:
            return (-1, -1025, -263169, 1151)[width - 1] - value
        return (128, 1152, 263296, 0)[width - 1] + value

    def blob(self):
        tag = self.integer()
        if tag < 128:
            raise FormatError("Отсутствует заголовок блока")
        if tag & 0x40:
            return self.take(tag & 31)
        width = 1 + (tag & 3)
        size = self.integer(width)
        if size > MAX_BLOB:
            raise FormatError("Слишком большой блок")
        if not tag & 12:
            return self.take(size)
        expanded = self.integer(width)
        if expanded > MAX_BLOB:
            raise FormatError("Слишком большой распакованный блок")
        encoded = Cursor(self.take(size))
        out = bytearray()
        while encoded.pos < len(encoded.data):
            flags = encoded.integer()
            for bit in range(8):
                if encoded.pos == len(encoded.data):
                    break
                if flags & (1 << bit):
                    out.extend(encoded.take(1))
                else:
                    a, b = encoded.take(2)
                    distance, count = a + ((b & 0xF0) << 4), 3 + (b & 15)
                    if not 0 < distance <= len(out):
                        raise FormatError("Недопустимая ссылка сжатого блока")
                    if len(out) + count > expanded:
                        raise FormatError("Переполнение распакованного блока")
                    for _ in range(count):
                        out.append(out[-distance])
                if len(out) > expanded:
                    raise FormatError("Переполнение блока")
        if len(out) != expanded:
            raise FormatError("Размер распакованного блока не совпал")
        return bytes(out)


def number(value: int) -> bytes:
    if 0 <= value < 128:
        return bytes([value])
    if not 0 <= value <= 0xFFFFFFFF:
        raise FormatError("Для новой записи ожидается беззнаковое 32-битное число")
    width = max(1, (value.bit_length() + 7) // 8)
    return bytes([128 | width]) + value.to_bytes(width, "little")


def blob(value: bytes) -> bytes:
    if len(value) > MAX_BLOB:
        raise FormatError("Слишком большой блок замены")
    if len(value) <= 31:
        return bytes([0xC0 | len(value)]) + value
    width = max(1, (len(value).bit_length() + 7) // 8)
    return bytes([0x80 | (width - 1)]) + len(value).to_bytes(width, "little") + value


@dataclass
class Record:
    key: str
    section: int
    opcode: int
    ident: int
    args: tuple[int, int, int]
    data: bytes
    raw: bytes = b""
    header: bytes = b""
    shared: bool = False

    @property
    def kind(self):
        return self.opcode & 127

    def encode(self, value: bytes, args=None):
        if args is None and self.header:
            header = self.header
        else:
            header = bytes([self.opcode])
            if self.opcode < 128:
                header += number(self.ident)
            for i, v in enumerate(self.args if args is None else args, 1):
                if v:
                    header += bytes([i]) + number(v)
        return header + b"\x04" + blob(value)


@dataclass
class Section:
    ident: int
    original: bytes
    records: list[Record] = field(default_factory=list)
    diagnostic: str = ""

    def parse(self):
        stream = Cursor(self.original)
        previous = None
        try:
            while True:
                start = stream.pos
                opcode = stream.integer()
                if opcode == 0:
                    if stream.pos != len(self.original):
                        raise FormatError("Дополнительные данные после конца секции")
                    return
                if opcode in (1, 2, 3, 4, 5, 255):
                    raise FormatError("Неподдерживаемая управляющая запись секции")
                ident = stream.number() if opcode < 128 else 0
                args = [0, 0, 0]
                marker = stream.integer()
                while marker in (1, 2, 3):
                    args[marker - 1] = stream.number()
                    marker = stream.integer()
                header = self.original[start:stream.pos - 1]
                if marker == 255:
                    if previous is None:
                        raise FormatError("Нет предыдущего общего блока")
                    payload = previous
                elif marker == 4:
                    payload = stream.blob()
                else:
                    raise FormatError("Неизвестный маркер записи")
                key = f"{self.ident}:{len(self.records)}"
                self.records.append(Record(key, self.ident, opcode, ident, tuple(args), payload,
                                           self.original[start:stream.pos], header, marker == 255))
                previous = payload
        except FormatError as error:
            self.records.clear()
            self.diagnostic = str(error)


class Archive:
    def __init__(self, path: Path):
        self.path = Path(path).resolve()
        if self.path.is_dir():
            self.path /= "PILOTS.EXE"
        if self.path.stat().st_size > MAX_TOTAL:
            raise FormatError("Исходный файл превышает ограничение размера")
        raw = self.path.read_bytes()
        if len(raw) < 24 or len(raw) > MAX_TOTAL or raw[-4:] != b"=VS=":
            raise FormatError("Не найден поддерживаемый индекс ресурсов =VS=")
        self.sha256 = hashlib.sha256(raw).hexdigest()
        footer_size = struct.unpack_from("<I", raw, len(raw) - 12)[0]
        footer_start = len(raw) - 12 - footer_size
        if footer_size < 8 or footer_start < 0:
            raise FormatError("Некорректный каталог ресурсов")
        count, self.base = struct.unpack_from("<II", raw, footer_start)
        index_start = footer_start - 5 * (count + 1)
        if not 1 <= count <= 255 or not 0 <= self.base < index_start:
            raise FormatError("Некорректные границы индекса")
        self.entries = [struct.unpack_from("<IB", raw, index_start + 5 * n) for n in range(count + 1)]
        if len({ident for off, ident in self.entries}) != len(self.entries):
            raise FormatError("Повторные ID секций")
        ordered = sorted(self.entries)
        if any(self.base + offset > index_start for offset, _ in ordered):
            raise FormatError("Секция выходит за границы данных")
        if any(a[0] == b[0] for a, b in zip(ordered, ordered[1:])):
            raise FormatError("Совпадающие границы секций пока не поддерживаются")
        self.sections = []
        decoded = 0
        for (start, ident), (end, _) in zip(ordered, ordered[1:]):
            section = Section(ident, raw[self.base + start:self.base + end])
            section.parse()
            decoded += sum(len(r.data) for r in section.records)
            if decoded > MAX_TOTAL:
                raise FormatError("Распакованные ресурсы превышают ограничение памяти")
            self.sections.append(section)
        self.prefix = raw[:self.base + ordered[0][0]]
        self.sentinel = ordered[-1][1]
        self.tail = raw[self.base + ordered[-1][0]:index_start]
        self.footer = raw[footer_start:]
        self.by_key = {r.key: r for section in self.sections for r in section.records}

    def export(self, destination: Path, changes: dict, added=(), deleted=()):
        destination = Path(destination).resolve()
        if destination == self.path:
            raise FormatError("Выберите новый EXE: исходная игра не перезаписывается")
        current = self.path.read_bytes()
        if hashlib.sha256(current).hexdigest() != self.sha256:
            raise FormatError("Исходный EXE изменился после открытия")
        out = bytearray(self.prefix)
        positions = {}
        deleted = set(deleted)
        for section in self.sections:
            positions[section.ident] = len(out) - self.base
            additions = [r for r in added if r.section == section.ident]
            relevant = any(r.key in changes or r.key in deleted for r in section.records) or additions
            if not relevant:
                out.extend(section.original)
                continue
            if section.diagnostic:
                raise FormatError("Секция с неизвестным форматом доступна только для просмотра")
            previous = None
            for record in section.records + additions:
                if record.key in deleted:
                    continue
                override = changes.get(record.key)
                value = override[0] if override else record.data
                args = override[1] if override else None
                if not override and record.raw and (not record.shared or previous == record.data):
                    out.extend(record.raw)
                else:
                    out.extend(record.encode(value, args))
                previous = value
            out.append(0)
        positions[self.sentinel] = len(out) - self.base
        out.extend(self.tail)
        for _, ident in self.entries:
            out.extend(struct.pack("<IB", positions[ident], ident))
        out.extend(self.footer)
        fd, filename = tempfile.mkstemp(prefix=destination.name + ".", suffix=".tmp", dir=destination.parent)
        os.close(fd)
        temporary = Path(filename)
        try:
            temporary.write_bytes(out)
            check = Archive(temporary)
            if len(check.sections) != len(self.sections):
                raise FormatError("Проверка экспортированного индекса не прошла")
            for record in self.by_key.values():
                if record.key in changes and record.key not in deleted:
                    actual = check.by_key.get(record.key)
                    # Ordinals can change on deletion; full semantic verification is
                    # done by the project using identity tuples, not PE execution.
                    if not deleted and actual and actual.data != changes[record.key][0]:
                        raise FormatError("Проверка замены не прошла")
            temporary.replace(destination)
        finally:
            temporary.unlink(missing_ok=True)
