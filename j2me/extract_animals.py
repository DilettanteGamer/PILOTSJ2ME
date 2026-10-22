#!/usr/bin/env python3
"""Compile section 6: objects, clips, source rules and original map operations."""
import hashlib,json,struct,sys
from collections import Counter
sys.dont_write_bytecode=True
from extract_scene02 import records,absolute_cells,EXPECTED
from build import require

def extract(original):
    require(hashlib.sha256(original).hexdigest()==EXPECTED,'Original SHA mismatch')
    source,start,end=records(original,6);common,_,_=records(original,1)
    table={(r['code'],r['id'],r['args'][0]):r for r in common+source}
    bg=table[0x18,0,0]['data'];palette=bg[24+640*480:];resources={};clips=[]
    for group in [common,source]:
        for r in group:
            if r['code']!=0x40 or r['id'] in [38,39]:continue
            ident=r['id'];header=r['data']
            if header[2]!=1:continue
            pivots=next(v['data'] for v in group if v['code']==0x42 and v['id']==ident)
            out=bytearray(b'ANM1'+struct.pack('>HHB',header[3],100,0)+palette)
            for frame in range(header[3]):
                image=next(v['data'] for v in group if v['code']==0x43 and v['id']==ident and v['args'][1]==frame)
                _,px,py=struct.unpack_from('<Ihh',pivots,frame*8);w,h=struct.unpack_from('<HH',image)
                require(len(image)==4+w*h,'Invalid animation frame')
                out.extend(struct.pack('>hhHH',-px,-py,w,h)+image[4:])
            packed=20000+ident if group is common else ident
            resources['scenes/04-%d.anm'%packed]=bytes(out)
            clips.append(dict(id=ident,packed=packed,frames=header[3],flags=header[1]))
    # Preserve rule metadata as well as bytecode: accepted rules normally stop the object;
    # native 00 continues, metadata 80 terminates the rule list, metadata 10 writes map cells.
    entities=[];binary=bytearray(b'AVM1')
    descriptors={r['id']:r for r in common+source if r['code']==0x20 and r['id']!=15}
    binary.extend(struct.pack('>H',len(descriptors)))
    def blob(data):return struct.pack('>I',len(data))+data
    for ident,r in sorted(descriptors.items()):
        records_for=[v for v in common+source if v['id']==ident]
        init=next((v['data'] for v in records_for if v['code']==0x21),b'\0')
        exit_=next((v['data'] for v in records_for if v['code']==0x22),b'\0')
        rules=[]
        for meta in sorted([v for v in records_for if v['code']==0x2a],key=lambda v:v['args'][0]):
            index=meta['args'][0]
            pred=table.get((0x2b,ident,index),{'data':b'\x1d\x01\0\0\0\0'})['data']
            body=table.get((0x2c,ident,index),{'data':b'\0'})['data']
            # Original inline labels live only in entity 15, excluded above.
            rules.append((meta['data'],pred,body))
        binary.extend(struct.pack('>HBBB',ident,*r['data'][:3])+blob(init)+blob(exit_)+struct.pack('>H',len(rules)))
        for triple in rules:
            for data in triple:binary.extend(blob(data))
        entities.append(dict(id=ident,type=r['data'][0],layer=r['data'][2],rules=len(rules)))
    maps=[r for r in source if r['code']==0x60]
    binary.extend(struct.pack('>H',len(maps)))
    for r in maps:binary.extend(struct.pack('>H',r['id'])+blob(r['data']))
    initial=table[0x19,0,0]
    binary.extend(blob(initial['data']))
    binary.extend(struct.pack('>H',len(clips)))
    for c in clips:binary.extend(struct.pack('>HBB',c['id'],c['frames'],c['flags']))
    resources['scenes/04.vm']=bytes(binary)
    roots={50:(128,288),53:(256,288),54:(256,272),55:(256,272),56:(256,272),
           66:(320,144),67:(320,144),68:(320,144),69:(320,144),70:(448,208),
           79:(512,208),81:(512,208),82:(576,224),83:(0,192),84:(256,208),86:(512,240),
           88:(512,208),93:(448,208),94:(448,208),95:(512,80),96:(512,80),97:(384,192),98:(0,192),
           99:(192,288),100:(448,400),101:(192,288),102:(192,288),104:(576,384),110:(192,288),
           111:(64,368),112:(64,368),113:(64,368),116:(0,224),119:(0,224),121:(256,0),123:(0,352),
           124:(64,368),125:(448,400),126:(192,304),127:(192,304),128:(192,304),129:(256,0),130:(64,368),131:(0,224),135:(0,416),138:(512,208)}
    # NPC clips use entity roots from the source maps, not the heroes' approach marks.
    placement_records=[initial]+[r for r in source if r['code']==0x60]
    for owner in range(13,76):
        locations={tuple(c['world']) for record in placement_records for c in absolute_cells(record,64,16) if c['entity']==owner}
        if len(locations)!=1:continue
        for record in source:
            if record['id']!=owner or record['code'] not in [0x21,0x22,0x2c] or owner==15:continue
            data=record['data'];pos=0
            while (pos:=data.find(bytes.fromhex('3613000000'),pos))>=0:
                if pos>=6 and data[pos-6]==0x1d and data[pos-1]==0x21:
                    clip=struct.unpack_from('<I',data,pos-5)[0]
                    if clip>=46:roots[clip]=next(iter(locations))
                pos+=5
    names={50:'Шеф — кран',53:'Фонтан',54:'Фонтан — состояние 1',55:'Фонтан — состояние 2',56:'Фонтан — состояние 3',
           66:'Шеф — подбор предмета',67:'Коллега — подбор предмета',68:'Коллега — грабли',69:'Коллега — лейка',70:'Шеф — полив пальмы',
           79:'Бананы — появление',81:'Коллега — бананы',82:'Носорог — эффект',83:'Носорог — движение',84:'Носорог — реакция',
           86:'Звериная тропа',88:'Носорог — появление',93:'Пальма — действие',94:'Пальма — реакция',95:'Коллега — на пальме',96:'Коллега — ответ',
           97:'Пальма — превращение',98:'Коллега — перемещение',99:'Фонтан — выброс',100:'Выход — анимация',101:'Фонтан — вода',
           102:'Фонтан — исчезновение',104:'Птица',110:'Фонтан — эффект',111:'Кенгуру — ожидание',112:'Кенгуру — реакция',113:'Кенгуру — появление',
           116:'Начальная сцена — декорация',119:'Начальная сцена — кадр',121:'Начальная сцена — эффект',123:'Шеф — кенгуру',124:'Кенгуру — действие',
           125:'Выход — действие',126:'Шеф — вход',127:'Шеф — ожидание',128:'Шеф — реакция',129:'Начальная сцена — пролёт',130:'Кенгуру — вступление',131:'Начальная сцена — объект',135:'Начальная сцена — передний план',138:'Носорог — уход'}
    previews=[c for c in clips if c['id']<20000 and c['id']>=46 and (c['frames']>1 or c['id']==127)]
    catalog=bytearray(b'ANC2'+struct.pack('>H',len(previews)))
    for n,c in enumerate(previews,1):
        ident=c['id'];require(ident in roots,'Missing preview placement '+str(ident));x,y=roots[ident]
        text=('%02d %s'%(n,names[ident])).encode('utf-8');f=resources['scenes/04-%d.anm'%ident]
        ox,oy,w,h=struct.unpack_from('>hhHH',f,777)
        catalog.extend(struct.pack('>HhhHHH',ident,x,min(416,y),max(0,min(639,x+ox+w//2)),max(0,min(416,y+oy+h//2)),len(text))+text)
    resources['scenes/04.animations']=bytes(catalog)
    opcodes=Counter();native_apis=Counter()
    for record in source:
        if record['code'] not in [0x21,0x22,0x2b,0x2c] or record['id']==15:continue
        code=record['data'];p=0
        while p<len(code):
            op=code[p];p+=1;opcodes[op]+=1
            require(op in [0,1,3,5,0x10,0x15,0x17,0x19,0x1d,0x1e,0x1f,0x21,0x22,0x2d,0x31,0x36],'Unsupported animal VM opcode')
            if op in [0x10,0x1d,0x31,0x36]:
                value=struct.unpack_from('<I',code,p)[0];p+=4
                if op==0x36:
                    require(value in [0,1,3,4,9,0x0e,0x10,0x11,0x13,0x14,0x19,0x1b,0x1e,0x1f,0x23,0x24,0x26],'Unsupported animal VM native')
                    native_apis[value]+=1
    report=dict(source_sha256=EXPECTED,section=6,source_opcode_counts=dict(opcodes),source_native_counts=dict(native_apis),start=start,end=end,entities=entities,clips=clips,
                initial=absolute_cells(initial,64,16),preview_ids=[c['id'] for c in previews],preview_roots={str(c['id']):list(roots[c['id']]) for c in previews},
                rules=[dict(code=r['code'],entity=r['id'],index=r['args'][0],offset=r['offset'],bytecode=r['data'].hex()) for r in source if r['code'] in [0x21,0x22,0x2a,0x2b,0x2c,0x60]])
    return resources,report
