#!/usr/bin/env python3
"""Original folder, menu swatches, both 97-glyph fonts and seven credit pages."""
import hashlib,struct
from extract_scene02 import records,EXPECTED
from build import require

def p8(pixels,palette,w,h):return b'P8BG'+struct.pack('>HHHH',w,h,w,h)+palette+pixels

def extract(raw):
 require(hashlib.sha256(raw).hexdigest()==EXPECTED,'Original SHA mismatch')
 common,_,_=records(raw,1);folder,_,_=records(raw,2);original,_,_=records(raw,3);credits,_,_=records(raw,20)
 bg=next(r['data'] for r in folder if r['code']==24);palette=bg[24+640*480:]
 # Portrait framing excludes both baked-in copyright lines (x=20..181).
 crop=b''.join(bg[24+y*640+192:24+(y+1)*640] for y in range(480))
 out={'ui/folder.p8':p8(crop,palette,448,480)}
 fonts=bytearray(b'FNT1'+struct.pack('>H',97))
 for ident in [38,39]:
  piv=next(r['data'] for r in common if r['code']==66 and r['id']==ident)
  for index in range(97):
   r=next(r for r in common if r['code']==67 and r['id']==ident and r['args'][1]==index);w,h=struct.unpack_from('<HH',r['data']);_,px,py=struct.unpack_from('<Ihh',piv,index*8)
   mask=bytearray((w*h+7)//8)
   if index:
    for j,value in enumerate(r['data'][4:4+w*h]):
     if value in (246,250):mask[j//8]|=128>>(j&7)
   fonts.extend(struct.pack('>hhhhH',w,h,-px,-py,len(mask))+mask)
 out['ui/original.font']=bytes(fonts)
 bg=next(r['data'] for r in credits if r['code']==24);palette=bg[24+640*480:]
 for r in credits:
  if r['code']==67:out['ui/credits-%d.p8'%(r['id']-46)]=p8(r['data'][4:],palette,640,480)
 out['ui/leather.p8']=p8(bg[24:24+640*480],palette,640,480)
 return out,dict(source_sha256=EXPECTED,folder_section=2,folder_crop=[192,0,448,480],font_ids=[38,39],glyphs_per_font=97,credits_section=20,credit_pages=7,button_rgb=0xcbc397,selected_rgb=0xdfdfcf)

def scale(data,w,h):
 sw,sh=struct.unpack_from('>HH',data,4);src=data[780:]
 pixels=bytes(src[(y*sh//h)*sw+x*sw//w] for y in range(h) for x in range(w))
 return p8(pixels,data[12:780],w,h)

def profile(entries,w,h):
 entries['ui/folder.p8']=scale(entries['ui/folder.p8'],w,h)
 # One credit page at a time; 2x phone width permits readable zoom at low heap cost.
 for name in ['ui/leather.p8']+['ui/credits-%d.p8'%i for i in range(7)]:entries[name]=scale(entries[name],w*2,w*3//2)
