#!/usr/bin/env python3
"""Compile the original first-location object routing and action rules for the port."""
import hashlib
import json
from pathlib import Path
import struct
import sys
sys.dont_write_bytecode = True
from build import require
from extract_scene02 import records, absolute_cells, EXPECTED
from extract_window_gameplay import execute

ROOT=Path(__file__).resolve().parent
TARGETS={1:(23,7),2:(20,4),3:(22,6),4:(21,5),5:(30,11),6:(27,9),7:(26,8),8:(28,10)}


def extract(original):
    require(hashlib.sha256(original).hexdigest()==EXPECTED,'Original hash mismatch')
    source,_,_=records(original,4)
    table={(r['code'],r['id'],r['args'][0]):r for r in source}
    evidence=[]
    def get(code,obj,index):
        r=table[code,obj,index]
        if r not in evidence:evidence.append(r)
        return r['data']
    routes={}
    for obj in [20,21,22,23,24,26,27,28,29,30]:
        routes[obj]=[]
        for r in source:
            b=r['data']
            if r['code']==0x2c and r['id']==obj and len(b)==11 and b[0]==0x1d and b[5]==0x10 and b[6:10]==struct.pack('<I',0x20):
                value=struct.unpack_from('<I',b,1)[0]
                if value != next(base for target,(object_,base) in TARGETS.items() if object_==({24:23,29:28}.get(obj,obj))):
                    routes[obj].append((r['args'][0],value))
    fixtures=[];binary=bytearray(b'ZRT1')
    for target,(obj,base) in TARGETS.items():
        for hero in (0,1):
            for item in range(4):
                for flags in range(16):
                    object_=24 if target==1 and flags&1 else 29 if target==8 and flags&8 else obj
                    g={0x18:hero,0x1c:2+item,0x20:base,0x88:1,0x90:0}
                    queries={50,51,54,55,58,59,62,63,66,67,70,71,74,75,78,79,
                             11 if flags&2 else 12,4 if flags&4 else 3}
                    action=0
                    for index,value in routes[object_]:
                        if execute(get(0x2b,object_,index),g,[],queries):
                            execute(get(0x2c,object_,index),g,[],queries)
                            action=g[0x20];break
                    binary.append(action)
                    fixtures.append([target,hero,item,flags,action])
    # Original actor rules. IDs are source IDs; Java maps globals to 20000 + ID.
    starts={13:(3,2),15:(19,45),16:(24,50),17:(None,55),18:(29,16),19:(None,30),
            20:(34,None),21:(47,24),23:(53,None),24:(None,35),25:(None,40),26:(15,None),27:(39,None),28:(60,None)}
    actions=[]
    for action,indices in starts.items():
        for hero,index in enumerate(indices):
            if index is None:continue
            for branch in ((0,1) if action==13 else (0,)):
                obj=17+hero;start=(3+branch if hero==0 else 2 if branch==0 else 6) if action==13 else index
                g={0x18:hero,0x20:action,0x88:1,0x30:0,8:branch,12:branch}
                require(execute(get(0x2b,obj,start),g,[]),'Source start predicate failed')
                calls=[];execute(get(0x2c,obj,start),g,calls)
                clip=next(arg for api,arg in calls if api==0x13)
                end={13:36 if hero==0 else 23 if branch==0 else 53,15:10 if hero==0 else 6,16:10 if hero==0 else 6,
                     17:45,18:13 if hero==0 else 88,19:14,20:20,21:10 if hero==0 else 7,23:69,24:31,25:28,26:14,27:34,28:28}[action]
                finish={13:7 if hero==0 else 4 if branch==0 else 9,15:22 if hero==0 else 48,16:27 if hero==0 else 53,
                        17:59,18:32 if hero==0 else 22,19:33,20:37,21:51 if hero==0 else 28,23:58,24:38,25:43,26:17,27:45,28:63}[action]
                g[0x30]=end-1
                require(not execute(get(0x2b,obj,finish),g,[]),'Premature source action completion')
                g[0x30]=end
                require(execute(get(0x2b,obj,finish),g,[]),'Source action end counter changed')
                actions.append([action,hero,branch,clip,end])
    actions.append([30,0,0,95,70])
    require(execute(get(0x2b,17,65),{0x18:0,0x20:30,0x88:1,0x34:1},[]),'Guard payment Chief predicate')
    calls=[];execute(get(0x2c,17,65),{0x18:0,0x20:30,0x88:1,0x34:1},calls)
    require([0x13,95] in calls,'Chief payment clip changed')
    require(execute(get(0x2b,14,13),{0x20:30,0x30:70},[]),'Guard end predicate changed')
    require(get(0x2c,17,0)==bytes.fromhex('1d0300000021360e00000000'),'Original section transition changed')
    require(get(0x60,84,0)[-8:]==bytes.fromhex('121407101114070f'),'Exit formation changed')
    binary.extend(struct.pack('>H',len(actions)))
    for action,hero,branch,clip,end in actions: binary.extend(struct.pack('>BBBHH',action,hero,branch,clip,end))
    walking=[]
    for hero,indices in [(0,[79,77,75,73]),(1,[69,67,63,65])]:
        for direction,index in enumerate(indices,1):
            native_direction={1:6,2:2,3:0,4:4}[direction]
            for half in [0,1]:
                g={8:half,12:half,0x40:10,0x44:10};calls=[]
                require(execute(get(0x2b,17+hero,index+half),g,[],walk_direction=native_direction),'Walking predicate changed')
                execute(get(0x2c,17+hero,index+half),g,calls)
                clip=next(arg for api,arg in calls if api==0x13)
                walking.append([hero,direction,half,clip,g[0x10 if hero==0 else 0x14],g[8 if hero==0 else 12],g[0x40 if hero==0 else 0x44]-10])
    common,_,_=records(original,1)
    require(all(next(r['data'] for r in common if r['code']==0x40 and r['id']==n)[3]==5 for n in range(20,34)),
            'Original gait frame counts changed')
    # Record every action/event body used by this location, including metadata and map mutations.
    for r in source:
        if r['id'] in [14,17,18,25,29,30,31,33,34,35] and r['code'] in [0x21,0x22,0x2a,0x2b,0x2c]:
            if r not in evidence:evidence.append(r)
        if r['code']==0x60 and r['id'] in [0,1,2,3,4,14,41,42,44,45,82,83,84,85,86]:
            if r not in evidence:evidence.append(r)
    placements=absolute_cells(next(r for r in source if r['code']==0x19),64,16)
    static_roots={c['entity']:c['world'] for c in placements if c['entity'] in [11,37,38]}
    require(static_roots=={11:[576,48],37:[384,96],38:[192,288]},'Gate column/sign roots changed')
    layers={r['id']:r['data'][2] for r in source if r['code']==0x20}
    require({i:layers[i] for i in [11,22,26,28,37]}=={11:18,22:12,26:16,28:17,37:18},'Source layers changed')
    for r in source:
        if r['code'] in [0x19,0x20] and r not in evidence:evidence.append(r)
    # Gate can only be paid while the guard exists; scene completion is native 0e(3).
    require(binary[4+(((7-1)*2+0)*4+3)*16+4]==30,'Coin/guard gate routing changed')
    require(binary[4+(((7-1)*2+0)*4+3)*16+0]==26,'Absent guard routing changed')
    exit_map=get(0x60,83,0)
    require(bytes.fromhex('fefc0512') in exit_map,'Payment must clear gate cell (5,18)')
    require(bytes.fromhex('00100611') in exit_map and bytes.fromhex('00100711') in exit_map,'Inner wall barrier changed')
    require(all(any(c['entity']==0 and c['cell']==[x,18] for c in placements) for x in [5,6,7,8,9]),'Initial gate/wall barrier changed')
    require(execute(get(0x2b,17,67),{0x18:0,0x20:30,0x30:12},[]) and
            not execute(get(0x2b,17,67),{0x18:0,0x20:30,0x30:11},[]),'Chief handover end counter changed')
    return bytes(binary),dict(source_sha256=EXPECTED,routing_order=['target','hero','item','flags'],
        exit_walk_topology={'source_map':83,'opened_gate_cell':[320,288],'wall_row_y':288,'wall_columns_x':[384,448,512,576],
                            'inner_barrier_cells':[[384,272],[448,272]],'exit_formation':[[448,240],[448,256]],'chief_handover_end_counter':12},
        flags={'window_open':1,'cat_present':2,'guard_present':4,'wall_broken':8},
        fixtures=fixtures,actions=actions,walking_fields=['hero','direction','half','clip','idle_pose','next_branch','layer_delta'],walking_cases=walking,static_gate_roots=static_roots,descriptor_layers=layers,
        records=[dict(code=r['code'],object=r['id'],index=r['args'][0],offset=r['offset'],bytecode=r['data'].hex()) for r in evidence])


if __name__=='__main__':
    binary,report=extract((ROOT.parent/'PILOTS.EXE').read_bytes())
    (ROOT/'analysis/first-location-original-rules.json').write_text(json.dumps(report,indent=2)+'\n')
    print(len(report['fixtures']),'source routing cases,',len(report['actions']),'action variants')
