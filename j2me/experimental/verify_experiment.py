"""Verify resource fidelity and produce fixtures for the desktop runtime tests."""
from pathlib import Path
import hashlib,json,struct,sys,zipfile
ROOT=Path(__file__).resolve().parents[1];sys.path.insert(0,str(ROOT));from animation_codec import decode_animation
work=Path('/tmp/pilots-exp-tests');work.mkdir(exist_ok=True)
with zipfile.ZipFile(ROOT/'dist/Pilots-128x160.jar') as jar:
 (work/'frames.properties').write_text('\n'.join(n+'='+hashlib.sha256(decode_animation(jar.read(n))[777:]).hexdigest() for n in jar.namelist() if n.endswith('.anm'))+'\n')
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
