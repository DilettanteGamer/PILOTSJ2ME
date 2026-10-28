"""Build the separate 0.27.24 reduced-art experiment; keep prior builds intact."""
from pathlib import Path
import hashlib,json,os,pickle,shutil,struct,subprocess,sys,zipfile
sys.dont_write_bytecode=True
EXP=Path(__file__).resolve().parent;ROOT=EXP.parent
sys.path.insert(0,str(ROOT))
from graphics_format_audit import collect_frames
from presentation_cuts import graphics_source as cuts
from halfsize import graphics_source,background,patch
from presented_audio import patch as audio_presentation_patch
from batch_background import patch as background_patch
from sewer_cache import patch as sewer_patch
from hybrid_frames import graphics
WORK=Path('/tmp/pilots-half-build');WORK.mkdir(exist_ok=True)
JDK=Path('/workspace/pilots-tools/jdk-21.0.8+9/bin');TOOLS=Path('/workspace/pilots-tools/j2me-placement')
current=EXP/'dist/half128/Pilots-Experimental-128x160.jar'
if current.exists():
    with zipfile.ZipFile(current) as z:old_manifest=z.read('META-INF/MANIFEST.MF').decode()
    if 'MIDlet-Version: 0.27.22' in old_manifest:
        old_archive=EXP/'dist/0.27.22';old_archive.mkdir(exist_ok=True)
        for filename in (current.name,current.with_suffix('.jad').name,'graphics.json','validation.json'):
            src=current.parent/filename;dest=old_archive/filename
            if dest.exists():assert dest.read_bytes()==src.read_bytes(),'Refuse to replace archived 0.27.22'
            else:shutil.copyfile(src,dest)
    if 'MIDlet-Version: 0.27.23' in old_manifest:
        old_archive=EXP/'dist/0.27.23';old_archive.mkdir(exist_ok=True)
        for filename in (current.name,current.with_suffix('.jad').name,'graphics.json','validation.json'):
            src=current.parent/filename;dest=old_archive/filename
            if dest.exists():assert dest.read_bytes()==src.read_bytes(),'Refuse to replace archived 0.27.23'
            else:shutil.copyfile(src,dest)
baseline=EXP/'dist/Pilots-Experimental-128x160.jar'
archive=EXP/'dist/0.27.21';archive.mkdir(exist_ok=True)
for suffix in ('.jar','.jad'):
    src=baseline.with_suffix(suffix);dest=archive/src.name
    if dest.exists():assert dest.read_bytes()==src.read_bytes(),'Refuse to replace archived 0.27.21'
    else:shutil.copyfile(src,dest)
saved={suffix:baseline.with_suffix(suffix).read_bytes() for suffix in ('.jar','.jad')}
try:subprocess.run([sys.executable,str(EXP/'build_experiment.py')],check=True)
finally:
    for suffix,raw in saved.items():baseline.with_suffix(suffix).write_bytes(raw)
with zipfile.ZipFile(baseline) as z:entries={n:z.read(n) for n in z.namelist()}
sample_map=bytearray(entries['tiles/sampled']);sample_banks={}
for i in range(struct.unpack_from('>H',sample_map)[0]):
    bank=struct.unpack_from('>H',sample_map,2+i*10+6)[0]
    sample_banks[bank]=entries['tiles/b'+str(bank)]
source=ROOT/'dist/Pilots-128x160.jar'
cache=WORK/('half-v1-'+hashlib.sha256(source.read_bytes()).hexdigest()+'.pkl')
if cache.exists():reduced,mask_entries,stats,graphic_entries,graphic_stats=pickle.loads(cache.read_bytes())
else:
    cut_source,_,_=cuts(*collect_frames(source))
    reduced,mask_entries,stats=graphics_source(*cut_source)
    graphic_entries,graphic_stats=graphics(*reduced)
    cache.write_bytes(pickle.dumps((reduced,mask_entries,stats,graphic_entries,graphic_stats)))
# ANM6 metadata keeps original dimensions/pivots/counts; only backing storage changes.
entries={n:b for n,b in entries.items() if not n.startswith(('tiles/b','tiles/m','tiles/d')) and not n.endswith('.class') and n!='tiles/sampled'}
entries.update(graphic_entries);entries.update(mask_entries)
first_bank=max(int(n[7:]) for n in graphic_entries if n.startswith('tiles/b'))+1
remap={old:first_bank+i for i,old in enumerate(sorted(sample_banks))}
for old,new in remap.items():entries['tiles/b'+str(new)]=sample_banks[old]
for i in range(struct.unpack_from('>H',sample_map)[0]):
    at=2+i*10+6;old=struct.unpack_from('>H',sample_map,at)[0];struct.pack_into('>H',sample_map,at,remap[old])
