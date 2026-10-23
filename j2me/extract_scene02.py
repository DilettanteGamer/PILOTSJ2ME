#!/usr/bin/env python3
"""Recover placement evidence from the zoo section; not a full game interpreter."""
import hashlib
import json
from pathlib import Path
import struct
import sys
import zipfile

sys.dont_write_bytecode = True
from build import require

ROOT = Path(__file__).resolve().parent
EXPECTED = 'c980fb1ad09c3543d89dd0be26c062b8847fab73e86b92012c4fe0507ba34e1f'


class Stream:
    def __init__(self, data, start, end):
        self.data, self.pos, self.end = data, start, end

    def take(self, count):
        require(0 <= count <= self.end - self.pos, 'Truncated original record')
        result = self.data[self.pos:self.pos + count]
        self.pos += count
        return result

    def uint(self, count=1):
        return int.from_bytes(self.take(count), 'little')

    def number(self):
        tag = self.uint()
        if tag < 128:
            return tag
        if not tag & 32:
            return self.uint(tag & 31)
        count = ((tag >> 2) & 3) + 1
        value = self.uint(count) + ((tag & 3) << ((count * 8) & 31))
        # Original tables referenced by FUN_00404980, for this pinned binary.
        positive = [0, 0x80, 0x480, 0x40480, 0]
        negative = [0, 0xffffffff, 0xfffffbff, 0xfffbfbff, 0x47f]
        return positive[count] + value if not tag & 16 else negative[count] - value

    def blob(self):
        tag = self.uint()
        require(tag & 128, 'Missing original blob tag')
        if tag & 64:
            return self.take(tag & 31)
        width = (tag & 3) + 1
        size = self.uint(width)
        if not tag & 12:
            return self.take(size)
        expanded = self.uint(width)
        require(expanded <= 4 * 1024 * 1024, 'Expansion limit exceeded')
        raw = self.take(size)
        out, pos = bytearray(), 0
        while pos < len(raw):
            flags = raw[pos]
            pos += 1
            for bit in range(8):
                if pos == len(raw):
                    break
                if flags & (1 << bit):
                    out.append(raw[pos])
                    pos += 1
                else:
                    require(pos + 2 <= len(raw), 'Truncated back-reference')
                    low, high = raw[pos:pos + 2]
                    pos += 2
                    distance, length = low + ((high >> 4) << 8), (high & 15) + 3
                    require(0 < distance <= len(out), 'Invalid back-reference')
                    for _ in range(length):
                        out.append(out[-distance])
                require(len(out) <= expanded, 'Expansion size exceeded')
        require(len(out) == expanded, 'Expansion size mismatch')
        return bytes(out)


def records(data, section=4):
    footer_size = int.from_bytes(data[-12:-8], 'little')
    require(data[-4:] == b'=VS=', 'Unsupported original footer')
    count, base = struct.unpack_from('<II', data, len(data) - 12 - footer_size)
    index = {}
    for i in range(count + 1):
        pos = len(data) - ((count - i) * 5 + footer_size + 17)
        offset, ident = struct.unpack_from('<IB', data, pos)
        index[ident] = base + offset
    s = Stream(data, index[section], index[section + 1])
    result, previous = [], None
    while True:
        offset, opcode = s.pos, s.uint()
        if opcode == 0:
            break
        require(opcode not in [1, 2, 3, 4, 5, 255], 'Unsupported top-level opcode')
        ident = s.number() if opcode < 128 else 0
        marker, args = s.uint(), [0, 0, 0]
        while marker in [1, 2, 3]:
            args[marker - 1] = s.number()
            marker = s.uint()
        require(marker in [4, 255], 'Unsupported payload marker')
        payload = previous if marker == 255 else s.blob()
        require(payload is not None, 'Unresolved shared payload')
        result.append(dict(code=opcode & 127, id=ident, args=args, data=payload, offset=offset))
        previous = payload
    require(s.pos == index[section + 1], 'Section boundary mismatch')
    return result, index[section], index[section + 1]


def absolute_cells(record, cell_width, cell_height):
    values = list(struct.unpack('<' + 'I' * (len(record['data']) // 4), record['data']))
    flags, pos = values[0], 1
    if flags & 1:
        pos += 1
    if flags & 2:
        while True:
            header = values[pos]
            pos += 1 + (header & 65535)
            if header & 65536:
                break
    if flags & 8:
        pos += 1
    cells = []
    if flags & 16:
        while True:
            header = values[pos]
            pos += 1
            for word in values[pos:pos + (header & 65535)]:
                entity, orientation, x, y = struct.pack('<I', word)
                if entity < 254:
                    cells.append(dict(entity=entity, cell=[x, y], world=[x * cell_width, y * cell_height],
                                      rule_id=record['id'], record_offset=record['offset']))
            pos += header & 65535
            if header & 65536:
                break
    return cells


def main():
    data = (ROOT.parent / 'PILOTS.EXE').read_bytes()
    require(hashlib.sha256(data).hexdigest() == EXPECTED, 'Original SHA-256 mismatch')
    source, start, end = records(data)
    global_records, _, _ = records(data, 1)
    header = next(r['data'] for r in global_records if r['code'] == 0x10)
    cell_width, cell_height = struct.unpack_from('<II', header, 20)
    require((cell_width, cell_height) == (64, 16), 'Unsupported world cell geometry')
    background = next(r['data'] for r in source if r['code'] == 0x18)
    require(struct.unpack_from('<6I', background) == (0, 0, 640, 480, 307200, 307968), 'Unexpected background layout')
    with zipfile.ZipFile(ROOT / 'input/Pilots-240x320.jar') as z:
        target = z.read('scenes/02.p8')
    require(background[24:24 + 640 * 417] == target[780:], 'Zoo background pixel mismatch')
    require(background[24 + 640 * 480:] == target[12:780], 'Zoo palette mismatch')
    cells = []
    for record in source:
        if record['code'] in [0x19, 0x60]:
            cells.extend(absolute_cells(record, cell_width, cell_height))
    report = dict(source_sha256=EXPECTED, original_section=4, start=start, end=end,
                  record_count=len(source), cell_size=[cell_width, cell_height],
                  background_pixels_and_palette_identical=True,
                  animation_ids=sorted(r['id'] for r in source if r['code'] == 0x40),
                  frame_record_count=sum(r['code'] == 0x43 for r in source),
                  placement_evidence=[c for c in cells if c['entity'] in [6, 7, 14, 15, 16, 17, 18, 23, 24, 30]],
                  scope='Absolute placement rules only; not executed gameplay or a complete interpreter')
    (ROOT / 'analysis/original-scene02.json').write_text(json.dumps(report, indent=2) + '\n')
    print('Original section 4:', len(source), 'records; zoo background and palette match JAR exactly')


if __name__ == '__main__':
    main()
