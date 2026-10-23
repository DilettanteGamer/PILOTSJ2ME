#!/usr/bin/env python3
"""Verify spatial registration of window resources without testing playback."""
import argparse
import json
from pathlib import Path
import struct
import zipfile

ROOT = Path(__file__).resolve().parent


def frame(data, selected=0):
    position = 777
    for _ in range(selected + 1):
        x, y, width, height = struct.unpack_from('>hhHH', data, position)
        position += 8
        pixels = data[position:position + width * height]
        position += width * height
    return dict(x=x, y=y, width=width, height=height, pixels=pixels,
                palette=data[9:777], transparent=data[8])


def require(condition, message):
    if not condition:
        raise ValueError(message)


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--render', action='store_true', help='Render static comparison using Pillow')
    args = parser.parse_args()
    settings = json.loads((ROOT / 'animation-adjustments.json').read_text())
    evidence = json.loads((ROOT / 'analysis/original-scene02.json').read_text())
    for entity in [23, 24]:
        locations = {tuple(item['world']) for item in evidence['placement_evidence'] if item['entity'] == entity}
        require(locations == {(128, 224)}, 'Unexpected original window placement')
    with zipfile.ZipFile(ROOT / 'input/Pilots-240x320.jar') as archive:
        closed = frame(archive.read('scenes/02-62.anm'))
        animated = frame(archive.read('scenes/02-64.anm'))
        require(closed['palette'] == animated['palette'], 'Palette mismatch')
        pane_top_left = [128 + closed['x'], 224 + closed['y']]
        actor_anchor = [pane_top_left[0] - animated['x'], pane_top_left[1] - animated['y']]
        require(actor_anchor == [192, 304], 'Unexpected window actor anchor')
        matched, opaque, differences = 0, 0, []
        for y in range(closed['height']):
            for x in range(closed['width']):
                expected = closed['pixels'][y * closed['width'] + x]
                if expected == closed['transparent']:
                    continue
                actual = animated['pixels'][y * animated['width'] + x]
                opaque += 1
                if actual == expected:
                    matched += 1
                else:
                    differences.append(dict(x=x, y=y, closed_index=expected, animation_index=actual))
        require((matched, opaque) == (1276, 1277), 'Closed-pane registration changed')
        for aid in [64, 86, 87]:
            require(settings['anchors'][str(aid)][:2] == actor_anchor, 'Incorrect configured window anchor')
        require(settings['actor_anchors']['64'] == actor_anchor, 'Default actor disagrees with preview')
        report = dict(version=settings['version'], original_window_root=[128, 224],
                      closed_pane_top_left=pane_top_left, actor_anchor=actor_anchor,
                      matched_opaque_pixels=matched, opaque_pixels=opaque, differences=differences,
                      scope='Static resource geometry only; not a playback, emulator or gameplay test')
        (ROOT / 'analysis/window-alignment.json').write_text(json.dumps(report, indent=2) + '\n')
        if args.render:
            render(archive, settings)
    print('Window registration: 1276/1277 opaque pane pixels; actor anchor (192,304) for 05/16/17')


def render(archive, settings):
    from PIL import Image, ImageDraw

    def image_of(info):
        image = Image.frombytes('P', (info['width'], info['height']), info['pixels'])
        image.putpalette(info['palette'])
        image = image.convert('RGBA')
        image.putalpha(Image.frombytes('L', image.size, bytes(0 if p == info['transparent'] else 255 for p in info['pixels'])))
        return image

    data = archive.read('scenes/02.p8')
    background = Image.frombytes('P', (640, 417), data[780:])
    background.putpalette(data[12:780])
    background = background.convert('RGBA')
    result = Image.new('RGB', (1440, 3 * 474), (35, 35, 35))
    draw = ImageDraw.Draw(result)
    open_info = frame(archive.read('scenes/02-63.anm'))
    open_reference = image_of(open_info)
    for row, (number, aid, selected, old_anchor) in enumerate([
            (5, 64, 0, [200, 328]), (16, 86, 6, [144, 320]), (17, 87, 7, [144, 320])]):
        info = frame(archive.read(f'scenes/02-{aid}.anm'), selected)
        sprite = image_of(info)
        for col in range(3):
            image = background.copy()
            if col < 2:
                anchor = old_anchor if col == 0 else settings['anchors'][str(aid)][:2]
                image.alpha_composite(sprite, (anchor[0] + info['x'], anchor[1] + info['y']))
                label = f'{number:02} ' + ('0.3.5 ' if col == 0 else '0.3.6 ') + str(anchor)
            else:
                # Reference open shutters are drawn for alignment only, not added to runtime.
                image.alpha_composite(open_reference, (128 + open_info['x'], 224 + open_info['y']))
                label = 'Reference: open shutters, original root [128,224]'
            crop = image.convert('RGB').crop((120, 200, 280, 350)).resize((480, 450), Image.Resampling.NEAREST)
            result.paste(crop, (col * 480, row * 474 + 24))
            draw.text((col * 480 + 3, row * 474 + 5), label, fill='white')
    result.save(ROOT / 'analysis/window-alignment-0.3.6.png')


if __name__ == '__main__':
    main()
