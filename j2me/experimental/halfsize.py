"""128x160 experiment: half-size large indexed cels, original logical geometry/hit masks."""
import struct
import numpy as np

def dimensions(w,h):
    return ((w+1)//2,(h+1)//2) if w>64 or h>64 else (w,h)

def graphics_source(blob,metadata,rows):
    reduced=bytearray();new_rows=[];masks=[];changed=0
    for w,h,offset,length in rows:
        src=np.frombuffer(blob,dtype=np.uint8,count=w*h,offset=offset+4).reshape(h,w)
        sw,sh=dimensions(w,h)
        data=src[np.arange(sh)*h//sh][:,np.arange(sw)*w//sw].tobytes()
        new_rows.append((sw,sh,len(reduced),4+len(data)))
        reduced.extend(struct.pack('>HH',sw,sh)+data)
        masks.append(np.packbits(src.reshape(-1)!=0,bitorder='big').tobytes())
        changed+=(sw,sh)!=(w,h)
    assert all(c['transparent']==0 for c in metadata['clips'])
    # Separate on-demand bit masks preserve the original pixel hit tests exactly.
    # Bounded banks avoid decompressing large masks when hovering over a small item.
    entries={};bank=bytearray();refs=[];bank_id=0
    for raw in masks:
        if bank and len(bank)+len(raw)>32768:
            entries['half/b'+str(bank_id)]=bytes(bank);bank_id+=1;bank=bytearray()
        refs.append((bank_id,len(bank)));bank.extend(raw)
    if bank:entries['half/b'+str(bank_id)]=bytes(bank)
    for group in range((len(refs)+31)//32):
        entries['half/m'+str(group)]=b''.join(struct.pack('>HH',*r) for r in refs[group*32:group*32+32])
    entries={n:struct.pack('>I',len(data))+data for n,data in entries.items()}
    return (bytes(reduced),metadata,new_rows),entries,dict(images=len(rows),reduced_images=changed,
        original_pixel_bytes=len(blob)-4*len(rows),reduced_pixel_bytes=len(reduced)-4*len(rows),
        exact_hit_mask_bytes=sum(map(len,masks)),hit_banks=bank_id+1,
        small_cels_preserved_max=[64,64],logical_geometry_unchanged=True)

def background(name,raw):
    # Native-size flight/sewer/puzzle art and every interface resource stay sharp.
    if not name.startswith('scenes/') or not name.endswith('.p8'):return raw
    assert raw[:4]==b'P8BG'
    w,h=struct.unpack_from('>HH',raw,4)
    if w<=128 and h<=160:return raw
    # The shipped small-phone backgrounds are ALREADY 320x208 (half original).
    # Bake the normal 128x148 viewport instead of reducing them a second time.
    ww,wh=struct.unpack_from('>HH',raw,8)
    if (ww,wh)!=(640,417):return raw
    sw,sh=228,148
    if w<=sw and h<=sh:return raw
    src=np.frombuffer(raw,dtype=np.uint8,offset=780).reshape(h,w)
    data=src[np.arange(sh)*h//sh][:,np.arange(sw)*w//sw].tobytes()
    return raw[:4]+struct.pack('>HH',sw,sh)+raw[8:780]+data

def patch(name,text):
    if name=='PilotsMidlet.java':return text.replace('WalkStreams.clear();','WalkStreams.clear();HalfPixels.clear();')
    if name!='AnimationClip.java':return text
    def replace(old,new):
        nonlocal text
        assert text.count(old)==1,(name,old)
        text=text.replace(old,new)
    replace('private int[] sampleColumns;','private int[] sampleColumns;\n    private int pixelWidth,pixelHeight;')
    replace('''            if (!(deferred&&tiled) && (this.pixels == null || this.pixels.length < n * n2)) {
                this.pixels = new byte[n * n2];
            }''','''            pixelWidth=tiled?HalfPixels.width(n,n2):n;
            pixelHeight=tiled?HalfPixels.height(n,n2):n2;
            if (!(deferred&&tiled) && (this.pixels == null || this.pixels.length < pixelWidth*pixelHeight)) {
                this.pixels = new byte[pixelWidth*pixelHeight];
            }''')
    replace('TileStore.fill(tileImage,pixels,n,n2);','TileStore.fill(tileImage,pixels,pixelWidth,pixelHeight);')
    replace('int size=width*height;if(pixels==null || pixels.length<size)pixels=new byte[size];','int size=pixelWidth*pixelHeight;if(pixels==null || pixels.length<size)pixels=new byte[size];')
    replace('TileStore.fill(tileImage,pixels,width,height);','TileStore.fill(tileImage,pixels,pixelWidth,pixelHeight);')
    replace('return x>=0 && y>=0 && x<width && y<height && ensurePixels() && (pixels[y*width+x]&255)!=transparent;', '''if(x<0 || y<0 || x>=width || y>=height)return false;
        if(tiled)try{return HalfPixels.contains(tileImage,width,height,x,y);}catch(IOException failure){pixelFailure=failure;return false;}
        return ensurePixels() && (pixels[y*width+x]&255)!=transparent;''')
    # Keep the existing exact-size fast path for AnimalScene's three hot clips.
    # Every other scale, hit test and clip uses the reduced storage geometry.
    replace('PixelSampling.columns(sampleColumns,width,drawWidth,rasterX,rasterWidth)','PixelSampling.columns(sampleColumns,pixelWidth,drawWidth,rasterX,rasterWidth)')
    replace('(rasterY+yy)*height/drawHeight*width','(rasterY+yy)*pixelHeight/drawHeight*pixelWidth')
    replace('(sy - top) * height / drawHeight * width','(sy - top) * pixelHeight / drawHeight * pixelWidth')
    replace('(sx - left) * width / drawWidth','(sx - left) * pixelWidth / drawWidth')
    return text
