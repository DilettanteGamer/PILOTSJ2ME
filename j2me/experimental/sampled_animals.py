"""Exact screen-size indexed samples for three repeatedly animated AnimalScene clips."""
import struct
CLIPS={'scenes/04-53.anm','scenes/04-54.anm','scenes/04-111.anm'}
def resources(blob,metadata,rows,first_bank):
 ids=sorted({f[2] for c in metadata['clips'] if c['name'] in CLIPS for f in c['frames']})
 entries={};records=[];total=0
 for i in ids:
  w,h,offset,length=rows[i];source=blob[offset+4:offset+length];bank=bytearray()
  # 128x160 game area: 128x148, world 640x417, scaled width ceil(148*640/417)=228.
  # Endpoint rounding can produce either floor or ceil independently on each axis.
  for dw in sorted({max(1,w*228//640),max(1,(w*228+639)//640)}):
   for dh in sorted({max(1,h*148//417),max(1,(h*148+416)//417)}):
    records.append((i,dw,dh,first_bank+len(entries),len(bank)))
    bank.extend(source[(y*h//dh)*w+x*w//dw] for y in range(dh) for x in range(dw))
  assert len(bank)<32768
  entries['tiles/b'+str(first_bank+len(entries))]=struct.pack('>i',-len(bank))+bank;total+=len(bank)
 entries['tiles/sampled']=struct.pack('>H',len(records))+b''.join(struct.pack('>HHHHH',*r) for r in records)
 return entries,dict(clips=sorted(CLIPS),images=len(ids),variants=len(records),banks=len(ids),raw_bytes=total,viewport=[128,148],world=[640,417],exact_nearest_neighbour=True,original_frames_retained=True)

def patch(name,text):
 if name!='AnimationClip.java':return text
 old='''        if(!ensurePixels())return;
        raster.clear();'''
 new='''        byte[] sampled=null;
        if(deferred&&tiled && RasterCache.allows(rasterWidth*rasterHeight*2)) {
            try {sampled=TileStore.sampled(tileImage,drawWidth,drawHeight,samplePixels);if(sampled!=null)samplePixels=sampled;}
            catch(IOException failure){pixelFailure=failure;return;}
        }
        if(sampled==null && !ensurePixels())return;
        raster.clear();'''
 assert text.count(old)==1;text=text.replace(old,new)
 text=text.replace('private IOException pixelFailure;','private IOException pixelFailure;\n    private byte[] samplePixels;')
 old='''                sampleColumns=PixelSampling.columns(sampleColumns,width,drawWidth,rasterX,rasterWidth);
                for(int yy=0;yy<rasterHeight;yy++) {
                    int sourceRow=(rasterY+yy)*height/drawHeight*width;
                    for(int xx=0;xx<rasterWidth;xx++)
                        rgb[yy*rasterWidth+xx]=palette[pixels[sourceRow+sampleColumns[xx]]&255];
                }'''
 new='''                if(sampled!=null) {
                    for(int yy=0;yy<rasterHeight;yy++)for(int xx=0;xx<rasterWidth;xx++)
                        rgb[yy*rasterWidth+xx]=palette[sampled[(rasterY+yy)*drawWidth+rasterX+xx]&255];
                } else {
                    sampleColumns=PixelSampling.columns(sampleColumns,width,drawWidth,rasterX,rasterWidth);
                    for(int yy=0;yy<rasterHeight;yy++) {
                        int sourceRow=(rasterY+yy)*height/drawHeight*width;
                        for(int xx=0;xx<rasterWidth;xx++)rgb[yy*rasterWidth+xx]=palette[pixels[sourceRow+sampleColumns[xx]]&255];
                    }
                }'''
 assert text.count(old)==1;text=text.replace(old,new)
 old='''        x1=Math.max(x1,view.paintLeft);x2=Math.min(x2,view.paintRight);'''
 assert text.count(old)==1;text=text.replace(old,'''        if(!ensurePixels())return;
'''+old)
 text=text.replace('encoded=null;','encoded=null;samplePixels=null;')
 return text
