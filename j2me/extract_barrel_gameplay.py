#!/usr/bin/env python3
"""Source oracle for kettle inventory, barrel interactions and the cat/crow dependency."""
import hashlib
import json
from pathlib import Path
import struct
import sys
sys.dont_write_bytecode = True
from build import require
from extract_scene02 import records, absolute_cells, EXPECTED
from extract_window_gameplay import execute

ROOT = Path(__file__).resolve().parent


def extract(original):
    require(hashlib.sha256(original).hexdigest() == EXPECTED, 'Original EXE hash mismatch')
    source, _, _ = records(original, 4)
    table = {(r['code'], r['id'], r['args'][0]): r for r in source}
    evidence, fixtures, cat_cases = [], [], []

    def rule(code, obj, index):
        r = table[code, obj, index]
        if r not in evidence: evidence.append(r)
        return r['data']

    def actor(g):
        action, hero, branch = g[0x20], g[0x18], g[8]
        obj = 17 + hero
        if action == 13:
            start = 3 + branch if hero == 0 else 2 if branch == 0 else 6
            finish = 7 if hero == 0 else 4 if branch == 0 else 9
            end = 36 if hero == 0 else 23 if branch == 0 else 53
        else:
            start, finish, end = {
                (0, 15): (19,22,10), (1,15): (45,48,6),
                (0,27): (39,45,34), (1,17): (55,59,45), (1,24): (35,38,31)
            }[hero, action]
        require(execute(rule(0x2b,obj,start),g,[]), 'Actor predicate mismatch')
        calls = []
        execute(rule(0x2c,obj,start),g,calls)
        clip = next(arg for api,arg in calls if api == 0x13)
        g[0x30] = end - 1
        require(not execute(rule(0x2b,obj,finish),g,[]), 'Early source completion')
        g[0x30] = end
        require(execute(rule(0x2b,obj,finish),g,[]), 'Source completion mismatch')
        execute(rule(0x2c,obj,finish),g,[])
        require(g[0x20] == 0 and g[0x30] == 0, 'Action did not reset')
        return clip, end

    def case(target, hero, item, cat, opened, branch):
        g = {0x18:hero, 0x1c:3 if item else 2, 0x20:{1:7,2:4,3:6}[target],
             0x88:1, 0x30:0, 8:branch, 12:branch}
        queries = {50,51,58,59,62,63,11 if cat else 12}
        obj, routes = ((24 if opened else 23), [2,10] if opened else [1,8]) if target == 1 else (20,[8,9]) if target == 2 else (22,[1,2,3,11,14,15])
        matched = False
        for index in routes:
            if execute(rule(0x2b,obj,index),g,[],queries):
                execute(rule(0x2c,obj,index),g,[],queries)
                matched = True
                break
        clip, end = actor(g) if matched else (0,0)
        fixtures.append([target,hero,item,cat,opened,branch,clip,end])

    for hero in (0,1):
        case(2,hero,0,0,0,0)
        for item in (0,1):
            for cat in (0,1):
                for branch in (0,1): case(3,hero,item,cat,0,branch)
        for opened in (0,1):
            for branch in (0,1): case(1,hero,1,0,opened,branch)
    for obj,index,step,effect,action in [(17,20,8,41,15),(18,46,4,41,15),(17,40,25,13,27)]:
        g = {0x18:obj-17,0x20:action,0x30:step}
        require(execute(rule(0x2b,obj,index),g,[]), 'Inventory/barrel event predicate')
        calls=[];execute(rule(0x2c,obj,index),g,calls)
        require([0x14,effect] in calls, 'Inventory/barrel event map mismatch')
        rule(0x60,effect,0)
    # Source cat v94: 0 without crow, 1 with crow, 3 falling.
    for index,flag,step in [(1,1,15),(9,0,15),(2,1,32),(5,1,45),(7,1,58),(11,0,32),(13,3,16)]:
        g={0x94:flag,0x98:step}
        require(execute(rule(0x2b,16,index),g,[]), 'Cat event predicate')
        calls=[];execute(rule(0x2c,16,index),g,calls)
        clip=next((arg for api,arg in calls if api==0x13),0)
        cat_cases.append([flag,step,clip,int([0x14,10] in calls),int([0x14,43] in calls),int(index==13)])
        rule(0x2a,16,index)
    require(struct.unpack_from('<I',rule(0x2a,16,13))[0] == 0x199, 'Cat cleanup flags changed')
    require(rule(0x2a,16,13)[-4:] == bytes.fromhex('fefc0000'), 'Cat cleanup no longer removes its own cell')
    for code,obj,index in [(0x21,16,0),(0x2b,16,0),(0x2c,16,0),(0x2b,16,8),(0x2c,16,8),
                           (0x21,30,0),(0x2b,30,0),(0x2c,30,0),(0x21,33,0),(0x2b,33,1),(0x2c,33,1),
                           (0x21,20,0),(0x21,22,0)]:
        rule(code,obj,index)
    for ident in [8,9,10,11,12,13,41,43,50,51,58,59]: rule(0x60,ident,0)
    for ident,expected in [(13,[(16,[192,176])]),(41,[(33,[256,448])]),(43,[(30,[256,272])])]:
        cells=absolute_cells(table[0x60,ident,0],64,16)
        require([(c['entity'],c['world']) for c in cells]==expected, 'Map placement changed')
    return dict(source_sha256=EXPECTED, oracle_scope='Selected kettle/barrel/item rules with approach complete; cat events include map metadata',
                fixtures=fixtures, cat_cases=cat_cases,
                records=[dict(code=r['code'],object=r['id'],index=r['args'][0],offset=r['offset'],bytecode=r['data'].hex()) for r in evidence])


if __name__ == '__main__':
    result=extract((ROOT.parent/'PILOTS.EXE').read_bytes())
    (ROOT/'analysis/barrel-original-rules.json').write_text(json.dumps(result,indent=2)+'\n')
    print('Original kettle/barrel rules:',len(result['fixtures']),'interaction cases;',len(result['cat_cases']),'cat events')
