#!/usr/bin/env python3
"""Independent original record oracle for all sewer sprites, crop and timing guards."""
import sys,struct,hashlib,json,zipfile,wave,io
from pathlib import Path
ROOT=Path(__file__).resolve().parents[1];sys.path.insert(0,str(ROOT))
from extract_scene02 import records,EXPECTED
from animation_codec import decode_animation
raw=(ROOT.parent/'PILOTS.EXE').read_bytes();assert hashlib.sha256(raw).hexdigest()==EXPECTED
local,_,_=records(raw,9);common,_,_=records(raw,1);table={(r['code'],r['id'],r['args'][0],r['args'][1]):r for r in common+local};bg=table[24,0,0,0]['data'];palette=bg[24+640*480:]
report=[]
for profile in ['128x128','128x160','240x320']:
 with zipfile.ZipFile((Path(sys.argv[1]) if len(sys.argv)>1 else ROOT/'dist')/('Pilots-'+profile+'.jar')) as jar:
  names=[n for n in jar.namelist() if n.startswith('scenes/07-') and n.endswith('.anm')];frames=0
  for name in names:
   packed=int(name.split('-')[-1][:-4]);ident=packed-20000 if packed>=20000 else packed;h=table[64,ident,0,0]['data'];piv=table[66,ident,0,0]['data'];data=decode_animation(jar.read(name));assert data[9:777]==palette and struct.unpack_from('>H',data,4)[0]==h[3];p=777
   for f in range(h[3]):
    image=table[67,ident,0,f]['data'];w,hh=struct.unpack_from('<HH',image);_,px,py=struct.unpack_from('<Ihh',piv,f*8);assert struct.unpack_from('>hhHH',data,p)==(-px,-py,w,hh);p+=8;assert data[p:p+w*hh]==image[4:];p+=w*hh;frames+=1
   assert p==len(data)
  assert len(names)==31
  crop=jar.read('scenes/07.p8');assert struct.unpack_from('>HHHH',crop,4)==(432,208,432,208);assert crop[12:780]==palette
  assert crop[780:]==b''.join(bg[24+y*640+104:24+y*640+536] for y in range(144,352))
  assert 'scenes/07.bar' not in jar.namelist() and 'scenes/07.hot' not in jar.namelist()
  cat=jar.read('scenes/07.animations');assert struct.unpack_from('>H',cat,4)[0]==16
  assert [n for n in jar.namelist() if n.endswith('.animations')]==['scenes/07.animations']
  assert jar.read('catalog.bin')==b'PLT2\x00\x10'+bytes(range(2,18))
  samples={r['id']:r for r in common+local if r['code']==81}
  for cue,record in samples.items():
   size=struct.unpack_from('<I',record['data'])[0]
   with wave.open(io.BytesIO(jar.read('audio/07-%02d.wav'%cue))) as sound:
    assert (sound.getnchannels(),sound.getsampwidth(),sound.getframerate())==(1,1,11025)
    assert sound.readframes(sound.getnframes())==record['data'][4:4+size]
  music=jar.read('audio/07.mid');assert music[:4]==b'MThd' and struct.unpack_from('>H',music,12)[0]==50
  p=22;ticks=0;events=bytearray();count=0
  def number():
   global p
   result=0
   while True:
    b=music[p];p+=1;result=(result<<7)|(b&127)
    if b<128:return result
  while p<len(music):
   ticks+=number();status=music[p];p+=1
   if status==255:
    kind=music[p];p+=1;size=number();p+=size
    if kind==47:break
   else:
    size=1 if status&240 in [192,208] else 2
    events.extend(struct.pack('>IB',ticks,status)+music[p:p+size]);p+=size;count+=1
  assert count==8523 and ticks==8528 and hashlib.sha256(events).hexdigest()=='11ff87a1659f0c384beaa3c8d0ba9978dc7b25206bd37151309b2e0547e3fc9d'
  report.append(dict(profile=profile,clips=len(names),frames=frames,crop=[104,144,432,208],source_sha256=EXPECTED))
print('SewerAssets: original palettes/pixels/pivots for 31 clips, bridge crop, no inventory resources and sewer-only preview catalog passed')
(ROOT/'analysis/sewer-assets-audit.json').write_text(json.dumps(report,indent=2)+'\n')
