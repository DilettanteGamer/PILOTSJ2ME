#!/usr/bin/env python3
"""Every displayed sprite index/color and pivot must derive from the original frame."""
from pathlib import Path
import hashlib,json,struct,sys,zipfile
root=Path(__file__).resolve().parents[1];sys.path.insert(0,str(root));sys.dont_write_bytecode=True
from extract_hotel import extract
source,_=extract((root.parent/'PILOTS.EXE').read_bytes(),15)
report=json.loads((root/'analysis/basement-platform-assets.json').read_text())
assert hashlib.sha256((root/'assets/basement-platform-background.png').read_bytes()).hexdigest()==report['background_sha256']
def original(clip,f):
 b=source['scenes/15-%d.anm'%(20000+clip if clip<46 else clip)];p=777
 for i in range(f+1):
  x,y,w,h=struct.unpack_from('>hhHH',b,p);p+=8;pixels=b[p:p+w*h];p+=w*h
 return b[9:777],x,y,w,h,pixels
output=Path(sys.argv[1]) if len(sys.argv)>1 else root/'dist';canonical=None
for profile in ['128x128','128x160','240x320']:
 with zipfile.ZipFile(output/('Pilots-'+profile+'.jar')) as jar:
  resources={n:jar.read(n) for n in report['resources']}
  if canonical is None:canonical=resources
  assert resources==canonical
  bg=resources['puzzles/basement.p8'];assert bg[:4]==b'P8BG' and struct.unpack_from('>HHHH',bg,4)==(128,160,128,160) and len(bg)==12+768+128*160
  for name,info in report['original_sprites'].items():
   b=resources[name];assert b[:4]==b'ANM1';assert struct.unpack_from('>H',b,4)[0]==len(info['source_frames']);p=777
   num,den=info['scale'];ax,ay=info['anchor']
   for clip,f in info['source_frames']:
    palette,x,y,w,h,pixels=original(clip,f);assert b[9:777]==palette
    xx,yy,ww,hh=struct.unpack_from('>hhHH',b,p);p+=8
    assert (xx,yy)==(round((x-ax)*num/den),round((y-ay)*num/den))
    assert (ww,hh)==(max(1,round(w*num/den)),max(1,round(h*num/den)))
    actual=b[p:p+ww*hh];p+=ww*hh
    for dy in range(hh):
     for dx in range(ww):assert actual[dy*ww+dx]==pixels[dy*h//hh*w+dx*w//ww],(name,clip,f,dx,dy)
   assert p==len(b)
print('BasementPlatformAssets: three profiles, original crate 50/balloons 72, servant 86/62 and Chief sprites, every sampled palette index and authored pivot passed')