entries['tiles/sampled']=bytes(sample_map)
stats['retained_exact_screen_sample_variants']=struct.unpack_from('>H',sample_map)[0]
backgrounds=[]
for name,raw in list(entries.items()):
    if raw[:4]==b'P8BG':
        result=background(name,raw)
        if result!=raw:backgrounds.append(dict(name=name,before=len(raw)-780,after=len(result)-780))
        entries[name]=result
stats['backgrounds']=backgrounds;stats['hybrid']=graphic_stats
if (WORK/'source').exists():shutil.rmtree(WORK/'source')
for src in Path('/tmp/pilots-exp-build/source').rglob('*.java'):
    dest=WORK/'source'/src.relative_to('/tmp/pilots-exp-build/source');dest.parent.mkdir(parents=True,exist_ok=True)
    dest.write_text(sewer_patch(src.name,background_patch(src.name,audio_presentation_patch(src.name,patch(src.name,src.read_text())))))
extra_sources=[p for p in (EXP/'src').rglob('*.java') if p.name not in ('PackedResources.java','OpusWave.java') and 'lzma' not in p.parts and 'concentus' not in p.parts]
for src in extra_sources:
    dest=WORK/'source'/src.relative_to(EXP/'src');dest.parent.mkdir(parents=True,exist_ok=True);dest.write_text(sewer_patch(src.name,src.read_text()))
sources=list((WORK/'source').rglob('*.java'))+list((EXP/'half_src').rglob('*.java'))+[EXP/'native_src/pilots/PackedResources.java']
if (WORK/'classes').exists():shutil.rmtree(WORK/'classes')
api=str(TOOLS/'cldcapi11-2.0.4.jar')+':'+str(TOOLS/'midpapi20-2.0.4.jar')
subprocess.run([str(JDK/'java'),'-jar',str(TOOLS/'ecj-4.6.1.jar'),'-proc:none','-encoding','UTF-8','-1.3','-target','1.1','-bootclasspath',api,'-d',str(WORK/'classes')]+list(map(str,sources)),check=True)
subprocess.run([str(JDK/'java'),'-jar',str(TOOLS/'proguard-base-6.2.2.jar'),'-injars',str(WORK/'classes'),'-outjars',str(WORK/'verified.jar'),'-libraryjars',str(TOOLS/'cldcapi11-2.0.4.jar'),'-libraryjars',str(TOOLS/'midpapi20-2.0.4.jar'),'-microedition','-target','1.1','-dontshrink','-dontoptimize','-dontobfuscate','-keep','class ** { *; }'],check=True)
with zipfile.ZipFile(WORK/'verified.jar') as z:entries.update({n:z.read(n) for n in z.namelist() if n.endswith('.class')})
text=entries['META-INF/MANIFEST.MF'].decode().replace('0.27.21','0.27.24')
entries['META-INF/MANIFEST.MF']=text.encode()
out=EXP/'dist/half128';out.mkdir(exist_ok=True);target=out/baseline.name
with zipfile.ZipFile(target,'w') as z:
    for name,raw in entries.items():z.writestr(name,raw,compress_type=zipfile.ZIP_DEFLATED,compresslevel=9)
attrs=dict(line.split(': ',1) for line in text.splitlines() if ': ' in line)
attrs.update({'MIDlet-Jar-URL':target.name,'MIDlet-Jar-Size':str(target.stat().st_size)})
target.with_suffix('.jad').write_text('\n'.join(k+': '+v for k,v in attrs.items() if k!='Manifest-Version')+'\n')
stats.update(version='0.27.24',jar_bytes=target.stat().st_size,baseline_jar_bytes=baseline.stat().st_size,baseline_sha256=hashlib.sha256(baseline.read_bytes()).hexdigest(),jar_sha256=hashlib.sha256(target.read_bytes()).hexdigest(),audio_waits_for_full_paint=True,background_scratch_pixel_limit=1024,sewer_deferred_pixels=True,sewer_raster_key_slots=192)
stats.update(sewer_raster_pixel_partition=45056,normal_raster_pixel_partition=32768,shared_raster_pixel_ceiling=65536)
(out/'graphics.json').write_text(json.dumps(stats,indent=2)+'\n')
print('Built reduced-art experiment',target,target.stat().st_size,flush=True)
