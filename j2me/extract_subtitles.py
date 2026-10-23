#!/usr/bin/env python3
"""Audit native 11 sound calls and compile text captions at verified speech events."""
import hashlib
import json
import struct
from pathlib import Path
from extract_scene02 import records, EXPECTED
from extract_window_gameplay import execute
from build import require
ROOT=Path(__file__).resolve().parent


def extract(original):
    require(hashlib.sha256(original).hexdigest()==EXPECTED,'Original SHA mismatch')
    common,_,_=records(original,1)
    scene,_,_=records(original,4)
    table={(r['code'],r['id'],r['args'][0]):r for r in scene}
    samples={r['id']:r for r in common+scene if r['code']==0x51}
    settings=json.loads((ROOT/'subtitles.json').read_text())
    captions={int(k):v for k,v in settings['captions'].items()}
    audit=[]
    for r in common+scene:
        if r['code'] not in [0x21,0x22,0x2c]:continue
        # All native 11 sites in these sections use a literal argument followed by PUSH.
        needle=bytes.fromhex('3611000000');pos=0
        while (pos:=r['data'].find(needle,pos))>=0:
            require(pos>=6 and r['data'][pos-6]==0x1d and r['data'][pos-1]==0x21,'Nonliteral sound call')
            cue=struct.unpack_from('<I',r['data'],pos-5)[0]
            require(cue in samples,'Missing original PCM cue')
            audit.append(dict(section=1 if r in common else 4,object=r['id'],code=r['code'],index=r['args'][0],
                              offset=r['offset'],cue=cue,caption=captions.get(cue),bytecode=r['data'].hex()))
            pos+=5
    bindings=[(17,4,13,0,1,0,6),(18,7,13,1,1,13,7),(14,9,30,0,0,30,36)]
    events=[]
    for obj,index,action,hero,branch,step,cue in bindings:
        g={0x18:hero,0x20:action,0x88:1,0x30:step,8:branch,12:branch}
        require(execute(table[0x2b,obj,index]['data'],g,[]),'Speech event predicate changed')
        calls=[];execute(table[0x2c,obj,index]['data'],g,calls)
        require([0x11,cue] in calls,'Speech cue changed')
        events.append([action,hero,branch,max(1,step),cue])
    for obj,index,cue in [(17,68,34),(14,4,35)]:
        calls=[];execute(table[0x2c,obj,index]['data'],{},calls)
        require([0x11,cue] in calls,'Guard dialogue cue changed')
    require({a['cue'] for a in audit if a['caption']}==set(captions),'Unbound speech caption')
    data=bytearray(b'ZST1'+struct.pack('>H',len(captions)))
    for cue,text in sorted(captions.items()):
        encoded=text.encode('utf-8')
        data.extend(struct.pack('>BH',cue,len(encoded))+encoded)
    data.extend(struct.pack('>H',len(events)))
    for event in events:data.extend(bytes(event))
    report=dict(source_sha256=EXPECTED,caption_origin=settings['origin'],captions=settings['captions'],
                event_fields=['action','hero','branch','step','cue'],events=events,
                dialogue_cues=[34,35],source_sound_calls=audit,
                samples=[dict(cue=n,offset=samples[n]['offset'],pcm_sha256=hashlib.sha256(samples[n]['data'][4:]).hexdigest(),
                              sample_count=struct.unpack_from('<I',samples[n]['data'])[0],sample_rate=11025)
                         for n in sorted({a['cue'] for a in audit})],
                scope='All native 11 calls in common and zoo scripts; nonverbal sounds have no fabricated spoken text.')
    return bytes(data),report
