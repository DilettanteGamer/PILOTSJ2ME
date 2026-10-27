package pilots;
import javax.microedition.media.*;
import javax.microedition.media.Manager.FakePlayer;

/** Actual worker: replay, pause, timeout, native failures, voice priority and cleanup. */
public final class AudioReuseTest {
 private static void check(boolean ok,String why)throws Exception{if(!ok)throw new Exception(why);}
 private static void waitFor(java.util.function.BooleanSupplier ok)throws Exception {
  long end=System.currentTimeMillis()+4000;while(!ok.getAsBoolean()&&System.currentTimeMillis()<end)Thread.sleep(10);
  check(ok.getAsBoolean(),"Audio reuse worker timeout");
 }
 private static FakePlayer latest(){return Manager.players.get(Manager.players.size()-1);}
 public static void main(String[] args)throws Exception {
  GameAudio audio=new GameAudio();
  try {
   audio.active(true);waitFor(()->Manager.players.size()==1);
   audio.play(31);waitFor(()->Manager.players.size()==2 && latest().starts==1);FakePlayer step=latest();
   for(int i=0;i<12;i++) {
    int started=step.starts;step.state=Player.PREFETCHED;Thread.sleep(120);audio.play(31);waitFor(()->step.starts==started+1);
    check(Manager.players.size()==2 && step.lastSeek==0,"Footstep decoded again or resumed midway");
   }
   audio.active(false);waitFor(()->step.stops==1);audio.active(true);waitFor(()->step.starts==14);
   check(step.seeks==12,"Pause restarted the sound instead of resuming it");
   step.state=Player.PREFETCHED;waitFor(()->step.state==Player.CLOSED);
   audio.play(31);waitFor(()->Manager.players.size()==3 && latest().starts==1);FakePlayer retry=latest();
   retry.state=Player.PREFETCHED;Manager.rejectSeek=true;audio.play(31);
   waitFor(()->retry.state==Player.CLOSED && Manager.players.size()==4 && latest().starts==1);Manager.rejectSeek=false;
   FakePlayer retryStart=latest();retryStart.state=Player.PREFETCHED;Manager.rejectReplay=true;audio.play(31);
   waitFor(()->retryStart.state==Player.CLOSED && Manager.players.size()==5 && latest().starts==1);Manager.rejectReplay=false;
   FakePlayer idle=latest();idle.state=Player.PREFETCHED;Manager.maxPCM=1;Manager.oomNextEffect=true;audio.play(34);
   waitFor(()->idle.state==Player.CLOSED && latest()!=idle && latest().starts==1);FakePlayer voice=latest();
   voice.state=Player.PREFETCHED;waitFor(()->voice.state==Player.CLOSED);
   Manager.maxPCM=3;audio.play(31);waitFor(()->latest()!=voice && latest().starts==1);FakePlayer sceneStep=latest();
   sceneStep.state=Player.PREFETCHED;audio.scene(3);waitFor(()->sceneStep.state==Player.CLOSED);
   audio.play(0);waitFor(()->latest()!=sceneStep && latest().type.equals("audio/mpeg") && latest().state==Player.STARTED);FakePlayer muted=latest();
   muted.state=Player.PREFETCHED;audio.toggle();waitFor(()->muted.state==Player.CLOSED);
  } finally {audio.close();Manager.maxPCM=3;Manager.rejectSeek=Manager.rejectReplay=Manager.oomNextEffect=false;}
  waitFor(()->Manager.players.stream().allMatch(p->p.state==Player.CLOSED));
  check(GameAudio.effectReuses==12 && GameAudio.effectReuseFailures==2,"Replay diagnostics mismatch");
  int prior=Manager.players.size();GameAudio overlapping=new GameAudio();
  try {
   overlapping.active(true);waitFor(()->Manager.players.size()==prior+1);
   overlapping.play(31);waitFor(()->Manager.players.size()==prior+2 && latest().starts==1);FakePlayer first=latest();
   overlapping.play(31);waitFor(()->Manager.players.size()==prior+3 && latest().starts==1);FakePlayer second=latest();
   check(first.state==Player.STARTED,"Overlapping cue interrupted an active footstep");
   first.state=second.state=Player.PREFETCHED;
   waitFor(()->first.state==Player.CLOSED);check(second.state==Player.PREFETCHED,"More than one idle player kept or newest player discarded");
  } finally {overlapping.close();}
  waitFor(()->Manager.players.stream().allMatch(p->p.state==Player.CLOSED));
  System.out.println("12 repeated footsteps: one MMAPI player, exact rewind; one idle slot; active overlap, pause, idle expiry, seek/start failure fallback, native allocation failure recovery, voice priority, scene/mute/close cleanup passed");
 }
}
