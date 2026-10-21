#!/usr/bin/env python3
"""Package compiled MIDlets with explicit resource corrections; no source compilation."""
import argparse
import hashlib
import json
from pathlib import Path
import struct
import zipfile

ROOT = Path(__file__).resolve().parent


def require(condition, message):
    if not condition:
        raise ValueError(message)


class Reader:
    def __init__(self, data):
        self.data, self.pos = data, 0

    def take(self, count):
        require(count >= 0 and self.pos + count <= len(self.data), 'Truncated resource')
        result = self.data[self.pos:self.pos + count]
        self.pos += count
        return result

    def unpack(self, fmt):
        return struct.unpack(fmt, self.take(struct.calcsize(fmt)))

    def end(self):
        require(self.pos == len(self.data), 'Trailing resource bytes')


def manifest(data):
    lines = data.decode('utf-8').replace('\r\n', '\n').splitlines()
    unfolded = []
    for line in lines:
        if line.startswith(' '):
            require(bool(unfolded), 'Invalid manifest continuation')
            unfolded[-1] += line[1:]
        elif line:
            unfolded.append(line)
    return dict(line.split(': ', 1) for line in unfolded)


def adjust_animations(entries, adjustments):
    """Apply explicit catalog changes; never invent missing placement coordinates."""
    entries = dict(entries)
    name = 'scenes/02.animations'
    r = Reader(entries[name])
    require(r.take(4) == b'ANC2', 'Invalid animation catalog')
    count, = r.unpack('>H')
    records, removed, updated, seen = [], [], [], set()
    for _ in range(count):
        actor, x, y, fx, fy, size = r.unpack('>HhhHHH')
        label = r.take(size)
        seen.add(actor)
        if actor in adjustments['remove_preview_ids']:
            removed.append(actor)
            del entries[f'scenes/02-{actor}.anm']
            continue
        anchor = adjustments['anchors'].get(str(actor))
        if anchor is not None:
            require(len(anchor) == 4 and all(type(v) is int for v in anchor), 'Anchor must contain four integers')
            x, y, fx, fy = anchor
            updated.append(actor)
        records.append(struct.pack('>HhhHHH', actor, x, y, fx, fy, size) + label)
    r.end()
    require(set(adjustments['remove_preview_ids']) <= seen, 'Unknown removal ID')
    require(set(map(int, adjustments['anchors'])) <= seen - set(removed), 'Unknown anchor ID')
    entries[name] = b'ANC2' + struct.pack('>H', len(records)) + b''.join(records)
    mf = manifest(entries['META-INF/MANIFEST.MF'])
    mf['MIDlet-Version'] = adjustments['version']
    entries['META-INF/MANIFEST.MF'] = ('\r\n'.join(k + ': ' + v for k, v in mf.items()) + '\r\n\r\n').encode('utf-8')
    actor_updates = adjustments.get('actor_anchors', {})
    actor_catalog = 'scenes/02.act'
    r = Reader(entries[actor_catalog])
    require(r.take(4) == b'ACT1', 'Invalid actor catalog')
    count, = r.unpack('>B')
    actors, changed_actors = [], []
    actor_ids = set()
    for _ in range(count):
        actor, x, y = r.unpack('>Hhh')
        actor_ids.add(actor)
        if str(actor) in actor_updates:
            value = actor_updates[str(actor)]
            require(len(value) == 2 and all(type(v) is int for v in value), 'Actor anchor must contain two integers')
            x, y = value
            changed_actors.append(actor)
        actors.append(struct.pack('>Hhh', actor, x, y))
    r.end()
    require(set(map(int, actor_updates)) <= actor_ids, 'Unknown actor anchor ID')
    if changed_actors:
        entries[actor_catalog] = b'ACT1' + struct.pack('>B', count) + b''.join(actors)
    return entries, dict(removed_animation_ids=removed, updated_anchor_ids=updated,
                         updated_scene_actor_ids=changed_actors,
                         pending_placement_menu_numbers=adjustments['pending_placement_menu_numbers'],
                         pending_character_relative_menu_numbers=adjustments['character_relative_menu_numbers'])


