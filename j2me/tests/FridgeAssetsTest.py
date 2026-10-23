"""Audit the sole intentional source-media edit and the nine moving handles."""
import sys
from pathlib import Path
sys.dont_write_bytecode=True
ROOT=Path(__file__).resolve().parents[1]
sys.path.insert(0,str(ROOT))
from extract_hotel import extract
from adapt_fridge import frames
original=(ROOT.parent/'PILOTS.EXE').read_bytes()
source,_=extract(original,adapt_door=False)
adapted,report=extract(original)
assert source.keys()==adapted.keys()
assert [k for k in source if source[k]!=adapted[k]]==['scenes/09-184.anm']
a=source['scenes/09-184.anm'];b=adapted['scenes/09-184.anm']
assert a[:777]==b[:777], 'Animation timing/palette changed'
for f,(old,new,edit) in enumerate(zip(frames(a),frames(b),report['fridge_door_adaptation']['frames'])):
    assert old[:3]==new[:3], 'Pivot or dimensions changed'
    _,w,h,before=old;after=new[3];box=edit['edit_bounds']
    for i in range(w*h):
        if before[i]!=after[i]:
            assert box and box[0]<=i%w<box[2] and box[1]<=i//w<box[3]
    assert (before!=after)==(f not in [9,10,13]), 'Unexpected frame edits'
# At the broad face of frame 4, old 4x4 knobs are gone. Count independent
# dark handle bands in a strip safely inside all four sides of the plate.
_,w,h,pixels=frames(b)[4]
rows=[y for y in range(40,109) if any(pixels[y*w+x] not in (0,36) for x in range(45,89))]
bands=[]
for y in rows:
    if not bands or y>bands[-1][-1]+1:bands.append([])
    bands[-1].append(y)
assert len(bands)==3, ('Door has extra handle rows',bands)
# In the middle of each band there must also be exactly three knobs.
for band in bands:
    columns=[x for x in range(43,92) if any(pixels[y*w+x] not in (0,36) for y in band)]
    groups=[]
    for x in columns:
        if not groups or x>groups[-1][-1]+1:groups.append([])
        groups[-1].append(x)
    assert len(groups)==3, ('Door has extra handle columns',groups)
print('FridgeAssets: only door clip184 edited; all 14 pivots/timings and other media preserved; moving panel has 3x3 knobs')
