from pathlib import Path
import sys,os,zipfile,struct,json,lzma,subprocess,re,hashlib,time,pickle,shutil
sys.dont_write_bytecode=True
ROOT=Path(__file__).resolve().parents[1];sys.path.insert(0,str(ROOT))
from graphics_format_audit import collect_frames
from local_tiles import group_images
from instrument_debug import patch as debug_patch
from optimize_updates import patch as optimize_patch
EXP=ROOT/'experimental';WORK=Path('/tmp/pilots-exp-build');WORK.mkdir(exist_ok=True);CACHE=Path('/workspace/pilots-tools/j2me-placement');JAVA=Path('/workspace/pilots-tools/jdk-21.0.8+9/bin/java')
filters=[dict(id=lzma.FILTER_LZMA1,dict_size=262144,lc=3,lp=0,pb=2,mode=lzma.MODE_NORMAL,nice_len=128,mf=lzma.MF_BT4)]
props=b'\x5d'+struct.pack('<I',262144)
def packed(raw):return props+lzma.compress(raw,format=lzma.FORMAT_RAW,filters=filters)
def resource(raw):return b'LZM1'+struct.pack('>I',len(raw))+packed(raw)
source=ROOT/'dist/Pilots-128x160.jar'
fast_native=os.environ.get('PILOTS_FAST_NATIVE','1')=='1'
if fast_native and os.environ.get('PILOTS_NATIVE_TILES','1')!='1':raise SystemExit('Native profile requires native ZIP tiles; use PILOTS_FAST_NATIVE=0 for legacy LZMA/Opus')
cache=WORK/('local64k8-payload-'+hashlib.sha256(source.read_bytes()).hexdigest()+'.pkl')
audio_dir=WORK/'audio'
with zipfile.ZipFile(EXP/'input/opus-corpus.zip') as z:z.extractall(audio_dir)
if cache.exists():
 entries=pickle.loads(cache.read_bytes())
