package pilots;
import javax.microedition.media.*;import javax.microedition.media.Manager.FakePlayer;
import java.lang.reflect.Field;
/** A driver that breaks PCM when completed MIDI is closed; lifecycle failures must not kill the worker. */
public final class AudioMusicEndTest {
 static void check(boolean ok,String why)throws Exception{if(!ok)throw new Exception(why);}
 static void waitFor(java.util.function.BooleanSupplier ok)throws Exception{long until=System.currentTimeMillis()+4000;while(!ok.getAsBoolean()&&System.currentTimeMillis()<until)Thread.sleep(10);check(ok.getAsBoolean(),"MIDI-end timeout");}
 static FakePlayer last(){return Manager.players.get(Manager.players.size()-1);}
 static int size(){return Manager.players.size();}
 public static void main(String[] args)throws Exception{
  GameAudio audio=new GameAudio();Field f=GameAudio.class.getDeclaredField("worker");f.setAccessible(true);Thread worker=(Thread)f.get(audio);
  try{
   audio.scene(13);audio.active(true);waitFor(()->size()==1&&last().state==Player.STARTED);FakePlayer music=last();
   audio.play(6);waitFor(()->size()==2&&last().state==Player.STARTED);FakePlayer voice=last();
   music.breakEffectsOnClose=true;music.state=Player.PREFETCHED;
   waitFor(()->AudioDiagnostics.musicEnds==1);
   check(music.state==Player.PREFETCHED&&!Manager.rejectEffectPrefetch,"Natural MIDI end closed the shared audio device");check(voice.state==Player.STARTED,"MIDI end interrupted speech");
   audio.play(7);waitFor(()->size()==3&&last().state==Player.STARTED);FakePlayer second=last();
   audio.active(false);waitFor(()->voice.stops==1&&second.stops==1);audio.active(true);waitFor(()->voice.starts==2&&second.starts==2);
   check(music.starts==1&&music.stops==0&&size()==3,"Finished MIDI restarted on resume");
   voice.state=second.state=Player.PREFETCHED;waitFor(()->voice.state==Player.CLOSED&&second.state==Player.CLOSED);
   audio.play(10);waitFor(()->size()==4&&last().state==Player.STARTED);
   check(AudioDiagnostics.effectStarts==3&&AudioDiagnostics.effectFailures==0&&worker.isAlive(),"FX failed after completed music");
   audio.toggle();waitFor(()->music.state==Player.CLOSED&&last().state==Player.CLOSED);check(Manager.rejectEffectPrefetch,"Test driver did not model destructive close");Manager.rejectEffectPrefetch=false;
   audio.toggle();waitFor(()->size()==5&&last().state==Player.STARTED);
   audio.scene(9);waitFor(()->size()==6&&last().state==Player.STARTED);FakePlayer room=last();room.state=Player.PREFETCHED;waitFor(()->AudioDiagnostics.musicEnds==2);
   audio.music(2);waitFor(()->room.state==Player.CLOSED&&size()==7&&last().state==Player.STARTED);
   audio.menu(true);waitFor(()->size()==8&&last().state==Player.STARTED);check(last().loops==-1,"Menu loop changed");
   audio.menu(false);waitFor(()->size()==9&&last().state==Player.STARTED);audio.scene(13);waitFor(()->size()==10&&last().state==Player.STARTED);FakePlayer failedMusic=last();
   failedMusic.failClose=true;failedMusic.failState=true;
   waitFor(()->failedMusic.state==Player.CLOSED&&AudioDiagnostics.failedStage==11);
   final int firstSize=size();audio.play(12);waitFor(()->size()>firstSize&&last().state==Player.STARTED);FakePlayer failedFx=last();
   check(worker.isAlive()&&AudioDiagnostics.workerAlive,"Driver getState/close exception killed audio worker");
   failedFx.failClose=true;failedFx.state=Player.PREFETCHED;waitFor(()->failedFx.state==Player.CLOSED);
   final int old=size();audio.play(13);waitFor(()->size()>old&&last().state==Player.STARTED);check(worker.isAlive(),"Effect close killed audio worker");
   DebugHud.scene(13);while(DebugHud.mode()!=3)DebugHud.toggle();DebugHud.paint(new javax.microedition.lcdui.Graphics(128,160),128,148,0,0);
   Field labels=DebugHud.class.getDeclaredField("labels");labels.setAccessible(true);check(((String[])labels.get(null))[0].contains("W1 M0 N2"),"Music/worker state missing from HUD");
  }finally{audio.close();waitFor(()->!worker.isAlive());Manager.rejectEffectPrefetch=false;}
  check(!AudioDiagnostics.workerAlive&&Manager.players.stream().allMatch(p->p.state==Player.CLOSED),"Cleanup retained players");
  System.out.println("MIDI natural end retains one completed player without closing shared audio; ongoing/new FX survive; completed music never resumes; mute/scene/track/menu cleanup and getState/close failure isolation passed");
 }
}
