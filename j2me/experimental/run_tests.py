"""Desktop checks; no assertion of real-device MMAPI or KVM speed."""
from pathlib import Path
import subprocess,json
ROOT=Path(__file__).resolve().parents[1];EXP=ROOT/'experimental';JDK=Path('/workspace/pilots-tools/jdk-21.0.8+9/bin');API=Path('/workspace/pilots-tools/j2me-placement/midpapi20-2.0.4.jar');TEST=Path('/tmp/pilots-exp-tests');JAR=EXP/'dist/Pilots-Experimental-128x160.jar'
def run(args,log=None):
 if log:
  with open(log,'w') as out:subprocess.run(list(map(str,args)),check=True,stdout=out,stderr=subprocess.STDOUT)
 else:subprocess.run(list(map(str,args)),check=True)
run(['python3',EXP/'verify_experiment.py']);TEST.mkdir(exist_ok=True)
sources=list((EXP/'tests').glob('*.java'))+[ROOT/'tests'/n for n in ['AnimationDecodeTest.java','LocationEntryTest.java','BasementTestRoute.java','SewerWorldTest.java']]+list((ROOT/'tests/stubs').rglob('*.java'))
run([JDK/'javac','-encoding','UTF-8','-cp',str(JAR)+':'+str(API),'-d',TEST]+sources)
cp=str(TEST)+':'+str(JAR)+':'+str(API);base=[JDK/'java','-Xmx32m','-Djava.awt.headless=true','-Dpilots.test.width=128','-Dpilots.test.height=160','-cp',cp]
for cls,args,log in [('CodecPortTest',['/tmp/pilots-exp-build/audio'],'/tmp/exp-tests.log'),('pilots.PackedMediaTest',[TEST/'audio.csv'],'/tmp/exp-media-final.log'),('pilots.ResourceBytesTest',[TEST/'resources.csv'],'/tmp/exp-resources.log'),('pilots.LocationEntryTest',[],'/tmp/exp-entry-final.log'),('pilots.AnimationDecodeTest',[TEST/'frames.properties'],'/tmp/exp-animation-final.log'),('pilots.FramePerformance',[],'/tmp/exp-walking-final.log')]:run(base+[cls]+args,log)
r=json.loads((ROOT/'analysis/audio-original-cues.json').read_text());hashes=[r['scenes'][str(n)]['music']['event_sha256'] for n in [2,3,4,5,6,8,9,10,11,12,13,15,14,16,17]];run(base+['pilots.AudioTest']+hashes,'/tmp/exp-audio-lifecycle.log')
run([JDK/'java','-Xmx16m','-cp',str(TEST)+':'+str(ROOT/'dist/Pilots-128x160.jar')+':'+str(API),'pilots.FramePerformance'],'/tmp/base-walking.log');run(base+['pilots.ActionLatencyTest',EXP/'tests/action-latency-cases.txt'],'/tmp/latency-native.json');run(['python3',EXP/'write_report.py'])
