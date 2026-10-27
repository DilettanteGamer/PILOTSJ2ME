#!/usr/bin/env python3
"""Measure exact indexed tile dictionaries, complete metadata and random access.

Offline experiments only: no game runtime or release archive is modified.
The global tile dictionary is tested at 4, 8 and 16 pixels. All pixel indices,
image dimensions, palettes, clip pivots and timings remain independently intact.
"""
import argparse,bisect,hashlib,io,json,lzma,struct,sys,time,zipfile,zlib
from pathlib import Path
sys.dont_write_bytecode=True
import numpy as np
from graphics_format_audit import collect_frames
ROOT=Path(__file__).resolve().parent

def sha(b):return hashlib.sha256(b).hexdigest()
def filters(dictionary=262144):return [dict(id=lzma.FILTER_LZMA1,dict_size=dictionary,lc=3,lp=0,pb=2,mode=lzma.MODE_NORMAL,nice_len=128,mf=lzma.MF_BT4)]
def compress(b,dictionary):return lzma.compress(b,format=lzma.FORMAT_RAW,filters=filters(dictionary))
def decompress(b,dictionary):return lzma.decompress(b,format=lzma.FORMAT_RAW,filters=filters(dictionary))

def tile_bank(blob,rows,size,metadata,alignment='frame_local'):
 first_pivots={}
 for clip in metadata['clips']:
  for x,y,ident in clip['frames']:
   if ident not in first_pivots:first_pivots[ident]=(x%size,y%size)
 start=time.perf_counter();tiles={};bank=bytearray();maps=bytearray();map_rows=[];reference_count=0;source_hash=hashlib.sha256();pixel_hash=hashlib.sha256()
 for i,(w,h,offset,length) in enumerate(rows):
  pixels=blob[offset+4:offset+length];assert len(pixels)==w*h
  left,top=first_pivots[i] if alignment=='native_first_pivot' else (0,0)
  tw,th=(w+left+size-1)//size,(h+top+size-1)//size
  original=np.frombuffer(pixels,dtype=np.uint8).reshape(h,w)
  padded=np.pad(original,((top,th*size-h-top),(left,tw*size-w-left)),constant_values=0)
  split=padded.reshape(th,size,tw,size).transpose(0,2,1,3).reshape(-1,size*size)
  ids=np.empty(len(split),dtype=np.uint32)
  for j,tile in enumerate(split):
   key=tile.tobytes();ident=tiles.get(key)
   if ident is None:ident=len(tiles);tiles[key]=ident;bank.extend(key)
   ids[j]=ident
  assert int(ids.max())<1<<24
  packed_ids=np.stack((ids>>16,(ids>>8)&255,ids&255),axis=1).astype(np.uint8).tobytes()
  map_rows.append((w,h,len(maps),left+(top<<4)));maps.extend(packed_ids);reference_count+=len(ids)
  # Reconstruct every pixel from tile data and IDs, not from the source image.
  reconstructed=np.frombuffer(bank,dtype=np.uint8).reshape(-1,size*size)[ids].reshape(th,tw,size,size).transpose(0,2,1,3).reshape(th*size,tw*size)[top:top+h,left:left+w].tobytes()
  assert reconstructed==pixels,(size,i,w,h)
  source_hash.update(pixels);pixel_hash.update(reconstructed)
  if i and i%2000==0:print('Tiling',size,i,'images,',len(tiles),'unique tiles',flush=True)
 return bytes(bank),bytes(maps),map_rows,dict(tile_pixels=size,alignment=alignment,unique_tiles=len(tiles),tile_references=reference_count,tile_bank_raw_bytes=len(bank),map_raw_bytes=len(maps),build_seconds=round(time.perf_counter()-start,4),every_image_exact=True,pixel_sha256=pixel_hash.hexdigest(),source_pixel_sha256=source_hash.hexdigest())

def base_index(metadata,map_rows,size,tile_count,bank_blocks,map_blocks,dictionary):
 out=bytearray(b'TILI'+struct.pack('>BBHIII',2,size,len(metadata['palettes']),len(metadata['clips']),len(map_rows),tile_count))
 for pal in metadata['palettes']:out.extend(pal)
 for clip in metadata['clips']:
  name=clip['name'].encode();out.extend(struct.pack('>H',len(name))+name+struct.pack('>HHBB',clip['count'],clip['duration'],clip['transparent'],clip['palette']))
  for frame in clip['frames']:out.extend(struct.pack('>hhI',*frame))
 for row in map_rows:out.extend(struct.pack('>HHIB',*row))
 out.extend(struct.pack('>I',dictionary))
 for blocks in (bank_blocks,map_blocks):
  out.extend(struct.pack('>I',len(blocks)))
  for offset,length in blocks:out.extend(struct.pack('>II',offset,length))
 out.extend(bytes.fromhex(metadata['source_frame_sha256']))
 return bytes(out)

