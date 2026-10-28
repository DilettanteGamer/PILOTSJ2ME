"""A project contains user replacements and source identity, never an engine snapshot."""
from __future__ import annotations
import base64
import json
import os
from pathlib import Path
import tempfile
import uuid
from .archive import Archive, Record, FormatError, MAX_BLOB


class Project:
    def __init__(self, archive: Archive):
        self.archive = archive
        self.changes: dict[str, tuple[bytes, tuple | None]] = {}
        self.added: list[Record] = []
        self.deleted: set[str] = set()
        self.history = []
        self.future = []
        self.path = None

    def records(self, section=None):
        return [r for r in list(self.archive.by_key.values()) + self.added
                if r.key not in self.deleted and (section is None or r.section == section)]

    def get(self, record):
        data, args = self.changes.get(record.key, (record.data, None))
        return Record(record.key, record.section, record.opcode, record.ident,
                      tuple(args) if args is not None else record.args, data)

    def checkpoint(self):
        self.history.append((self.changes.copy(), self.added.copy(), self.deleted.copy()))
        self.history = self.history[-100:]
        self.future.clear()

    def undo(self, redo=False):
        source, destination = (self.future, self.history) if redo else (self.history, self.future)
        if not source:
            return False
        destination.append((self.changes.copy(), self.added.copy(), self.deleted.copy()))
        self.changes, self.added, self.deleted = source.pop()
        return True

    def replace(self, record, data, args=None):
        if len(data) > MAX_BLOB:
            raise FormatError("Замена слишком велика")
        if args is not None and (len(args) != 3 or any(not isinstance(v, int) or not 0 <= v <= 0xFFFFFFFF for v in args)):
            raise FormatError("Нужны три беззнаковых 32-битных параметра")
        self.checkpoint()
        if args is None and record.key in self.changes:
            args = self.changes[record.key][1]
        self.changes[record.key] = bytes(data), args

    def add(self, section, kind, ident, args, data):
        if not any(s.ident == section and not s.diagnostic for s in self.archive.sections):
            raise FormatError("Нельзя добавлять запись в неизвестную секцию")
        if not 6 <= kind <= 127 or len(data) > MAX_BLOB:
            raise FormatError("Неподдерживаемый тип или размер новой записи")
        if not 0 <= ident <= 0xFFFFFFFF or len(args) != 3 or any(not isinstance(v, int) or not 0 <= v <= 0xFFFFFFFF for v in args):
            raise FormatError("Неверный ID или параметры новой записи")
        if any((r.kind, r.ident, r.args) == (kind, ident, tuple(args)) for r in self.records(section)):
            raise FormatError("Запись с таким типом, ID и параметрами уже существует")
        self.checkpoint()
        self.added.append(Record("new:" + uuid.uuid4().hex, section, kind, ident, tuple(args), bytes(data)))

    def delete(self, record):
        self.checkpoint()
        self.deleted.add(record.key)

    def save(self, path):
        def encoded(data):
            return base64.b64encode(data).decode("ascii")
        content = dict(format="pilots-editor-project", version=1,
                       source=str(self.archive.path), sha256=self.archive.sha256,
                       changes={key: dict(data=encoded(data), args=args) for key, (data, args) in self.changes.items()},
                       deleted=sorted(self.deleted),
                       added=[dict(key=r.key, section=r.section, opcode=r.opcode, ident=r.ident,
                                   args=r.args, data=encoded(r.data)) for r in self.added])
        path = Path(path)
        if path.resolve() == self.archive.path:
            raise FormatError("Проект нельзя записывать поверх исходной игры")
        fd, filename = tempfile.mkstemp(prefix=path.name + ".", suffix=".tmp", dir=path.resolve().parent)
        os.close(fd);temporary = Path(filename)
        try:
            temporary.write_text(json.dumps(content, ensure_ascii=False, indent=2), encoding="utf-8")
            temporary.replace(path)
        finally:temporary.unlink(missing_ok=True)
        self.path = path

    @classmethod
    def load(cls, path, source=None):
        path = Path(path)
        if path.stat().st_size > 64 * 1024 * 1024:
            raise FormatError("Проект слишком велик")
        content = json.loads(path.read_text(encoding="utf-8"))
        if content.get("format") != "pilots-editor-project" or content.get("version") != 1:
            raise FormatError("Неизвестный формат проекта")
        result = cls(Archive(Path(source or content["source"])))
        if result.archive.sha256 != content["sha256"]:
            raise FormatError("Проект создан для другой версии исходной игры")
        def decoded(value):
            data = base64.b64decode(value, validate=True)
            if len(data) > MAX_BLOB:
                raise FormatError("Слишком большой блок в проекте")
            return data
        for row in content.get("added", []):
            result.add(int(row["section"]), int(row["opcode"]), int(row["ident"]), tuple(row["args"]), decoded(row["data"]))
            result.added[-1].key = row["key"]
        allowed = {r.key for r in result.records()}
        for key, change in content.get("changes", {}).items():
            if key not in allowed:
                raise FormatError("Замена ссылается на отсутствующий ресурс")
            args = change.get("args")
            if args is not None and (len(args) != 3 or any(not isinstance(v, int) for v in args)):
                raise FormatError("Неверные параметры замены")
            result.changes[key] = decoded(change["data"]), tuple(args) if args is not None else None
        result.deleted = set(content.get("deleted", []))
        if not result.deleted <= allowed:
            raise FormatError("Удаление ссылается на отсутствующую запись")
        result.history.clear()
        result.path = path
        return result

    def export(self, path):
        self.archive.export(Path(path), self.changes, self.added, self.deleted)

    def bundle(self, record, path):
        # Object/puzzle exchanges include its descriptor, conditions, actions,
        # animation frames and other records sharing the source resource ID.
        if Path(path).resolve() == self.archive.path:
            raise FormatError("Набор нельзя записывать поверх исходной игры")
        rows = [self.get(r) for r in self.records(record.section) if r.ident == record.ident]
        content = dict(format="pilots-resource-set", version=1, sha256=self.archive.sha256,
                       records=[dict(kind=r.kind, ident=r.ident, args=r.args,
                                     data=base64.b64encode(r.data).decode("ascii")) for r in rows])
        Path(path).write_text(json.dumps(content, indent=2), encoding="utf-8")

    def import_bundle(self, section, path):
        content = json.loads(Path(path).read_text(encoding="utf-8"))
        if content.get("format") != "pilots-resource-set" or content.get("version") != 1 or content.get("sha256") != self.archive.sha256:
            raise FormatError("Набор ресурсов имеет другой формат или источник")
        snapshot = self.changes.copy(), self.added.copy(), self.deleted.copy()
        history = self.history.copy()
        try:
            for row in content["records"]:
                data = base64.b64decode(row["data"], validate=True)
                if len(data) > MAX_BLOB:
                    raise FormatError("Слишком большой ресурс")
                args = tuple(row["args"])
                found = next((r for r in self.records(section) if (r.kind, r.ident, r.args) == (row["kind"], row["ident"], args)), None)
                if found:
                    self.replace(found, data)
                else:
                    self.add(section, row["kind"], row["ident"], args, data)
            self.history = history
            self.history.append(snapshot)
        except Exception:
            self.changes, self.added, self.deleted = snapshot
            self.history = history
            raise
