#!/usr/bin/env python3
"""Independent section-18 oracle: pixels, pivots, directional poses and untouched VM records."""
import hashlib,struct,sys,zipfile,json
from pathlib import Path
sys.dont_write_bytecode=True
ROOT=Path(__file__).resolve().parents[1]
sys.path.insert(0,str(ROOT))
from extract_scene02 import records,EXPECTED
from animation_codec import decode_animation
raw=(ROOT.parent/'PILOTS.EXE').read_bytes()
assert hashlib.sha256(raw).hexdigest()==EXPECTED
common,_,_=records(raw,1);local,_,_=records(raw,18)
table={(r['code'],r['id'],r['args'][0]):r for r in common+local}
palette=table[24,0,0]['data'][24+640*480:]
output=Path(sys.argv[1]) if len(sys.argv)>1 else ROOT/'dist'
profiles=[]
for profile in ['128x128','128x160','240x320']:
 with zipfile.ZipFile(output/('Pilots-'+profile+'.jar')) as jar:
  count=frames=0
  for group in [common,local]:
   for clip in group:
    if clip['code']!=64 or clip['id'] in [38,39]:continue
    ident=clip['id'];h=clip['data'];packed=ident+20000 if group is common else ident
    converted=decode_animation(jar.read('scenes/16-%d.anm'%packed))
    dirs=[0] if h[2]==1 else [1,3,5,7]
    assert converted[:4]==b'ANM1' and converted[9:777]==palette
    extra={57:20,70:66,72:40} if group is local else {}
    total=extra.get(ident,h[3])*len(dirs)
    assert struct.unpack_from('>H',converted,4)[0]==total
    p=777
    for d in dirs:
     pivot=next(r['data'] for r in group if r['code']==66 and r['id']==ident and r['args'][0]==d)
     for f in range(extra.get(ident,h[3])):
      extension={57:(3,85,6,3),70:(38,24,4,-2),72:(10,14,4,-2)}.get(ident) if group is local else None
      source_frame=min(f,extension[0]) if extension else f
      source=next(r['data'] for r in group if r['code']==67 and r['id']==ident and r['args'][:2]==[d,source_frame])
      _,px,py=struct.unpack_from('<Ihh',pivot,source_frame*8);w,hh=struct.unpack_from('<HH',source)
      x,y=-px,-py;pixels=source[4:4+w*hh]
      if extension and f>source_frame:
       base,speed,accel,drift=extension;elapsed=f-base
       x+=drift*elapsed;y+=speed*elapsed+accel*elapsed*(elapsed-1)//2
      if group is local and ident==64 and 3<=f<=16:
       middle_pivot=next(r['data'] for r in local if r['code']==66 and r['id']==63 and r['args'][0]==0)
       middle=next(r['data'] for r in local if r['code']==67 and r['id']==63 and r['args'][:2]==[0,f])
       _,mx,my=struct.unpack_from('<Ihh',middle_pivot,f*8);mw,mh=struct.unpack_from('<HH',middle)
       top=-my-96;missing=y-top;assert w==mw and x==-mx and missing>0
       pixels=middle[4:4+missing*w]+pixels;y=top;hh+=missing
      assert struct.unpack_from('>hhHH',converted,p)==(x,y,w,hh),(profile,ident,d,f)
      p+=8;assert converted[p:p+w*hh]==pixels,(profile,ident,d,f);p+=w*hh;frames+=1
    assert p==len(converted);count+=1
  assert (count,frames)==(71,1636)
  sky=jar.read('scenes/16.p8');width,height=map(int,profile.split('x'));bar=(width*63+320)//640;play=height-bar
  assert struct.unpack_from('>HHHH',sky,4)==(width,play,640,417)
  bg=table[24,0,0]['data'];x0=(640-width)//2;y0=(417-play)//2
  crop=b''.join(bg[24+(y0+y)*640+x0:24+(y0+y)*640+x0+width] for y in range(play))
  assert sky[12:780]==palette and sky[780:]==crop,'Sky was scaled instead of cropped'
  data=jar.read('scenes/16.vm');p=0
  def take(n):
   global p
   b=data[p:p+n];assert len(b)==n;p+=n;return b
  def u16():return struct.unpack('>H',take(2))[0]
  def blob():return take(struct.unpack('>I',take(4))[0])
  assert take(4)==b'AVM1'
  descriptors={r['id']:r for r in common+local if r['code']==32 and r['id']!=11}
  assert u16()==len(descriptors)
  rule_count=0
  for ident,record in sorted(descriptors.items()):
   assert u16()==ident and take(3)==record['data'][:3]
   for op in [33,34]:assert blob()==table.get((op,ident,0),{'data':b'\0'})['data']
   rules=sorted([r for r in common+local if r['id']==ident and r['code']==42],key=lambda r:r['args'][0])
   assert u16()==len(rules)
   for meta in rules:
    n=meta['args'][0];assert blob()==meta['data']
    assert blob()==table.get((43,ident,n),{'data':b'\x1d\1\0\0\0\0'})['data']
    assert blob()==table.get((44,ident,n),{'data':b'\0'})['data'];rule_count+=1
  maps=[r for r in local if r['code']==96];assert u16()==len(maps)
  for record in maps:assert u16()==record['id'] and blob()==record['data']
  assert blob()==table[25,0,0]['data']
  n=u16()
  for i in range(n):
   ident=u16();f,flag=take(2);h=table[64,ident,0]['data'];assert (f,flag)==({57:20,70:66,72:40}.get(ident,h[3]),h[1])
  for op in [56,57]:
   rr=[r for r in local if r['code']==op];assert u16()==len(rr)
   for r in rr:assert u16()==r['id'] and blob()==r['data']
  assert u16()==0 and p==len(data),'Unexpected text relocation'
  assert [n for n in jar.namelist() if n.endswith('.animations')]==['scenes/16.animations']
  profiles.append(dict(profile=profile,clips=count,frames=frames,source_rules=rule_count,source_maps=len(maps),source_sha256=EXPECTED,vm_sha256=hashlib.sha256(data).hexdigest()))
print('FlightAssets: all three profiles, 71 clips / 1636 frames, original pixels plus repaired top and extended drops, all VM definitions/rules/maps and new-only preview catalog passed')
(ROOT/'analysis/flight-assets-audit.json').write_text(json.dumps(profiles,indent=2)+'\n')
