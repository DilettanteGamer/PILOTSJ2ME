"""Replace the baked 4x4 handles on the moving door with source-art 3x3 handles.

Only clip 184 changes. Frame count, pivots, palette and all other clips stay exact.
Coordinates are local to the original indexed frames, before profile scaling.
"""
import hashlib
import struct

# Interior of the metal plate, clockwise: TL, TR, BR, BL. The plate turns
# away from the camera; frames 7/8/11/12 show only the projecting knob ends.
FACES = {
    0: ((35, 21), (101, 21), (98, 87), (31, 87)),
    1: ((38, 21), (104, 21), (101, 87), (34, 87)),
    2: ((43, 21), (109, 21), (106, 87), (39, 87)),
    3: ((32, 29), (100, 32), (96, 105), (29, 103)),
    4: ((42, 35), (95, 38), (92, 113), (41, 114)),
    5: ((43, 37), (73, 38), (71, 111), (42, 117)),
    6: ((43, 39), (61, 40), (60, 101), (43, 116)),
}
EDGES = {7: (43, 49, 45, 112), 8: (40, 47, 54, 123),
         11: (40, 47, 54, 123), 12: (40, 47, 54, 123)}

def frames(data):
    p = 777
    result = []
    for _ in range(struct.unpack_from('>H', data, 4)[0]):
        header = data[p:p+8]
        x, y, w, h = struct.unpack('>hhHH', header)
        p += 8
        result.append((header, w, h, data[p:p+w*h]))
        p += w*h
    assert p == len(data)
    return result

def adapt(resources):
    name = 'scenes/09-184.anm'
    source = resources[name]
    sprite = frames(resources['scenes/09-116.anm'])[0]
    _, sw, sh, handle = sprite
    output = bytearray(source[:777])
    edits = []
    for f, (header, w, h, before) in enumerate(frames(source)):
        pixels = bytearray(before)
        allowed = set()
        def put(x, y, c):
            if 0 <= x < w and 0 <= y < h:
                allowed.add(y*w+x)
                pixels[y*w+x] = c
        if f in FACES:
            tl, tr, br, bl = FACES[f]
            # Inverse bilinear sampling along horizontal scanlines preserves the
            # perspective and clips handle pixels to the visible metal face.
            ymin = min(tl[1], tr[1]); ymax = max(bl[1], br[1])
            for y in range(ymin, ymax+1):
                v = (y-ymin)/float(ymax-ymin)
                left = tl[0]*(1-v)+bl[0]*v
                right = tr[0]*(1-v)+br[0]*v
                for x in range(round(left), min(w, round(right)+1)):
                    u = (x-left)/max(1., right-left)
                    top = tl[1]*(1-u)+tr[1]*u
                    bottom = bl[1]*(1-u)+br[1]*u
                    if y < top or y > bottom:
                        continue
                    vv = (y-top)/max(1., bottom-top)
                    # A 48x48 board with the same 16px cells as the room/detail.
                    gx = min(47, int(u*48)); gy = min(47, int(vv*48))
                    sx = gx%16-(8-sw//2); sy = gy%16-(8-sh//2)
                    color = handle[sy*sw+sx] if 0<=sx<sw and 0<=sy<sh else 0
                    put(x, y, color or 36)  # original flat plate colour
        elif f in EDGES:
            left, right, top, bottom = EDGES[f]
            # These pixels are transparent outside the door. Keep the existing
            # bolt silhouette, but space its three projections over three rows.
            sample = max((before[y*w+left:y*w+right+1] for y in range(top,top+12)),
                         key=lambda row:sum(c!=0 for c in row))
            for y in range(top, bottom+1):
                for x in range(left, right+1):
                    put(x,y,0)
            for row in range(3):
                y = top+4+row*(bottom-top-8)//2
                for dy in range(2):
                    for x in range(left,right+1):
                        c = sample[x-left]
                        put(x,y+dy,c if c else 36)
        changed = [i for i in range(w*h) if pixels[i]!=before[i]]
        assert all(i in allowed for i in changed)
        edits.append(dict(frame=f, changed_pixels=len(changed), edit_bounds=(
            [min(i%w for i in allowed),min(i//w for i in allowed),
             max(i%w for i in allowed)+1,max(i//w for i in allowed)+1] if allowed else None)))
        output.extend(header+pixels)
    resources[name] = bytes(output)
    return dict(clip=184,matrix=[3,3],source_handle=116,frames=edits,
                source_sha256=hashlib.sha256(source).hexdigest(),
                adapted_sha256=hashlib.sha256(output).hexdigest())
