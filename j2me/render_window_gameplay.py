#!/usr/bin/env python3
"""Render fixed snapshots of the corrected window layers (optional Pillow dependency)."""
from pathlib import Path
import zipfile
import sys
sys.dont_write_bytecode = True
from PIL import Image, ImageDraw
from verify_window_alignment import frame

ROOT = Path(__file__).resolve().parent
with zipfile.ZipFile(ROOT / 'dist/Pilots-240x320.jar') as archive:
    bg = archive.read('scenes/02.p8')
    background = Image.frombytes('P', (640, 417), bg[780:])
    background.putpalette(bg[12:780])
    background = background.convert('RGBA')

    def paste(image, ident, selected, x, y):
        f = frame(archive.read('scenes/02-%d.anm' % ident), selected)
        sprite = Image.frombytes('P', (f['width'], f['height']), f['pixels'])
        sprite.putpalette(f['palette'])
        sprite = sprite.convert('RGBA')
        sprite.putalpha(Image.frombytes('L', sprite.size, bytes(0 if p == f['transparent'] else 255 for p in f['pixels'])))
        image.alpha_composite(sprite, (x + f['x'], y + f['y']))

    result = Image.new('RGB', (1200, 576), (30, 30, 30))
    draw = ImageDraw.Draw(result)
    for index, step in enumerate([1, 12, 13, 21, 38, 88]):
        image = background.copy()
        if step >= 13:
            paste(image, 63, 0, 128, 224)
        paste(image, 64 if step < 88 else 20016, min(step - 1, 47) if step < 88 else 0, 192, 304)
        x, y = index % 3 * 400, index // 3 * 288
        result.paste(image.crop((130, 215, 330, 350)).resize((400, 270), Image.Resampling.NEAREST), (x, y + 18))
        draw.text((x + 4, y + 3), 'Step %d: %s' % (step, 'open shutters persist' if step >= 13 else 'opening frames own shutters'), fill='white')
    result.save(ROOT / 'analysis/window-gameplay-0.4.1.png')
    print('Rendered fixed window layer snapshots; no device/playback test')
