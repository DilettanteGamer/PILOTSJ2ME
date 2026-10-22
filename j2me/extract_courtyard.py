#!/usr/bin/env python3
"""Extract the original second location in order: objects, clips, placement, actions, speech."""
import hashlib,json,struct
from pathlib import Path
from extract_scene02 import records,absolute_cells,EXPECTED
from extract_window_gameplay import execute
from build import require
ROOT=Path(__file__).resolve().parent


def extract(original):
    require(hashlib.sha256(original).hexdigest()==EXPECTED,'Original hash mismatch')
    source,start,end=records(original,5);common,_,_=records(original,1)
    table={(r['code'],r['id'],r['args'][0]):r for r in source}
    background=table[0x18,0,0]['data'];palette=background[24+640*480:]
    require(len(palette)==768,'Unexpected courtyard palette')
    sprites={}
    clips=[]
    for group,ids in [(common,list(range(38))+[44]),(source,list(range(46,76)))]:
        for ident in ids:
            header=next(r['data'] for r in group if r['code']==0x40 and r['id']==ident)
            pivots=next(r['data'] for r in group if r['code']==0x42 and r['id']==ident)
            require(header[2]==1 and len(pivots)==header[3]*8,'Unsupported directional clip')
            out=bytearray(b'ANM1'+struct.pack('>HHB',header[3],100,0)+palette)
            for index in range(header[3]):
                frame=next(r['data'] for r in group if r['code']==0x43 and r['id']==ident and r['args'][1]==index)
                _,px,py=struct.unpack_from('<Ihh',pivots,index*8);w,h=struct.unpack_from('<HH',frame)
                require(len(frame)==4+w*h,'Invalid frame')
                out.extend(struct.pack('>hhHH',-px,-py,w,h)+frame[4:])
            packed=20000+ident if group is common else ident
            sprites['scenes/03-%d.anm'%packed]=bytes(out)
            if group is source:clips.append(dict(id=ident,frames=header[3],pivot=list(struct.unpack_from('<Ihh',pivots)[1:])))
    # The shared original panel is reused; source background 03 already exists in both profiles.
    cells=absolute_cells(table[0x19,0,0],64,16)
    mask=bytearray(10*27)
    for c in cells:
        x,y=c['cell']
        if y<27 and (c['entity']==0 or c['entity'] in [16,17,18,19,20,21,28]):mask[y*10+x]=1
    watched=bytearray(10*27)
    query=table[0x60,29,0]['data']
    for offset in range(8,len(query),4):
        entity,flags,x,y=query[offset:offset+4]
        require(entity==0 and flags in [0x1b,0x15],'Unexpected animal proximity query')
        watched[y*10+x]=1
    # 1b conditions short-circuit on a matching hero; the final 15 is mandatory if none matched.
    sprites['scenes/03.walk']=b'ZWM1'+bytes(mask)+bytes(watched)
    actions=[]
    for hero,obj,indices in [(0,12,[8,13,18]),(1,11,[7,12,17])]:
        for sheet,index in enumerate(indices):
            action=(51 if hero==0 else 56)+sheet
            g={0x18:hero,0x20:action,0x88:1,0x30:0};calls=[]
            require(execute(table[0x2b,obj,index]['data'],g,[]),'Piano start predicate changed')
            execute(table[0x2c,obj,index]['data'],g,calls)
            clip=next(arg for api,arg in calls if api==0x13)
            actions.append([hero,sheet,action,clip,65 if hero==0 else 25])
    # Route each original note state and hero through source object 15.
    routes=[]
    for hero in [0,1]:
        for sheet in range(3):
            index=(7 if hero==0 else 10)+sheet
            g={0x18:hero,0x1c:2,0x20:1,0x88:1};calls=[]
            queries={6 if hero==1 else 7,16+sheet}
            require(execute(table[0x2b,15,index]['data'],g,[],queries),'Piano route changed')
            execute(table[0x2c,15,index]['data'],g,calls)
            routes.append([hero,sheet,g[0x20]])
    require(table[0x60,23,0]['data'][-12:]==bytes.fromhex('19100408151009191f10000a'),'Arrow/exit event changed')
    require(table[0x2c,31,2]['data']==bytes.fromhex('1d0400000021360e00000000'),'Original third-location boundary changed')
    labels={53:('Стрелка часов',128,48),56:('Поворот указателя',256,128),57:('Коллега — мелодия 1',320,288),
            58:('Коллега — мелодия 2',320,288),59:('Коллега — мелодия 3',320,288),
            60:('Коллега — ноты 1',448,176),61:('Коллега — ноты 2',448,176),62:('Коллега — ноты 3',448,176),
            63:('Шеф — мелодия 1',512,176),64:('Шеф — мелодия 2',512,176),65:('Шеф — мелодия 3',512,176),
            72:('Кот — уход',0,176),73:('Кот — ожидание',0,176),74:('Кот — появление',0,176),75:('Пузырьки',192,400)}
    catalog=bytearray(b'ANC2'+struct.pack('>H',len(labels)))
    for number,(ident,(label,x,y)) in enumerate(sorted(labels.items()),1):
        text=('%02d %s'%(number,label)).encode('utf-8')
        f=sprites['scenes/03-%d.anm'%ident];ox,oy,w,h=struct.unpack_from('>hhHH',f,777)
        fx=max(0,min(639,x+ox+w//2));fy=max(0,min(416,y+oy+h//2))
        catalog.extend(struct.pack('>HhhHHH',ident,x,y,fx,fy,len(text))+text)
    sprites['scenes/03.animations']=bytes(catalog)
    require(table[0x2c,13,2]['data'].startswith(b'Hjzkm d recnf[\0'),'Original piano hover text changed')
    require(table[0x2c,13,6]['data'].startswith(b'Rjn\0'),'Original cat hover text changed')
    objects=[dict(entity=r['id'],layer=r['data'][2],descriptor=r['data'].hex(),
                  initial=[c['world'] for c in cells if c['entity']==r['id']]) for r in source if r['code']==0x20]
    sounds=[]
    for r in source:
        if r['code'] in [0x21,0x22,0x2c]:
            d=r['data'];pos=0;needle=bytes.fromhex('3611000000')
            while (pos:=d.find(needle,pos))>=0:
                require(d[pos-6]==0x1d and d[pos-1]==0x21,'Nonliteral sound')
                cue=struct.unpack_from('<I',d,pos-5)[0]
                sounds.append(dict(entity=r['id'],index=r['args'][0],cue=cue,offset=r['offset']));pos+=5
    report=dict(source_sha256=EXPECTED,section=5,start=start,end=end,objects=objects,clips=clips,
                routes=routes,actions=actions,animal_trigger_cells=[[i%10,i//10] for i,v in enumerate(watched) if v],preview_scene=3,preview_ids=sorted(labels),
                speech={'12':'Коллега: Маэстро!'},object_labels={'piano':'Рояль в кустах','cat':'Кот'},sound_calls=sounds,
                maps=[dict(id=r['id'],bytecode=r['data'].hex(),placements=absolute_cells(r,64,16)) for r in source if r['code']==0x60],
                script_records=[dict(code=r['code'],entity=r['id'],index=r['args'][0],offset=r['offset'],bytecode=r['data'].hex())
                                for r in source if r['code'] in [0x21,0x22,0x2a,0x2b,0x2c]],
                boundary='Source native 0e(4) is recorded as completion of scene 03; scene 04 is not opened.')
    return sprites,report
