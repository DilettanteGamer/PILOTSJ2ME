"""Resource adapters based on binary layout, without game scripts or engine code."""
import io
import struct
import wave
from PIL import Image
from .archive import FormatError

KINDS = {0x10: "Параметры", 0x18: "Фон", 0x19: "Начальная расстановка",
         0x20: "Предмет / персонаж", 0x21: "Инициализация", 0x22: "Завершение",
         0x2A: "Событие", 0x2B: "Условие", 0x2C: "Действие / текст",
         0x38: "Группа", 0x39: "Выбор", 0x40: "Анимация", 0x42: "Смещения кадров",
         0x43: "Изображение кадра", 0x51: "Звук / реплика", 0x52: "Музыка",
         0x60: "Карта / изменение сцены", 0x61: "Композиция"}


def palette(project, section):
    rows = project.records(section) + project.records(1) + project.records(4)
    for r in rows:
        if r.kind == 0x18:
            data = project.get(r).data
            if len(data) >= 24:
                w, h = struct.unpack_from("<II", data, 8)
                at = 24 + w * h
                if 0 < w <= 2048 and 0 < h <= 2048 and at + 768 <= len(data):
                    return data[at:at + 768]
    raise FormatError("В секции не найдена палитра фона")


def image(record, colors):
    data = record.data
    if record.kind == 0x18:
        if len(data) < 24:
            raise FormatError("Короткий заголовок фона")
        w, h = struct.unpack_from("<II", data, 8)
        at = 24
    elif record.kind == 0x43:
        if len(data) < 4:
            raise FormatError("Короткий заголовок кадра")
        w, h = struct.unpack_from("<HH", data)
        at = 4
    else:
        raise FormatError("Выбранная запись не является изображением")
    if not 0 < w <= 2048 or not 0 < h <= 2048 or at + w * h > len(data):
        raise FormatError("Неподдерживаемые размеры изображения")
    result = Image.frombytes("P", (w, h), data[at:at + w * h])
    result.putpalette(colors)
    if record.kind == 0x43:
        result.info["transparency"] = 0
    return result


