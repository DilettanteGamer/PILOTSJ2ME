#!/usr/bin/env python3
"""Spatial lossless predictors and background image formats; offline experiments only."""
import argparse,hashlib,io,json,lzma,math,struct,sys,time,zipfile,zlib
from pathlib import Path
sys.dont_write_bytecode=True
import numpy as np
from PIL import Image
from graphics_format_audit import collect_frames
ROOT=Path(__file__).resolve().parent
FILTER=[dict(id=lzma.FILTER_LZMA1,dict_size=262144,lc=3,lp=0,pb=2,mode=lzma.MODE_NORMAL,nice_len=128,mf=lzma.MF_BT4)]

def sha(b):return hashlib.sha256(b).hexdigest()
def save(report,path):path.write_text(json.dumps(report,ensure_ascii=False,indent=2)+'\n')

def spatial(path,report,out):
 blob,metadata,rows=collect_frames(path)
 report['spatial_source']=dict(images=len(rows),raw_bytes=len(blob),sha256=sha(blob),clips=len(metadata['clips']),frames=metadata['frame_count'])
 for mode,axis in [('subtract_left',1),('subtract_above',0)]:
  transformed=bytearray();t=time.perf_counter()
  for w,h,offset,length in rows:
   raw=blob[offset+4:offset+length];pixels=np.frombuffer(raw,dtype=np.uint8).reshape(h,w);delta=pixels.copy()
   if axis==1:delta[:,1:]-=pixels[:,:-1]
   else:delta[1:,:]-=pixels[:-1,:]
   restored=np.cumsum(delta,axis=axis,dtype=np.uint32).astype(np.uint8)
   assert restored.tobytes()==raw
   transformed.extend(struct.pack('>HH',w,h));transformed.extend(delta.tobytes())
  transform_seconds=time.perf_counter()-t;t=time.perf_counter();packed=lzma.compress(transformed,format=lzma.FORMAT_RAW,filters=FILTER);encode_seconds=time.perf_counter()-t
  t=time.perf_counter();restored=lzma.decompress(packed,format=lzma.FORMAT_RAW,filters=FILTER);decode_seconds=time.perf_counter()-t;assert restored==transformed
  report['spatial'].append(dict(mode=mode,payload_bytes=len(packed),transform_seconds=transform_seconds,encode_seconds=encode_seconds,native_desktop_lzma_decode_seconds=decode_seconds,exact_every_frame=True,caveat='Sequential payload only; same original metadata/index required, not a random-access runtime.'))
  save(report,out);print(mode,len(packed),flush=True)

def lzma_p8_probe(raw):
 t=time.perf_counter();encoded=lzma.compress(raw,format=lzma.FORMAT_RAW,filters=FILTER);encode_seconds=time.perf_counter()-t
 t=time.perf_counter();decoded=lzma.decompress(encoded,format=lzma.FORMAT_RAW,filters=FILTER);decode_seconds=time.perf_counter()-t;assert decoded==raw
 return dict(format='lzma_p8_256k',payload_bytes=len(encoded),encode_seconds=encode_seconds,native_desktop_decode_seconds=decode_seconds,lossless=True,rgb_exact=True,p8_bytes_exact=True,mse=0.0,psnr_db=None)

def backgrounds(path,profile):
 records=[]
 with zipfile.ZipFile(path) as jar:
  for info in jar.infolist():
   if not info.filename.endswith('.p8'):continue
   raw=jar.read(info);assert raw[:4]==b'P8BG';w,h,ww,wh=struct.unpack_from('>HHHH',raw,4);pixels=raw[780:];assert len(pixels)==w*h
   pal=Image.frombytes('P',(w,h),pixels);pal.putpalette(raw[12:780]);rgb=pal.convert('RGB');rgb_raw=rgb.tobytes();original=np.asarray(rgb,dtype=np.int16)
   row=dict(name=info.filename,width=w,height=h,world_width=ww,world_height=wh,original_zip_bytes=info.compress_size,formats=[])
   for name,fmt,options,lossless in [('png_palette','PNG',dict(optimize=True),True),('webp_lossless','WEBP',dict(lossless=True,method=6),True),('jpeg_q85','JPEG',dict(quality=85,subsampling=0,optimize=True),False),('jpeg_q95','JPEG',dict(quality=95,subsampling=0,optimize=True),False),('webp_q80','WEBP',dict(quality=80,method=6),False),('webp_q95','WEBP',dict(quality=95,method=6),False),('avif_q85','AVIF',dict(quality=85,speed=6,subsampling='4:4:4',max_threads=1),False)]:
    image=pal if name=='png_palette' else rgb;target=io.BytesIO();t=time.perf_counter();image.save(target,format=fmt,**options);encoded=target.getvalue();encode_seconds=time.perf_counter()-t;t=time.perf_counter()
    decoded=Image.open(io.BytesIO(encoded)).convert('RGB');decoded.load();decode_seconds=time.perf_counter()-t
    exact=decoded.tobytes()==rgb_raw
    if lossless:assert exact,(info.filename,name)
    delta=np.asarray(decoded,dtype=np.int16)-original;mse=float(np.mean(delta.astype(np.float64)**2));psnr=None if mse==0 else 10*math.log10(255**2/mse)
    row['formats'].append(dict(format=name,payload_bytes=len(encoded),encode_seconds=encode_seconds,native_desktop_decode_seconds=decode_seconds,lossless=lossless,rgb_exact=exact,mse=mse,psnr_db=psnr))
    if profile=='240x320' and info.filename=='scenes/17.p8':
     dest=ROOT/'analysis/image-format-samples';dest.mkdir(exist_ok=True)
     (dest/('forest-'+name+'.'+{'PNG':'png','JPEG':'jpg','WEBP':'webp','AVIF':'avif'}[fmt])).write_bytes(encoded)
   row['formats'].append(lzma_p8_probe(raw))
   records.append(row)
 return records

def main():
 p=argparse.ArgumentParser(description=__doc__);p.add_argument('--out',type=Path,default=ROOT/'analysis/image-format-audit.json');p.add_argument('--skip-spatial',action='store_true');args=p.parse_args()
 report=dict(baseline='0.26.0',source_jar_sha256={p:sha((ROOT/'dist'/('Pilots-'+p+'.jar')).read_bytes()) for p in ('128x128','128x160','240x320')},spatial=[],backgrounds={},notes=['Lossless comparisons preserve every RGB pixel. Palette PNG may choose a different index layout internally; matching RGB is the displayed invariant for opaque backgrounds.','Lossy PSNR is not approval of visual quality; user has accepted lossy Opus, not lossy graphics.','Per-image payload sums exclude scene metadata/resource index/ZIP headers; original_zip_bytes is actual ZIP payload.','PNG is a candidate for native Image decoding; native JPEG/WebP/AVIF availability must be checked separately.','All timings are desktop native libraries, not Java CLDC timings.'])
 if not args.skip_spatial:spatial(ROOT/'dist/Pilots-240x320.jar',report,args.out)
 for profile in ('128x128','128x160','240x320'):
  records=backgrounds(ROOT/'dist'/('Pilots-'+profile+'.jar'),profile)
  report['backgrounds'][profile]=dict(files=records,original_zip_bytes=sum(r['original_zip_bytes'] for r in records),totals={f['format']:sum(next(x for x in r['formats'] if x['format']==f['format'])['payload_bytes'] for r in records) for f in records[0]['formats']})
  save(report,args.out);print(profile,report['backgrounds'][profile]['totals'],flush=True)
 print('Saved',args.out,flush=True)
if __name__=='__main__':main()
