"""Reproducible host comparison, explicitly not C1-02 emulation or predicted FPS."""
from pathlib import Path
import argparse,hashlib,json,subprocess,statistics
ROOT=Path(__file__).resolve().parents[1];EXP=ROOT/'experimental';JDK=Path('/workspace/pilots-tools/jdk-21.0.8+9/bin');API=Path('/workspace/pilots-tools/j2me-placement/midpapi20-2.0.4.jar');WORK=Path('/tmp/pilots-location-profile')
p=argparse.ArgumentParser();p.add_argument('--scenes',type=int,nargs='*');p.add_argument('--ticks',type=int);p.add_argument('--repeats',type=int);p.add_argument('--engine',choices=['jit','interpreter'],default='jit');args=p.parse_args()
config=json.loads((EXP/'profiles/nokia-c1-02.json').read_text());jar=EXP/'dist/Pilots-Experimental-128x160.jar';WORK.mkdir(exist_ok=True)
sources=list((ROOT/'tests/stubs').rglob('*.java'))+[EXP/'tests/LocationProfile.java']
subprocess.run([str(JDK/'javac'),'-encoding','UTF-8','-cp',str(jar)+':'+str(API),'-d',str(WORK)]+list(map(str,sources)),check=True)
results=[]
for scene in args.scenes or config['scene_ids']:
 for mode in config['modes']:
  for repeat in range(args.repeats or config['repeats']):
   cmd=[str(JDK/'java'),'-Xmx%dm'%config['host_heap_mib']]+[flag for flag in config['host_jvm_flags'] if args.engine=='interpreter' or flag!='-Xint']+['-Djava.awt.headless=true','-Dpilots.test.width=128','-Dpilots.test.height=160','-cp',str(WORK)+':'+str(jar)+':'+str(API),'pilots.LocationProfile',str(scene),mode,str(args.ticks or config['ticks'])]
   proc=subprocess.run(cmd,text=True,stdout=subprocess.PIPE,stderr=subprocess.STDOUT)
   log=WORK/f'{scene}-{mode}-{repeat}.log';log.write_text(proc.stdout)
   if proc.returncode:raise RuntimeError(f'Profile failed: {log}\n{proc.stdout}')
   result=json.loads(next(line[8:] for line in proc.stdout.splitlines() if line.startswith('PROFILE ')));result['repeat']=repeat;results.append(result)
   print(scene,mode,repeat,'bytes',result['graphics_bytes'],'banks',result['bank_reads'],'paint p95',result['paint_p95_ms'],flush=True)
report=dict(engine=args.engine,profile=config,jar_sha256=hashlib.sha256(jar.read_bytes()).hexdigest(),ticks_requested=args.ticks or config['ticks'],repeats=args.repeats or config['repeats'],results=results,summary=[])
for scene in args.scenes or config['scene_ids']:
 for mode in config['modes']:
  rows=[r for r in results if r['scene']==scene and r['mode']==mode];summary=dict(scene=scene,mode=mode)
  for k in rows[0]:
   if isinstance(rows[0][k],(int,float)) and k not in ('scene','repeat'):summary[k]=statistics.median(r[k] for r in rows)
  summary['graphics_bytes_per_tick']=summary['graphics_bytes']/summary['ticks'];summary['banks_per_tick']=summary['bank_reads']/summary['ticks'];summary['images_per_tick']=summary['image_allocations']/summary['ticks'];report['summary'].append(summary)
report['deterministic_resource_counters']=True if (args.repeats or config['repeats'])>1 else None
for scene in args.scenes or config['scene_ids']:
 for mode in config['modes']:
  rows=[r for r in results if r['scene']==scene and r['mode']==mode]
  for field in ['ticks','end_scene','graphics_bytes','bank_reads','resource_opens','image_allocations']:
   if any(r[field]!=rows[0][field] for r in rows):report['deterministic_resource_counters']=False
report['test_source_sha256']=hashlib.sha256((EXP/'tests/LocationProfile.java').read_bytes()).hexdigest()
report['host_flags']=[flag for flag in config['host_jvm_flags'] if args.engine=='interpreter' or flag!='-Xint']
if args.engine=='interpreter' and (EXP/'dist/location-profile-jit.json').exists():
 reference=json.loads((EXP/'dist/location-profile-jit.json').read_text())
 if reference['jar_sha256']==report['jar_sha256'] and reference['ticks_requested']==report['ticks_requested']:
  compared=[]
  for row in results:
   peers=[r for r in reference['results'] if r['scene']==row['scene'] and r['mode']==row['mode']]
   if peers:compared.append(all(all(peer[field]==row[field] for field in ['ticks','end_scene','graphics_bytes','bank_reads','resource_opens','image_allocations']) for peer in peers))
  report['resource_counts_match_jit']=all(compared) if len(compared)==len(results) else None
out=EXP/('dist/location-profile-'+args.engine+'.json');out.write_text(json.dumps(report,ensure_ascii=False,indent=2)+'\n');print('Report',out)
