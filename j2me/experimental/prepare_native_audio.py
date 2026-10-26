import zipfile,hashlib,concurrent.futures,subprocess,json,sys
from pathlib import Path
root=Path(__file__).resolve().parents[1];out=Path('/tmp/pilots-native-mp3');out.mkdir(exist_ok=True)
archive=root/'experimental/input/native-mp3.zip'
if archive.exists() and '--rebuild' not in sys.argv:
 with zipfile.ZipFile(archive) as z:z.extractall(out)
 print('Pinned native MP3 corpus restored');sys.exit(0)
with zipfile.ZipFile(root/'dist/Pilots-128x160.jar') as z:
 inputs={hashlib.sha256(z.read(n)).hexdigest():z.read(n) for n in z.namelist() if n.endswith('.wav')}
def encode(row):
 key,wav=row;p=out/(key+'.mp3')
 if '--rebuild' in sys.argv or not p.exists():
  r=subprocess.run(['ffmpeg','-hide_banner','-loglevel','error','-i','pipe:0','-ac','1','-ar','11025','-c:a','libmp3lame','-b:a','16k','-write_xing','1','-id3v2_version','0','-map_metadata','-1','-y',str(p)],input=wav,check=True)
 return len(p.read_bytes())
with concurrent.futures.ThreadPoolExecutor(max_workers=6) as pool:sizes=list(pool.map(encode,inputs.items()))
print('MP3 unique',len(sizes),'bytes',sum(sizes))

with zipfile.ZipFile(archive,'w',compression=zipfile.ZIP_DEFLATED,compresslevel=9) as z:
 for key in inputs:z.write(out/(key+'.mp3'),key+'.mp3')
 z.writestr('source-shas.json',json.dumps(list(inputs)))
