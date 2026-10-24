#!/usr/bin/env python3
"""Front-view backdrop with unretouched original indexed sprites and authored pivots."""
from pathlib import Path
import hashlib,struct
from PIL import Image
from extract_hotel import extract as original_resources
ROOT=Path(__file__).resolve().parent

def extract(original):
    atlas=Image.open(ROOT/'assets/basement-platform-atlas.png').convert('RGB')
    bg=atlas.crop((0,0,1254,640)).resize((256,176),Image.Resampling.NEAREST).quantize(colors=255)
    pal=bytes(bg.getpalette()[:768]).ljust(768,b'\0')
    out={'puzzles/basement.p8':b'P8BG'+struct.pack('>HHHH',256,176,256,176)+pal+bg.tobytes()}
    source,_=original_resources(original,15);evidence={}
    def frames(clip):
        packed=20000+clip if clip<46 else clip;b=source['scenes/15-%d.anm'%packed];p=777;items=[]
        for f in range(struct.unpack_from('>H',b,4)[0]):
            x,y,w,h=struct.unpack_from('>hhHH',b,p);p+=8;items.append((x,y,w,h,b[p:p+w*h]));p+=w*h
        return b[9:777],items
    def scaled(name,clips,num,den,ax,ay):
        items=[];origin=[];palette=None
        for clip in clips:
            colors,ff=frames(clip);palette=colors
            for f,(x,y,w,h,pixels) in enumerate(ff):
                ww=max(1,round(w*num/den));hh=max(1,round(h*num/den))
                indices=bytes(pixels[min(h-1,yy*h//hh)*w+min(w-1,xx*w//ww)] for yy in range(hh) for xx in range(ww))
                items.append(struct.pack('>hhHH',round((x-ax)*num/den),round((y-ay)*num/den),ww,hh)+indices)
                origin.append([clip,f])
        out[name]=b'ANM1'+struct.pack('>HHB',len(items),100,0)+palette+b''.join(items)
        evidence[name]=dict(source_frames=origin,scale=[num,den],anchor=[ax,ay])
    scaled('puzzles/basement-crates.sprite',[50,72],3,8,32,16)
    scaled('puzzles/basement-servant.sprite',[86,62],11,25,51,11)
    for name,clip in [('idle',13),('left',20),('right',21),('jump',24),('gun',53)]:
        scaled('puzzles/basement-chief-'+name+'.sprite',[clip],3,8,32,16)
    return out,dict(atlas_sha256=hashlib.sha256((ROOT/'assets/basement-platform-atlas.png').read_bytes()).hexdigest(),resources=list(out),stage=[256,176],crate_columns=4,lanes=['crates','safe'],original_sprites=evidence,controls={'2':'crate lane','8':'safe lane','4':'left','6':'right','5':'gun on safe lane','0':'back'})
