#!/usr/bin/env python3
"""Measure existing release assets and reversible compression experiments.

This is an offline audit, not a J2ME decoder or a release packager. No game
resources are modified. --experiments requires ffmpeg, NumPy and zstandard.
"""
import argparse, collections, concurrent.futures, hashlib, io, json, lzma
from pathlib import Path
import struct, subprocess, sys, wave, zipfile, zlib
sys.dont_write_bytecode = True
from animation_codec import decode_animation

ROOT=Path(__file__).resolve().parent

def sha(b):return hashlib.sha256(b).hexdigest()
def category(n):
 for suffix,group in [('.anm','animation'),('.wav','audio_pcm'),('.mid','midi'),('.p8','background'),('.class','code')]:
  if n.endswith(suffix):return group
 return 'other'

def scan(path):
 groups={};unique={};duplicates=[]
 with zipfile.ZipFile(path) as jar:
  for info in jar.infolist():
   b=jar.read(info);group=groups.setdefault(category(info.filename),dict(files=0,raw_bytes=0,zip_bytes=0));group['files']+=1;group['raw_bytes']+=len(b);group['zip_bytes']+=info.compress_size
   h=sha(b)
   if h in unique:duplicates.append(dict(name=info.filename,original=unique[h],zip_bytes=info.compress_size))
   else:unique[h]=info.filename
 return dict(path=str(path.relative_to(ROOT)),jar_bytes=path.stat().st_size,jar_sha256=sha(path.read_bytes()),groups=groups,duplicate_zip_bytes=sum(d['zip_bytes'] for d in duplicates),duplicates=duplicates)

def ffmpeg(data,args):
 return subprocess.run(['ffmpeg','-v','error','-threads','1','-i','pipe:0']+args,input=data,stdout=subprocess.PIPE,stderr=subprocess.PIPE,check=True).stdout

def audio_trial(item):
 name,data,zip_bytes=item
 with wave.open(io.BytesIO(data)) as sound:
  channels,width,rate=sound.getnchannels(),sound.getsampwidth(),sound.getframerate();pcm=sound.readframes(sound.getnframes())
 assert (channels,width,rate)==(1,1,11025)
 flac=ffmpeg(data,['-c:a','flac','-compression_level','12','-metadata_header_padding','0','-f','flac','pipe:1'])
 recovered=ffmpeg(flac,['-f','u8','-acodec','pcm_u8','pipe:1']);assert recovered==pcm,'FLAC changed original PCM: '+name
 delta=bytes((v-(pcm[i-1] if i else 128))&255 for i,v in enumerate(pcm))
 lossy={}
 for bitrate in (8000,12000,16000,24000):
  opus=ffmpeg(data,['-c:a','libopus','-application','audio','-ar','12000','-b:a',str(bitrate),'-vbr','on','-f','ogg','pipe:1'])
  lossy[str(bitrate)]=len(opus)
 return dict(name=name,pcm_bytes=len(pcm),pcm_sha256=sha(pcm),zip_bytes=zip_bytes,delta_zip_bytes=len(zlib.compress(delta,9)),flac_bytes=len(flac),flac_pcm_exact=True,opus_ogg_bytes=lossy)

