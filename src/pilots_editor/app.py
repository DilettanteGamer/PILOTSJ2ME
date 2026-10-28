"""Windows desktop UI. All game-dependent content comes from a selected local file."""
from __future__ import annotations
import os
from pathlib import Path
import struct
import subprocess
import sys
import tempfile
from PIL import Image
from PySide6.QtCore import Qt, QTimer, QUrl
from PySide6.QtGui import QAction, QColor, QDesktopServices, QImage, QKeySequence, QPainter, QPen, QPixmap
from PySide6.QtWidgets import (QApplication, QCheckBox, QComboBox, QFileDialog, QFormLayout, QGraphicsItem,
    QGraphicsItemGroup, QGraphicsPixmapItem, QGraphicsRectItem, QGraphicsScene, QGraphicsSimpleTextItem,
    QGraphicsView, QHBoxLayout, QInputDialog, QLabel, QLineEdit, QMainWindow, QMessageBox,
    QPlainTextEdit, QPushButton, QSlider, QSpinBox, QSplitter, QTabWidget, QTreeWidget,
    QTreeWidgetItem, QVBoxLayout, QWidget)
from .archive import Archive, FormatError
from .project import Project
from . import resources as R


def pixmap(image):
    rgba = image.convert("RGBA")
    return QPixmap.fromImage(QImage(rgba.tobytes(), rgba.width, rgba.height, rgba.width * 4,
                                    QImage.Format.Format_RGBA8888).copy())


class View(QGraphicsView):
    def __init__(self):
        super().__init__()
        self.setScene(QGraphicsScene(self))
        self.setBackgroundBrush(QColor("#171b22"))
        self.setRenderHint(QPainter.RenderHint.SmoothPixmapTransform, False)
        self.setDragMode(QGraphicsView.DragMode.RubberBandDrag)

    def wheelEvent(self, event):
        factor = 1.2 if event.angleDelta().y() > 0 else 1 / 1.2
        if 0.1 <= self.transform().m11() * factor <= 16:
            self.setTransformationAnchor(QGraphicsView.ViewportAnchor.AnchorUnderMouse)
            self.scale(factor, factor)
        event.accept()


class ObjectItem(QGraphicsItemGroup):
    def __init__(self, entry, cell, picture, shift, moved):
        super().__init__()
        self.entry, self.cell, self.moved = entry, cell, moved
        self.setFlags(QGraphicsItem.GraphicsItemFlag.ItemIsMovable | QGraphicsItem.GraphicsItemFlag.ItemIsSelectable)
        if picture:
            art = QGraphicsPixmapItem(picture)
            art.setPos(*shift)
            self.addToGroup(art)
        box = QGraphicsRectItem(0, 0, *cell)
        box.setPen(QPen(QColor("#ffd76b"), 1, Qt.PenStyle.DashLine))
        self.addToGroup(box)
        label = QGraphicsSimpleTextItem(str(entry["entity"]))
        label.setBrush(QColor("#fff2bf"));label.setPos(1, -17);self.addToGroup(label)
        self.setPos(entry["x"], entry["y"])
        self.setToolTip(f'ID {entry["entity"]} · {entry["x"]}, {entry["y"]}')

    def mouseReleaseEvent(self, event):
        super().mouseReleaseEvent(event)
        if (round(self.x()), round(self.y())) != (self.entry["x"], self.entry["y"]):
            self.moved(self.entry, self.x(), self.y())


