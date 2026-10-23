#!/usr/bin/env python3
"""Original section 7: garden map, actors, bytecode and fresh preview catalog."""
import struct,hashlib
from extract_scene02 import records,absolute_cells,EXPECTED
from build import require

def extract(original):
    require(hashlib.sha256(original).hexdigest()==EXPECTED,'Original SHA mismatch')
    local,start,end=records(original,7);common,_,_=records(original,1)
    table={(r['code'],r['id'],r['args'][0]):r for r in common+local}
    bg=table[24,0,0]['data'];palette=bg[24+640*480:];out={};clips=[]
    out['scenes/05.p8']=b'P8BG'+struct.pack('>HHHH',640,417,640,417)+palette+bg[24:24+640*417]
    out['scenes/05.bar']=b'P8BG'+struct.pack('>HHHH',640,63,640,63)+palette+bg[24+640*417:24+640*480]
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
            out['scenes/05-%d.anm'%packed]=bytes(data);clips.append(dict(id=ident,packed=packed,frames=h[3],flags=h[1]))
    binary=bytearray(b'AVM1');descriptors={r['id']:r for r in common+local if r['code']==32 and r['id'] not in [28,29]}
    def blob(b):return struct.pack('>I',len(b))+b
    binary.extend(struct.pack('>H',len(descriptors)));entities=[]
    for ident,r in sorted(descriptors.items()):
        same=[x for x in common+local if x['id']==ident]
        init=next((x['data'] for x in same if x['code']==33),b'\0');exit_=next((x['data'] for x in same if x['code']==34),b'\0')
        rules=[]
        for meta in sorted([x for x in same if x['code']==42],key=lambda x:x['args'][0]):
            n=meta['args'][0];rules.append((meta['data'],table.get((43,ident,n),{'data':b'\x1d\1\0\0\0\0'})['data'],table.get((44,ident,n),{'data':b'\0'})['data']))
        binary.extend(struct.pack('>HBBB',ident,*r['data'][:3])+blob(init)+blob(exit_)+struct.pack('>H',len(rules)))
        for rule in rules:
            for part in rule:binary.extend(blob(part))
        entities.append(dict(id=ident,type=r['data'][0],layer=r['data'][2],rules=len(rules)))
    maps=[r for r in local if r['code']==96];binary.extend(struct.pack('>H',len(maps)))
    for r in maps:binary.extend(struct.pack('>H',r['id'])+blob(r['data']))
    initial=table[25,0,0];binary.extend(blob(initial['data'])+struct.pack('>H',len(clips)))
    for c in clips:binary.extend(struct.pack('>HBB',c['id'],c['frames'],c['flags']))
    out['scenes/05.vm']=bytes(binary)
    positions={}
    for record in [initial]+maps:
        for cell in absolute_cells(record,64,16):positions.setdefault(cell['entity'],[]).append(cell['world'])
    roots={}
    for owner in descriptors:
        loc=positions.get(owner,[[128,368] if owner==11 else [64,368] if owner==12 else [512,368]])[0]
        for r in local:
            if r['id']!=owner or r['code'] not in [33,34,44]:continue
            b=r['data'];p=0
            while (p:=b.find(b'\x36\x13\0\0\0',p))>=0:
                if p>=6 and b[p-6]==29 and b[p-1]==33:roots[struct.unpack_from('<I',b,p-5)[0]]=loc
                p+=5
    roots[80]=[320,384];roots[78]=[256,384];roots[79]=[256,384]
    preview=[c for c in clips if c['packed']<20000 and c['frames']>1]
    catalog=bytearray(b'ANC2'+struct.pack('>H',len(preview)))
    for i,c in enumerate(preview,1):
        ident=c['id'];x,y=roots.get(ident,[320,272]);y=min(416,y)
        title='Мартышка' if ident<=50 or ident==77 else 'Шеф' if ident in [61,62,63,64,65,80,82,83] else 'Коллега'
        text=('%02d %s — %d'%(i,title,ident)).encode();ox,oy,w,h=struct.unpack_from('>hhHH',out['scenes/05-%d.anm'%ident],777)
        catalog.extend(struct.pack('>HhhHHH',ident,x,y,max(0,min(639,x+ox+w//2)),max(0,min(416,y+oy+h//2)),len(text))+text)
    out['scenes/05.animations']=bytes(catalog)
    return out,dict(section=7,source_sha256=EXPECTED,start=start,end=end,entities=entities,clips=clips,initial=absolute_cells(initial,64,16),preview_count=len(preview),preview_roots=roots,rules=[{**r,'data':r['data'].hex()} for r in local if r['code'] in [42,43,44,96,97]],captions=['Или это ключ от номера Карбофоса...','...или одно из двух.'])
