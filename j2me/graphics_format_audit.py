#!/usr/bin/env python3
"""Lossless codec and random-access block audit for the complete animation bank.

No runtime or JAR release is changed. collect_frames() is reusable by other audits.
Desktop timings measure native Python libraries, not CLDC Java decoders.
"""
import argparse,bz2,hashlib,io,json,lzma,struct,sys,time,zipfile,zlib
from pathlib import Path
sys.dont_write_bytecode=True
from animation_codec import decode_animation
ROOT=Path(__file__).resolve().parent

def digest(b):return hashlib.sha256(b).hexdigest()

def collect_frames(path):
 """Return (concatenated unique width/height/pixels, metadata, frame_rows).

 frame_rows are (width,height,byte_offset,byte_length), in stable first-use order.
 metadata retains every clip's name, duration, transparent index, palette and
 original pivot/frame references. Shared pixel indices never imply shared palettes.
 """
 images=[];rows=[];ids={};palettes=[];pal_ids={};clips=[];offset=0;source_hash=hashlib.sha256();frame_count=0
 with zipfile.ZipFile(path) as jar:
  for name in jar.namelist():
   if not name.endswith('.anm'):continue
   raw=decode_animation(jar.read(name));count,duration,transparent=struct.unpack_from('>HHB',raw,4);pal=raw[9:777]
   if pal not in pal_ids:pal_ids[pal]=len(palettes);palettes.append(pal)
   p=777;refs=[]
   for _ in range(count):
    header=raw[p:p+8];x,y,w,h=struct.unpack('>hhHH',header);p+=8;pixels=raw[p:p+w*h];p+=w*h
    assert len(pixels)==w*h
    key=struct.pack('>HH',w,h)+pixels;sha=digest(key)
    if sha not in ids:
     ident=len(images);ids[sha]=ident;images.append(key);rows.append((w,h,offset,len(key)));offset+=len(key)
    else:
     ident=ids[sha];assert images[ident]==key
    refs.append((x,y,ident));source_hash.update(header);source_hash.update(pixels);frame_count+=1
   assert p==len(raw)
   clips.append(dict(name=name,count=count,duration=duration,transparent=transparent,palette=pal_ids[pal],frames=refs))
 blob=b''.join(images)
 metadata=dict(clips=clips,palettes=palettes,frame_count=frame_count,source_frame_sha256=source_hash.hexdigest(),raw_frame_bytes=sum(len(b)-4 for b in images))
 return blob,metadata,rows

def index_bytes(metadata,frame_rows,blocks,codec):
 out=bytearray(b'GFXI'+struct.pack('>HHI',1,len(metadata['palettes']),len(metadata['clips'])))
 for palette in metadata['palettes']:out.extend(palette)
 for clip in metadata['clips']:
  name=clip['name'].encode();out.extend(struct.pack('>H',len(name))+name+struct.pack('>HHBB',clip['count'],clip['duration'],clip['transparent'],clip['palette']))
  for frame in clip['frames']:out.extend(struct.pack('>hhI',*frame))
 out.extend(struct.pack('>I',len(frame_rows)))
 for w,h,offset,length in frame_rows:out.extend(struct.pack('>HHI',w,h,offset));assert length==w*h+4
 out.extend(struct.pack('>I',len(blocks)))
 for offset,length in blocks:out.extend(struct.pack('>II',offset,length))
 spec=json.dumps(codec,sort_keys=True,separators=(',',':')).encode();out.extend(struct.pack('>H',len(spec))+spec)
 out.extend(bytes.fromhex(metadata['source_frame_sha256']))
 return bytes(out)

def verify_index(data,metadata,frame_rows,blocks,codec):
 """Parse the binary manifest independently and compare all original metadata."""
 p=0
 def take(n):
  nonlocal p
  result=data[p:p+n];p+=n;assert len(result)==n;return result
 def values(fmt):return struct.unpack(fmt,take(struct.calcsize(fmt)))
 assert take(4)==b'GFXI'
 version,npal,nclips=values('>HHI');assert (version,npal,nclips)==(1,len(metadata['palettes']),len(metadata['clips']))
 for palette in metadata['palettes']:assert take(768)==palette
 for clip in metadata['clips']:
  n,=values('>H');assert take(n).decode()==clip['name']
  assert values('>HHBB')==(clip['count'],clip['duration'],clip['transparent'],clip['palette'])
  for frame in clip['frames']:assert values('>hhI')==tuple(frame)
 count,=values('>I');assert count==len(frame_rows)
 for w,h,offset,length in frame_rows:assert values('>HHI')==(w,h,offset)
 count,=values('>I');assert count==len(blocks)
 for row in blocks:assert values('>II')==tuple(row)
 n,=values('>H');assert json.loads(take(n))==codec
 assert take(32).hex()==metadata['source_frame_sha256'];assert p==len(data)
 return True

