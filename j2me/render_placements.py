#!/usr/bin/env python3
"""Optional static placement contact sheet; requires Pillow, no playback tests."""
import json
from pathlib import Path
import struct
import zipfile

from PIL import Image, ImageDraw

ROOT = Path(__file__).resolve().parent


def frame_at(data, index):
    position = 777
    for _ in range(index + 1):
        x, y, width, height = struct.unpack_from('>hhHH', data, position)
        position += 8
        pixels = data[position:position + width * height]
        position += width * height
    image = Image.frombytes('P', (width, height), pixels)
    image.putpalette(data[9:777])
    image = image.convert('RGBA')
    alpha = bytes(0 if value == data[8] else 255 for value in pixels)
    image.putalpha(Image.frombytes('L', (width, height), alpha))
    return x, y, image


def main():
    adjustments = json.loads((ROOT / 'animation-adjustments.json').read_text())
    catalog = json.loads((ROOT / 'analysis/animation-catalog.json').read_text())
    rows = [r for r in catalog if str(r['resource_id']) in adjustments['anchors']]
    height = ((len(rows) + 2) // 3) * 235
    atlas = Image.new('RGB', (1920, height), (35, 35, 35))
    draw = ImageDraw.Draw(atlas)
    report = []
    with zipfile.ZipFile(ROOT / 'input/Pilots-240x320.jar') as archive:
        data = archive.read('scenes/02.p8')
        background = Image.frombytes('P', (640, 417), data[780:])
        background.putpalette(data[12:780])
        background = background.convert('RGBA')
        for number, row in enumerate(rows):
            aid = row['resource_id']
            raw = archive.read(f'scenes/02-{aid}.anm')
            count = struct.unpack_from('>H', raw, 4)[0]
            selected = count - 1 if aid == 66 else count // 2 if aid in [69, 83] else 0
            offset_x, offset_y, sprite = frame_at(raw, selected)
            before = row['current_anchor']
            after = adjustments['anchors'][str(aid)][:2]
            group_x, group_y = (number % 3) * 640, (number // 3) * 235
            for column, anchor in enumerate([before, after]):
                image = background.copy()
                image.alpha_composite(sprite, (anchor[0] + offset_x, anchor[1] + offset_y))
                image = image.convert('RGB').resize((320, 208))
                atlas.paste(image, (group_x + column * 320, group_y + 24))
                label = f'{row["menu_number"]:02} ID{aid} '
                label += ('before ' if column == 0 else 'after ') + str(anchor)
                draw.text((group_x + column * 320 + 3, group_y + 4), label, fill='white')
            if aid in [66, 88, 90]:
                basis = 'background calibration; not original event position'
            elif aid in [69, 83]:
                basis = 'barrel alignment and original ground reference'
            else:
                basis = 'original actor default reference; not original per-action trajectory'
            basis = adjustments.get('placement_basis', {}).get(str(aid), basis)
            report.append(dict(menu_number=row['menu_number'], resource_id=aid,
                               old_anchor=before, new_anchor=after,
                               new_focus=adjustments['anchors'][str(aid)][2:], basis=basis))
    stem = ROOT / 'analysis' / ('placement-' + adjustments['version'])
    # Do not use with_suffix: the version itself contains periods.
    atlas.save(str(stem) + '.png')
    Path(str(stem) + '.json').write_text(json.dumps(report, ensure_ascii=False, indent=2) + '\n')


if __name__ == '__main__':
    main()
