#!/usr/bin/env python3
"""Sections 10/11/12: hotel entrance, refrigerator room and balcony."""
import hashlib,struct
from extract_scene02 import records,absolute_cells,EXPECTED
from build import require
from adapt_fridge import adapt

def extract(original, scene=9, adapt_door=True):
    require(hashlib.sha256(original).hexdigest()==EXPECTED,'Original SHA mismatch')
    local,start,end=records(original,scene+2);common,_,_=records(original,1)
    prefix='scenes/%02d'%scene
    table={(r['code'],r['id'],r['args'][0]):r for r in common+local}
    bg=table[24,0,0]['data'];palette=bg[24+640*480:];out={};clips=[]
    out[prefix+'.p8']=b'P8BG'+struct.pack('>HHHH',640,417,640,417)+palette+bg[24:24+640*417]
    out[prefix+'.bar']=b'P8BG'+struct.pack('>HHHH',640,63,640,63)+palette+bg[24+640*417:24+640*480]
    for group in [common,local]:
        for r in group:
            if r['code']!=64 or r['id'] in [38,39]:continue
            ident=r['id'];h=r['data'];require(h[2]==1,'Directional clip needs conversion')
            pivots=next(x['data'] for x in group if x['code']==66 and x['id']==ident)
            data=bytearray(b'ANM1'+struct.pack('>HHB',h[3],100,0)+palette)
            for f in range(h[3]):
                image=next(x['data'] for x in group if x['code']==67 and x['id']==ident and x['args'][1]==f)
                _,px,py=struct.unpack_from('<Ihh',pivots,f*8);w,hh=struct.unpack_from('<HH',image)
                require(len(image)==4+w*hh or scene==10 and w==hh==1 and len(image)==16 and image[4]==0,'Bad frame');data.extend(struct.pack('>hhHH',-px,-py,w,hh)+image[4:4+w*hh])
            packed=20000+ident if group is common else ident
            out[prefix+'-%d.anm'%packed]=bytes(data);clips.append(dict(id=ident,packed=packed,frames=h[3],flags=h[1]))
    door_adaptation=adapt(out) if scene==9 and adapt_door else None
    # Interior object 15 owns bilingual hover labels; HotelScene renders these.
    # Cinematic caption bytecode is retained with relocated inline text IDs.
    descriptors={r['id']:r for r in common+local if r['code']==32 and not (scene==9 and r['id']==15 or scene==10 and r['id']==13)}
    texts=[]
    def script(record):
        if record is None:return b'\0'
        b=record['data'];entry=record['args'][2]
        if entry:
            raw=b[:entry].split(b'\0')[0].decode('ascii')
            en="qwertyuiop[]asdfghjkl;'zxcvbnm,./QWERTYUIOP{}ASDFGHJKL:\"ZXCVBNM<>?"
            ru="йцукенгшщзхъфывапролджэячсмитьбю.ЙЦУКЕНГШЩЗХЪФЫВАПРОЛДЖЭЯЧСМИТЬБЮ,"
            text=raw.translate(str.maketrans(en,ru));texts.append(text)
            b=b[entry:];pos=b.index(b'\x23');b=b[:pos+1]+struct.pack('<I',len(texts)-1)+b[pos+5:]
        return b
    binary=bytearray(b'AVM1'+struct.pack('>H',len(descriptors)));entities=[]
    def blob(b):return struct.pack('>I',len(b))+b
    for ident,r in sorted(descriptors.items()):
        init=script(table.get((33,ident,0)));exit_=script(table.get((34,ident,0)))
        rules=[]
        for meta in sorted([x for x in common+local if x['id']==ident and x['code']==42],key=lambda x:x['args'][0]):
            n=meta['args'][0];rules.append((meta['data'],table.get((43,ident,n),{'data':b'\x1d\1\0\0\0\0'})['data'],script(table.get((44,ident,n)))))
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
    choices=[r for r in local if r['code']==57]
    binary.extend(struct.pack('>H',len(choices)))
    for r in choices:binary.extend(struct.pack('>H',r['id'])+blob(r['data']))
    binary.extend(struct.pack('>H',len(texts)))
    for text in texts:binary.extend(struct.pack('>H',len(text.encode()))+text.encode())
    out[prefix+'.vm']=bytes(binary)
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
    names={}
    preview=[c for c in clips if c['packed']<20000 and c['frames']>1]
    catalog=bytearray(b'ANC2'+struct.pack('>H',len(preview)))
    for i,c in enumerate(preview,1):
        ident=c['id'];x,y=roots.get(ident,[320,320]);y=min(416,y)
        text=('%02d %s — %d'%(i,names.get(ident,'Комната с балконом' if scene==10 else 'Гостиница'),ident)).encode();ox,oy,w,h=struct.unpack_from('>hhHH',out[prefix+'-%d.anm'%ident],777)
        catalog.extend(struct.pack('>HhhHHH',ident,x,y,max(0,min(639,x+ox+w//2)),max(0,min(416,y+oy+h//2)),len(text))+text)
    out[prefix+'.animations']=bytes(catalog)
    return out,dict(fridge_door_adaptation=door_adaptation,section=scene+2,source_sha256=EXPECTED,start=start,end=end,entities=entities,clips=clips,initial=absolute_cells(initial,64,16),preview_count=len(preview),preview_roots=roots,groups={str(r['id']):r['data'].hex() for r in groups},texts=texts,random_choices={str(r['id']):r['data'].hex() for r in choices},rules=[{**r,'data':r['data'].hex()} for r in local if r['code'] in [42,43,44,96,97]],hover_labels={20:'Выход',21:'Выход',22:'Штора',23:'Куча',24:'Куча',25:'Отвёртка',26:'Баллончик',27:'Злобное чудище',28:'Ванная',29:'Ванная',30:'Чайник',31:'Сахарница',32:'Сахарница',33:'Замок',34:'Ключ',35:'Ручка',36:'Включатель',37:'Включатель'} if scene==9 else {})
