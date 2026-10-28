import os
os.environ.setdefault("QT_QPA_PLATFORM", "offscreen")
from pathlib import Path
import tempfile
import unittest
from PySide6.QtWidgets import QApplication
from pilots_editor.app import Editor
from fixtures import source


class UITests(unittest.TestCase):
    @classmethod
    def setUpClass(cls):cls.app = QApplication.instance() or QApplication([])

    def test_load_select_animate_replace_and_drag(self):
        with tempfile.TemporaryDirectory() as temp:
            path = Path(temp) / "authored.exe";source(path);window = Editor();window.load_source(path);window.show();self.app.processEvents()
            try:
                p = window.project;frame = next(r for r in p.records(4) if r.kind == 0x43 and r.ident == 7)
                window.focus_key(frame.key);self.assertEqual(len(window.preview_frames), 2);window.advance_frame();self.assertEqual(window.slider.value(), 1)
                obj = next(r for r in p.records(4) if r.kind == 0x20);window.focus_key(obj.key);window.descriptor[2].setValue(10);window.apply_properties();self.assertEqual(p.get(obj).data[2], 10)
                initial = next(r for r in p.records(4) if r.kind == 0x19);from pilots_editor.resources import placements
                entry = placements(p.get(initial), (8, 8))[0];window.dragged(initial, entry, 32, 24);self.assertTrue(window.dirty)
                self.assertEqual(placements(p.get(initial), (8, 8))[0]["x"], 32)
                project = Path(temp) / "mod.pweproj";p.save(project);window.dirty = False
                self.assertGreater(len(window.scene_view.scene().items()), 2)
            finally:window.dirty = False;window.close()