def validate(entries, screen):
    mf = manifest(entries['META-INF/MANIFEST.MF'])
    require(mf['Pilots-Screen'] == screen, 'Wrong screen profile')
    require(mf['MicroEdition-Profile'] == 'MIDP-2.0', 'Wrong MIDP version')
    require(mf['MicroEdition-Configuration'] == 'CLDC-1.1', 'Wrong CLDC version')
    require(mf['MIDlet-1'].split(',')[-1].strip() == 'pilots.PilotsMidlet', 'Wrong entry point')
    require('pilots/PilotsMidlet.class' in entries, 'Missing MIDlet class')
    classes = [n for n in entries if n.endswith('.class')]
    for name in classes:
        require(entries[name][:8] == b'\xca\xfe\xba\xbe\x00\x03\x00\x2d', 'Unexpected bytecode version: ' + name)
    dimensions = {}
    frames = animations = 0
    for name, data in entries.items():
        if name.endswith(('.p8', '.bar')):
            r = Reader(data)
            require(r.take(4) == b'P8BG', 'Invalid background: ' + name)
            w, h, ww, wh = r.unpack('>HHHH')
            require(1 <= w <= 640 and 1 <= h <= 480 and 1 <= ww <= 2048 and 1 <= wh <= 2048, 'Invalid dimensions: ' + name)
            r.take(768 + w * h)
            r.end()
            dimensions[name] = [w, h, ww, wh]
        elif name.endswith('.anm'):
            r = Reader(data)
            require(r.take(4) == b'ANM1', 'Invalid animation: ' + name)
            count, duration, transparent = r.unpack('>HHB')
            require(1 <= count <= 1024 and 20 <= duration <= 1000, 'Invalid animation header: ' + name)
            r.take(768)
            for _ in range(count):
                x, y, w, h = r.unpack('>hhHH')
                require(-2048 <= x <= 2048 and -2048 <= y <= 2048 and 1 <= w <= 640 and 1 <= h <= 480, 'Invalid frame: ' + name)
                r.take(w * h)
            r.end()
            frames += count
            animations += 1
    r = Reader(entries['catalog.bin'])
    require(r.take(4) == b'PLT2', 'Invalid scene catalog')
    count, = r.unpack('>H')
    scenes = list(r.take(count))
    r.end()
    require(len(set(scenes)) == count and count > 0, 'Invalid scene IDs')
    for scene in scenes:
        stem = f'scenes/{scene:02d}'
        require(stem + '.p8' in dimensions, 'Missing scene background: ' + stem)
        _, _, ww, wh = dimensions[stem + '.p8']
        if stem + '.hot' in entries:
            r = Reader(entries[stem + '.hot'])
            require(r.take(4) == b'HOT1', 'Invalid hotspot catalog')
            count, = r.unpack('>B')
            require(count <= 64, 'Too many hotspots')
            for _ in range(count):
                x, y, w, h, size = r.unpack('>HHHHH')
                require(w > 0 and h > 0 and x + w <= ww and y + h <= wh and 1 <= size <= 1024, 'Invalid hotspot')
                r.take(size)
            r.end()
        if stem + '.act' in entries:
            r = Reader(entries[stem + '.act'])
            require(r.take(4) == b'ACT1', 'Invalid actor catalog')
            count, = r.unpack('>B')
            require(count <= 8, 'Too many actors')
            for _ in range(count):
                actor, x, y = r.unpack('>Hhh')
                require(0 <= x < ww and 0 <= y < wh, 'Invalid actor anchor')
                require(f'{stem}-{actor}.anm' in entries, 'Missing actor animation')
            r.end()
        if stem + '.animations' in entries:
            r = Reader(entries[stem + '.animations'])
            require(r.take(4) == b'ANC2', 'Invalid animation catalog')
            count, = r.unpack('>H')
            require(1 <= count <= 254, 'Invalid animation catalog size')
            previous = -1
            for _ in range(count):
                actor, x, y, fx, fy, size = r.unpack('>HhhHHH')
                require(actor > previous and 0 <= x < 640 and 0 <= y < 417 and fx < 640 and fy < 417, 'Invalid animation catalog entry')
                r.take(size)  # Java modified UTF-8 label; preserve bytes unchanged.
                require(f'{stem}-{actor}.anm' in entries, 'Missing preview animation')
                previous = actor
            r.end()
    return dict(profile=screen, classes=len(classes), scenes=scenes,
                animations=animations, frames=frames, backgrounds=dimensions)


