#!/usr/bin/env python3
"""Section 9: unmodified crossing sprites, bridge crop and rat timing evidence."""
import hashlib,struct
from extract_scene02 import records,EXPECTED
from build import require

def extract(original):
 require(hashlib.sha256(original).hexdigest()==EXPECTED,'Original SHA mismatch')
 local,_,_=records(original,9);common,_,_=records(original,1)
 table={(r['code'],r['id'],r['args'][0],r['args'][1]):r for r in common+local}
 bg=table[24,0,0,0]['data'];pal=bg[24+640*480:]
 x0,y0,w,h=104,144,432,256
 crop=b''.join(bg[24+y*640+x0:24+y*640+x0+w] for y in range(y0,y0+h))
 out={'scenes/07.p8':b'P8BG'+struct.pack('>HHHH',w,h,w,h)+pal+crop}
 ids=[16,17,18,19,28,29,32,33,37,46,47,48,56,57,58,59,60,61,62,63,64,66,70,71,72,73,74,75,76,77,78,79,80,81,82,83,84]
 clips=[]
 for ident in ids:
  header=table[64,ident,0,0]['data'];require(header[2]==1,'Unexpected sewer directions')
  data=bytearray(b'ANM1'+struct.pack('>HHB',header[3],100,0)+pal)
  piv=table[66,ident,0,0]['data']
  # Empty 1x1 source frames may include a record tail beyond their pixel payload.
  for f in range(header[3]):
   frame=table[67,ident,0,f]['data'];fw,fh=struct.unpack_from('<HH',frame);require(len(frame)>=4+fw*fh,'Truncated sewer frame')
   _,px,py=struct.unpack_from('<Ihh',piv,f*8);data.extend(struct.pack('>hhHH',-px,-py,fw,fh)+frame[4:4+fw*fh])
  packed=20000+ident if ident<46 else ident;out['scenes/07-%d.anm'%packed]=bytes(data);clips.append({'id':ident,'packed':packed,'frames':header[3]})
 # Per-frame collision columns use visible original pixels at the hero's torso.
 # Stabilize the middle rat horizontally: its PC clip includes a drifting root.
 contact=bytearray(b'RCT1')
 for ident,root in [(46,192),(47,272),(48,384)]:
  contact.extend(struct.pack('>h',root));piv=table[66,ident,0,0]['data']
  for f in range(20):
   raw=table[67,ident,0,f]['data'];fw,fh=struct.unpack_from('<HH',raw);_,px,py=struct.unpack_from('<Ihh',piv,f*8)
   shift=304-(root-px+fw//2) if ident==47 else 0
   columns=[root+shift-px+x for x in range(fw) if sum(raw[4+y*fw+x]!=0 for y in range(fh) if 235<=160-py+y<=272)>=4]
   left=columns[0] if columns else 0;span=columns[-1]-left+1 if columns else 0;mask=bytearray((span+7)//8)
   for x in columns:mask[(x-left)//8]|=1<<((x-left)%8)
   contact.extend(struct.pack('>hhH',shift,left,span)+mask)
 out['scenes/07.contact']=bytes(contact)
 # The crossing is cropped; previews use roots in the cropped coordinates too.
 preview=[(46,88,16),(47,152,16),(48,280,16),(57,200,128),(59,200,128),(61,200,128),(63,200,128),(64,83,260),(71,24,96),(72,152,224),(74,-104,224),(75,-104,224),(77,472,224),(79,-104,160),(81,24,-128),(83,472,160)]
 cat=bytearray(b'ANC2'+struct.pack('>H',len(preview)))
 for n,(ident,x,y) in enumerate(preview,1):
  # Keep roots valid for the general preview UI; off-crop ambient roots are
  # moved into the crop only for their isolated animation previews.
  x=max(0,min(399,x));y=max(0,min(207,y));label=('%02d Канализация — %d'%(n,ident)).encode()
  cat.extend(struct.pack('>HhhHHH',ident,x,y,min(399,x+32),min(207,y),len(label))+label)
 out['scenes/07.animations']=bytes(cat)
 return out,dict(section=9,source_sha256=EXPECTED,crop=[x0,y0,w,h],bridge={'clip':70,'root':[448,288]},hero={'id':11,'start':[448,256],'goal':[128,256],'safe_stops':[128,328,448],'speed_pixels_per_tick':16,'leg_ticks':[13,8],'fall_clip':64,'swim_clip':66,'return_clip':66,'return_voice':17,'hit_columns':'07.contact','contact_y':[235,272],'minimum_contact_pixels':4,'middle_rat_center':304},rats=[{'object':15,'clip':46,'root':[192,160],'period':20,'danger':[14,20]},{'object':16,'clip':47,'root':[272,160],'period':20,'danger':[6,11],'moving_only':False},{'object':17,'clip':48,'root':[384,160],'period':20,'danger':[9,16]}],clips=clips,preview_count=len(preview),source_rules=[{**r,'data':r['data'].hex()} for r in local if r['id'] in [11,15,16,17,18] and r['code'] in [42,43,44]])