else:
 source=ROOT/'dist/Pilots-128x160.jar'
 with zipfile.ZipFile(source) as jar:entries={n:jar.read(n) for n in jar.namelist()}
 blob,metadata,rows=collect_frames(source)
 banks,image_maps=group_images(blob,rows)
 print('Local banks',len(banks),'raw bytes',sum(map(len,banks)),'largest',max(map(len,banks)),flush=True)
 entries['tiles/palettes']=b''.join(metadata['palettes'])
 for i,raw in enumerate(banks):entries['tiles/b'+str(i)]=struct.pack('>I',len(raw))+packed(raw)
 for group in range((len(rows)+31)//32):
  payload=image_maps[group*32:group*32+32]
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
if fast_native:
 subprocess.run([sys.executable,str(EXP/'prepare_native_audio.py')],check=True)
 mapping=[0]*(18*64);sound_ids={}
 with zipfile.ZipFile(source) as original:
  for name in list(entries):
   if name.endswith('.wav'):
    sha=hashlib.sha256(original.read(name)).hexdigest()
    if sha not in sound_ids:sound_ids[sha]=len(sound_ids)
    ident=sound_ids[sha];scene,cue=map(int,Path(name).stem.split('-'));mapping[scene*64+cue]=ident+1
    del entries[name];entries['sounds/'+str(ident)+'.mp3']=(Path('/tmp/pilots-native-mp3')/(sha+'.mp3')).read_bytes()
   elif name.endswith(('.p8','.bar','.mid')):entries[name]=original.read(name)
 for name in list(entries):
  if name.endswith('.opx'):del entries[name]
 entries['audio/native-map']=struct.pack('>'+'h'*len(mapping),*mapping)+struct.pack('>H',len(sound_ids))+b''.join(struct.pack('>I',(Path('/tmp/pilots-native-mp3')/(sha+'.mp3')).stat().st_size) for sha in sound_ids)
for path in (ROOT/'src').rglob('*.java'):
 text=path.read_text();text=re.sub(r'(?:this\.)?getClass\(\)\.getResourceAsStream\(', 'PackedResources.open(',text);text=re.sub(r'\w+\.class\.getResourceAsStream\(', 'PackedResources.open(',text)
 if path.name in ['OriginalZooRules.java','ZooSubtitles.java']:text=path.read_text()
 text=text.replace('"pilots-','"pilots-exp-')
 if path.name=='PlayerProgress.java':text=text.replace('return scene!=9 && (available(scene)||scene==8&&available(9));','return scene!=9 && valid(scene);')
 if fast_native and path.name=='GameAudio.java':text=text.replace('\"audio/x-wav\"','\"audio/mpeg\"')
 if path.name=='AnimationClip.java':
  text=text.replace('private boolean packed;','private boolean packed,tiled;').replace('packed=magic==0x414e4d33;','packed=magic==0x414e4d33;tiled=magic==0x414e4d36;').replace('magic != 1095650609 && !packed','magic != 1095650609 && !packed && !tiled').replace('byte[] colors=new byte[768];input.readFully(colors);','byte[] colors=new byte[768];if(tiled)TileStore.palette(input.readUnsignedByte(),colors);else input.readFully(colors);').replace('if(stored==0) this.input.readFully(this.pixels, 0, size);','if(tiled)TileStore.fill(input.readInt(),pixels,n,n2);\n            else if(stored==0) this.input.readFully(this.pixels, 0, size);')
 if path.name=='PilotsMidlet.java':
  text=text.replace('synchronized void tick(long l) throws IOException {','synchronized void tick(long l) throws IOException {\n            l=Math.min(l,100L);')
  text=text.replace('this.lastTick = l;\n                        }\n                        Thread.sleep(40L);','this.lastTick = l;\n                        }\n                        this.serviceRepaints();\n                        Thread.sleep(40L);')
  text=text.replace('WalkStreams.clear();','WalkStreams.clear();FrameRasters.clear();')
 if path.name=='AnimationClip.java':
  text=text.replace('private boolean packed,tiled;','private boolean packed,tiled;private int tileImage,tilePalette;')
  text=text.replace('if(tiled)TileStore.palette(input.readUnsignedByte(),colors);','if(tiled){tilePalette=input.readUnsignedByte();TileStore.palette(tilePalette,colors);}')
  text=text.replace('if(tiled)TileStore.fill(input.readInt(),pixels,n,n2);','if(tiled){tileImage=input.readInt();TileStore.fill(tileImage,pixels,n,n2);}')
  text=text.replace('Image cached=raster.get();','Image shared=tiled?FrameRasters.get(tileImage,tilePalette*256+transparent,drawWidth,drawHeight):null;\n        if(shared!=null){paintRaster(graphics,shared,x1,y1,x1-left,y1-top,cropWidth,cropHeight);return;}\n        Image cached=raster.get();')
  text=text.replace('raster.store(bitmap);','if(tiled && rasterX==0 && rasterY==0 && rasterWidth==drawWidth && rasterHeight==drawHeight)FrameRasters.put(tileImage,tilePalette*256+transparent,bitmap);else raster.store(bitmap);')
 text=optimize_patch(path.name,text)
 text=debug_patch(path.name,text)
 p=WORK/'source/pilots'/path.name;p.parent.mkdir(parents=True,exist_ok=True);p.write_text(text)
if (WORK/'classes').exists():shutil.rmtree(WORK/'classes')
sources=list((WORK/'source').rglob('*.java'))+list((EXP/'src').rglob('*.java'))
if fast_native:
 sources=[p for p in sources if p.name not in ('PackedResources.java','OpusWave.java') and 'lzma' not in p.parts and 'concentus' not in p.parts]+[EXP/'native_src/pilots/PackedResources.java']
api=str(CACHE/'cldcapi11-2.0.4.jar')+':'+str(CACHE/'midpapi20-2.0.4.jar')
subprocess.run([str(JAVA),'-jar',str(CACHE/'ecj-4.6.1.jar'),'-proc:none','-encoding','UTF-8','-1.3','-target','1.1','-bootclasspath',api,'-classpath',str(EXP/'lib/concentus-silk-nb-cldc.jar'),'-d',str(WORK/'classes')]+list(map(str,sources)),check=True)
subprocess.run([str(JAVA),'-jar',str(CACHE/'proguard-base-6.2.2.jar'),'-injars',str(WORK/'classes'),'-outjars',str(WORK/'verified.jar'),'-libraryjars',str(CACHE/'cldcapi11-2.0.4.jar'),'-libraryjars',str(CACHE/'midpapi20-2.0.4.jar'),'-libraryjars',str(EXP/'lib/concentus-silk-nb-cldc.jar'),'-microedition','-target','1.1','-dontshrink','-dontoptimize','-dontobfuscate','-keep','class ** { *; }'],check=True)
native_tiles=os.environ.get('PILOTS_NATIVE_TILES','1')=='1'
if native_tiles:
 for name in list(entries):
  if name.startswith('tiles/b'):
   raw=lzma.decompress(entries[name][9:],format=lzma.FORMAT_RAW,filters=filters);assert len(raw)==struct.unpack_from('>I',entries[name])[0];entries[name]=struct.pack('>i',-len(raw))+raw
if fast_native and native_tiles:
 from hybrid_frames import graphics as hybrid_graphics
 hybrid_cache=WORK/('hybrid316-group32k4-native-v2-'+hashlib.sha256(source.read_bytes()).hexdigest()+'.pkl')
 if hybrid_cache.exists():graphic_entries,graphic_stats=pickle.loads(hybrid_cache.read_bytes())
 else:
  graphic_entries,graphic_stats=hybrid_graphics(*collect_frames(source));hybrid_cache.write_bytes(pickle.dumps((graphic_entries,graphic_stats)))
 entries={n:b for n,b in entries.items() if not n.startswith(('tiles/b','tiles/m','tiles/d'))};entries.update(graphic_entries)
 (EXP/'dist/hybrid-graphics.json').write_text(json.dumps(graphic_stats,indent=2)+'\n');print('Hybrid graphics',graphic_stats,flush=True)
entries={n:b for n,b in entries.items() if not n.endswith('.class')}
for path in ([WORK/'verified.jar'] if fast_native else [EXP/'lib/concentus-silk-nb-cldc.jar',WORK/'verified.jar']):
 with zipfile.ZipFile(path) as jar:entries.update({n:jar.read(n) for n in jar.namelist() if n.endswith('.class')})
entries['META-INF/LICENSE-CONCENTUS.txt']=(EXP/'CONCENTUS-LICENSE').read_bytes();entries['META-INF/LICENSE-LZMA.txt']=(EXP/'LZMA-LICENSE').read_bytes()
text=entries['META-INF/MANIFEST.MF'].decode();text=text.replace('0.26.1','0.27.6').replace('Pilots Engine 128x160','Pilots Experimental 128x160');entries['META-INF/MANIFEST.MF']=text.encode()
out=EXP/'dist';out.mkdir(exist_ok=True);target=out/'Pilots-Experimental-128x160.jar'
with zipfile.ZipFile(target,'w') as jar:
 for name,data in entries.items():jar.writestr(name,data,compress_type=zipfile.ZIP_STORED if (name.startswith('tiles/b') and not native_tiles) or (not fast_native and name.endswith(('.p8','.bar','.mid'))) else zipfile.ZIP_DEFLATED,compresslevel=9)
attrs=dict(line.split(': ',1) for line in text.splitlines() if ': ' in line);attrs['MIDlet-Jar-URL']=target.name;attrs['MIDlet-Jar-Size']=str(target.stat().st_size);target.with_suffix('.jad').write_text('\n'.join(k+': '+v for k,v in attrs.items() if k!='Manifest-Version')+'\n')
print('Built',target,target.stat().st_size,flush=True)