def verify_catalog(blob,metadata,rows,path):
 """Reconstruct every complete ANM1 (including palette/timing/pivots) exactly."""
 h=hashlib.sha256()
 with zipfile.ZipFile(path) as jar:
  for clip in metadata['clips']:
   raw=bytearray(b'ANM1'+struct.pack('>HHB',clip['count'],clip['duration'],clip['transparent'])+metadata['palettes'][clip['palette']])
   for x,y,ident in clip['frames']:
    w,height,offset,length=rows[ident];key=blob[offset:offset+length];assert struct.unpack_from('>HH',key)==(w,height)
    raw.extend(struct.pack('>hhHH',x,y,w,height)+key[4:])
   assert raw==decode_animation(jar.read(clip['name'])),clip['name'];h.update(raw)
 return h.hexdigest()

def brotli_native_encode(raw,quality,window):
 import ctypes,ctypes.util
 lib=ctypes.CDLL(ctypes.util.find_library('brotlienc'));lib.BrotliEncoderMaxCompressedSize.argtypes=[ctypes.c_size_t];lib.BrotliEncoderMaxCompressedSize.restype=ctypes.c_size_t
 lib.BrotliEncoderCompress.argtypes=[ctypes.c_int,ctypes.c_int,ctypes.c_int,ctypes.c_size_t,ctypes.c_void_p,ctypes.POINTER(ctypes.c_size_t),ctypes.c_void_p];lib.BrotliEncoderCompress.restype=ctypes.c_int
 size=ctypes.c_size_t(lib.BrotliEncoderMaxCompressedSize(len(raw)));out=ctypes.create_string_buffer(size.value)
 assert lib.BrotliEncoderCompress(quality,window,0,len(raw),ctypes.c_char_p(raw),ctypes.byref(size),out)==1
 return ctypes.string_at(out,size.value)

def brotli_native_decode(packed,expected_size):
 import ctypes,ctypes.util
 lib=ctypes.CDLL(ctypes.util.find_library('brotlidec'));lib.BrotliDecoderDecompress.argtypes=[ctypes.c_size_t,ctypes.c_void_p,ctypes.POINTER(ctypes.c_size_t),ctypes.c_void_p];lib.BrotliDecoderDecompress.restype=ctypes.c_int
 size=ctypes.c_size_t(expected_size);out=ctypes.create_string_buffer(expected_size)
 assert lib.BrotliDecoderDecompress(len(packed),ctypes.c_char_p(packed),ctypes.byref(size),out)==1
 assert size.value==expected_size;return ctypes.string_at(out,size.value)

def filters(codec):
 return [dict(id=lzma.FILTER_LZMA1 if codec['format']=='lzma1' else lzma.FILTER_LZMA2,dict_size=codec['dictionary'],lc=3,lp=0,pb=2,mode=lzma.MODE_NORMAL,nice_len=128,mf=lzma.MF_BT4)]

def encode(raw,codec):
 kind=codec['format']
 if kind.startswith('lzma'):return lzma.compress(raw,format=lzma.FORMAT_RAW,filters=filters(codec))
 if kind=='deflate':return zlib.compress(raw,9)
 if kind=='jar-deflate':return raw
 if kind=='bz2':return bz2.compress(raw,compresslevel=codec['level'])
 if kind=='zstd':
  import zstandard as zs
  params=zs.ZstdCompressionParameters.from_level(codec['level'],window_log=codec['window_log']);return zs.ZstdCompressor(compression_params=params).compress(raw)
 if kind=='brotli':
  try:
   import brotli
   return brotli.compress(raw,quality=codec['level'],lgwin=codec['window_log'])
  except ImportError:return brotli_native_encode(raw,codec['level'],codec['window_log'])
 raise ValueError(codec)

def decode(packed,codec,expected_size=None):
 kind=codec['format']
 if kind.startswith('lzma'):return lzma.decompress(packed,format=lzma.FORMAT_RAW,filters=filters(codec))
 if kind=='deflate':return zlib.decompress(packed)
 if kind=='jar-deflate':return packed
 if kind=='bz2':return bz2.decompress(packed)
 if kind=='zstd':
  import zstandard as zs
  return zs.ZstdDecompressor().decompress(packed)
 if kind=='brotli':
  try:
   import brotli
   return brotli.decompress(packed)
  except ImportError:return brotli_native_decode(packed,expected_size)
 raise ValueError(codec)

