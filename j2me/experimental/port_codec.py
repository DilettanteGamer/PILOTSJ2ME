"""Retarget only the verified restricted decoder closure to CLDC APIs."""
from pathlib import Path
import zipfile,struct,subprocess,os
ROOT=Path(__file__).resolve().parent
CACHE=Path('/workspace/pilots-tools/j2me-placement');JAVA=Path('/workspace/pilots-tools/jdk-21.0.8+9/bin/java')
def patch(b):
 out=bytearray(b[:10]);p=10;i=1;count=int.from_bytes(b[8:10],'big')
 while i<count:
  tag=b[p];p+=1;out.append(tag)
  if tag==1:
   n=int.from_bytes(b[p:p+2],'big');p+=2;s=b[p:p+n];p+=n
   s=s.replace(b'java/util/Arrays',b'org/concentus/ArrayOps').replace(b'java/lang/AssertionError',b'org/concentus/CodecFailure')
   out.extend(struct.pack('>H',len(s))+s)
  else:
   n={3:4,4:4,5:8,6:8,7:2,8:2,9:4,10:4,11:4,12:4,15:3,16:2,18:4}[tag];out.extend(b[p:p+n]);p+=n
   if tag in(5,6):i+=1
  i+=1
 out.extend(b[p:]);return bytes(out)
work=Path('/tmp/pilots-cldc-nb');work.mkdir(exist_ok=True)
subprocess.run([str(JAVA),'-jar',str(CACHE/'ecj-4.6.1.jar'),'-proc:none','-1.3','-target','1.1','-bootclasspath',str(CACHE/'cldcapi11-2.0.4.jar'),'-d',str(work/'helpers')]+[str(p) for p in (ROOT/'src').rglob('*.java')],check=True)
with zipfile.ZipFile(ROOT/'vendor/concentus-silk-nb-desktop.jar') as source,zipfile.ZipFile(work/'patched.jar','w') as target:
 for n in source.namelist():
  if n.endswith('.class'):target.writestr(n,patch(source.read(n)))
 for p in (work/'helpers').rglob('*.class'):target.writestr(str(p.relative_to(work/'helpers')),p.read_bytes())
subprocess.run([str(JAVA),'-jar',str(CACHE/'proguard-base-6.2.2.jar'),'-injars',str(work/'patched.jar'),'-outjars',str(ROOT/'lib/concentus-silk-nb-cldc.jar'),'-libraryjars',str(CACHE/'cldcapi11-2.0.4.jar'),'-microedition','-target','1.1','-dontshrink','-dontoptimize','-dontobfuscate','-keep','class org.concentus.** { *; }'],check=True)
