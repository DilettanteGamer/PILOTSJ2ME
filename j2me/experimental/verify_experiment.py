"""Verify resource fidelity and produce fixtures for the desktop runtime tests."""
from pathlib import Path
import hashlib,json,struct,sys,zipfile
ROOT=Path(__file__).resolve().parents[1];sys.path.insert(0,str(ROOT));from animation_codec import decode_animation
work=Path('/tmp/pilots-exp-tests');work.mkdir(exist_ok=True)
with zipfile.ZipFile(ROOT/'dist/Pilots-128x160.jar') as jar:
 (work/'frames.properties').write_text('\n'.join(n+'='+hashlib.sha256(decode_animation(jar.read(n))[777:]).hexdigest() for n in jar.namelist() if n.endswith('.anm'))+'\n')
 seek_rows=[]
 for name in jar.namelist():
  if not name.endswith('.anm'):continue
  raw=decode_animation(jar.read(name));count=struct.unpack_from('>H',raw,4)[0];p=777;frames=[]
  for i in range(count):
   w,h=struct.unpack_from('>HH',raw,p+4);length=8+w*h;frames.append(hashlib.sha256(raw[p:p+length]).hexdigest());p+=length
  for i in [count-1,0,count//2,count-1]:seek_rows.append(name+','+str(i)+','+frames[i])
 (work/'seek.csv').write_text('\n'.join(seek_rows)+'\n')
 (work/'resources.csv').write_text('\n'.join(n+','+hashlib.sha256(jar.read(n)).hexdigest() for n in jar.namelist() if n.endswith(('.p8','.bar','.mid')))+'\n')
with zipfile.ZipFile(ROOT/'experimental/input/opus-corpus.zip') as jar:manifest=json.loads(jar.read('manifest.json'))
(work/'audio.csv').write_text('\n'.join(r['name']+','+str(r['source_frames']) for r in manifest['clips'])+'\n')
for name,data,size in [('overflow',bytes([130,1]),2),('literal',bytes([2,1]),2),('incomplete',bytes([128,1]),2),('size',b'1111',4)]:
 (work/('bad-rle-'+name+'.anm')).write_bytes(b'ANM3'+struct.pack('>HHB',1,100,0)+bytes(768)+struct.pack('>hhHHI',0,0,4,1,size)+data)
jar_path=ROOT/'experimental/dist/Pilots-Experimental-128x160.jar'
with zipfile.ZipFile(jar_path) as jar:
 assert jar.testzip() is None
 classes=[n for n in jar.namelist() if n.endswith('.class')]
 assert all(jar.read(n)[6:8]==b'\x00\x2d' for n in classes),'Class target must be 45/1.1'
 jad=jar_path.with_suffix('.jad').read_text();assert 'MIDlet-Jar-Size: '+str(jar_path.stat().st_size) in jad
 print('CRC, JAD, CLDC classfile targets passed; fixtures:',work)

with zipfile.ZipFile(jar_path) as jar,zipfile.ZipFile(ROOT/'dist/Pilots-128x160.jar') as source,zipfile.ZipFile(ROOT/'experimental/input/native-mp3.zip') as corpus:
 import io
 mapping=struct.unpack('>'+'h'*(18*64),jar.read('audio/native-map')[:18*64*2])
 native=[]
 for name in source.namelist():
  if name.endswith('.wav'):
   scene,cue=map(int,Path(name).stem.split('-'));ident=mapping[scene*64+cue]-1;assert ident>=0
   expected=corpus.read(hashlib.sha256(source.read(name)).hexdigest()+'.mp3');assert jar.read('sounds/'+str(ident)+'.mp3')==expected
   native.append(name+','+hashlib.sha256(expected).hexdigest())
 (work/'native-audio.csv').write_text('\n'.join(native)+'\n')
 assert not any(n.startswith(('org/concentus/','lzma/')) for n in jar.namelist())
 assert all(not jar.read(n).startswith(b'LZM1') for n in jar.namelist() if n.endswith(('.p8','.bar','.mid')))
