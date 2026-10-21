#!/usr/bin/env python3
"""Extract window rule evidence, source-derived test cases and missing global sprites."""
import hashlib
import json
from pathlib import Path
import struct
import sys

sys.dont_write_bytecode = True
from extract_scene02 import records, absolute_cells, EXPECTED
from build import require

ROOT = Path(__file__).resolve().parent


def execute(code, globals_, calls, map_queries=None):
    """Bounded oracle for the straight-line VM subset used by the selected rules.

    Opcodes are transcribed from 00408e84..00409068, not from the port's logic.
    Native API 09 is evaluated with both heroes already at the interaction mark.
    """
    pos, a, d, stack = 0, 0, 0, []
    if map_queries is None:
        map_queries = {62, 63}
    while pos < len(code):
        op = code[pos]
        pos += 1
        if op == 0:
            require(pos == len(code) and not stack, 'Invalid rule termination')
            return a
        if op in (0x10, 0x1d, 0x31, 0x36):
            value, = struct.unpack_from('<I', code, pos)
            pos += 4
            if op == 0x1d:
                a = value
            elif op == 0x31:
                a = globals_.get(value, 0)
            elif op == 0x10:
                globals_[value] = a
            else:
                require(value in (0, 9, 0x11, 0x13, 0x14, 0x19), 'Unsupported oracle native API')
                arg = stack.pop() if value != 0 else None
                calls.append([value, arg])
                a = int(arg in map_queries) if value == 9 else 1
        elif op == 0x21:
            stack.append(a)
        elif op == 0x22:
            d = stack.pop()
        elif op == 1:
            a = int(d == a)
        elif op == 3:
            a = int(d < a)
        elif op == 5:
            a = int(d > a)
        elif op == 0x17:
            a |= d
        elif op == 0x15:
            a += d
        elif op == 0x19:
            a &= d
        else:
            raise ValueError('Unsupported oracle opcode %02x' % op)
    raise ValueError('Unterminated rule')


def extract(original, palette):
    require(hashlib.sha256(original).hexdigest() == EXPECTED, 'Original EXE hash mismatch')
    zoo, _, _ = records(original, 4)
    common, _, _ = records(original, 1)
    lookup = {(r['code'], r['id'], r['args'][0]): r for r in zoo}
    evidence, fixtures = [], []

    def rule(code, obj, index):
        r = lookup[code, obj, index]
        if r not in evidence:
            evidence.append(r)
        return r['data']

    for hero in (0, 1):
        for opened in (0, 1):
            for branch in (0, 1):
                g = {0x18: hero, 0x1c: 2, 0x20: 7, 0x88: 1, 0x30: 0, 0x08: branch}
                obj = 24 if opened else 23
                route = 1 if opened and hero == 0 else 13 if opened else 10 + hero
                require(execute(rule(0x2b, obj, route), g, []), 'Source route predicate failed')
                execute(rule(0x2c, obj, route), g, [])
                action = g[0x20]
                actor = 17 + hero
                start = 3 + branch if action == 13 else 29 if hero == 0 else 30 if opened else 16
                require(execute(rule(0x2b, actor, start), g, []), 'Source actor predicate failed')
                calls = []
                execute(rule(0x2c, actor, start), g, calls)
                clips = [arg for api, arg in calls if api == 0x13]
                require(len(clips) == 1, 'Expected one source clip')
                clip = clips[0]
                end = 88 if clip == 64 else 13 if clip == 86 else 14 if clip == 87 else 36
                finish = 22 if clip == 64 else 32 if clip == 86 else 33 if clip == 87 else 7
                g[0x30] = end - 1
                require(not execute(rule(0x2b, actor, finish), g, []), 'Premature source release')
                g[0x30] = end
                require(execute(rule(0x2b, actor, finish), g, []), 'Source release predicate changed')
                execute(rule(0x2c, actor, finish), g, [])
                require(g[0x20] == 0 and g[0x30] == 0, 'Source release does not reset action')
                fixtures.append([hero, opened, branch, action, clip, end])
    # Actual map changes within the opening action: 18.17 -> rule 6; 18.18 -> rule 7.
    for index, tick, effect in [(17, 13, 6), (18, 38, 7)]:
        g = {0x18: 1, 0x20: 18, 0x30: tick}
        require(execute(rule(0x2b, 18, index), g, []), 'Source event predicate failed')
        calls = []
        execute(rule(0x2c, 18, index), g, calls)
        require([0x14, effect] in calls, 'Source map event changed')
        rule(0x60, effect, 0)
        cells = absolute_cells(lookup[0x60, effect, 0], 64, 16)
        expected = (24, [128, 224]) if effect == 6 else (15, [320, 112])
        require(len(cells) == 1 and (cells[0]['entity'], cells[0]['world']) == expected,
                'Source event changes a different object or position')
    for obj, index in [(17, 7), (17, 32), (18, 22), (18, 33)]:
        rule(0x2b, obj, index)
        rule(0x2c, obj, index)
    for code, obj in [(0x21, 15), (0x2b, 15), (0x2c, 15), (0x21, 23), (0x21, 24)]:
        rule(code, obj, 0)
    rule(0x60, 5, 0)
    report = dict(source_sha256=EXPECTED, vm_dispatch='00408e64', native_table='0041030c',
                  oracle_scope='Selected primary window action with approach complete; not a complete Windows VM',
                  opening_events={'remove_closed_object': 1, 'open_shutters': 13, 'spawn_crow': 38, 'release_colleague': 88},
                  records=[dict(code=r['code'], object=r['id'], index=r['args'][0], offset=r['offset'], bytecode=r['data'].hex()) for r in evidence],
                  fixtures=fixtures)
    sprites = {}
    for ident in (12, 16, 34, 35, 36, 37):
        header = next(r['data'] for r in common if r['code'] == 0x40 and r['id'] == ident)
        table = next(r['data'] for r in common if r['code'] == 0x42 and r['id'] == ident)
        require(header[2] == 1 and len(table) == header[3] * 8, 'Expected single-direction source sprite')
        frames = {r['args'][1]: r['data'] for r in common if r['code'] == 0x43 and r['id'] == ident}
        output = bytearray(b'ANM1' + struct.pack('>HHB', header[3], 100, 0) + palette)
        for index in range(header[3]):
            _, px, py = struct.unpack_from('<Ihh', table, index * 8)
            payload = frames[index]
            w, h = struct.unpack_from('<HH', payload)
            require(len(payload) == 4 + w * h, 'Invalid source sprite payload')
            output.extend(struct.pack('>hhHH', -px, -py, w, h) + payload[4:])
        sprites['scenes/02-%d.anm' % (20000 + ident)] = bytes(output)
    return sprites, report


if __name__ == '__main__':
    import zipfile
    with zipfile.ZipFile(ROOT / 'input/Pilots-240x320.jar') as z:
        palette = z.read('scenes/02-64.anm')[9:777]
    sprites, report = extract((ROOT.parent / 'PILOTS.EXE').read_bytes(), palette)
    (ROOT / 'analysis/window-original-rules.json').write_text(json.dumps(report, indent=2) + '\n')
    print('Original window rules:', len(report['records']), 'records,', len(report['fixtures']), 'oracle cases;', len(sprites), 'global sprites')