def blocks_for(blob,rows,target,mode):
 if mode=='sequential':return [(0,len(blob))]
 if mode=='split':return [(offset,min(target,len(blob)-offset)) for offset in range(0,len(blob),target)]
 assert mode=='whole_frames';blocks=[];start=size=0
 for w,h,offset,length in rows:
  if size and size+length>target:blocks.append((start,size));start=offset;size=0
  size+=length
 if size:blocks.append((start,size))
 return blocks

def measured_bank(blob,metadata,rows,codec,target=0,mode='sequential'):
 blocks=blocks_for(blob,rows,target,mode);packed=[];encode_seconds=decode_seconds=0;worst_decode=0;restored_hash=hashlib.sha256()
 for offset,length in blocks:
  raw=blob[offset:offset+length];t=time.perf_counter();encoded=encode(raw,codec);encode_seconds+=time.perf_counter()-t
  t=time.perf_counter();restored=decode(encoded,codec,len(raw));elapsed=time.perf_counter()-t;decode_seconds+=elapsed;worst_decode=max(worst_decode,elapsed)
  assert restored==raw;restored_hash.update(restored);packed.append(b'GFXB'+struct.pack('>II',length,zlib.crc32(raw))+encoded)
 assert restored_hash.hexdigest()==digest(blob)
 index=index_bytes(metadata,rows,blocks,codec)
 verify_index(index,metadata,rows,blocks,codec)
 archive=io.BytesIO();native=codec['format']=='jar-deflate';archive_start=time.perf_counter()
 with zipfile.ZipFile(archive,'w') as jar:
  jar.writestr('graphics/index.bin',index,compress_type=zipfile.ZIP_DEFLATED,compresslevel=9)
  for i,b in enumerate(packed):jar.writestr('graphics/%04d.blk'%i,b,compress_type=zipfile.ZIP_DEFLATED if native else zipfile.ZIP_STORED,compresslevel=9)
 if native:encode_seconds=time.perf_counter()-archive_start;decode_seconds=worst_decode=0
 with zipfile.ZipFile(io.BytesIO(archive.getvalue())) as jar:
  assert jar.testzip() is None;assert jar.read('graphics/index.bin')==index
  zip_index=jar.getinfo('graphics/index.bin').compress_size
  stored_block_bytes=sum(jar.getinfo('graphics/%04d.blk'%i).compress_size for i in range(len(packed)))
  if native:
   for i,expected in enumerate(packed):
    t=time.perf_counter();restored=jar.read('graphics/%04d.blk'%i);elapsed=time.perf_counter()-t;decode_seconds+=elapsed;worst_decode=max(worst_decode,elapsed);assert restored==expected
 total_payload=stored_block_bytes if native else sum(len(b)-12 for b in packed)
 # Locate each image's intersecting blocks; fixed splits may share a block or
 # require several blocks for one large frame. A tiny LRU can avoid re-decoding.
 import bisect
 starts=[b[0] for b in blocks];read_sizes=[];spans=[]
 for w,h,offset,length in rows:
  first=bisect.bisect_right(starts,offset)-1;last=bisect.bisect_right(starts,offset+length-1)-1
  read_sizes.append(sum(blocks[i][1] for i in range(first,last+1)));spans.append(last-first+1)
 read_sizes.sort()
 return dict(codec=codec,layout=mode,target_raw_block_bytes=target,blocks=len(blocks),largest_raw_block_bytes=max(n for _,n in blocks),payload_bytes=total_payload,index_raw_bytes=len(index),index_zip_bytes=zip_index,block_headers_bytes=0 if native else len(blocks)*12,size_accounting='native ZIP payload includes compressed GFXB headers' if native else 'codec payload excludes 12-byte GFXB headers',zip_headers_bytes=len(archive.getvalue())-stored_block_bytes-zip_index,container_bytes=len(archive.getvalue()),encode_seconds=round(encode_seconds,4),desktop_native_decode_seconds=round(decode_seconds,4),desktop_native_decode_mib_per_second=round(len(blob)/1048576/decode_seconds,2),desktop_worst_block_decode_ms=round(worst_decode*1000,3),per_image_decoded_bytes_p50=read_sizes[len(read_sizes)//2],per_image_decoded_bytes_p95=read_sizes[int(len(read_sizes)*.95)],per_image_decoded_bytes_max=max(read_sizes),max_blocks_for_one_image=max(spans),exact_roundtrip=True,index_roundtrip_exact=True,index_sha256=digest(index),blob_sha256=restored_hash.hexdigest())

def tool_versions():
 import ctypes,ctypes.util,zstandard
 info=dict(python=sys.version.split()[0],zlib=zlib.ZLIB_VERSION,zstandard_module=zstandard.__version__,zstd_library='.'.join(map(str,zstandard.ZSTD_VERSION)))
 for library,function,label in [('lzma','lzma_version_string','lzma_library')]:
  found=ctypes.util.find_library(library)
  if found:
   fn=getattr(ctypes.CDLL(found),function);fn.restype=ctypes.c_char_p;info[label]=fn().decode()
 found=ctypes.util.find_library('brotlienc')
 if found:
  fn=ctypes.CDLL(found).BrotliEncoderVersion;fn.restype=ctypes.c_uint32;version=fn();info['brotli_library']='%d.%d.%d'%((version>>24)&255,(version>>12)&4095,version&4095)
 return info

def main():
 p=argparse.ArgumentParser(description=__doc__);p.add_argument('--jar',type=Path,default=ROOT/'dist/Pilots-240x320.jar');p.add_argument('--out',type=Path,default=ROOT/'analysis/graphics-format-audit.json');p.add_argument('--quick',action='store_true');args=p.parse_args()
 blob,metadata,rows=collect_frames(args.jar)
 catalog_hash=verify_catalog(blob,metadata,rows,args.jar)
 report=dict(tools=tool_versions(),source=dict(path=str(args.jar),jar_sha256=digest(args.jar.read_bytes()),unique_images=len(rows),clips=len(metadata['clips']),frames=metadata['frame_count'],palettes=len(metadata['palettes']),unique_raw_bank_bytes=len(blob),largest_unique_image_bytes=max(r[3] for r in rows),source_frame_sha256=metadata['source_frame_sha256'],complete_anm_roundtrip_exact=True,complete_anm_sha256=catalog_hash),container_spec=dict(index='GFXI1: palette bank, UTF-8 names, clip count/duration/transparent/palette and original x/y/image references, image width/height/raw offsets, block boundaries, codec config, source fingerprint',block='GFXB: 4-byte magic, 4-byte raw length, 4-byte CRC32, compressed payload',jar='index DEFLATE; codec blocks STORED, native jar-deflate blocks ZIP_DEFLATED; includes real ZIP names/headers/CRC',caveats=['Experimental offline container, not a deployed CLDC decoder.','Native desktop library timings cannot predict Java CLDC speed.','Dictionary/window bounds exclude codec workspace, frame pixels, caches and VM.','Timings are single wall-clock passes on a shared desktop machine and are not device performance guarantees.','Sequential stream requires decoding prior data for random access; only independent block layouts bound this work.']),results=[])
 codecs=[]
 for kind in ['lzma1','lzma2']:
  for size in ([262144] if args.quick else [65536,131072,262144,524288]):codecs.append(dict(format=kind,dictionary=size))
 codecs += [dict(format='deflate',window=32768),dict(format='bz2',level=1),dict(format='zstd',level=9,window_log=18),dict(format='zstd',level=19,window_log=18)]
 try:
  import ctypes.util
  if ctypes.util.find_library('brotlienc') and ctypes.util.find_library('brotlidec'):codecs.extend([dict(format='brotli',level=5,window_log=18),dict(format='brotli',level=9,window_log=18)])
 except ImportError:pass
 def save():
  temporary=args.out.with_suffix(args.out.suffix+'.tmp');temporary.write_text(json.dumps(report,indent=2)+'\n');temporary.replace(args.out)
 for codec in codecs:
  result=measured_bank(blob,metadata,rows,codec);report['results'].append(result);save();print(codec,'sequential',result['container_bytes'],flush=True)
 for mode in ['split','whole_frames']:
  for target in [32768,65536,131072,262144]:
   codec=dict(format='lzma1',dictionary=min(262144,max(65536,target)));result=measured_bank(blob,metadata,rows,codec,target,mode);report['results'].append(result);save();print(codec,mode,target,result['container_bytes'],flush=True)
 for codec in [dict(format='deflate',window=32768),dict(format='zstd',level=19,window_log=18),dict(format='jar-deflate',window=32768)]:
  for target in [65536,262144]:
   result=measured_bank(blob,metadata,rows,codec,target,'split');report['results'].append(result);save();print(codec,'split',target,result['container_bytes'],flush=True)
 print('Saved',args.out,flush=True)
if __name__=='__main__':main()