def ranges(length,target):return [(offset,min(target,length-offset)) for offset in range(0,length,target)] if target else [(0,length)]
def percentile(values,p):return sorted(values)[min(len(values)-1,int(len(values)*p))]

def measured(bank,maps,map_rows,metadata,stats,target=0):
 size=stats['tile_pixels'];dictionary=min(262144,target) if target else 262144
 # Align boundaries to tile and uint24-ID sizes, respectively.
 bank_target=target//(size*size)*(size*size) if target else 0;map_target=target//3*3 if target else 0
 bank_blocks=ranges(len(bank),bank_target);map_blocks=ranges(len(maps),map_target)
 index=base_index(metadata,map_rows,size,stats['unique_tiles'],bank_blocks,map_blocks,dictionary)
 archive=io.BytesIO();encode_seconds=decode_seconds=0;worst_decode=0;payload=0
 with zipfile.ZipFile(archive,'w') as jar:
  jar.writestr('tiles/index.bin',index,compress_type=zipfile.ZIP_DEFLATED,compresslevel=9)
  for prefix,raw,blocks in [('pixels',bank,bank_blocks),('maps',maps,map_blocks)]:
   for n,(offset,length) in enumerate(blocks):
    chunk=raw[offset:offset+length];t=time.perf_counter();packed=compress(chunk,dictionary);encode_seconds+=time.perf_counter()-t
    t=time.perf_counter();recovered=decompress(packed,dictionary);elapsed=time.perf_counter()-t;decode_seconds+=elapsed;worst_decode=max(worst_decode,elapsed)
    assert recovered==chunk
    data=b'TILB'+struct.pack('>II',length,zlib.crc32(chunk))+packed;payload+=len(packed)
    jar.writestr('tiles/%s-%04d.blk'%(prefix,n),data,compress_type=zipfile.ZIP_STORED)
 zipped=archive.getvalue()
 with zipfile.ZipFile(io.BytesIO(zipped)) as jar:
  assert jar.testzip() is None and jar.read('tiles/index.bin')==index
  index_zip=jar.getinfo('tiles/index.bin').compress_size
 bank_starts=[p for p,_ in bank_blocks];map_starts=[p for p,_ in map_blocks];touched=[];read_sizes=[];frame_map_sizes=[];frame_needs=[]
 for w,h,offset,phase in map_rows:
  left,top=phase&15,phase>>4
  count=((w+left+size-1)//size)*((h+top+size-1)//size);packed=np.frombuffer(maps,dtype=np.uint8,count=count*3,offset=offset).reshape(-1,3).astype(np.uint32);ids=(packed[:,0]<<16)+(packed[:,1]<<8)+packed[:,2]
  if target:
   chunks=np.unique(ids.astype(np.uint64)*(size*size)//bank_target)
   pixel_bytes=sum(bank_blocks[int(n)][1] for n in chunks)
   first=bisect.bisect_right(map_starts,offset)-1;last=bisect.bisect_right(map_starts,offset+count*3-1)-1
   map_bytes=sum(map_blocks[n][1] for n in range(first,last+1));touched.append(len(chunks)+(last-first+1));frame_needs.append([('maps',n) for n in range(first,last+1)]+[('pixels',int(n)) for n in chunks])
  else:pixel_bytes=(int(ids.max())+1)*(size*size);map_bytes=offset+count*3;touched.append(2)
  read_sizes.append(pixel_bytes+map_bytes);frame_map_sizes.append(count*3)
 lru=[]
 if target:
  for capacity in [2,4,8]:
   frame_reads=[];misses=0
   for clip in metadata['clips']:
    cache=[];previous=-1
    for _,_,ident in clip['frames']:
     read=0
     # Retain the current decoded frame if an adjacent cel repeats its image.
     for kind,n in ([] if ident==previous else frame_needs[ident]):
      key=(kind,n)
      if key in cache:cache.remove(key)
      else:
       read+=(map_blocks if kind=='maps' else bank_blocks)[n][1];misses+=1
      cache.append(key)
      if len(cache)>capacity:cache.pop(0)
     previous=ident;frame_reads.append(read)
   lru.append(dict(block_capacity=capacity,decoded_cache_payload_bound_bytes=capacity*target,frames=len(frame_reads),block_misses=misses,decoded_bytes_total=sum(frame_reads),decoded_bytes_per_frame_p50=percentile(frame_reads,.5),decoded_bytes_per_frame_p95=percentile(frame_reads,.95),decoded_bytes_per_frame_max=max(frame_reads),note='Analytical per-clip LRU; reset between clips, one actor at a time, adjacent identical images reuse current pixel buffer. Not a device benchmark.'))
 headers=12*(len(bank_blocks)+len(map_blocks))
 return dict(tile_pixels=size,alignment=stats['alignment'],layout='independent_blocks' if target else 'sequential_banks',target_raw_block_bytes=target,lzma_dictionary_bytes=dictionary,tile_blocks=len(bank_blocks),map_blocks=len(map_blocks),payload_bytes=payload,index_raw_bytes=len(index),index_zip_bytes=index_zip,block_headers_bytes=headers,zip_headers_bytes=len(zipped)-payload-index_zip-headers,container_bytes=len(zipped),encode_seconds=round(encode_seconds,4),desktop_native_decode_seconds=round(decode_seconds,4),desktop_native_decode_mib_per_second=round((len(bank)+len(maps))/1048576/decode_seconds,3),desktop_worst_block_decode_ms=round(worst_decode*1000,3),cold_frame_raw_decode_bytes_p50=percentile(read_sizes,.5),cold_frame_raw_decode_bytes_p95=percentile(read_sizes,.95),cold_frame_raw_decode_bytes_max=max(read_sizes),cold_frame_chunks_p50=percentile(touched,.5),cold_frame_chunks_p95=percentile(touched,.95),cold_frame_chunks_max=max(touched),largest_frame_map_bytes=max(frame_map_sizes),largest_raw_block_bytes=max(n for _,n in bank_blocks+map_blocks),clip_isolated_lru=lru,exact_roundtrip=True)

def main():
 parser=argparse.ArgumentParser(description=__doc__);parser.add_argument('--jar',type=Path,default=ROOT/'dist/Pilots-240x320.jar');parser.add_argument('--out',type=Path,default=ROOT/'analysis/tile-format-audit.json');parser.add_argument('--sizes',nargs='+',type=int,default=[4,8,16]);parser.add_argument('--aligned-only',action='store_true');args=parser.parse_args()
 blob,metadata,rows=collect_frames(args.jar)
 report=dict(source=dict(jar=str(args.jar),jar_sha256=sha(args.jar.read_bytes()),clips=len(metadata['clips']),frames=metadata['frame_count'],unique_images=len(rows),palettes=len(metadata['palettes']),original_unique_image_bank_bytes=len(blob)),definition=dict(tile='Row-major square tiles in frame-local coordinates; incomplete right/bottom tile is padded with zero and cropped on restoration.',map='Row-major uint24 tile IDs, dimensions, map byte offset and packed x/y origin phase for every unique image.',index='TILI2: tile size, palette bank, all original clip names/counts/durations/transparency/palette IDs/frame x/y and image refs, image dimensions/map offsets, LZMA dictionary, independent block boundaries, source frame fingerprint.',blocks='TILB: magic, raw length, CRC32, raw LZMA1 payload; index DEFLATE and blocks STORED in actual ZIP.',notes=['Offline bank, no CLDC decoder deployed.','Padding is outside restored image bounds and changes no visible or hit-test pixel.','Desktop native LZMA timings are not evidence of Java CLDC performance.','Cold-frame estimates exclude cache and describe decompressed bytes, not bytes of permanent heap.','A decoder needs its dictionary/workspace plus image pixels, current map and caches; no decoder needs the entire raw bank resident if independently blocked.','Sequential banks have unbounded costly prior decoding for random access; dictionary size alone is not a random-access memory solution.']),tiling=[],results=[])
 candidates={}
 variants=[(8,'native_first_pivot')] if args.aligned_only else [(n,'frame_local') for n in args.sizes]+[(8,'native_first_pivot')]
 for size,alignment in variants:
  bank,maps,map_rows,stats=tile_bank(blob,rows,size,metadata,alignment);report['tiling'].append(stats)
  result=measured(bank,maps,map_rows,metadata,stats);report['results'].append(result);print('Sequential',size,result['container_bytes'],flush=True)
  candidates[size,alignment]=(bank,maps,map_rows,stats)
  args.out.write_text(json.dumps(report,indent=2)+'\n')
 best_row=min(report['results'],key=lambda r:r['container_bytes']);best=best_row['tile_pixels'];best_alignment=best_row['alignment']
 bank,maps,map_rows,stats=candidates[best,best_alignment]
 for target in [65536,262144]:
  result=measured(bank,maps,map_rows,metadata,stats,target);report['results'].append(result);print('Independent blocks',best,target,result['container_bytes'],flush=True)
  args.out.write_text(json.dumps(report,indent=2)+'\n')
 report['best_sequential_tile_pixels']=best;report['best_sequential_alignment']=best_alignment
 args.out.write_text(json.dumps(report,indent=2)+'\n');print('Saved',args.out,flush=True)
if __name__=='__main__':main()
