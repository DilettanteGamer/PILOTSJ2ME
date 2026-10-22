#!/usr/bin/env python3
"""Deterministic, static inventory of the supplied and repackaged MIDlets."""
import collections
import hashlib
import json
from pathlib import Path
import re
import struct
import sys
import zipfile

sys.dont_write_bytecode = True
from build import Reader, manifest, require, validate

ROOT = Path(__file__).resolve().parent


def class_references(data):
    r = Reader(data)
    require(r.take(4) == b'\xca\xfe\xba\xbe', 'Invalid class')
    minor, major, count = r.unpack('>HHH')
    strings, index = [], 1
    while index < count:
        tag, = r.unpack('>B')
        if tag == 1:
            size, = r.unpack('>H')
            strings.append(r.take(size).decode('utf-8', 'replace'))
        elif tag in (3, 4, 9, 10, 11, 12):
            r.take(4)
        elif tag in (5, 6):
            r.take(8)
            index += 1
        elif tag in (7, 8):
            r.take(2)
        else:
            raise ValueError('Unsupported constant pool tag: ' + str(tag))
        index += 1
    refs = sorted({m for s in strings for m in re.findall(r'(?:java|javax)/[A-Za-z0-9_/$]+', s)})
    return {'minor': minor, 'major': major, 'api_references': refs,
            'stackmap_utf8_present': 'StackMap' in strings}


def inspect(path):
    with zipfile.ZipFile(path) as z:
        require(z.testzip() is None, 'CRC failure')
        entries = {n: z.read(n) for n in z.namelist()}
        compressed = {i.filename: i.compress_size for i in z.infolist()}
    mf = manifest(entries['META-INF/MANIFEST.MF'])
    result = validate(entries, mf['Pilots-Screen'])
    result.update(path=str(path.relative_to(ROOT)), manifest=mf,
                  sha256=hashlib.sha256(path.read_bytes()).hexdigest(),
                  jar_bytes=path.stat().st_size, entry_count=len(entries),
                  uncompressed_bytes=sum(map(len, entries.values())),
                  extension_counts=dict(sorted(collections.Counter(Path(n).suffix for n in entries).items())))
    result['classes_detail'] = {n: dict(class_references(v), sha256=hashlib.sha256(v).hexdigest())
                                for n, v in sorted(entries.items()) if n.endswith('.class')}
    result['hotspot_counts'] = {n: v[4] for n, v in sorted(entries.items()) if n.endswith('.hot')}
    result['actor_records'] = {}
    result['animation_frames'] = {}
    for n, v in sorted(entries.items()):
        if n.endswith('.anm'):
            r = Reader(v)
            require(r.take(4) == b'ANM1', 'Invalid animation')
            count, duration, transparent = r.unpack('>HHB')
            r.take(768)
            largest = 0
            for _ in range(count):
                x, y, w, h = r.unpack('>hhHH')
                largest = max(largest, w * h)
                r.take(w * h)
            r.end()
            result['animation_frames'][n] = dict(count=count, duration_ms=duration,
                                                max_indexed_frame_bytes=largest)
        if n.endswith('.act'):
            r = Reader(v)
            require(r.take(4) == b'ACT1', 'Invalid actors')
            count, = r.unpack('>B')
            result['actor_records'][n] = [dict(zip(['id', 'x', 'y'], r.unpack('>Hhh'))) for _ in range(count)]
            r.end()
    preview_name='scenes/03.animations' if 'scenes/03.animations' in entries else 'scenes/02.animations'
    result['preview_scene']=int(preview_name[7:9])
    r = Reader(entries[preview_name])
    require(r.take(4) == b'ANC2', 'Invalid previews')
    count, = r.unpack('>H')
    previews = []
    for _ in range(count):
        aid, x, y, fx, fy, size = r.unpack('>HhhHHH')
        previews.append(dict(resource_id=aid, anchor=[x, y], focus=[fx, fy], label=r.take(size).decode('utf-8')))
    r.end()
    result['preview_catalog'] = previews
    result['largest_resources'] = [dict(path=n, bytes=len(entries[n]), compressed_bytes=compressed[n])
                                   for n in sorted(entries, key=lambda n: (-len(entries[n]), n))[:10]]
    return result


def main():
    reports = [inspect(p) for folder in ['input', 'dist'] for p in sorted((ROOT / folder).glob('*.jar'))]
    require(len(reports) == 4, 'Expected two original and two adjusted JARs')
    original = [r for r in reports if r['path'].startswith('input/')]
    require(original[0]['classes_detail'] == original[1]['classes_detail'], 'Original bytecode differs across profiles')
    result = {'scope': 'Static inventory only; no emulator or device tests executed',
              'original_classes_identical_across_profiles': True, 'artifacts': reports}
    (ROOT / 'analysis/inventory.json').write_text(json.dumps(result, ensure_ascii=False, indent=2) + '\n')
    for r in reports:
        print(r['path'], r['jar_bytes'], 'bytes;', r['animations'], 'animation files;',
              len(r['preview_catalog']), 'preview entries;', sum(r['hotspot_counts'].values()), 'hotspots')


if __name__ == '__main__':
    main()
