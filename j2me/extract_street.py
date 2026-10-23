#!/usr/bin/env python3
"""Section 8: street, crossing, corkscrew, sewer covers and the Chief at the traffic light."""
import hashlib,struct
from extract_scene02 import records,absolute_cells,EXPECTED
from build import require

def extract(original):
    require(hashlib.sha256(original).hexdigest()==EXPECTED,'Original SHA mismatch')
    local,start,end=records(original,8);common,_,_=records(original,1)
    table={(r['code'],r['id'],r['args'][0]):r for r in common+local}
    bg=table[24,0,0]['data'];palette=bg[24+640*480:];out={};clips=[]
    out['scenes/06.p8']=b'P8BG'+struct.pack('>HHHH',640,417,640,417)+palette+bg[24:24+640*417]
    out['scenes/06.bar']=b'P8BG'+struct.pack('>HHHH',640,63,640,63)+palette+bg[24+640*417:24+640*480]
    for group in [common,local]:
        for r in group:
            if r['code']!=64 or r['id'] in [38,39]:continue
            ident=r['id'];h=r['data'];require(h[2]==1,'Directional clip needs conversion')
            pivots=next(x['data'] for x in group if x['code']==66 and x['id']==ident)
            data=bytearray(b'ANM1'+struct.pack('>HHB',h[3],100,0)+palette)
            for f in range(h[3]):
                image=next(x['data'] for x in group if x['code']==67 and x['id']==ident and x['args'][1]==f)
                _,px,py=struct.unpack_from('<Ihh',pivots,f*8);w,hh=struct.unpack_from('<HH',image)
                require(len(image)==4+w*hh,'Bad frame');data.extend(struct.pack('>hhHH',-px,-py,w,hh)+image[4:])
            packed=20000+ident if group is common else ident
            out['scenes/06-%d.anm'%packed]=bytes(data);clips.append(dict(id=ident,packed=packed,frames=h[3],flags=h[1]))
    # Object 14 is the original hover-label controller; captions are handled by
    # StreetScene, keeping its Windows text bytecode out of the game VM.
    descriptors={r['id']:r for r in common+local if r['code']==32 and r['id']!=14}
    binary=bytearray(b'AVM1'+struct.pack('>H',len(descriptors)));entities=[]
    def blob(b):return struct.pack('>I',len(b))+b
    for ident,r in sorted(descriptors.items()):
        init=table.get((33,ident,0),{'data':b'\0'})['data'];exit_=table.get((34,ident,0),{'data':b'\0'})['data']
        rules=[]
        for meta in sorted([x for x in common+local if x['id']==ident and x['code']==42],key=lambda x:x['args'][0]):
            n=meta['args'][0];rules.append((meta['data'],table.get((43,ident,n),{'data':b'\x1d\1\0\0\0\0'})['data'],table.get((44,ident,n),{'data':b'\0'})['data']))
        binary.extend(struct.pack('>HBBB',ident,*r['data'][:3])+blob(init)+blob(exit_)+struct.pack('>H',len(rules)))
        for rule in rules:
            for part in rule:binary.extend(blob(part))
        entities.append(dict(id=ident,type=r['data'][0],layer=r['data'][2],rules=len(rules)))
    maps=[r for r in local if r['code']==96];binary.extend(struct.pack('>H',len(maps)))
    for r in maps:binary.extend(struct.pack('>H',r['id'])+blob(r['data']))
    initial=table[25,0,0];binary.extend(blob(initial['data'])+struct.pack('>H',len(clips)))
    for c in clips:binary.extend(struct.pack('>HBB',c['id'],c['frames'],c['flags']))
    groups=[r for r in local if r['code']==56]
    binary.extend(struct.pack('>H',len(groups)))
    for r in groups:binary.extend(struct.pack('>H',r['id'])+blob(r['data']))
    out['scenes/06.vm']=bytes(binary)
    positions={}
    for record in [initial]+maps:
        for cell in absolute_cells(record,64,16):positions.setdefault(cell['entity'],[]).append(cell['world'])
    roots={}
    for owner in descriptors:
        loc=positions.get(owner,[[512,400]])[0]
        for r in local:
            if r['id']!=owner or r['code'] not in [33,34,44]:continue
            b=r['data'];p=0
            while (p:=b.find(b'\x36\x13\0\0\0',p))>=0:
                if p>=6 and b[p-6]==29 and b[p-1]==33:roots[struct.unpack_from('<I',b,p-5)[0]]=loc
                p+=5
    # Native hero roots after the approach markers / Chief's original waiting mark.
    for c in [50]:roots[c]=[384,384]
    for c in [54,74]:roots[c]=[192,352]
    for c in [59]:roots[c]=[128,400]
    for c in [68,70]:roots[c]=[320,320]
    for c in [69,72,78,79]:roots[c]=[192,336]
    roots[71]=[256,320]
    for c in [77]:roots[c]=[192,336]
    for c in [82,87,88,89,90,92]:roots[c]=[448,368]
    roots[91]=[256,352];roots[47]=roots[48]=[256,448]
    names={50:'Коллега — штопор',52:'Двери кафе',54:'Коллега — люк',55:'Прохожий',56:'Прохожий — выход',59:'Коллега — урна',60:'Штопор',61:'Машина',62:'Машина',63:'Машина — выход',64:'Машина — выход',65:'Машина — столкновение',66:'Машина — крышка люка',68:'Коллега — переход',69:'Коллега — переход',74:'Коллега — штопор и люк',78:'Коллега — крышка люка',79:'Коллега — выход',80:'Урна — эффект',81:'Обитатель урны',82:'Шеф — ожидание',88:'Шеф — реакция 1',89:'Шеф — реакция 2',90:'Шеф — реакция 3',91:'Шеф — выход'}
    preview=[c for c in clips if c['packed']<20000 and c['frames']>1]
    catalog=bytearray(b'ANC2'+struct.pack('>H',len(preview)))
    for i,c in enumerate(preview,1):
        ident=c['id'];x,y=roots.get(ident,[320,320]);y=min(416,y)
        text=('%02d %s — %d'%(i,names.get(ident,'Улица'),ident)).encode();ox,oy,w,h=struct.unpack_from('>hhHH',out['scenes/06-%d.anm'%ident],777)
        catalog.extend(struct.pack('>HhhHHH',ident,x,y,max(0,min(639,x+ox+w//2)),max(0,min(416,y+oy+h//2)),len(text))+text)
    out['scenes/06.animations']=bytes(catalog)
    return out,dict(section=8,source_sha256=EXPECTED,start=start,end=end,entities=entities,clips=clips,initial=absolute_cells(initial,64,16),preview_count=len(preview),preview_roots=roots,groups={str(r['id']):r['data'].hex() for r in groups},rules=[{**r,'data':r['data'].hex()} for r in local if r['code'] in [42,43,44,96,97]],hover_labels={16:'Та сторона',17:'Переход',18:'Штопор',19:'Урна',20:'Люк',21:'Люк'})
