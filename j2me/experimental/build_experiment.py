from pathlib import Path
import sys,os,zipfile,struct,json,lzma,subprocess,re,hashlib,time,pickle,shutil
sys.dont_write_bytecode=True
ROOT=Path(__file__).resolve().parents[1];sys.path.insert(0,str(ROOT))
from graphics_format_audit import collect_frames
from tile_format_audit import tile_bank
EXP=ROOT/'experimental';WORK=Path('/tmp/pilots-exp-build');WORK.mkdir(exist_ok=True);CACHE=Path('/workspace/pilots-tools/j2me-placement');JAVA=Path('/workspace/pilots-tools/jdk-21.0.8+9/bin/java')
filters=[dict(id=lzma.FILTER_LZMA1,dict_size=262144,lc=3,lp=0,pb=2,mode=lzma.MODE_NORMAL,nice_len=128,mf=lzma.MF_BT4)]
props=b'\x5d'+struct.pack('<I',262144)
def packed(raw):return props+lzma.compress(raw,format=lzma.FORMAT_RAW,filters=filters)
def resource(raw):return b'LZM1'+struct.pack('>I',len(raw))+packed(raw)
source=ROOT/'dist/Pilots-128x160.jar'
cache=WORK/('payload-'+hashlib.sha256(source.read_bytes()).hexdigest()+'.pkl')
audio_dir=WORK/'audio'
with zipfile.ZipFile(EXP/'input/opus-corpus.zip') as z:z.extractall(audio_dir)
if cache.exists():
 entries=pickle.loads(cache.read_bytes())
