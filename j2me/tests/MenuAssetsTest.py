#!/usr/bin/env python3
"""Original-resource oracle for folder framing, glyph masks and complete credits."""
import sys,struct,zipfile,hashlib,json
from pathlib import Path
ROOT=Path(__file__).resolve().parents[1];sys.path.insert(0,str(ROOT))
from extract_scene02 import records,EXPECTED
raw=(ROOT.parent/'PILOTS.EXE').read_bytes();assert hashlib.sha256(raw).hexdigest()==EXPECTED
common,_,_=records(raw,1);folder,_,_=records(raw,2);credits,_,_=records(raw,20);menu,_,_=records(raw,3)
folder=next(r['data'] for r in folder if r['code']==24);palette=folder[24+640*480:]
mp=next(r['data'] for r in menu if r['code']==24)[24+640*480:];assert mp[242*3:242*3+3]==bytes.fromhex('cbc397') and mp[65*3:65*3+3]==bytes.fromhex('dfdfcf')
report=[]
for profile in ['128x128','128x160','240x320']:
 w,h=map(int,profile.split('x'))
 with zipfile.ZipFile((Path(sys.argv[1]) if len(sys.argv)>1 else ROOT/'dist')/('Pilots-'+profile+'.jar')) as jar:
  data=jar.read('ui/folder.p8');assert struct.unpack_from('>HHHH',data,4)==(w,h,w,h) and data[12:780]==palette
  assert data[780:]==bytes(folder[24+(y*480//h)*640+192+x*448//w] for y in range(h) for x in range(w))
  font=jar.read('ui/original.font');assert font[:6]==b'FNT1\0a';p=6
  for ident in [38,39]:
   piv=next(r['data'] for r in common if r['code']==66 and r['id']==ident)
   for f in range(97):
    source=next(r['data'] for r in common if r['code']==67 and r['id']==ident and r['args'][1]==f);gw,gh=struct.unpack_from('<HH',source);_,px,py=struct.unpack_from('<Ihh',piv,f*8)
    ww,hh,ox,oy,size=struct.unpack_from('>hhhhH',font,p);p+=10;assert (ww,hh,ox,oy)==(gw,gh,-px,-py)
    mask=font[p:p+size];p+=size
    for j,value in enumerate(source[4:4+gw*gh]):assert bool(mask[j//8]&(128>>(j&7)))==(f>0 and value in [246,250])
  assert p==len(font)
  cp=next(r['data'] for r in credits if r['code']==24)[24+640*480:]
  assert not any(n.startswith('ui/credits-') for n in jar.namelist()),'Bitmap credits still packaged'
  data=jar.read('ui/leather.p8');assert struct.unpack_from('>HHHH',data,4)==(w,h,w,h) and data[12:780]==cp
  pixels=next(r['data'] for r in credits if r['code']==24)[24:24+640*480]
  assert data[780:]==bytes(pixels[(y*480//h)*640+x*640//w] for y in range(h) for x in range(w))
  report.append(dict(profile=profile,folder_crop=[192,0,448,480],glyphs=194,credits_format='text',backdrop=[w,h],source_sha256=EXPECTED))
(ROOT/'analysis/menu-assets-audit.json').write_text(json.dumps(report,indent=2)+'\n')
print('MenuAssets: original folder without baked copyright, 194 original glyphs, exact button colors and original leather backdrop and no bitmap credits passed')
