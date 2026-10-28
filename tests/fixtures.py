"""Authored synthetic resources. No bytes or strings taken from the game."""
import struct
from pilots_editor.archive import blob, number


def record(kind, ident, data, args=(0, 0, 0), shared=False):
    result = bytes([kind]) + number(ident)
    for n, value in enumerate(args, 1):
        if value:result += bytes([n]) + number(value)
    return result + (b"\xff" if shared else b"\x04" + blob(data))


def source(path):
    colors = bytes(v for i in range(256) for v in (i, (i * 3) % 256, (i * 7) % 256))
    background = struct.pack("<6I", 0, 0, 64, 48, 3072, 3840) + bytes(range(64)) * 48 + colors
    common = record(0x10, 0, bytes(20) + struct.pack("<II", 8, 8)) + b"\0"
    frame = struct.pack("<HH", 8, 8) + bytes([3] * 64)
    scene = record(0x18, 0, background)
    scene += record(0x43, 7, frame) + record(0x43, 8, frame, shared=True)
    scene += record(0x43, 7, frame, args=(0, 1, 0))
    scene += record(0x40, 7, bytes([0, 0, 1, 2]))
    scene += record(0x42, 7, struct.pack("<IhhIhh", 0, 2, 7, 0, 3, 7))
    scene += record(0x20, 2, bytes([1, 0, 20]))
    scene += record(0x21, 2, b"\x1d" + struct.pack("<I", 7) + b"\x21\x36\x13\0\0\0\0")
    scene += record(0x19, 0, struct.pack("<II", 16, 0x10001) + bytes([2, 0, 1, 2]))
    scene += record(0x2C, 2, b"Ntcn\0".ljust(12, b"\0") + b"\x00", (0, 0, 12))
    scene += record(0x51, 4, struct.pack("<I", 128) + bytes(range(128))) + b"\0"
    opaque = b"\xffSynthetic opaque section"
    prefix = b"MZ" + bytes(62)
    offsets = {1: 0, 4: len(common), 0: len(common) + len(scene), 5: len(common) + len(scene) + len(opaque)}
    # Deliberately non-monotonic logical IDs, with untouched data after sentinel.
    index = b"".join(struct.pack("<IB", offsets[i], i) for i in (0, 1, 4, 5))
    footer = struct.pack("<II", 3, len(prefix)) + bytes([0, 0, 1])
    path.write_bytes(prefix + common + scene + opaque + b"trailing private data" + index + footer + struct.pack("<II", len(footer), 0x04000000) + b"=VS=")
    return colors
