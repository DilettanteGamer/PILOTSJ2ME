"""Compare every quest tick's serialized state against the published 0.27.13 engine."""
from pathlib import Path
import subprocess,re
ROOT=Path(__file__).resolve().parents[1];EXP=ROOT/'experimental';WORK=Path('/tmp/pilots-exp-tests');JDK=Path('/workspace/pilots-tools/jdk-21.0.8+9/bin');API='/workspace/pilots-tools/j2me-placement/midpapi20-2.0.4.jar'
baseline=WORK/'animal-baseline-02713.jar'
baseline.write_bytes(subprocess.check_output(['git','show','7bd2b50:j2me/experimental/dist/Pilots-Experimental-128x160.jar'],cwd=ROOT.parent))
text=(ROOT/'tests/AnimalWorldTest.java').read_text().replace('AnimalWorldTest','AnimalWorldTrace')
text=re.sub(r'\b(world|original|replay)\.tick\(\)',r'traceTick(\1)',text)
text=text.replace('    static GameState state;', '''    static java.security.MessageDigest digest;
    static int ticks;
    static {try{digest=java.security.MessageDigest.getInstance("SHA-256");}catch(Exception e){throw new RuntimeException(e);}}
    static void traceTick(AnimalWorld w){w.tick();try{digest.update(w.state.encode());}catch(IOException e){throw new RuntimeException(e);}ticks++;}
    static GameState state;''')
text=text.replace('        System.out.println("AnimalWorld:', '''        String sha="";for(byte b:digest.digest())sha+=Integer.toHexString((b&255)|256).substring(1);
        System.out.println("trace_ticks="+ticks+" state_sha256="+sha);
        System.out.println("AnimalWorld:''')
source=WORK/'AnimalWorldTrace.java';source.write_text(text)
subprocess.run([str(JDK/'javac'),'-encoding','UTF-8','-cp',str(WORK)+':'+str(baseline)+':'+API,'-d',str(WORK),str(source)],check=True)
traces=[]
for label,jar in [('baseline',baseline),('current',EXP/'dist/Pilots-Experimental-128x160.jar')]:
 result=subprocess.check_output([str(JDK/'java'),'-Xmx32m','-cp',str(WORK)+':'+str(jar)+':'+API,'pilots.AnimalWorldTrace'],text=True)
 (WORK/('animal-trace-'+label+'.log')).write_text(result);traces.append(next(x for x in result.splitlines() if x.startswith('trace_ticks=')))
assert traces[0]==traces[1],traces
print('Full animal quest controller trace matches 0.27.13:',traces[0])
