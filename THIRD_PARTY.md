# Сторонние компоненты

Код редактора — MIT. Игровые материалы не поставляются и не получают новую
лицензию от редактора.

Qt/PySide6/Shiboken распространяются на условиях их LGPL/GPL/commercial
лицензий; Pillow — на условиях HPND и лицензий включённых компонентов.
PyInstaller — сборочный инструмент с собственными условиями и исключением
для создаваемых приложений. Точные тексты установленных версий копируются
скриптом сборки в `licenses/` дистрибутива.

Windows-пакет использует отдельные DLL Qt в каталоге `_internal` (onedir),
а не включает движок игры. Сохраняйте этот каталог и `licenses/` при
распространении. Сведения и исходники зависимостей:

- https://code.qt.io/pyside/pyside-setup.git/
- https://code.qt.io/qt/qtbase.git/
- https://github.com/python-pillow/Pillow
- https://github.com/pyinstaller/pyinstaller