def build(profile, locked, output):
    source = ROOT / 'input' / ('Pilots-' + profile + '.jar')
    require(hashlib.sha256(source.read_bytes()).hexdigest() == locked[profile], 'Baseline SHA-256 mismatch: ' + str(source))
    with zipfile.ZipFile(source) as z:
        require(z.testzip() is None, 'Baseline CRC failure')
        require(len(z.namelist()) == len(set(z.namelist())), 'Duplicate ZIP members')
        entries = {name: z.read(name) for name in z.namelist()}
    validate(entries, profile)
    baseline_entries = entries
    adjustments = json.loads((ROOT / 'animation-adjustments.json').read_text())
    entries, changes = adjust_animations(entries, adjustments)
    report = validate(entries, profile)
    require(all(entries[n] == baseline_entries[n] for n in entries if n.endswith('.class')), 'Bytecode must remain unchanged')
    report.update(changes)
    output.mkdir(parents=True, exist_ok=True)
    target = output / source.name
    with zipfile.ZipFile(target, 'w', compression=zipfile.ZIP_DEFLATED, compresslevel=9) as z:
        for name in sorted(entries, key=lambda n: (n != 'META-INF/MANIFEST.MF', n)):
            info = zipfile.ZipInfo(name, date_time=(2000, 1, 1, 0, 0, 0))
            info.compress_type = zipfile.ZIP_DEFLATED
            info.create_system = 3
            info.external_attr = 0o100644 << 16
            z.writestr(info, entries[name], compress_type=zipfile.ZIP_DEFLATED, compresslevel=9)
    with zipfile.ZipFile(target) as z:
        require(z.testzip() is None, 'Output CRC failure')
        require(set(z.namelist()) == set(entries), 'Output member mismatch')
        require(all(z.read(n) == data for n, data in entries.items()), 'Output payload mismatch')
    mf = manifest(entries['META-INF/MANIFEST.MF'])
    attrs = {k: v for k, v in mf.items() if k != 'Manifest-Version'}
    attrs.update({'MIDlet-Jar-URL': target.name, 'MIDlet-Jar-Size': str(target.stat().st_size)})
    jad = target.with_suffix('.jad')
    jad.write_bytes(('\r\n'.join(k + ': ' + v for k, v in attrs.items()) + '\r\n').encode('utf-8'))
    checked = manifest(jad.read_bytes())
    require(int(checked['MIDlet-Jar-Size']) == target.stat().st_size, 'JAD size mismatch')
    report.update(jar_sha256=hashlib.sha256(target.read_bytes()).hexdigest(), jar_size=target.stat().st_size,
                  bytecode_preserved=True,
                  modified_entries=sorted(n for n in entries if entries[n] != baseline_entries[n]),
                  removed_entries=sorted(set(baseline_entries) - set(entries)),
                  validation='static; no emulator or device tests executed')
    target.with_suffix('.report.json').write_text(json.dumps(report, indent=2) + '\n')
    print(f'{profile}: {report["classes"]} classes, {len(report["scenes"])} scenes, {report["animations"]} animations, {report["frames"]} frames; bytecode preserved; removed {changes["removed_animation_ids"]}')


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--profile', choices=['all', '128x128', '240x320'], default='all')
    parser.add_argument('--out', type=Path, default=ROOT / 'dist')
    args = parser.parse_args()
    output = args.out.resolve()
    inputs = (ROOT / 'input').resolve()
    require(output != inputs and inputs not in output.parents, 'Output must be outside input/')
    locked = json.loads((ROOT / 'baselines.json').read_text())
    for profile in (locked if args.profile == 'all' else [args.profile]):
        build(profile, locked, output)


if __name__ == '__main__':
    main()
