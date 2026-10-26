#!/usr/bin/env python3
"""Two-stage offline audio format comparison; never changes the game runtime/JAR.

Requires ffmpeg with libopus/libspeex/libmp3lame and libopencore-amrnb.
The 12-clip matrix compares alternatives, then 14 selected configurations are
measured on every one of the release's 327 unique WAV resources. All outputs
are decoded for structural/duration checks; lossy codecs are NOT bit-exact.
"""
import argparse, concurrent.futures, ctypes, ctypes.util, hashlib, io, json
from pathlib import Path
import re, struct, subprocess, sys, tempfile, time, wave, zipfile, zlib
sys.dont_write_bytecode=True
ROOT=Path(__file__).resolve().parent

CONFIGS=[]
def add(name,family,args,rate,full=False,**settings):
 CONFIGS.append(dict(id=name,family=family,args=args,rate=rate,full=full,settings=settings))
for mode in ['audio','voip']:
 for kbps in [4,6,8,10,12]:
  add('opus-%s-%dk-12khz'%(mode,kbps),'opus',['-c:a','libopus','-application',mode,'-ar','12000','-b:a',str(kbps*1000),'-vbr','on','-f','ogg'],12000,full=(mode=='audio' and kbps in [4,8] or mode=='voip' and kbps in [6,8]),bitrate=kbps*1000,application=mode,sample_rate=12000,vbr='on',frame_ms=20)
