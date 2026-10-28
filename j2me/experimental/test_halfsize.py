"""Full reduced-resource verification and deterministic desktop comparison with 0.27.21.

No desktop milliseconds are presented as Nokia KVM/MMAPI measurements.
"""
from pathlib import Path
import hashlib,json,pickle,struct,subprocess,sys,zipfile
import numpy as np
sys.dont_write_bytecode=True
EXP=Path(__file__).resolve().parent;ROOT=EXP.parent;sys.path.insert(0,str(ROOT))
from graphics_format_audit import collect_frames
from presentation_cuts import graphics_source as cuts
from halfsize import dimensions,background
WORK=Path('/tmp/pilots-half-tests');WORK.mkdir(exist_ok=True)
JDK=Path('/workspace/pilots-tools/jdk-21.0.8+9/bin');API=Path('/workspace/pilots-tools/j2me-placement/midpapi20-2.0.4.jar')
JAR=EXP/'dist/half128/Pilots-Experimental-128x160.jar';BASE=EXP/'dist/0.27.21/Pilots-Experimental-128x160.jar'
def run(args,log):
    with open(WORK/(log+'.log'),'w') as out:subprocess.run(list(map(str,args)),check=True,stdout=out,stderr=subprocess.STDOUT)
source,_,_=cuts(*collect_frames(ROOT/'dist/Pilots-128x160.jar'));blob,meta,rows=source
with zipfile.ZipFile(JAR) as z,zipfile.ZipFile(BASE) as base:
    assert z.testzip() is None
    for name in z.namelist():
        if name.endswith('.class'):assert z.read(name)[6:8]==b'\x00\x2d'
        elif name.endswith('.anm'):assert z.read(name)==base.read(name),'Logical animation metadata changed'
        elif name.startswith(('sounds/','audio/')) or name.endswith(('.mid','.bar')) or name.startswith('ui/'):
            assert z.read(name)==base.read(name),name
        elif name.endswith('.p8'):assert z.read(name)==background(name,base.read(name)),name
    assert 'MIDlet-Jar-Size: '+str(JAR.stat().st_size) in JAR.with_suffix('.jad').read_text()
    samples=[];old_map=base.read('tiles/sampled');new_map=z.read('tiles/sampled')
    assert old_map[:2]==new_map[:2]
    for i in range(struct.unpack_from('>H',old_map)[0]):
        ident,w,h,old_bank,at=struct.unpack_from('>HHHHH',old_map,2+i*10)
        nident,nw,nh,new_bank,nat=struct.unpack_from('>HHHHH',new_map,2+i*10)
        assert (ident,w,h,at)==(nident,nw,nh,nat)
        raw=base.read('tiles/b'+str(old_bank));assert raw==z.read('tiles/b'+str(new_bank))
        samples.append(f'{ident},{w},{h},'+hashlib.sha256(raw[4+at:4+at+w*h]).hexdigest())
    (WORK/'samples.csv').write_text('\n'.join(samples)+'\n')
    for i,(w,h,offset,length) in enumerate(rows):
        m=z.read('half/m'+str(i//32));assert len(m)-4==struct.unpack_from('>I',m)[0]
        bank,at=struct.unpack_from('>HH',m,4+i%32*4)
        mask=z.read('half/b'+str(bank));assert len(mask)-4==struct.unpack_from('>I',mask)[0]
        expected=np.packbits(np.frombuffer(blob,dtype=np.uint8,count=w*h,offset=offset+4)!=0,bitorder='big').tobytes()
        assert mask[4+at:4+at+len(expected)]==expected,('hit mask',i)
fixtures=[]
for c in meta['clips']:
    data=hashlib.sha256();hits=hashlib.sha256()
    for x,y,i in c['frames']:
        w,h,offset,length=rows[i];sw,sh=dimensions(w,h)
        src=np.frombuffer(blob,dtype=np.uint8,count=w*h,offset=offset+4).reshape(h,w)
        data.update(struct.pack('>hhHH',x,y,w,h));data.update(src[np.arange(sh)*h//sh][:,np.arange(sw)*w//sw].tobytes())
        points=[(0,0),(w-1,h-1),(w//2,h//2),(-1,0),(w,0),(0,h)]+[(k*37%w,k*53%h) for k in range(16)]
        hits.update(bytes(int(0<=px<w and 0<=py<h and src[py,px]!=0) for px,py in points))
    fixtures.append(','.join(map(str,[c['name'],c['count'],c['duration'],data.hexdigest(),hits.hexdigest()])))
(WORK/'frames.csv').write_text('\n'.join(fixtures)+'\n')
del blob,source
sources=[p for p in (EXP/'tests').glob('*.java') if p.name not in ('CodecPortTest.java','PackedMediaTest.java')]+[p for p in (ROOT/'tests').glob('*.java') if p.name!='AudioTest.java']+list((ROOT/'tests/stubs').rglob('*.java'))
# The experimental profile deliberately unlocks every menu entry for phone tests.
# Adapt only those two release-menu expectations and the experimental RMS prefix.
menu=(ROOT/'tests/GraphicalMenuTest.java').read_text().replace('GraphicalMenuTest','HalfsizeMenuTest')
menu=menu.replace('length==2,"fresh locations expose future levels"','length==16,"experimental locations not unlocked"').replace('length==3,"visited location not unlocked"','length==16,"experimental location list changed"').replace('"pilots-port-v1"','"pilots-exp-port-v1"')
(WORK/'HalfsizeMenuTest.java').write_text(menu);sources.append(WORK/'HalfsizeMenuTest.java')
run([JDK/'javac','-encoding','UTF-8','-cp',str(JAR)+':'+str(API),'-d',WORK]+sources,'compile')
def java(jar,cls,args,log):
    run([JDK/'java','-Xmx32m','-Djava.awt.headless=true','-Dpilots.test.width=128','-Dpilots.test.height=160','-cp',str(WORK)+':'+str(jar)+':'+str(API),'pilots.'+cls]+args,log)
java(JAR,'HalfsizePixelsTest',[WORK/'frames.csv',WORK/'samples.csv'],'pixels')
checks=[('PresentationCutsTest',[]),('InstantLightTest',[]),('AudioMusicEndTest',[]),('AudioStartFailureTest',[]),('AudioReuseTest',[]),('SceneRouteWorkloadTest',[]),('ZooSceneTest',[]),('CourtyardSceneTest',[]),('AnimalWorldTest',[]),('AnimalSequenceRenderTest',[128,160,WORK/'animal']),('SwitchboardRenderTest',[128,160,WORK/'electrical']),('SwitchboardWorldTest',[]),('SewerRenderTest',[128,160,WORK/'sewer']),('FlightRenderTest',[128,160,WORK/'flight']),('FlightWorldTest',[]),('LocationEntryTest',[]),('PixelSamplingTest',[]),('FramePacingTest',[]),('DebugHudTest',[])]
for cls,args in checks:java(JAR,cls,args,cls)
extra_checks=[('HotelWorldTest',[ROOT/'tests/fixtures'/n for n in ['hotel-0.12.0-room.psv','hotel-0.13.2-final-start.psv','hotel-0.13.2-final-done.psv','hotel-0.13.2-final-hold.psv']]),('BathroomWorldTest',[]),('BasementWorldTest',[]),('RoofWorldTest',[]),('FinalWorldTest',[]),('HoverCoverageTest',[]),('BasementRenderTest',[128,160,WORK/'basement']),('RoofRenderTest',[128,160,WORK/'roof']),('FinalRenderTest',[128,160,WORK/'final']),('StreetRenderTest',[128,160,WORK/'street']),('HalfsizeMenuTest',[128,160,WORK/'menu'])]
for cls,args in extra_checks:java(JAR,cls,args,cls)
# Source rules/state must match despite deliberately different pixel samples.
for jar,label in [(BASE,'baseline'),(JAR,'half')]:java(jar,'ElectricalCacheTest',['reference'],'electrical-state-'+label)
baseline_state=(WORK/'electrical-state-baseline.log').read_text().split('state_sha256=')[1].split()[0]
assert baseline_state==(WORK/'electrical-state-half.log').read_text().split('state_sha256=')[1].split()[0]
for jar,label in [(BASE,'baseline'),(JAR,'half')]:java(jar,'HalfsizeBackgroundTest',[],'backgrounds-'+label)
background_hash=next(s for s in (WORK/'backgrounds-baseline.log').read_text().splitlines() if s.startswith('background_normal_scale_sha256='))
assert background_hash in (WORK/'backgrounds-half.log').read_text()
profiles=[];previews=EXP/'previews/half128';previews.mkdir(exist_ok=True)
for jar,label in [(BASE,'baseline'),(JAR,'half')]:
    for scene in (2,4,7,13):
        for mode in ('stationary','camera'):
            log=f'profile-{label}-{scene}-{mode}';java(jar,'LocationProfile',[scene,mode,400],log)
            row=json.loads(next(s[8:] for s in (WORK/(log+'.log')).read_text().splitlines() if s.startswith('PROFILE ')))
            row['build']=label;profiles.append(row)
    for scene,tick in [(2,100),(4,100),(6,0),(13,100)]:
        java(jar,'HalfsizePreview',[scene,tick,previews/f'{scene:02}-{label}.png'],f'preview-{label}-{scene}')
report=dict(note='Desktop JVM and MIDP/MMAPI test doubles, 32 MiB host heap; no device FPS/RAM claim.',tests=[c for c,a in checks+extra_checks]+['HalfsizePixelsTest','all packaged source alpha masks','original electrical state trace'],profiles=profiles)
report['normal_scale_backgrounds_identical']=background_hash
report.update(version='0.27.22',jar_sha256=hashlib.sha256(JAR.read_bytes()).hexdigest(),baseline_sha256=hashlib.sha256(BASE.read_bytes()).hexdigest())
(EXP/'dist/half128/validation.json').write_text(json.dumps(report,indent=2)+'\n')
print('PASS: complete reduced corpus, exact hit masks, routes, saves, audio lifecycle and profiles. Logs:',WORK)
