"""Recover screen-clipped flight art using only original palette-index pixels."""
import struct

def frames(data):
    result=[];p=777
    for _ in range(struct.unpack_from('>H',data,4)[0]):
        x,y,w,h=struct.unpack_from('>hhHH',data,p);p+=8
        result.append((x,y,w,h,data[p:p+w*h]));p+=w*h
    assert p==len(data)
    return result

def adapt(out,clips):
    prefix='scenes/16-'
    source={c:frames(out[prefix+str(c)+'.anm']) for c in (57,63,64,70,72)}
    repaired=list(source[64])
    for i in range(3,17):
        x,y,w,h,pixels=source[64][i]
        mx,my,mw,mh,mp=source[63][i];top=my-96;missing=y-top
        assert x==mx and w==mw and missing>0
        # Upper/middle variants share the balloon; keep the entire original
        # high frame below its PC top edge, adding only the missing top rows.
        repaired[i]=(x,top,w,h+missing,mp[:missing*w]+pixels)
    changed={64:repaired}
    for clip,whole,count,speed,accel,drift in ((57,3,20,85,6,3),(70,38,66,24,4,-2),(72,10,40,14,4,-2)):
        original=source[clip];x,y,w,h,pixels=original[whole]
        result=original[:whole+1]
        for i in range(1,count-whole):
            result.append((x+drift*i,y+speed*i+accel*i*(i-1)//2,w,h,pixels))
        if clip==72:
            # The first eight PC frames contain only the portion above y=417.
            # Reuse the first complete pose while it rises through the phone edge.
            _,_,ew,eh,ep=original[8]
            for i in range(8):
                ex,ey,_,_,_=original[i]
                result[i]=(ex,ey,ew,eh,ep)
        changed[clip]=result
    for clip,result in changed.items():
        key=prefix+str(clip)+'.anm';old=out[key]
        out[key]=old[:4]+struct.pack('>H',len(result))+old[6:777]+b''.join(struct.pack('>hhHH',x,y,w,h)+p for x,y,w,h,p in result)
        for entry in clips:
            if entry['packed']==clip:entry['frames']=len(result)
    return dict(bottom_entry_clip=72,bottom_entry_whole_pose=8,repaired_balloon_frames=list(range(3,17)),extended_clips={str(k):len(v) for k,v in changed.items() if k!=64})