class Editor(QMainWindow):
    def __init__(self):
        super().__init__()
        self.project = None;self.current = None;self.dirty = False;self.last_export = None
        self.preview_frames = [];self.loading = False;self.temp = tempfile.TemporaryDirectory(prefix="pilots-editor-", ignore_cleanup_errors=True)
        self.setWindowTitle("Pilots Resource Editor — 0.1.0");self.resize(1440, 880)
        toolbar = self.addToolBar("Проект")
        self.action(toolbar, "Открыть игру…", self.open_game, "Ctrl+O")
        self.action(toolbar, "Открыть проект…", self.open_project, "Ctrl+Shift+O")
        self.action(toolbar, "Сохранить проект", self.save_project, "Ctrl+S")
        self.action(toolbar, "Экспорт копии EXE…", self.export_game, "Ctrl+E")
        self.action(toolbar, "Запустить копию", self.run_game)
        self.action(toolbar, "Отменить", lambda: self.undo(False), "Ctrl+Z")
        self.action(toolbar, "Повторить", lambda: self.undo(True), "Ctrl+Y")
        layout = QVBoxLayout();container = QWidget();container.setLayout(layout);self.setCentralWidget(container)
        row = QHBoxLayout();self.sections = QComboBox();self.sections.currentIndexChanged.connect(self.section_changed)
        self.search = QLineEdit();self.search.setPlaceholderText("Фильтр: тип, ID, текст…");self.search.textChanged.connect(self.filter_tree)
        self.show_empty = QCheckBox("Служебные ячейки");self.show_empty.toggled.connect(lambda: self.rebuild_scene() if self.project else None)
        row.addWidget(QLabel("Секция / сцена"));row.addWidget(self.sections, 1);row.addWidget(self.search, 2);row.addWidget(self.show_empty);layout.addLayout(row)
        split = QSplitter();layout.addWidget(split)
        left = QWidget();ll = QVBoxLayout(left);ll.setContentsMargins(0, 0, 0, 0)
        self.tree = QTreeWidget();self.tree.setHeaderLabels(["Ресурс", "ID / параметры", "Байт"]);self.tree.currentItemChanged.connect(self.select_resource)
        ll.addWidget(self.tree)
        lr = QHBoxLayout();ll.addLayout(lr)
        for label, callback in [("+ Ресурс", self.add_resource), ("Удалить", self.delete_resource)]:
            button = QPushButton(label);button.clicked.connect(lambda checked=False, fn=callback: self.guard(fn));lr.addWidget(button)
        split.addWidget(left)
        middle = QWidget();ml = QVBoxLayout(middle);ml.setContentsMargins(0, 0, 0, 0)
        self.views = QTabWidget();self.scene_view = View();self.frame_view = View()
        self.views.addTab(self.scene_view, "Сцена · статический обзор");self.views.addTab(self.frame_view, "Кадр / анимация");ml.addWidget(self.views)
        controls = QHBoxLayout();self.play = QPushButton("▶");self.play.clicked.connect(self.toggle_play)
        self.slider = QSlider(Qt.Orientation.Horizontal);self.slider.valueChanged.connect(lambda: self.guard(self.preview_frame))
        self.counter = QLabel("Нет анимации");controls.addWidget(self.play);controls.addWidget(self.slider, 1);controls.addWidget(self.counter)
        fit = QPushButton("Вписать");fit.clicked.connect(self.fit_view);controls.addWidget(fit);ml.addLayout(controls)
        notice = QLabel("Обзор: исходная расстановка и распознанные первые кадры. Условия не исполняются. Колесо — масштаб; перетаскивание — ячейка карты.")
        notice.setWordWrap(True);ml.addWidget(notice);split.addWidget(middle)
        right = QWidget();rl = QVBoxLayout(right);rl.setContentsMargins(0, 0, 0, 0)
        self.info = QLabel("Откройте локальную копию PILOTS.EXE. Редактор поставляется без игры.");self.info.setWordWrap(True);rl.addWidget(self.info)
        self.tabs = QTabWidget();rl.addWidget(self.tabs)
        properties = QWidget();form = QFormLayout(properties)
        self.args = []
        for i in range(3):
            spin = QSpinBox();spin.setRange(0, 2147483647);form.addRow(f"Параметр {i + 1}", spin);self.args.append(spin)
        self.descriptor = []
        for name in ("Тип объекта", "Флаги объекта", "Слой объекта"):
            spin = QSpinBox();spin.setRange(0, 255);form.addRow(name, spin);self.descriptor.append(spin)
        self.offsets = []
        for name in ("Смещение кадра X", "Смещение кадра Y"):
            spin = QSpinBox();spin.setRange(-32767, 32767);form.addRow(name, spin);self.offsets.append(spin)
        self.apply_properties_button = QPushButton("Применить свойства");self.apply_properties_button.clicked.connect(lambda: self.guard(self.apply_properties));form.addRow(self.apply_properties_button)
        self.tabs.addTab(properties, "Свойства")
        raw_panel = QWidget();raw_layout = QVBoxLayout(raw_panel);self.raw = QPlainTextEdit();self.raw.setPlaceholderText("Данные ресурса в HEX")
        self.raw.setLineWrapMode(QPlainTextEdit.LineWrapMode.NoWrap);raw_layout.addWidget(self.raw)
        apply = QPushButton("Применить HEX");apply.clicked.connect(lambda: self.guard(self.apply_raw));raw_layout.addWidget(apply);self.tabs.addTab(raw_panel, "Данные / правила")
        text_panel = QWidget();tl = QVBoxLayout(text_panel);self.text = QPlainTextEdit();tl.addWidget(self.text)
        self.text_help = QLabel();self.text_help.setWordWrap(True);tl.addWidget(self.text_help)
        self.apply_text_button = QPushButton("Применить текст");self.apply_text_button.clicked.connect(lambda: self.guard(self.apply_text));tl.addWidget(self.apply_text_button);self.tabs.addTab(text_panel, "Текст")
        self.rules = QTreeWidget();self.rules.setHeaderLabels(["Связанные записи того же ID", "Ключ"]);self.rules.itemDoubleClicked.connect(lambda item: self.focus_key(item.data(0, Qt.ItemDataRole.UserRole)));self.tabs.addTab(self.rules, "Связи / загадка")
        for label, fn in [("Экспорт ресурса…", self.export_resource), ("Заменить ресурс…", self.import_resource),
                          ("Слушать реплику", self.listen), ("Экспорт набора ID…", self.export_bundle), ("Импорт набора ID…", self.import_bundle)]:
            button = QPushButton(label);button.clicked.connect(lambda checked=False, callback=fn: self.guard(callback));rl.addWidget(button)
        split.addWidget(right);split.setSizes([320, 740, 380])
        self.timer = QTimer(self);self.timer.setInterval(100);self.timer.timeout.connect(self.advance_frame)
        self.statusBar().showMessage("Нет загруженной игры · проект хранит только ваши изменения")

    def action(self, toolbar, label, fn, shortcut=None):
        action = QAction(label, self)
        if shortcut:action.setShortcut(QKeySequence(shortcut))
        action.triggered.connect(lambda checked=False: self.guard(fn));toolbar.addAction(action)

    def guard(self, callback):
        try:return callback()
        except Exception as error:
            self.timer.stop();self.play.setText("▶")
            QMessageBox.critical(self, "Операция не выполнена", str(error))

    def may_discard(self):
        if not self.dirty:return True
        choice = QMessageBox.question(self, "Несохранённые изменения", "Сохранить проект перед продолжением?", QMessageBox.StandardButton.Save | QMessageBox.StandardButton.Discard | QMessageBox.StandardButton.Cancel)
        if choice == QMessageBox.StandardButton.Cancel:return False
        if choice == QMessageBox.StandardButton.Save:return self.save_project()
        return True

    def open_game(self):
        if not self.may_discard():return
        path, _ = QFileDialog.getOpenFileName(self, "Выберите вашу копию игры", "", "Windows game (*.exe);;Все файлы (*)")
        if path:self.load_source(path)

    def load_source(self, path):
        self.statusBar().showMessage("Чтение и распаковка пользовательских ресурсов…");QApplication.processEvents()
        self.set_project(Project(Archive(Path(path))))

    def set_project(self, project):
        self.timer.stop();self.play.setText("▶");self.project = project;self.current = None;self.dirty = False
        self.sections.blockSignals(True);self.sections.clear()
        for section in project.archive.sections:
            self.sections.addItem(f"{section.ident:02} · {len(section.records)} записей" + (" · неизвестный формат" if section.diagnostic else ""), section.ident)
        index = self.sections.findData(4);self.sections.setCurrentIndex(max(0, index));self.sections.blockSignals(False)
        self.section_changed();self.statusBar().showMessage(f"Загружено {len(project.archive.by_key)} записей · источник {project.archive.path.name}")

    def open_project(self):
        if not self.may_discard():return
        path, _ = QFileDialog.getOpenFileName(self, "Открыть проект", "", "Проект (*.pweproj)")
        if path:
            try:project = Project.load(path)
            except FileNotFoundError:
                source, _ = QFileDialog.getOpenFileName(self, "Укажите исходный EXE для проекта")
                if not source:return
                project = Project.load(path, source)
            self.set_project(project)

    def save_project(self):
        if not self.project:return False
        path = self.project.path
        if not path:
            path, _ = QFileDialog.getSaveFileName(self, "Сохранить изменения отдельно от игры", "mod.pweproj", "Проект (*.pweproj)")
        if not path:return False
        self.project.save(path);self.dirty = False;self.statusBar().showMessage(f"Проект сохранён: {path}");return True

    def export_game(self):
        if not self.project:return
        path, _ = QFileDialog.getSaveFileName(self, "Новая копия EXE с заменами", "PILOTS-MOD.EXE", "Windows game (*.exe)")
        if path:
            self.project.export(path);self.last_export = Path(path);self.statusBar().showMessage("Копия EXE проверена. Для запуска нужны остальные файлы вашей игры рядом с ней.")

    def run_game(self):
        if os.name != "nt":raise FormatError("Запуск оригинальной игры доступен в Windows. Экспорт можно перенести на Windows отдельно")
        if not self.last_export:raise FormatError("Сначала экспортируйте копию игры")
        subprocess.Popen([str(self.last_export)], cwd=str(self.last_export.parent))

    def section_changed(self, *unused):
        if not self.project:return
        self.timer.stop();self.play.setText("▶");self.current = None;self.preview_frames = [];self.frame_view.scene().clear();self.loading = True;self.tree.clear();groups = {}
        for record in self.project.records(self.sections.currentData()):
            r = self.project.get(record);kind = R.KINDS.get(r.kind, f"Тип 0x{r.kind:02X}")
            if kind not in groups:
                groups[kind] = QTreeWidgetItem(self.tree, [kind])
            label = R.text(r)
            item = QTreeWidgetItem(groups[kind], [("* " if r.key in self.project.changes or r.key.startswith("new:") else "") + (label[:50] if label is not None else kind), f"{r.ident} · {r.args}", str(len(r.data))])
            item.setData(0, Qt.ItemDataRole.UserRole, record.key)
        self.tree.expandToDepth(0);self.loading = False;self.filter_tree();self.rebuild_scene()

    def filter_tree(self, *unused):
        query = self.search.text().casefold()
        for i in range(self.tree.topLevelItemCount()):
            group = self.tree.topLevelItem(i);visible = False
            for j in range(group.childCount()):
                item = group.child(j);match = query in " ".join(item.text(k) for k in range(3)).casefold();item.setHidden(not match);visible |= match
            group.setHidden(not visible)

    def record_by_key(self, key):
        return next((r for r in self.project.records() if r.key == key), None)

    def focus_key(self, key):
        record = self.record_by_key(key)
        if not record:return
        if self.sections.currentData() != record.section:self.sections.setCurrentIndex(self.sections.findData(record.section))
        for i in range(self.tree.topLevelItemCount()):
            group = self.tree.topLevelItem(i)
            for j in range(group.childCount()):
                item = group.child(j)
                if item.data(0, Qt.ItemDataRole.UserRole) == key:self.tree.setCurrentItem(item);self.tree.scrollToItem(item);return

    def select_resource(self, item, previous=None):
        if self.loading or not item:return
        key = item.data(0, Qt.ItemDataRole.UserRole)
        if not key:return
        self.current = self.record_by_key(key)
        if not self.current:return
        r = self.project.get(self.current)
        self.info.setText(f"{R.KINDS.get(r.kind, 'Ресурс')} · секция {r.section} · ID {r.ident}\nКлюч {r.key} · {len(r.data)} байт")
        for spin, value in zip(self.args, r.args):spin.setValue(max(0, min(2147483647, value)))
        self.apply_properties_button.setEnabled(all(0 <= v <= 2147483647 for v in r.args))
        for i, spin in enumerate(self.descriptor):spin.setEnabled(r.kind == 0x20 and len(r.data) >= 3);spin.setValue(r.data[i] if r.kind == 0x20 and len(r.data) >= 3 else 0)
        shift = R.pivot(self.project, r) if r.kind == 0x43 else (0, 0)
        for spin, value in zip(self.offsets, shift):spin.setEnabled(r.kind == 0x43);spin.setValue(value)
        self.raw.setReadOnly(len(r.data) > 262144)
        self.raw.setPlainText("\n".join(r.data[i:i + 16].hex(" ") for i in range(0, len(r.data), 16)) if len(r.data) <= 262144 else "Большой блок: используйте экспорт / замену файла, PNG или WAV.")
        value = R.text(r);self.text.setPlainText(value or "");self.text.setReadOnly(value is None);self.apply_text_button.setEnabled(value is not None)
        self.text_help.setText(f"Выделено {r.args[2] - 1} байт; позиции кода сохраняются." if value is not None else "Текстовый префикс не распознан. Произвольные данные доступны на вкладке HEX.")
        self.rules.clear()
        for candidate in self.project.records(r.section):
            if candidate.ident == r.ident:
                row = QTreeWidgetItem(self.rules, [R.KINDS.get(candidate.kind, f"0x{candidate.kind:02X}") + f" {candidate.args}", candidate.key]);row.setData(0, Qt.ItemDataRole.UserRole, candidate.key)
        self.preview_frames = R.frames(self.project, r) if r.kind in (0x40, 0x42, 0x43) else [r] if r.kind == 0x18 else []
        self.slider.blockSignals(True);self.slider.setRange(0, max(0, len(self.preview_frames) - 1));self.slider.setValue(next((i for i, f in enumerate(self.preview_frames) if f.key == r.key), 0));self.slider.blockSignals(False)
        self.guard(self.preview_frame)

    def preview_frame(self, *unused):
        self.frame_view.scene().clear()
        if not self.preview_frames:self.counter.setText("Нет изображения");return
        r = self.preview_frames[self.slider.value()];picture = R.image(self.project.get(r), R.palette(self.project, self.sections.currentData()))
        self.frame_view.scene().addPixmap(pixmap(picture));self.frame_view.scene().setSceneRect(0, 0, picture.width, picture.height)
        self.counter.setText(f"{self.slider.value() + 1}/{len(self.preview_frames)} · {picture.width}×{picture.height}")

    def toggle_play(self):
        if self.timer.isActive():self.timer.stop();self.play.setText("▶")
        elif self.preview_frames:self.timer.start();self.play.setText("Ⅱ");self.views.setCurrentIndex(1)

    def advance_frame(self):
        if self.preview_frames:self.slider.setValue((self.slider.value() + 1) % len(self.preview_frames))

    def fit_view(self):
        view = self.scene_view if self.views.currentIndex() == 0 else self.frame_view
        view.fitInView(view.scene().sceneRect(), Qt.AspectRatioMode.KeepAspectRatio)

    def cell_size(self):
        for record in self.project.records(1):
            r = self.project.get(record)
            if r.kind == 0x10 and len(r.data) >= 28:
                cell = struct.unpack_from("<II", r.data, 20)
                if all(1 <= v <= 256 for v in cell):return cell
        return 64, 16

    def rebuild_scene(self):
        scene = self.scene_view.scene();scene.clear();section = self.sections.currentData()
        background = next((self.project.get(r) for r in self.project.records(section) if r.kind == 0x18), None)
        if background is None:
            scene.addText("В секции нет фона. Кадры доступны в дереве ресурсов.");return
        try:
            colors = R.palette(self.project, section);picture = R.image(background, colors);art = scene.addPixmap(pixmap(picture));art.setZValue(-10000)
            self.scene_size = picture.size;scene.setSceneRect(0, 0, *picture.size);cell = self.cell_size()
            initial = next((r for r in self.project.records(section) if r.kind == 0x19), None)
            if initial:
                for entry in R.placements(self.project.get(initial), cell):
                    if entry["entity"] == 0 and not self.show_empty.isChecked():continue
                    frame = R.initial_picture(self.project, section, entry["entity"])
                    picture = pixmap(R.image(frame, colors)) if frame else None
                    shift = R.pivot(self.project, frame) if frame else (0, 0)
                    marker = ObjectItem(entry, cell, picture, shift, lambda e, x, y, rec=initial: self.guard(lambda: self.dragged(rec, e, x, y)))
                    descriptor = next((self.project.get(r) for r in self.project.records(section) + self.project.records(1) if r.kind == 0x20 and r.ident == entry["entity"]), None)
                    if descriptor and len(descriptor.data) >= 3:marker.setZValue(-descriptor.data[2])
                    scene.addItem(marker)
            self.scene_view.fitInView(scene.sceneRect(), Qt.AspectRatioMode.KeepAspectRatio)
        except FormatError as error:scene.addText(str(error))

    def dragged(self, record, entry, x, y):
        cell = self.cell_size();x = max(0, min(self.scene_size[0] // cell[0] - 1, round(x / cell[0]))) * cell[0];y = max(0, min(self.scene_size[1] // cell[1] - 1, round(y / cell[1]))) * cell[1]
        self.project.replace(record, R.move_cell(self.project.get(record), entry["offset"], x, y, cell));self.changed(record.key)

    def changed(self, key=None):
        self.dirty = True;self.section_changed()
        if key:self.focus_key(key)
        self.statusBar().showMessage("Изменения в проекте · исходная игра остаётся на месте")

    def apply_raw(self):
        if self.current and not self.raw.isReadOnly():self.project.replace(self.current, bytes.fromhex(self.raw.toPlainText()));self.changed(self.current.key)

    def apply_text(self):
        if self.current:self.project.replace(self.current, R.replace_text(self.project.get(self.current), self.text.toPlainText()));self.changed(self.current.key)

    def apply_properties(self):
        if not self.current:return
        r = self.project.get(self.current);data = r.data
        if r.kind == 0x20 and len(data) >= 3:data = bytes(spin.value() for spin in self.descriptor) + data[3:]
        self.project.replace(self.current, data, tuple(spin.value() for spin in self.args))
        if r.kind == 0x43:
            piv = next((p for p in self.project.records(r.section) if p.kind == 0x42 and p.ident == r.ident and p.args[0] == r.args[0]), None)
            if piv:
                body = bytearray(self.project.get(piv).data);at = r.args[1] * 8 + 4
                if at + 4 <= len(body):struct.pack_into("<hh", body, at, *(-s.value() for s in self.offsets));self.project.replace(piv, body)
        self.changed(r.key)

    def export_resource(self):
        if not self.current:return
        r = self.project.get(self.current);suffix = ".png" if r.kind in (0x18, 0x43) else ".wav" if r.kind == 0x51 else ".bin"
        path, _ = QFileDialog.getSaveFileName(self, "Экспорт выбранного ресурса", f"resource-{r.section}-{r.kind:02x}-{r.ident}{suffix}")
        if path:
            if Path(path).resolve() == self.project.archive.path:raise FormatError("Ресурс нельзя записывать поверх исходной игры")
            if suffix == ".png":R.image(r, R.palette(self.project, self.sections.currentData())).save(path)
            else:Path(path).write_bytes(R.wav(r) if suffix == ".wav" else r.data)

    def import_resource(self):
        if not self.current:return
        path, _ = QFileDialog.getOpenFileName(self, "PNG / WAV или исходный бинарный блок")
        if not path:return
        r = self.project.get(self.current);extension = Path(path).suffix.lower()
        if extension == ".png" and r.kind in (0x18, 0x43):
            with Image.open(path) as im:data = R.replace_image(r, im, R.palette(self.project, self.sections.currentData()))
        elif extension == ".wav" and r.kind == 0x51:data = R.replace_wav(r, Path(path).read_bytes())
        else:data = Path(path).read_bytes()
        self.project.replace(self.current, data);self.changed(r.key)

    def listen(self):
        if not self.current or self.current.kind != 0x51:raise FormatError("Выберите звуковую реплику")
        path = Path(self.temp.name) / "preview.wav";path.write_bytes(R.wav(self.project.get(self.current)));QDesktopServices.openUrl(QUrl.fromLocalFile(str(path)))

    def export_bundle(self):
        if self.current:
            path, _ = QFileDialog.getSaveFileName(self, "Набор записей одного ID", "resources.pwresources")
            if path:self.project.bundle(self.current, path)

    def import_bundle(self):
        if self.project:
            path, _ = QFileDialog.getOpenFileName(self, "Заменить / добавить записи набора", "", "Набор (*.pwresources)")
            if path:self.project.import_bundle(self.sections.currentData(), path);self.changed()

    def add_resource(self):
        if not self.project:return
        value, ok = QInputDialog.getText(self, "Добавить ресурс", "Тип (hex), ID, параметр1, параметр2, параметр3", text="43, 200, 0, 0, 0")
        if not ok:return
        values = [v.strip() for v in value.split(",")]
        if len(values) != 5:raise FormatError("Нужно пять чисел")
        kind = int(values[0], 16);ident, a, b, c = map(int, values[1:]);path, _ = QFileDialog.getOpenFileName(self, "Бинарное содержимое новой записи")
        if path:self.project.add(self.sections.currentData(), kind, ident, (a, b, c), Path(path).read_bytes());self.changed()

    def delete_resource(self):
        if self.current and QMessageBox.question(self, "Удалить ресурс", "Удалить выбранную запись из экспортируемой копии?") == QMessageBox.StandardButton.Yes:self.project.delete(self.current);self.changed()

    def undo(self, redo):
        if self.project and self.project.undo(redo):self.changed()

    def closeEvent(self, event):
        if self.may_discard():self.timer.stop();self.temp.cleanup();event.accept()
        else:event.ignore()


def main():
    app = QApplication(sys.argv);app.setStyle("Fusion")
    app.setStyleSheet("QMainWindow,QWidget{font-size:12px} QTreeWidget,QPlainTextEdit,QGraphicsView{background:#202630;color:#e9eef5} QToolBar{spacing:8px;padding:6px} QPushButton{padding:5px} QTabWidget::pane{border:1px solid #4b5665}")
    editor = Editor();editor.show()
    if len(sys.argv) > 1:QTimer.singleShot(0, lambda: editor.guard(lambda: editor.load_source(sys.argv[1])))
    sys.exit(app.exec())


if __name__ == "__main__":main()
