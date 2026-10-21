#!/usr/bin/env python3
"""Record immutable input provenance and separate PE images from appended data."""
import hashlib
import json
from pathlib import Path
import struct

ROOT = Path(__file__).resolve().parents[2]


def inspect(path):
    data = path.read_bytes()
    result = {'file': path.name, 'size': len(data),
              'sha256': hashlib.sha256(data).hexdigest()}
    if data[:2] != b'MZ':
        return result
    offset, = struct.unpack_from('<I', data, 60)
    result['header_offset'] = offset
    if data[offset:offset + 4] == b'PE\0\0':
        machine, count = struct.unpack_from('<HH', data, offset + 4)
        optional_size, = struct.unpack_from('<H', data, offset + 20)
        entry_rva, base = struct.unpack_from('<I8xI', data, offset + 40)
        sections = []
        end = 0
        for i in range(count):
            start = offset + 24 + optional_size + 40 * i
            name = data[start:start + 8].rstrip(b'\0').decode('ascii')
            virtual_size, rva, raw_size, raw_offset = struct.unpack_from('<IIII', data, start + 8)
            assert raw_offset + raw_size <= len(data), 'Truncated PE section'
            end = max(end, raw_offset + raw_size)
            sections.append(dict(name=name, virtual_size=virtual_size, rva=rva,
                                 raw_size=raw_size, raw_offset=raw_offset))
        result.update(format='PE', machine=hex(machine), image_base=hex(base),
                      entry_rva=hex(entry_rva), sections=sections,
                      section_data_end=end, appended_data_size=len(data) - end,
                      appended_data_sha256=hashlib.sha256(data[end:]).hexdigest(),
                      appended_data_note='Bytes after the last section, not classified or decoded as game resources')
    elif data[offset:offset + 2] == b'NE':
        result.update(format='NE', note='16-bit Windows New Executable')
    else:
        result.update(format='MZ', note='Unknown extended header')
    return result


if __name__ == '__main__':
    out = ROOT / 'reverse-engineering' / 'generated'
    out.mkdir(parents=True, exist_ok=True)
    inputs = [inspect(ROOT / name) for name in ['PILOTS.EXE', 'AUTORUN.EXE', 'GMW.DLL', 'PILOTS0.230']]
    (out / 'inputs.json').write_text(json.dumps(inputs, indent=2) + '\n')
    print(json.dumps(inputs, indent=2))