def experiments(path):
 import numpy as np
 import zstandard as zstd
 seen=set();audio=[];frames={};palettes=set();tot=collections.Counter();clips=[]
 with zipfile.ZipFile(path) as jar:
  for info in jar.infolist():
   if info.filename.endswith('.wav'):
    b=jar.read(info);h=sha(b)
    if h not in seen:seen.add(h);audio.append((info.filename,b,info.compress_size))
   if not info.filename.endswith('.anm'):continue
   encoded=jar.read(info);b=decode_animation(encoded);p=777;prev=None;delta=bytearray(b[:777]);palettes.add(b[9:777]);tot['clips']+=1
   for _ in range(struct.unpack_from('>H',b,4)[0]):
    header=b[p:p+8];ox,oy,w,h=struct.unpack('>hhHH',header);p+=8;f=b[p:p+w*h];p+=w*h;key=struct.pack('>HH',w,h)+f;digest=sha(key);tot['frames']+=1;tot['raw_frame_bytes']+=len(f)
    if digest not in frames:frames[digest]=key
    else:assert frames[digest]==key
    d=f
    if prev is not None:
     px,py,pw,ph,pf=prev;out=np.frombuffer(f,dtype=np.uint8).copy().reshape(h,w);x1=max(ox,px);x2=min(ox+w,px+pw);y1=max(oy,py);y2=min(oy+h,py+ph)
     if x1<x2 and y1<y2:out[y1-oy:y2-oy,x1-ox:x2-ox]^=np.frombuffer(pf,dtype=np.uint8).reshape(ph,pw)[y1-py:y2-py,x1-px:x2-px]
     d=out.tobytes()
    delta.extend(header+d);prev=(ox,oy,w,h,f)
   assert p==len(b)
   tot['existing_animation_zip_bytes']+=info.compress_size;tot['aligned_xor_zip_bytes']+=len(zlib.compress(delta,9))
   # Actual dictionary bound matters on CLDC; the codec has additional workspace.
   compressed=lzma.compress(encoded,format=lzma.FORMAT_ALONE,filters=[dict(id=lzma.FILTER_LZMA1,dict_size=65536,lc=3,lp=0,pb=2,mode=lzma.MODE_NORMAL,nice_len=128,mf=lzma.MF_BT4)])
   assert lzma.decompress(compressed,format=lzma.FORMAT_ALONE)==encoded
   tot['per_clip_lzma_64k_bytes']+=len(compressed)
   if tot['clips']%200==0:print('Animation audit:',tot['clips'],'clips',flush=True)
 tot['unique_frames']=len(frames);tot['unique_palettes']=len(palettes)
 blob=b''.join(frames.values());bank={}
 for dictionary in (65536,262144,1048576,8388608):
  # Optimistic bank: frame metadata/index/palettes are not included in this number.
  filters=[dict(id=lzma.FILTER_LZMA1,dict_size=dictionary,lc=3,lp=0,pb=2,mode=lzma.MODE_NORMAL,nice_len=128,mf=lzma.MF_BT4)]
  packed=lzma.compress(blob,format=lzma.FORMAT_ALONE,filters=filters)
  assert lzma.decompress(packed,format=lzma.FORMAT_ALONE)==blob
  bank[str(dictionary)]=len(packed);print('Frame bank LZMA dictionary',dictionary,'bytes:',len(packed),flush=True)
 zs=zstd.ZstdCompressor(level=19).compress(blob);assert zstd.ZstdDecompressor().decompress(zs)==blob
 print('Measuring',len(audio),'unique audio clips',flush=True)
 with concurrent.futures.ThreadPoolExecutor(max_workers=4) as pool:trials=list(pool.map(audio_trial,audio))
 totals={key:sum(t[key] for t in trials) for key in ('pcm_bytes','zip_bytes','delta_zip_bytes','flac_bytes')}
 totals['opus_ogg_bytes']={str(rate):sum(t['opus_ogg_bytes'][str(rate)] for t in trials) for rate in (8000,12000,16000,24000)}
 totals['unique_clips']=len(trials);totals['every_flac_decoded_to_exact_pcm']=True
 return dict(animations=dict(tot),frame_bank=dict(raw_bytes=len(blob),zlib_bytes=len(zlib.compress(blob,9)),lzma_by_dictionary=bank,zstd_bytes=len(zs),caveat='Offline aggregate only; excludes index/palettes/pivots and is not a playable container. Decoder dictionary is not its total heap requirement.'),audio=dict(totals=totals,clips=trials,lossy_caveat='Opus uses lossy resampling/encoding. Byte sizes are measurements, not evidence of equal sound quality or MIDP playback support.'))

def main():
 parser=argparse.ArgumentParser(description=__doc__);parser.add_argument('--experiments',action='store_true');parser.add_argument('--out',type=Path);args=parser.parse_args()
 report=dict(baseline='0.26.0',targets_decimal_bytes={'128x128':1000000,'128x160':2000000,'240x320':2000000},profiles={p:scan(ROOT/'dist'/('Pilots-'+p+'.jar')) for p in ('128x128','128x160','240x320')})
 if args.experiments:
  report['experiments']=experiments(ROOT/'dist/Pilots-240x320.jar')
  report['tools']=dict(python=sys.version.split()[0],zlib=zlib.ZLIB_VERSION,ffmpeg=subprocess.check_output(['ffmpeg','-version'],text=True).splitlines()[0])
 destination=args.out or ROOT/'analysis'/('compression-audit.json' if args.experiments else 'compression-inventory.json')
 destination.write_text(json.dumps(report,ensure_ascii=False,indent=2)+'\n');print('Saved',destination,flush=True)
if __name__=='__main__':main()
