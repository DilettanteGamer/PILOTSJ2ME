#!/usr/bin/env python3
"""Independent section-17 oracle: pixels, pivots, directional poses and untouched VM records."""
import hashlib,struct,sys,zipfile,json
from pathlib import Path
sys.dont_write_bytecode=True
ROOT=Path(__file__).resolve().parents[1]
sys.path.insert(0,str(ROOT))
from extract_scene02 import records,EXPECTED
from animation_codec import decode_animation
raw=(ROOT.parent/'PILOTS.EXE').read_bytes()
assert hashlib.sha256(raw).hexdigest()==EXPECTED
common,_,_=records(raw,1);local,_,_=records(raw,17)
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
    converted=decode_animation(jar.read('scenes/15-%d.anm'%packed))
    dirs=[0] if h[2]==1 else [1,3,5,7]
    assert converted[:4]==b'ANM1' and converted[9:777]==palette
    assert struct.unpack_from('>H',converted,4)[0]==h[3]*len(dirs)
    p=777
    for d in dirs:
     pivot=next(r['data'] for r in group if r['code']==66 and r['id']==ident and r['args'][0]==d)
     for f in range(h[3]):
      source=next(r['data'] for r in group if r['code']==67 and r['id']==ident and r['args'][:2]==[d,f])
      _,px,py=struct.unpack_from('<Ihh',pivot,f*8);w,hh=struct.unpack_from('<HH',source)
      assert struct.unpack_from('>hhHH',converted,p)==(-px,-py,w,hh),(profile,ident,d,f)
      p+=8;assert converted[p:p+w*hh]==source[4:4+w*hh],(profile,ident,d,f);p+=w*hh;frames+=1
    assert p==len(converted);count+=1
  assert (count,frames)==(95,1291)
  data=jar.read('scenes/15.vm');p=0
  def take(n):
   global p
   b=data[p:p+n];assert len(b)==n;p+=n;return b
  def u16():return struct.unpack('>H',take(2))[0]
  def blob():return take(struct.unpack('>I',take(4))[0])
  assert take(4)==b'AVM1'
  descriptors={r['id']:r for r in common+local if r['code']==32 and r['id']!=26}
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
   ident=u16();f,flag=take(2);h=table[64,ident,0]['data'];assert (f,flag)==(h[3],h[1])
  for op in [56,57]:
   rr=[r for r in local if r['code']==op];assert u16()==len(rr)
   for r in rr:assert u16()==r['id'] and blob()==r['data']
  assert u16()==0 and p==len(data),'Unexpected text relocation'
  assert [n for n in jar.namelist() if n.endswith('.animations')]==['scenes/14.animations']
  profiles.append(dict(profile=profile,clips=count,frames=frames,source_rules=rule_count,source_maps=len(maps),source_sha256=EXPECTED,vm_sha256=hashlib.sha256(data).hexdigest()))
print('BasementAssets: all three profiles, 95 original clips / 1291 frames, four directional poses, all VM definitions/rules/maps and new-only preview catalog passed')
(ROOT/'analysis/basement-assets-audit.json').write_text(json.dumps(profiles,indent=2)+'\n')