else:
 source=ROOT/'dist/Pilots-128x160.jar'
 with zipfile.ZipFile(source) as jar:entries={n:jar.read(n) for n in jar.namelist()}
 blob,metadata,rows=collect_frames(source);bank,maps,map_rows,stats=tile_bank(blob,rows,16,metadata)
 print('Tile bank',len(bank),stats,flush=True)
 entries['tiles/palettes']=b''.join(metadata['palettes'])
 for off in range(0,len(bank),262144):
  raw=bank[off:off+262144];entries['tiles/b'+str(off//262144)]=struct.pack('>I',len(raw))+packed(raw)
 for group in range((len(rows)+31)//32):
  payload=[]
  for w,h,offset,phase in map_rows[group*32:group*32+32]:
   assert phase==0;n=((w+15)//16)*((h+15)//16)*3;payload.append(maps[offset:offset+n])
  entries['tiles/m'+str(group)]=bytes([len(payload)])+b''.join(struct.pack('>H',len(p)) for p in payload)+b''.join(payload)
 for clip in metadata['clips']:
  data=b'ANM6'+struct.pack('>HHBB',clip['count'],clip['duration'],clip['transparent'],clip['palette'])
  for x,y,ident in clip['frames']:
   w,h,_,_=rows[ident];data+=struct.pack('>hhHHI',x,y,w,h,ident)
  entries[clip['name']]=data
 manifest=json.loads((audio_dir/'manifest.json').read_text());byhash={r['source_wav_sha256']:(i,r) for i,r in enumerate(manifest['clips'])}
 for name in list(entries):
  if name.endswith('.wav'):
   ident,row=byhash[hashlib.sha256(entries[name]).hexdigest()];dest='/sounds/'+str(ident)+'.opx'
   original=(audio_dir/row['file']).read_bytes();assert original[:4]==b'OPK1';count=struct.unpack_from('>I',original,4)[0];assert row['pre_skip']==312
   entries[dest[1:]]=b'OPU1'+struct.pack('>IIH',row['source_frames'],count,78)+original[8:]
   encoded=dest.encode();entries[name]=b'REF1'+struct.pack('>H',len(encoded))+encoded
  if name.endswith(('.p8','.bar','.mid')):entries[name]=resource(entries[name])
 cache.write_bytes(pickle.dumps(entries))
for path in (ROOT/'src').rglob('*.java'):
 text=path.read_text();text=re.sub(r'(?:this\.)?getClass\(\)\.getResourceAsStream\(', 'PackedResources.open(',text);text=re.sub(r'\w+\.class\.getResourceAsStream\(', 'PackedResources.open(',text)
 if path.name in ['OriginalZooRules.java','ZooSubtitles.java']:text=path.read_text()
 text=text.replace('"pilots-','"pilots-exp-')
 if path.name=='PlayerProgress.java':text=text.replace('return scene!=9 && (available(scene)||scene==8&&available(9));','return scene!=9 && valid(scene);')
 if path.name=='AnimationClip.java':
  text=text.replace('private boolean packed;','private boolean packed,tiled;').replace('packed=magic==0x414e4d33;','packed=magic==0x414e4d33;tiled=magic==0x414e4d36;').replace('magic != 1095650609 && !packed','magic != 1095650609 && !packed && !tiled').replace('byte[] colors=new byte[768];input.readFully(colors);','byte[] colors=new byte[768];if(tiled)TileStore.palette(input.readUnsignedByte(),colors);else input.readFully(colors);').replace('if(stored==0) this.input.readFully(this.pixels, 0, size);','if(tiled)TileStore.fill(input.readInt(),pixels,n,n2);\n            else if(stored==0) this.input.readFully(this.pixels, 0, size);')
 p=WORK/'source/pilots'/path.name;p.parent.mkdir(parents=True,exist_ok=True);p.write_text(text)
if (WORK/'classes').exists():shutil.rmtree(WORK/'classes')
sources=list((WORK/'source').rglob('*.java'))+list((EXP/'src').rglob('*.java'))
api=str(CACHE/'cldcapi11-2.0.4.jar')+':'+str(CACHE/'midpapi20-2.0.4.jar')
subprocess.run([str(JAVA),'-jar',str(CACHE/'ecj-4.6.1.jar'),'-proc:none','-encoding','UTF-8','-1.3','-target','1.1','-bootclasspath',api,'-classpath',str(EXP/'lib/concentus-silk-nb-cldc.jar'),'-d',str(WORK/'classes')]+list(map(str,sources)),check=True)
subprocess.run([str(JAVA),'-jar',str(CACHE/'proguard-base-6.2.2.jar'),'-injars',str(WORK/'classes'),'-outjars',str(WORK/'verified.jar'),'-libraryjars',str(CACHE/'cldcapi11-2.0.4.jar'),'-libraryjars',str(CACHE/'midpapi20-2.0.4.jar'),'-libraryjars',str(EXP/'lib/concentus-silk-nb-cldc.jar'),'-microedition','-target','1.1','-dontshrink','-dontoptimize','-dontobfuscate','-keep','class ** { *; }'],check=True)
entries={n:b for n,b in entries.items() if not n.endswith('.class')}
for path in [EXP/'lib/concentus-silk-nb-cldc.jar',WORK/'verified.jar']:
 with zipfile.ZipFile(path) as jar:entries.update({n:jar.read(n) for n in jar.namelist() if n.endswith('.class')})
entries['META-INF/LICENSE-CONCENTUS.txt']=(EXP/'CONCENTUS-LICENSE').read_bytes();entries['META-INF/LICENSE-LZMA.txt']=(EXP/'LZMA-LICENSE').read_bytes()
text=entries['META-INF/MANIFEST.MF'].decode();text=text.replace('0.26.1','0.27.0').replace('Pilots Engine 128x160','Pilots Experimental 128x160');entries['META-INF/MANIFEST.MF']=text.encode()
out=EXP/'dist';out.mkdir(exist_ok=True);target=out/'Pilots-Experimental-128x160.jar'
with zipfile.ZipFile(target,'w') as jar:
 for name,data in entries.items():jar.writestr(name,data,compress_type=zipfile.ZIP_STORED if name.startswith('tiles/b') or name.endswith(('.p8','.bar','.mid')) else zipfile.ZIP_DEFLATED,compresslevel=9)
attrs=dict(line.split(': ',1) for line in text.splitlines() if ': ' in line);attrs['MIDlet-Jar-URL']=target.name;attrs['MIDlet-Jar-Size']=str(target.stat().st_size);target.with_suffix('.jad').write_text('\n'.join(k+': '+v for k,v in attrs.items() if k!='Manifest-Version')+'\n')
print('Built',target,target.stat().st_size,flush=True)
