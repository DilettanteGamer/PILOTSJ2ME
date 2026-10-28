import base64
import io
import json
from pathlib import Path
import struct
import tempfile
import unittest
import wave
from PIL import Image
from pilots_editor.archive import Archive, Cursor, FormatError, number
from pilots_editor.project import Project
from pilots_editor import resources as R
from fixtures import source


class CoreTests(unittest.TestCase):
    def setUp(self):
        self.temp = tempfile.TemporaryDirectory();self.root = Path(self.temp.name);self.file = self.root / "source.exe";self.palette = source(self.file)
        self.archive = Archive(self.file);self.project = Project(self.archive)

    def tearDown(self):self.temp.cleanup()

    def find(self, kind, ident=0, args=None):
        return next(r for r in self.project.records(4) if r.kind == kind and r.ident == ident and (args is None or r.args == args))

    def test_unmodified_export_exact_and_prefix_tail_opaque(self):
        target = self.root / "copy.exe";self.project.export(target);self.assertEqual(target.read_bytes(), self.file.read_bytes())
        self.assertTrue(next(s for s in self.archive.sections if s.ident == 0).diagnostic)

    def test_shared_payload_does_not_accidentally_follow_replacement(self):
        first = self.find(0x43, 7, (0, 0, 0));second = self.find(0x43, 8)
        self.project.replace(first, b"arbitrary new payload longer than source")
        target = self.root / "changed.exe";self.project.export(target);a = Archive(target)
        self.assertEqual(a.by_key[first.key].data, b"arbitrary new payload longer than source")
        self.assertEqual(a.by_key[second.key].data, second.data)
        self.assertEqual(a.prefix, self.archive.prefix);self.assertEqual(a.tail, self.archive.tail)
        self.assertEqual(a.sections[-1].original, self.archive.sections[-1].original)

    def test_delete_and_add_relocate_index(self):
        first = self.find(0x43, 7, (0, 0, 0));self.project.delete(first);self.project.add(4, 0x43, 99, (0, 0, 0), b"new")
        target = self.root / "changed.exe";self.project.export(target);a = Archive(target)
        self.assertEqual(next(r for r in a.by_key.values() if r.kind == 0x43 and r.ident == 8).data, first.data)
        self.assertEqual(next(r for r in a.by_key.values() if r.ident == 99).data, b"new")

    def test_project_persists_patches_only_and_preserves_changed_args(self):
        r = self.find(0x20, 2);self.project.replace(r, b"abc", (2, 3, 4));self.project.replace(r, b"def")
        saved = self.root / "mod.pweproj";self.project.save(saved);p = Project.load(saved)
        self.assertEqual(p.get(r).args, (2, 3, 4));self.assertEqual(p.get(r).data, b"def")
        document = json.loads(saved.read_text());self.assertEqual(len(document["changes"]), 1);self.assertNotIn("MZ", saved.read_text())
        self.assertTrue(p.undo() is False)
        p.replace(r, b"ghi");p.undo();self.assertEqual(p.get(r).data, b"def");p.undo(True);self.assertEqual(p.get(r).data, b"ghi")

    def test_wrong_source_project_and_source_overwrite_rejected(self):
        with self.assertRaises(FormatError):self.project.export(self.file)
        with self.assertRaises(FormatError):self.project.save(self.file)
        with self.assertRaises(FormatError):self.project.bundle(self.find(0x43, 7, (0, 0, 0)), self.file)
        saved = self.root / "mod.pweproj";self.project.save(saved);self.file.write_bytes(self.file.read_bytes()[:-1] + b"!")
        with self.assertRaises(FormatError):Project.load(saved)
        with self.assertRaises(FormatError):self.project.export(self.root / "out.exe")

    def test_png_roundtrip_palette_alpha_and_offsets(self):
        frame = self.find(0x43, 7, (0, 0, 0));im = R.image(frame, self.palette)
        stream = io.BytesIO();im.save(stream, format="PNG");stream.seek(0)
        self.assertEqual(R.replace_image(frame, Image.open(stream), self.palette), frame.data)
        self.assertEqual(R.pivot(self.project, frame), (-2, -7))
        changed = im.convert("RGBA");changed.putpixel((0, 0), (255, 0, 0, 0))
        self.assertEqual(R.replace_image(frame, changed, self.palette)[4], 0)
        changed.putpixel((0, 0), (255, 0, 0, 127))
        with self.assertRaises(FormatError):R.replace_image(frame, changed, self.palette)

    def test_wav_replacement(self):
        r = self.find(0x51, 4);self.assertEqual(R.replace_wav(r, R.wav(r)), r.data)
        stream = io.BytesIO()
        with wave.open(stream, "wb") as out:out.setparams((2, 2, 44100, 0, "NONE", "not compressed"));out.writeframes(bytes(16))
        with self.assertRaises(FormatError):R.replace_wav(r, stream.getvalue())

    def test_text_preserves_script_offset_and_rejects_overflow(self):
        r = self.find(0x2C, 2);self.assertEqual(R.text(r), "Тест")
        data = R.replace_text(r, "Проверка");self.assertEqual(data[12:], r.data[12:]);self.assertEqual(len(data), len(r.data))
        with self.assertRaises(FormatError):R.replace_text(r, "Слишком длинная строка")
        from pilots_editor.archive import Record
        adjacent = Record("a", 4, 0x2C, 1, (0, 0, 12), b"Ntcn\0" + b"second\0" + b"\x00")
        self.assertEqual(R.replace_text(adjacent, "Да")[5:], adjacent.data[5:])
        with self.assertRaises(FormatError):R.replace_text(adjacent, "Длиннее")

    def test_move_scene_cell_and_static_animation_reference(self):
        r = self.find(0x19);entry = R.placements(r, (8, 8))[0]
        self.assertEqual((entry["entity"], entry["x"], entry["y"]), (2, 8, 16))
        self.project.replace(r, R.move_cell(r, entry["offset"], 32, 24, (8, 8)))
        self.assertEqual(R.placements(self.project.get(r), (8, 8))[0]["x"], 32)
        self.assertEqual(R.initial_picture(self.project, 4, 2).ident, 7)

    def test_resource_bundle_transaction(self):
        r = self.find(0x43, 7, (0, 0, 0));path = self.root / "set.pwresources";self.project.bundle(r, path)
        data = json.loads(path.read_text());data["records"][0]["data"] = base64.b64encode(b"custom").decode();path.write_text(json.dumps(data))
        self.project.import_bundle(4, path);self.assertEqual(self.project.get(r).data, b"custom");self.project.undo();self.assertEqual(self.project.get(r).data, r.data)

    def test_numbers_lz_overlap_and_corrupt_lengths(self):
        for value in (0, 127, 128, 65535, 0xFFFFFFFF):self.assertEqual(Cursor(number(value)).number(), value)
        self.assertEqual(Cursor(bytes([0x84, 4, 6, 1, 65, 1, 2])).blob(), b"AAAAAA")
        with self.assertRaises(FormatError):Cursor(bytes([0x84, 3, 3, 0, 0, 0])).blob()
        with self.assertRaises(FormatError):Cursor(bytes([0x83, 255, 255, 255, 127])).blob()


if __name__ == "__main__":unittest.main()