def replace_image(record, replacement, colors):
    old = image(record, colors)
    if record.kind == 0x18 and replacement.size != old.size:
        raise FormatError("Размер фона должен совпадать с исходным")
    if not 0 < replacement.width <= 640 or not 0 < replacement.height <= 480:
        raise FormatError("Кадр должен помещаться в 640×480")
    alpha_matches = replacement.info.get("transparency") == 0 if record.kind == 0x43 else "transparency" not in replacement.info
    if replacement.mode == "P" and bytes(replacement.getpalette()[:768]) == colors and alpha_matches:
        pixels = replacement.tobytes()
    else:
        rgba = replacement.convert("RGBA")
        if any(a not in (0, 255) for a in rgba.getchannel("A").getdata()):
            raise FormatError("Полупрозрачность не поддерживается: нужен непрозрачный или прозрачный пиксель")
        target = Image.new("P", (1, 1));target.putpalette(colors)
        indices = bytearray(rgba.convert("RGB").quantize(palette=target, dither=Image.Dither.NONE).tobytes())
        for i, alpha in enumerate(rgba.getchannel("A").getdata()):
            if not alpha:
                if record.kind == 0x18:
                    raise FormatError("Фон не поддерживает прозрачность")
                indices[i] = 0
            elif record.kind == 0x43 and indices[i] == 0:
                rgb = rgba.getpixel((i % rgba.width, i // rgba.width))[:3]
                indices[i] = min(range(1, 256), key=lambda n: sum((rgb[j] - colors[n * 3 + j]) ** 2 for j in range(3)))
        pixels = bytes(indices)
    if record.kind == 0x18:
        return record.data[:24] + pixels + record.data[24 + old.width * old.height:]
    return struct.pack("<HH", *replacement.size) + pixels + record.data[4 + old.width * old.height:]


def wav(record):
    if len(record.data) < 4:
        raise FormatError("Нет заголовка PCM")
    size = struct.unpack_from("<I", record.data)[0]
    if size > len(record.data) - 4:
        raise FormatError("Некорректный размер PCM")
    stream = io.BytesIO()
    with wave.open(stream, "wb") as out:
        out.setparams((1, 1, 11025, 0, "NONE", "not compressed"))
        out.writeframes(record.data[4:4 + size])
    return stream.getvalue()


def replace_wav(record, data):
    with wave.open(io.BytesIO(data), "rb") as source:
        if (source.getnchannels(), source.getsampwidth(), source.getframerate(), source.getcomptype()) != (1, 1, 11025, "NONE"):
            raise FormatError("Требуется PCM WAV: mono, 8 бит, 11025 Гц")
        pcm = source.readframes(source.getnframes())
    previous = struct.unpack_from("<I", record.data)[0]
    return struct.pack("<I", len(pcm)) + pcm + record.data[4 + previous:]


_US = "qwertyuiop[]asdfghjkl;'zxcvbnm,./QWERTYUIOP{}ASDFGHJKL:\"ZXCVBNM<>?"
_RU = "йцукенгшщзхъфывапролджэячсмитьбю.ЙЦУКЕНГШЩЗХЪФЫВАПРОЛДЖЭЯЧСМИТЬБЮ,"


def text(record):
    size = record.args[2]
    if record.kind not in (0x21, 0x22, 0x2C) or not 0 < size <= len(record.data):
        return None
    try:
        return record.data[:size].split(b"\0", 1)[0].decode("ascii").translate(str.maketrans(_US, _RU))
    except UnicodeError:
        return None


def replace_text(record, value):
    if text(record) is None:
        raise FormatError("У записи нет распознанного текстового префикса")
    encoded = value.translate(str.maketrans(_RU, _US)).encode("ascii") + b"\0"
    size = text_capacity(record)
    if len(encoded) > size:
        raise FormatError(f"Текст превышает выделенные {size - 1} байт. Код и соседние строки не сдвигаются")
    return encoded.ljust(size, b"\0") + record.data[size:]


def text_capacity(record):
    """Protect nonzero metadata/other strings in the same prefix window."""
    window = record.data[:record.args[2]]
    end = window.find(b"\0")
    if end < 0:return len(window)
    end += 1
    while end < len(window) and window[end] == 0:end += 1
    return end


def placements(record, cell=(64, 16)):
    """Return authored cell entries and byte positions, without executing rules."""
    data = record.data
    if len(data) < 4 or len(data) % 4:
        raise FormatError("Неполная таблица расстановки")
    words = struct.unpack("<" + "I" * (len(data) // 4), data)
    flags, pos = words[0], 1
    if flags & 1:
        pos += 1
    def group(at):
        while True:
            if at >= len(words):raise FormatError("Короткая группа расстановки")
            header = words[at];at += 1
            count = header & 65535
            if at + count > len(words):raise FormatError("Группа выходит за границы")
            yield at, count
            at += count
            if header & 65536:return
    if flags & 2:
        for at, count in group(pos):pos = at + count
    if flags & 8:
        pos += 1
    result = []
    if flags & 16:
        for at, count in group(pos):
            for index in range(at, at + count):
                entity, orientation, x, y = struct.pack("<I", words[index])
                if entity < 254:
                    result.append(dict(entity=entity, orientation=orientation, x=x * cell[0], y=y * cell[1], offset=index * 4))
    return result


def move_cell(record, offset, x, y, cell=(64, 16)):
    if not 0 <= offset <= len(record.data) - 4:
        raise FormatError("Неверное смещение ячейки")
    a, b = round(x / cell[0]), round(y / cell[1])
    if not 0 <= a <= 255 or not 0 <= b <= 255:
        raise FormatError("Ячейка выходит за пределы формата")
    data = bytearray(record.data);data[offset + 2:offset + 4] = bytes([a, b]);return bytes(data)


def frames(project, record):
    return sorted([project.get(r) for r in project.records(record.section) if r.kind == 0x43 and r.ident == record.ident], key=lambda r: (r.args[0], r.args[1]))


def pivot(project, frame):
    found = next((project.get(r) for r in project.records(frame.section) if r.kind == 0x42 and r.ident == frame.ident and r.args[0] == frame.args[0]), None)
    offset = frame.args[1] * 8
    if found and offset + 8 <= len(found.data):
        x, y = struct.unpack_from("<hh", found.data, offset + 4)
        return -x, -y
    return 0, 0


def initial_picture(project, section, entity):
    """Best-effort constant animation reference; does not evaluate any game code."""
    scopes = project.records(section) + project.records(1)
    initializer = next((project.get(r) for r in scopes if r.kind == 0x21 and r.ident == entity), None)
    if initializer is None:return None
    body = initializer.data[initializer.args[2]:]
    # A resource reference is a literal followed by the animation-selection call.
    # Conditions, jumps, callbacks and computed references remain uninterpreted.
    for at in range(max(0, len(body) - 10)):
        if body[at] == 0x1D and body[at + 5:at + 11] == b"\x21\x36\x13\0\0\0":
            ident = struct.unpack_from("<I", body, at + 1)[0]
            frame = next((project.get(r) for r in scopes if r.kind == 0x43 and r.ident == ident and r.args[:2] == (0, 0)), None)
            if frame:return frame
    return None
