package pilots;
import javax.microedition.media.*;
import javax.microedition.media.Manager.FakePlayer;
/** Reproduces the ambiguous C>0/H0/X0/S200/Z4 with a failed first prefetch. */
public final class AudioStartFailureTest {
 private static void check(boolean ok,String why)throws Exception{if(!ok)throw new Exception(why);}
 private static void waitFor(java.util.function.BooleanSupplier ok)throws Exception {
  long end=System.currentTimeMillis()+4000;while(!ok.getAsBoolean()&&System.currentTimeMillis()<end)Thread.sleep(10);
  check(ok.getAsBoolean(),"Audio start diagnostics timeout");
 }
 public static void main(String[] args)throws Exception {
  GameAudio audio=new GameAudio();
  try {
   audio.active(true);waitFor(()->Manager.players.size()==1 && Manager.players.get(0).state==Player.STARTED);
   Manager.rejectEffectPrefetch=true;audio.play(31);waitFor(()->AudioDiagnostics.startFailures==2 && Manager.players.stream().allMatch(p->p.state==Player.CLOSED));
   check(AudioDiagnostics.creates==2 && AudioDiagnostics.starts==0 && AudioDiagnostics.reuses==0 && AudioDiagnostics.failures==0,"Creation misreported as playback");
   check(AudioDiagnostics.endState==200 && AudioDiagnostics.reason==4,"Expected old ambiguous S200/Z4");
   check(AudioDiagnostics.failedStage==4 && AudioDiagnostics.errorType==1 && AudioDiagnostics.errorText.equals("TEST EFFECT PREFETCH REJECTED"),"Native error details missing");
   DebugHud.scene(2);DebugHud.paint(new javax.microedition.lcdui.Graphics(128,160),128,148,0,0);
   java.lang.reflect.Field field=DebugHud.class.getDeclaredField("labels");field.setAccessible(true);String[] labels=(String[])field.get(null);
   check(labels[2].contains("A4 E1 F2") && labels[3].equals("TEST EFFECT PREFETCH REJECTED") && labels[5].contains("C2 P0 H0 X0"),"Native failure not visible on default HUD");
   Manager.rejectEffectPrefetch=false;audio.play(31);waitFor(()->AudioDiagnostics.starts==1);FakePlayer step=Manager.players.get(Manager.players.size()-1);
   step.state=Player.PREFETCHED;audio.play(31);waitFor(()->AudioDiagnostics.reuses==1);
   check(AudioDiagnostics.starts==1 && AudioDiagnostics.creates==3 && step.lastSeek==0,"Fresh and replay starts conflated");
  } finally {Manager.rejectEffectPrefetch=false;audio.close();}
  waitFor(()->Manager.players.stream().allMatch(p->p.state==Player.CLOSED));
  System.out.println("Failed initial prefetch reproduces C>0/P0/H0/X0/S200/Z4; phase/type/message captured; successful start and replay distinguished; cleanup passed");
 }
}
