"""Lossless PackBits frame storage; world coordinates and palettes stay intact."""
import struct
import zlib


def pack_runs(data):
    out = bytearray()
    p = start = 0
    while p < len(data):
        end = p + 1
        while end < len(data) and end - p < 130 and data[end] == data[p]:
            end += 1
        if end - p >= 3:
            while start < p:
                size = min(128, p - start)
                out.append(size - 1)
                out.extend(data[start:start + size])
                start += size
            out.extend((128 + end - p - 3, data[p]))
            p = start = end
        else:
            p += 1
        if p - start >= 128:
            out.append(127)
            out.extend(data[start:start + 128])
            start += 128
    while start < p:
        size = min(128, p - start)
        out.append(size - 1)
        out.extend(data[start:start + size])
        start += size
    return bytes(out)


def unpack_runs(data, size):
    out = bytearray()
    p = 0
    while p < len(data):
        control = data[p]
        p += 1
        if control < 128:
            count = control + 1
            if p + count > len(data):
                raise ValueError('Truncated literal run')
            out.extend(data[p:p + count])
            p += count
        else:
            if p == len(data):
                raise ValueError('Truncated repeated run')
            out.extend(bytes([data[p]]) * ((control & 127) + 3))
            p += 1
        if len(out) > size:
            raise ValueError('Frame run overflow')
    if len(out) != size:
        raise ValueError('Incomplete frame')
    return bytes(out)


def decode_animation(data):
    if data[:4] == b'ANM1':
        return data
    if data[:4] != b'ANM3' or len(data) < 777:
        raise ValueError('Invalid animation encoding')
    out = bytearray(b'ANM1' + data[4:777])
    p = 777
    for _ in range(struct.unpack_from('>H', data, 4)[0]):
        header = data[p:p + 8]
        _, _, width, height = struct.unpack('>hhHH', header)
        p += 8
        size = width * height
        packed, = struct.unpack_from('>I', data, p)
        p += 4
        count = packed or size
        frame = data[p:p + count]
        if len(frame) != count:
            raise ValueError('Truncated packed frame')
        p += count
        out.extend(header)
        out.extend(unpack_runs(frame, size) if packed else frame)
    if p != len(data):
        raise ValueError('Trailing packed animation bytes')
    return bytes(out)


def encode_animation(data):
    if data[:4] != b'ANM1':
        raise ValueError('Expected original ANM1')
    out = bytearray(b'ANM3' + data[4:777])
    p = 777
    for _ in range(struct.unpack_from('>H', data, 4)[0]):
        header = data[p:p + 8]
        _, _, width, height = struct.unpack('>hhHH', header)
        p += 8
        size = width * height
        frame = data[p:p + size]
        p += size
        packed = pack_runs(frame)
        out.extend(header)
        if len(packed) < size:
            out.extend(struct.pack('>I', len(packed)))
            out.extend(packed)
        else:
            out.extend(b'\0' * 4)
            out.extend(frame)
    if p != len(data):
        raise ValueError('Invalid original animation size')
    result = bytes(out)
    # ZIP's temporal compression sometimes beats RLE. Keep whichever is smaller.
    if len(zlib.compress(result, 9)) + 64 >= len(zlib.compress(data, 9)):
        return data
    if decode_animation(result) != data:
        raise ValueError('Lossless animation verification failed')
    return result
