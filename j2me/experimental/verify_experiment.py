"""Verify resource fidelity and produce fixtures for the desktop runtime tests."""
from pathlib import Path
import hashlib,json,struct,sys,zipfile
ROOT=Path(__file__).resolve().parents[1];sys.path.insert(0,str(ROOT));from animation_codec import decode_animation
work=Path('/tmp/pilots-exp-tests');work.mkdir(exist_ok=True)
(work/'lazy-invalid.anm').write_bytes(b'ANM6'+struct.pack('>HHBBhhHHI',1,100,0,0,0,0,640,480,0))
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
 import io,wave
 mapping=struct.unpack('>'+'h'*(18*64),jar.read('audio/native-map')[:18*64*2])
 count=struct.unpack_from('>H',jar.read('audio/native-map'),18*64*2)[0];sizes=struct.unpack_from('>'+'i'*count,jar.read('audio/native-map'),18*64*2+2)
 native=[];pcm_assets={}
 for name in source.namelist():
  if name.endswith('.wav'):
   scene,cue=map(int,Path(name).stem.split('-'));ident=mapping[scene*64+cue]-1;assert ident>=0
   pcm=sizes[ident]<0;boosted=(scene,cue) in ((2,31),(3,0))
   if pcm and boosted:
    expected=jar.read('sounds/'+str(ident)+'.wav');original=source.read(name)
    assert expected[:44]==original[:44] and len(expected)==len(original)
    with wave.open(io.BytesIO(original)) as w:before=list(w.readframes(w.getnframes()))
    with wave.open(io.BytesIO(expected)) as w:after=list(w.readframes(w.getnframes()))
    assert len(before)==len(after) and all(a-128==(b-128)*16 for a,b in zip(after,before))
    assert min(after)==80 and max(after)==176,'Step clipped or unexpected gain'
   else:
    expected=source.read(name) if pcm else corpus.read(hashlib.sha256(source.read(name)).hexdigest()+'.mp3');assert jar.read('sounds/'+str(ident)+('.wav' if pcm else '.mp3'))==expected
   step=0 if scene in (3,6) else 3 if scene==9 else 31
   if (scene,cue) in ((2,31),(3,0)):assert pcm,'Step did not use original PCM'
   assert abs(sizes[ident])==len(expected)
   native.append(name+','+hashlib.sha256(expected).hexdigest()+','+('audio/x-wav' if pcm else 'audio/mpeg'))
   if pcm:
    if ident not in pcm_assets:
     with wave.open(io.BytesIO(expected)) as w:pcm_assets[ident]=dict(id=ident,bytes=len(expected),sample_rate=w.getframerate(),bits=w.getsampwidth()*8,channels=w.getnchannels(),duration_ms=round(w.getnframes()*1000/w.getframerate()),source_sha256=hashlib.sha256(source.read(name)).hexdigest(),packaged_sha256=hashlib.sha256(expected).hexdigest(),gain=16 if boosted else 1,aliases=[])
    pcm_assets[ident]['aliases'].append(name)
 (ROOT/'experimental/dist/pcm-steps.json').write_text(json.dumps(list(pcm_assets.values()),indent=2)+'\n')
 (work/'native-audio.csv').write_text('\n'.join(native)+'\n')
 assert not any(n.startswith(('org/concentus/','lzma/')) for n in jar.namelist())
 assert all(not jar.read(n).startswith(b'LZM1') for n in jar.namelist() if n.endswith(('.p8','.bar','.mid')))
