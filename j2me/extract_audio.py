#!/usr/bin/env python3
"""Lossless PCM wrapping and original 10 ms MIDI event stream conversion."""
import hashlib, io, struct, wave, sys
sys.dont_write_bytecode = True
from extract_scene02 import records, EXPECTED
from extract_window_gameplay import execute
from build import require


def vlq(n):
    out=[n&127];n>>=7
    while n: out.insert(0,128|(n&127));n>>=7
    return bytes(out)


def midi(blob):
    size=struct.unpack_from('<I',blob)[0]
    require(size<=len(blob)-4,'Truncated music')
    data=blob[4:4+size];p=0;status=0;delay=0;elapsed=0;events=[];track=bytearray(b'\0\xff\x51\x03\x07\xa1\x20')
    def number():
        nonlocal p
        value=data[p];p+=1
        if value&128: value=(value&127)|(data[p]<<7);p+=1
        return value
    # Optional source prefix handled by 00409bfc (not present in these tracks).
    require(data[0]!=0xf8,'Unsupported music prefix')
    while p<len(data):
        if data[p]&128: status=data[p];p+=1
        if status==0xff:
            track.extend(vlq(delay)+b'\xff\x2f\0');elapsed+=delay;break
        require(0x80<=status<0xf0,'Unsupported MIDI status')
        count=1 if status&0xf0 in (0xc0,0xd0) else 2
        payload=bytearray(data[p:p+count]);p+=count
        require(len(payload)==count and payload[0]<128,'Invalid MIDI data')
        skip_delay=count==2 and bool(payload[1]&128)
        if count==2:payload[1]&=127
        elapsed+=delay;events.append([elapsed,status,*payload])
        track.extend(vlq(delay)+bytes([status])+payload)
        delay=0 if skip_delay else number()
    else: raise ValueError('Music has no terminator')
    # 50 ticks/quarter at 500000 us/quarter = the original 10 ms timer.
    output=b'MThd'+struct.pack('>IHHH',6,0,1,50)+b'MTrk'+struct.pack('>I',len(track))+track
    return output,dict(events=len(events),duration_ms=elapsed*10,event_sha256=hashlib.sha256(b''.join(struct.pack('>IB',e[0],e[1])+bytes(e[2:]) for e in events)).hexdigest())


def extract(original):
    require(hashlib.sha256(original).hexdigest()==EXPECTED,'Original SHA mismatch')
    common,_,_=records(original,1);resources={};report={'source_sha256':EXPECTED,'scenes':{}}
    for scene,section,actors,actions in [(2,4,[17,18,14],range(13,34)),(3,5,[12,11],range(51,59)),(4,6,[],[]),(5,7,[],[])]:
        local,_,_=records(original,section);table={(r['code'],r['id'],r['args'][0]):r for r in local}
        samples={r['id']:r for r in common+local if r['code']==0x51};sample_report=[]
        for cue,r in sorted(samples.items()):
            count=struct.unpack_from('<I',r['data'])[0];pcm=r['data'][4:4+count]
            require(len(pcm)==count,'Truncated PCM')
            out=io.BytesIO()
            with wave.open(out,'wb') as wav:
                wav.setnchannels(1);wav.setsampwidth(1);wav.setframerate(11025);wav.writeframes(pcm)
            resources['audio/%02d-%02d.wav'%(scene,cue)]=out.getvalue()
            sample_report.append(dict(cue=cue,samples=count,duration_ms=count*1000//11025,pcm_sha256=hashlib.sha256(pcm).hexdigest()))
        music=next(r for r in local if r['code']==0x52 and r['id']==1)
        resources['audio/%02d.mid'%scene],music_report=midi(music['data'])
        sites=[];events=set()
        for r in local:
            if r['code']!=0x2c or r['id'] not in actors or b'\x36\x11\0\0\0' not in r['data']:continue
            pred=table[0x2b,r['id'],r['args'][0]]['data']
            # Walking and dialogue are emitted by their explicit runtime transitions.
            if b'\x36\x26\0\0\0' in pred or b'\x36\x05\0\0\0' in pred:continue
            sites.append(dict(object=r['id'],index=r['args'][0],offset=r['offset'],predicate=pred.hex(),body=r['data'].hex()))
            for hero in range(2):
                for action in actions:
                    if r['id']==14 and action!=30:continue
                    for branch in range(2):
                        for step in range(101):
                            g={0x18:hero,0x20:action,0x88:1,0x30:step,8:branch,12:branch}
                            if execute(pred,g,[]):
                                calls=[];execute(r['data'],g,calls)
                                for api,cue in calls:
                                    if api==0x11:events.add((hero,action,branch,max(1,step),cue))
        explicit=[]
        transitions=([(14,4,35),(17,68,34),(16,3,17),(16,4,19),(16,13,30)]+[(17,i,31) for i in range(75,81)]+[(18,i,31) for i in [63,64,67,68,69,70]]) if scene==2 else [(25,0,19),(30,0,15),(30,-1,16),(33,1,23)]+[(11,i,0) for i in range(34,42)]+[(12,i,0) for i in range(25,33)] if scene==3 else []
        for obj,index,cue in transitions:
            record=table[(0x22 if index==-1 else 0x21 if obj in (25,30) else 0x2c),obj,max(0,index)]
            needle=b'\x1d'+struct.pack('<I',cue)+b'\x21\x36\x11\0\0\0'
            require(needle in record['data'],'Explicit sound transition changed')
            explicit.append(dict(object=obj,index=index,cue=cue,offset=record['offset']))
        # Object 32 owns the recurring bubble sound in the courtyard.
        if scene==3:
            bubble=[r for r in local if r['id']==32 and r['code']==0x2c and b'\x36\x11\0\0\0' in r['data']]
            require(len(bubble)==1 and b'\x1d\x11\0\0\0\x21\x36\x11\0\0\0' in bubble[0]['data'],'Bubble cue changed')
            explicit.append(dict(object=32,index=bubble[0]['args'][0],cue=17,offset=bubble[0]['offset']))
        if scene>=4:
            sites=[dict(object=r['id'],kind=r['code'],index=r['args'][0],offset=r['offset'],body=r['data'].hex()) for r in local
                   if r['code'] in [0x21,0x22,0x2c] and b'\x36\x11\0\0\0' in r['data']]
        events=sorted(events)
        resources['audio/%02d.events'%scene]=b'ZAU1'+struct.pack('>H',len(events))+b''.join(bytes(e) for e in events)
        report['scenes'][str(scene)]=dict(section=section,binding='original section VM' if scene>=4 else 'compiled source predicates and explicit transitions',samples=sample_report,music=music_report,events=events,source_rules=sites,explicit_transitions=explicit)
    return resources,report

if __name__=='__main__':
    import pathlib,json
    data,report=extract(pathlib.Path('PILOTS.EXE').read_bytes())
    pathlib.Path('/tmp/pilots-audio.json').write_text(json.dumps(report,indent=2))
    print([(k,len(v)) for k,v in data.items() if not k.endswith('.wav')])
