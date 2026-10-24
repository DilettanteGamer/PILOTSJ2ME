#!/usr/bin/env python3
"""Slice the commissioned front-view atlas into indexed MIDP resources."""
from pathlib import Path
import hashlib,struct,io
from PIL import Image
from extract_hotel import extract as original_resources
ROOT=Path(__file__).resolve().parent

def extract(original):
    atlas=Image.open(ROOT/'assets/basement-platform-atlas.png').convert('RGB')
    result={};sprites=[]
    # Authored atlas cells, in native pixel coordinates. Magenta is reserved transparency.
    bounds=[(i*1254//4,644,(i+1)*1254//4,944) for i in range(4)]+[(i*1254//4,944,(i+1)*1254//4,1254) for i in range(4)]
    for box in bounds:
        crop=atlas.crop(box);rgba=crop.convert('RGBA');pixels=list(rgba.getdata())
        rgba.putdata([(r,g,b,0 if r>190 and b>190 and g<115 else 255) for r,g,b,a in pixels]);bbox=rgba.getbbox();rgba=rgba.crop(bbox)
        sprites.append(rgba)
    bg=atlas.crop((0,0,1254,640)).resize((256,176),Image.Resampling.NEAREST).quantize(colors=255)
    pal=bytes(bg.getpalette()[:768]).ljust(768,b"\0");result['puzzles/basement.p8']=b'P8BG'+struct.pack('>HHHH',256,176,256,176)+pal+bg.tobytes()
    def animation(images):
        # Shared per-clip palette, index zero stays transparent.
        strip=Image.new('RGB',(sum(im.width for im in images),max(im.height for im in images)),(255,0,255));x=0
        for im in images:strip.paste(im.convert('RGB'),(x,0));x+=im.width
        palette=strip.quantize(colors=254);rgb=palette.getpalette()[:762];p=bytes([255,0,255]+rgb+[0]*(765-len(rgb)))
        out=bytearray(b'ANM1'+struct.pack('>HHB',len(images),100,0)+p)
        for im in images:
            q=im.convert('RGB').quantize(palette=palette,dither=Image.Dither.NONE);indices=q.tobytes();alpha=im.getchannel('A').tobytes()
            data=bytes(v+1 if alpha[i]>=128 else 0 for i,v in enumerate(indices));w,h=im.size
            out.extend(struct.pack('>hhHH',-w//2,-h,w,h)+data)
        return bytes(out)
    def fit(im,w,h):
        scale=min(w/im.width,h/im.height);return im.resize((max(1,round(im.width*scale)),max(1,round(im.height*scale))),Image.Resampling.NEAREST)
    result['puzzles/basement-crates.sprite']=animation([fit(sprites[0],24,20)]+[fit(s,34,30) for s in sprites[1:4]])
    result['puzzles/basement-servant.sprite']=animation([fit(s,44,48) for s in sprites[4:]])
    source,_=original_resources(original,15)
    # Preserve Chief's original drawing, at a readable constant scale, with authored gait frames.
    for name,clip in [('idle',13),('left',20),('right',21),('jump',24),('gun',53)]:
        data=source['scenes/15-%d.anm'%(20000+clip if clip<46 else clip)];n=struct.unpack_from('>H',data,4)[0];palette=data[9:777];p=777;images=[]
        for f in range(n):
            x,y,w,h=struct.unpack_from('>hhHH',data,p);p+=8;im=Image.frombytes('P',(w,h),data[p:p+w*h]);p+=w*h;im.putpalette(palette)
            alpha=Image.frombytes('L',(w,h),bytes(0 if v==0 else 255 for v in im.tobytes()));im=im.convert('RGBA');im.putalpha(alpha);images.append(fit(im,36,30))
        result['puzzles/basement-chief-'+name+'.sprite']=animation(images)
    return result,dict(atlas_sha256=hashlib.sha256((ROOT/'assets/basement-platform-atlas.png').read_bytes()).hexdigest(),resources=list(result),stage=[256,176],crate_columns=4,lanes=['crates','safe'],controls={'2':'crate lane','8':'safe lane','4':'left','6':'right','5':'gun on safe lane','0':'back'})