for rate in [8000,16000]:
 add('opus-audio-8k-%dkhz'%(rate//1000),'opus',['-c:a','libopus','-application','audio','-ar',str(rate),'-b:a','8000','-vbr','on','-f','ogg'],rate,True,bitrate=8000,application='audio',sample_rate=rate,vbr='on',frame_ms=20)
for frame in [40,60]:
 add('opus-voip-8k-12khz-%dms'%frame,'opus',['-c:a','libopus','-application','voip','-ar','12000','-b:a','8000','-vbr','on','-frame_duration',str(frame),'-f','ogg'],12000,frame==60,bitrate=8000,application='voip',sample_rate=12000,vbr='on',frame_ms=frame)
for mode in ['off','constrained']:
 add('opus-audio-8k-12khz-'+mode,'opus',['-c:a','libopus','-application','audio','-ar','12000','-b:a','8000','-vbr',mode,'-f','ogg'],12000,bitrate=8000,application='audio',sample_rate=12000,vbr=mode,frame_ms=20)
for kbps in [8,16,24]:
 add('mp3-%dk-11025hz'%kbps,'mp3',['-c:a','libmp3lame','-ar','11025','-b:a',str(kbps*1000),'-write_xing','1','-f','mp3'],11025,kbps in [8,16],bitrate=kbps*1000,sample_rate=11025,xing=True)
for rate,q in [(8000,2),(8000,4),(8000,6),(16000,4)]:
 add('speex-vbr-q%d-%dkhz'%(q,rate//1000),'speex',['-c:a','libspeex','-ar',str(rate),'-q:a',str(q),'-f','ogg'],rate,(rate,q) in [(8000,2),(8000,4),(16000,4)],quality=q,sample_rate=rate,vbr=True)
for mode,kbps in [(0,4750),(3,6700),(5,7950),(7,12200)]:
 add('amr-nb-%d'%kbps,'amr',[],8000,mode in [0,3],mode=mode,bitrate=kbps,sample_rate=8000,frame_ms=20)
add('aac-lc-16k-11025hz','aac',['-c:a','aac','-ar','11025','-b:a','16000','-f','adts'],11025,bitrate=16000,sample_rate=11025)
add('ima-adpcm-11025hz','adpcm',['-c:a','adpcm_ima_wav','-ar','11025','-f','wav'],11025,sample_rate=11025)

LIB=None
def amr_lib():
 global LIB
 if LIB is None:
  name=ctypes.util.find_library('opencore-amrnb')
  if not name:raise RuntimeError('libopencore-amrnb is required for AMR experiments')
  LIB=ctypes.CDLL(name);LIB.Encoder_Interface_init.argtypes=[ctypes.c_int];LIB.Encoder_Interface_init.restype=ctypes.c_void_p
  LIB.Encoder_Interface_Encode.argtypes=[ctypes.c_void_p,ctypes.c_int,ctypes.POINTER(ctypes.c_short),ctypes.POINTER(ctypes.c_ubyte),ctypes.c_int];LIB.Encoder_Interface_Encode.restype=ctypes.c_int
  LIB.Encoder_Interface_exit.argtypes=[ctypes.c_void_p]
 return LIB

def amr_encode(pcm,mode):
 lib=amr_lib();state=lib.Encoder_Interface_init(0)
 if not state:raise RuntimeError('AMR initialization failed')
 out=bytearray(b'#!AMR\n');frame=(ctypes.c_short*160)();encoded=(ctypes.c_ubyte*64)()
 try:
  for p in range(0,len(pcm),320):
   b=pcm[p:p+320].ljust(320,b'\0');ctypes.memmove(frame,b,320)
   size=lib.Encoder_Interface_Encode(state,mode,frame,encoded,0)
   if not 0<size<=64:raise RuntimeError('Invalid AMR encoded frame')
   out.extend(encoded[:size])
 finally:lib.Encoder_Interface_exit(state)
 return bytes(out)

def ogg_packets(data,with_granule=False):
 final_granule=0
 p=0;current=bytearray();packets=[]
 while p<len(data):
  assert data[p:p+4]==b'OggS';final_granule=int.from_bytes(data[p+6:p+14],'little');segments=data[p+26];laces=data[p+27:p+27+segments];p+=27+segments
  for n in laces:
   current.extend(data[p:p+n]);p+=n
   if n<255:packets.append(bytes(current));current.clear()
 assert not current
 return (packets,final_granule) if with_granule else packets

def opus_bank(data,source_frames):
 packets,granule=ogg_packets(data,True);head=packets[0];assert head.startswith(b"OpusHead") and head[18]==0 and head[9]==1
 assert packets[1].startswith(b"OpusTags");packets=packets[2:]
 header=b"OPB1"+struct.pack("<IIHhQHBB",source_frames,11025,int.from_bytes(head[10:12],"little"),int.from_bytes(head[16:18],"little",signed=True),granule,len(packets),head[8],head[9]);assert len(header)==28
 bank=header+b"".join(struct.pack("<H",len(packet))+packet for packet in packets)
 # This verifies packet preservation; no runtime decoder is claimed.
 p=28;restored=[]
 for _ in packets:
  size=struct.unpack_from("<H",bank,p)[0];p+=2;restored.append(bank[p:p+size]);p+=size
 assert p==len(bank) and restored==packets
 return bank,packets

def opus_toc_counts(packets):
 modes={};bands={}
 for packet in packets:
  f=packet[0]>>3;mode='silk' if f<12 else 'hybrid' if f<16 else 'celt'
  band='NB' if f<4 or 16<=f<20 else 'MB' if f<8 else 'WB' if f<12 or 20<=f<24 else 'SWB' if f<14 or 24<=f<28 else 'FB'
  modes[mode]=modes.get(mode,0)+1;bands[band]=bands.get(band,0)+1
 return modes,bands

def sha(data):return hashlib.sha256(data).hexdigest()
def run(command,data):
 result=subprocess.run(command,input=data,stdout=subprocess.PIPE,stderr=subprocess.PIPE)
 if result.returncode:raise RuntimeError('FFmpeg failed: '+result.stderr.decode(errors='replace')[-1500:])
 return result

def trial(item,configs,sample_dir=None):
 name,data=item
 with wave.open(io.BytesIO(data)) as reader:
  assert (reader.getnchannels(),reader.getsampwidth(),reader.getframerate())==(1,1,11025)
  source=reader.readframes(reader.getnframes());source_frames=len(source)
 result=[]
 with tempfile.TemporaryDirectory(prefix='pilots-audio-') as tmp:
  tmp=Path(tmp);command=['ffmpeg','-hide_banner','-v','error','-y','-threads','1','-filter_threads','1','-filter_complex_threads','1','-i','pipe:0']
  for config in configs:
   if config['family']=='amr':continue
   command+=['-map','0:a:0']+config['args']+['-threads','1',str(tmp/config['id'])]
  if any(c['family']=='amr' for c in configs):command+=['-map','0:a:0','-ac','1','-ar','8000','-c:a','pcm_s16le','-f','s16le','-threads','1',str(tmp/'amr-input.pcm')]
  started=time.perf_counter();run(command,data);encode_wall=time.perf_counter()-started
  amr_pcm=(tmp/'amr-input.pcm').read_bytes() if (tmp/'amr-input.pcm').exists() else None
  for config in configs:
   encoded=amr_encode(amr_pcm,config['settings']['mode']) if config['family']=='amr' else (tmp/config['id']).read_bytes()
   assert encoded
   started=time.perf_counter();decoded=run(['ffmpeg','-hide_banner','-v','info','-benchmark','-threads','1','-filter_threads','1']+(['-c:a','libspeex'] if config['family']=='speex' else [])+['-i','pipe:0','-ac','1','-ar','11025','-c:a','pcm_s16le','-f','s16le','-threads','1','pipe:1'],encoded);wall=time.perf_counter()-started
   assert len(decoded.stdout)%2==0 and decoded.stdout
   frames=len(decoded.stdout)//2;delta=(frames-source_frames)/11025
   # Container/encoder padding is recorded, never silently trimmed or called exact.
   assert -.1<=delta<=.5,(name,config['id'],delta)
   bench=re.search(r'bench: utime=([\d.]+)s stime=([\d.]+)s rtime=([\d.]+)s',decoded.stderr.decode(errors='replace'))
   row=dict(config=config['id'],bytes=len(encoded),jar_deflate_bytes=len(zlib.compress(encoded,9))-6,decoded_pcm16_bytes=len(decoded.stdout),decoded_frames_at_11025=frames,duration_error_ms=delta*1000,ffmpeg_decode_wall_seconds=wall,ffmpeg_benchmark_seconds=dict(user=float(bench[1]),system=float(bench[2]),real=float(bench[3])) if bench else None)
   if config['family']=='opus':
    bank,packets=opus_bank(encoded,source_frames)
    row['audio_packet_bytes']=sum(map(len,packets));row['audio_packets']=len(packets);row['opus_modes'],row['opus_coded_bandwidth']=opus_toc_counts(packets);row['custom_packet_bank_estimate_bytes']=len(bank);row['custom_packet_bank_jar_deflate_bytes']=len(zlib.compress(bank,9))-6
   if sample_dir:
    extension={'opus':'ogg','speex':'spx','mp3':'mp3','amr':'amr','aac':'aac','adpcm':'wav'}[config['family']]
    (sample_dir/(name[:-4].replace('/','-')+'-'+config['id']+'.'+extension)).write_bytes(encoded)
   result.append(row)
 return dict(name=name,wav_sha256=sha(data),source_frames=source_frames,duration_seconds=source_frames/11025,source_pcm8_bytes=source_frames,source_whole_pcm16_bytes=source_frames*2,multi_output_encode_wall_seconds=encode_wall,results=result)

def aggregate(rows,configs):
 result={}
 for config in configs:
  trials=[next(r for r in row['results'] if r['config']==config['id']) for row in rows]
  item=dict(clips=len(trials),bytes=sum(t['bytes'] for t in trials),jar_deflate_bytes=sum(t['jar_deflate_bytes'] for t in trials),duration_error_ms=dict(min=min(t['duration_error_ms'] for t in trials),max=max(t['duration_error_ms'] for t in trials),mean=sum(t['duration_error_ms'] for t in trials)/len(trials)),max_whole_clip_pcm16_bytes=max(t['decoded_pcm16_bytes'] for t in trials),ffmpeg_process_wall_seconds=sum(t['ffmpeg_decode_wall_seconds'] for t in trials))
  times=[t['ffmpeg_benchmark_seconds'] for t in trials if t['ffmpeg_benchmark_seconds']]
  if times:item['ffmpeg_benchmark_seconds']={key:sum(t[key] for t in times) for key in ['user','system','real']}
  if config['family']=='opus':
   item.update(audio_packet_bytes=sum(t['audio_packet_bytes'] for t in trials),custom_packet_bank_estimate_bytes=sum(t['custom_packet_bank_estimate_bytes'] for t in trials))
   if all('custom_packet_bank_jar_deflate_bytes' in t for t in trials):item['custom_packet_bank_jar_deflate_bytes']=sum(t['custom_packet_bank_jar_deflate_bytes'] for t in trials)
   if all('opus_modes' in t for t in trials):
    for key in ['opus_modes','opus_coded_bandwidth']:
     item[key]={}
     for t in trials:
      for value,count in t[key].items():item[key][value]=item[key].get(value,0)+count
  result[config['id']]=item
 return result

def main():
 parser=argparse.ArgumentParser(description=__doc__);parser.add_argument('--out',type=Path,default=ROOT/'analysis/audio-format-audit.json');parser.add_argument('--workers',type=int,default=4);parser.add_argument('--resume',action='store_true',help='Reuse matching validated results and measure only missing corpus configurations');args=parser.parse_args()
 jar_path=ROOT/'dist/Pilots-240x320.jar';unique={};by_name={}
 with zipfile.ZipFile(jar_path) as jar:
  for name in sorted(jar.namelist()):
   if not name.endswith('.wav'):continue
   data=jar.read(name);key=sha(data)
   if key not in unique:unique[key]=(name,data)
   by_name[name]=(name,data)
 assert len(unique)==327
 selected=['audio/02-06.wav','audio/02-34.wav','audio/03-20.wav','audio/05-21.wav','audio/13-12.wav','audio/15-15.wav','audio/17-36.wav','audio/03-23.wav','audio/07-12.wav','audio/06-25.wav','audio/16-25.wav','audio/14-14.wav']
 sample=[by_name[name] for name in selected];assert len({sha(data) for _,data in sample})==12
 sample_dir=ROOT/'analysis/audio-format-samples';sample_dir.mkdir(exist_ok=True)
 sample_config_ids={'speex-vbr-q2-8khz','opus-audio-4k-12khz','opus-audio-8k-12khz','opus-voip-6k-12khz','opus-voip-8k-12khz-60ms','amr-nb-4750','amr-nb-6700','speex-vbr-q4-8khz','mp3-8k-11025hz'}
 amr_lib()
 existing=None
 if args.resume and args.out.exists():
  existing=json.loads(args.out.read_text());assert existing['baseline_jar_sha256']==sha(jar_path.read_bytes())
  signature=lambda config:{k:v for k,v in config.items() if k!='full'}
  assert [signature(c) for c in existing['configs']]==[signature(c) for c in CONFIGS],'Configuration changed; rerun without --resume'
  assert existing['tools']['ffmpeg']==subprocess.check_output(['ffmpeg','-version'],text=True).splitlines()[0],'Tool changed; rerun without --resume'
 print('Full matrix:',len(CONFIGS),'configurations on',len(sample),'diverse clips',flush=True)
 if existing:matrix=existing['sample_matrix']['clips']
 else:
  with concurrent.futures.ThreadPoolExecutor(max_workers=args.workers) as executor:matrix=list(executor.map(lambda item:trial(item,CONFIGS),sample))
 full=[c for c in CONFIGS if c['full']];assert len(full)==14
 print('Full corpus:',len(full),'configurations on',len(unique),'unique clips',flush=True)
 previous={r['wav_sha256']:r for r in existing['full_corpus']['clips']} if existing else {}
 def extend(item):
  old=previous.get(sha(item[1]));seen={r['config'] for r in old['results']} if old else set();missing=[c for c in full if c['id'] not in seen]
  if not missing:return old
  new=trial(item,missing)
  if old:new['results']=old['results']+new['results'];new['multi_output_encode_wall_seconds']+=old['multi_output_encode_wall_seconds']
  return new
 corpus=[]
 with concurrent.futures.ThreadPoolExecutor(max_workers=args.workers) as executor:
  for index,row in enumerate(executor.map(extend,unique.values()),1):
   corpus.append(row)
   if index%25==0:print('Validated',index,'clips',flush=True)
 for name in ['audio/13-12.wav','audio/03-23.wav','audio/16-25.wav']:
  item=by_name[name];(sample_dir/(name[:-4].replace('/','-')+'-original.wav')).write_bytes(item[1]);trial(item,[c for c in CONFIGS if c['id'] in sample_config_ids],sample_dir)
 # Ready-to-play WAV comparisons use original duration, independently of codec padding.
 with wave.open(io.BytesIO(by_name['audio/13-12.wav'][1])) as reader:voice_frames=reader.getnframes()
 for encoded_path in sorted(sample_dir.glob('audio-13-12-*')):
  if encoded_path.name.endswith('-original.wav') or encoded_path.name.endswith('-decoded.wav'):continue
  decoder=['-c:a','libspeex'] if encoded_path.suffix=='.spx' else []
  target=sample_dir/(encoded_path.stem+'-decoded.wav')
  command=['ffmpeg','-hide_banner','-v','error','-y']+decoder+['-i',str(encoded_path),'-af','aresample=11025,apad=whole_len=%d,atrim=end_sample=%d'%(voice_frames,voice_frames),'-ac','1','-c:a','pcm_s16le',str(target)]
  subprocess.run(command,check=True)
  with wave.open(str(target)) as reader:assert reader.getnframes()==voice_frames and reader.getsampwidth()==2
 report=dict(baseline_jar_sha256=sha(jar_path.read_bytes()),baseline_version='0.26.0',unique_clips=327,source_sample_rate=11025,source_pcm_bits=8,configs=CONFIGS,sample_matrix=dict(selection=selected,aggregate=aggregate(matrix,CONFIGS),clips=matrix),full_corpus=dict(aggregate=aggregate(corpus,full),clips=corpus),tools=dict(ffmpeg=subprocess.check_output(['ffmpeg','-version'],text=True).splitlines()[0],amr_library=ctypes.util.find_library('opencore-amrnb'),python=sys.version.split()[0]),caveats=['All tested encoders are lossy; this audit does not assert perceptual transparency.','All outputs decoded successfully; duration padding is measured and would need explicit trimming/metadata in runtime.','Decode timings are native FFmpeg on desktop, include output resampling to 11025 Hz, and do not predict a Java CLDC decoder or phone MMAPI speed.','Whole-clip PCM sizes are not streaming requirements. A runtime must stream or bound buffers.','Custom Opus packet-bank estimates exclude global lookup tables, implementation and ZIP directory; they are not a J2ME implementation.','Native availability of any compressed codec must be checked on target phones.','Speex validation explicitly uses libspeex. FFmpeg7.1.5 built-in speex decoder doubled the decoded length of a tested 16kHz clip; libspeex decoded its duration correctly.'])
 args.out.write_text(json.dumps(report,ensure_ascii=False,indent=2)+'\n');print('Saved',args.out,flush=True)
if __name__=='__main__':main()
