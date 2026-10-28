"""Build only the editor; no input game installation is read or bundled."""
from importlib import metadata
import os
from pathlib import Path
import shutil
import subprocess
import sys

ROOT = Path(__file__).resolve().parents[1]
if os.name != "nt":
    raise SystemExit("Windows EXE must be built on Windows; use the Windows editor workflow")
subprocess.run([sys.executable, "-m", "PyInstaller", "--noconfirm", "--clean", "--windowed", "--onedir",
                "--name", "PilotsEditor", "--paths", str(ROOT / "src"), str(ROOT / "scripts/launcher.py")], cwd=ROOT, check=True)
out = ROOT / "dist/PilotsEditor"
for name in ("README.md", "LICENSE", "THIRD_PARTY.md"):
    shutil.copyfile(ROOT / name, out / name)
for name in ("PySide6-Essentials", "shiboken6", "Pillow", "PyInstaller"):
    dist = metadata.distribution(name)
    copied = 0
    for entry in dist.files or []:
        if not any(token in str(entry).lower() for token in ("license", "copying", "notice")):
            continue
        source = Path(dist.locate_file(entry))
        if not source.is_file():continue
        safe = [p for p in Path(entry).parts if p not in ("..", ".") and ":" not in p]
        target = out / "licenses" / name / Path(*safe)
        target.parent.mkdir(parents=True, exist_ok=True);shutil.copyfile(source, target);copied += 1
    if not copied:
        raise SystemExit(f"Missing dependency license files: {name}")
print("Built editor without game content:", out)
