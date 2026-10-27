#!/usr/bin/env python3
"""Offline CLDC shrink/preverify and exact MIDI bank format measurements."""
from pathlib import Path
import zipfile,io,hashlib,zlib,lzma,json,subprocess,tempfile,os,shutil,atexit
root=Path(__file__).resolve().parents[1];jar=root/'j2me/dist/Pilots-240x320.jar';source=zipfile.ZipFile(jar);temporary=tempfile.TemporaryDirectory(prefix='pilots-code-formats-');atexit.register(temporary.cleanup);tmp=Path(temporary.name)
classes={i.filename:source.read(i) for i in source.infolist() if i.filename.endswith('.class')}
with zipfile.ZipFile(tmp/'input.jar','w',compression=zipfile.ZIP_DEFLATED,compresslevel=9) as z:
 for n,b in classes.items():z.writestr(n,b)
cache=Path(os.environ.get('PILOTS_TOOL_CACHE','/workspace/pilots-tools/j2me-placement'));java=str(Path(os.environ['JAVA_HOME'])/'bin/java') if os.environ.get('JAVA_HOME') else shutil.which('java');cmd=[java,'-jar',str(cache/'proguard-base-6.2.2.jar'),'-injars',str(tmp/'input.jar'),'-outjars',str(tmp/'small.jar'),'-libraryjars',str(cache/'cldcapi11-2.0.4.jar'),'-libraryjars',str(cache/'midpapi20-2.0.4.jar'),'-microedition','-target','1.1','-dontoptimize','-keep','public class pilots.PilotsMidlet { public <init>(); protected void startApp(); protected void pauseApp(); protected void destroyApp(boolean); }','-printmapping',str(tmp/'mapping.txt')]
subprocess.run(cmd,check=True,stdout=(tmp/'proguard.log').open('w'),stderr=subprocess.STDOUT)
small=zipfile.ZipFile(tmp/'small.jar');report={'source_jar_sha256':hashlib.sha256(jar.read_bytes()).hexdigest(),'code':{'original_classes':len(classes),'original_zip_payload_bytes':sum(source.getinfo(n).compress_size for n in classes),'shrink_obfuscate_classes':len([n for n in small.namelist() if n.endswith('.class')]),'shrink_obfuscate_zip_payload_bytes':sum(i.compress_size for i in small.infolist() if i.filename.endswith('.class')),'cldc_preverify':'passed','runtime_tested':False,'note':'Offline ProGuard experiment; entry/callback rules and gameplay must be validated before release. No optimization pass enabled.'},'midi':{}}
tracks={};original=0
for info in source.infolist():
 if not info.filename.endswith('.mid'):continue
 b=source.read(info);original+=info.compress_size;tracks.setdefault(hashlib.sha256(b).digest(),b)
blob=b''.join(len(b).to_bytes(4,'big')+b for b in tracks.values());filters=[dict(id=lzma.FILTER_LZMA1,dict_size=262144,lc=3,lp=0,pb=2,mode=lzma.MODE_NORMAL,nice_len=128,mf=lzma.MF_BT4)];packed=lzma.compress(blob,format=lzma.FORMAT_RAW,filters=filters);assert lzma.decompress(packed,format=lzma.FORMAT_RAW,filters=filters)==blob
report['midi']={'original_files':len([n for n in source.namelist() if n.endswith('.mid')]),'unique_tracks':len(tracks),'original_zip_bytes':original,'unique_bank_zip_bytes':len(zlib.compress(blob,9)),'unique_bank_lzma_256k_bytes':len(packed),'exact_bytes':True,'note':'These are actual original standard MIDI bytes; no instrument/event/tempo reduction. Bank sizes exclude ID lookup/container overhead.'}
(root/'j2me/analysis/code-midi-format-audit.json').write_text(json.dumps(report,indent=2)+'\n');print(json.dumps(report,indent=2))
