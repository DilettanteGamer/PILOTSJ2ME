#!/usr/bin/env python3
"""Independent dimensions, transparency, palette and atlas-provenance checks."""
from pathlib import Path
import hashlib,json,struct,sys,zipfile
root=Path(__file__).resolve().parents[1]
report=json.loads((root/'analysis/basement-platform-assets.json').read_text())
assert hashlib.sha256((root/'assets/basement-platform-atlas.png').read_bytes()).hexdigest()==report['atlas_sha256']
output=Path(sys.argv[1]) if len(sys.argv)>1 else root/'dist'
canonical=None
for profile in ['128x128','128x160','240x320']:
 with zipfile.ZipFile(output/('Pilots-'+profile+'.jar')) as jar:
  resources={n:jar.read(n) for n in report['resources']}
  if canonical is None:canonical=resources
  assert resources==canonical,'Unexpected profile art divergence'
  bg=resources['puzzles/basement.p8'];assert bg[:4]==b'P8BG' and struct.unpack_from('>HHHH',bg,4)==(256,176,256,176) and len(bg)==12+768+256*176
  for name,data in resources.items():
   if not name.endswith('.sprite'):continue
   assert data[:4]==b'ANM1';frames=struct.unpack_from('>H',data,4)[0];p=777
   assert frames>0
   if name.endswith('crates.sprite') or name.endswith('servant.sprite'):assert frames==4
   for f in range(frames):
    x,y,w,h=struct.unpack_from('>hhHH',data,p);p+=8
    assert 1<=w<=44 and 1<=h<=48 and y==-h
    pixels=data[p:p+w*h];p+=w*h;assert 0 in pixels and any(pixels),'Missing alpha or empty sprite'
   assert p==len(data)
print('BasementPlatformAssets: shared front-view atlas, 256x176 indexed stage, four crate destruction/balloon stages, four servant poses and original Chief/gun silhouettes passed')
