"""Recreate the pinned Opus input archive from the stable release and ffmpeg."""
from pathlib import Path
import sys,zipfile,hashlib,json,subprocess,struct
ROOT=Path(__file__).resolve().parents[1];sys.path.insert(0,str(ROOT));from audio_format_audit import ogg_packets
seen={};manifest={'config':'opus-audio-8k-12khz','clips':[]};entries={}
with zipfile.ZipFile(ROOT/'dist/Pilots-128x160.jar') as jar:
 for n in jar.namelist():
  if not n.endswith('.wav'):continue
  data=jar.read(n);sha=hashlib.sha256(data).hexdigest()
  if sha in seen:continue
  seen[sha]=n
  encoded=subprocess.run(['ffmpeg','-v','error','-i','pipe:0','-c:a','libopus','-application','audio','-ar','12000','-b:a','8000','-vbr','on','-f','ogg','pipe:1'],input=data,stdout=subprocess.PIPE,check=True).stdout
  packets,granule=ogg_packets(encoded,True);head=packets[0];assert head[9]==1 and int.from_bytes(head[10:12],'little')==312
  packets=packets[2:];assert all(p[0]==8 for p in packets)
  file=n.replace('/','-')[:-4]+'.packets';entries[file]=b'OPK1'+struct.pack('>I',len(packets))+b''.join(struct.pack('>H',len(p))+p for p in packets)
  manifest['clips'].append(dict(name=n,file=file,source_wav_sha256=sha,source_frames=len(data)-44,pre_skip=312,final_granule=granule))
entries['manifest.json']=json.dumps(manifest,indent=2).encode()
with zipfile.ZipFile(Path(__file__).parent/'input/opus-corpus.zip','w',compression=zipfile.ZIP_DEFLATED,compresslevel=9) as jar:
 for n,b in entries.items():jar.writestr(n,b)
