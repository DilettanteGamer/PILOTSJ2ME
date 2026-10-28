package pilots;
import java.lang.reflect.*;import java.lang.management.*;
import javax.microedition.media.*;import javax.microedition.media.Manager.FakePlayer;
import javax.microedition.lcdui.*;
/** Delayed/partial drawing must never release new cues; menu and running FX stay independent. */
public final class AudioPresentationTest {
 static void check(boolean b,String why)throws Exception{if(!b)throw new Exception(why);}
 static Object get(Object o,String n)throws Exception{Field f=o.getClass().getDeclaredField(n);f.setAccessible(true);return f.get(o);}
 static void set(Object o,String n,Object v)throws Exception{Field f=o.getClass().getDeclaredField(n);f.setAccessible(true);f.set(o,v);}
 static void waitFor(java.util.function.BooleanSupplier ready)throws Exception{long until=System.currentTimeMillis()+4000;while(!ready.getAsBoolean()&&System.currentTimeMillis()<until)Thread.sleep(10);check(ready.getAsBoolean(),"Audio presentation timeout");}
 static int started(){int n=0;synchronized(Manager.players){for(FakePlayer p:Manager.players)n+=p.starts;}return n;}
 public static void main(String[] args)throws Exception{
  GameAudio a=new GameAudio();Thread worker=(Thread)get(a,"worker");
  try{
   a.presentationDriven();a.scene(13);a.active(true);a.play(6);
   ThreadMXBean cpu=ManagementFactory.getThreadMXBean();long before=cpu.isThreadCpuTimeSupported()?cpu.getThreadCpuTime(worker.getId()):0;
   Thread.sleep(250);long used=cpu.isThreadCpuTimeSupported()?cpu.getThreadCpuTime(worker.getId())-before:0;
   check(started()==0&&a.presentationPending(),"Audio started before first frame");check(used<80000000,"Audio worker spins while waiting for paint");
   a.framePresented();waitFor(()->started()==2);FakePlayer voice=Manager.players.get(1);int old=started();
   a.play(7);Thread.sleep(160);check(started()==old&&voice.state==Player.STARTED,"New cue escaped gate or existing speech paused");
   a.framePresented();final int newCueTarget=old+1;waitFor(()->started()==newCueTarget);
   int stale=a.presentationTicket();a.holdPresentation();a.framePresented(stale);check(a.presentationPending(),"Stale frame released newer audio");a.framePresented();
   a.active(false);waitFor(()->voice.stops==1);a.holdPresentation();old=started();a.active(true);Thread.sleep(150);
   check(started()==old,"Paused players resumed before visible frame");a.framePresented();final int resumeStarts=old;waitFor(()->started()>resumeStarts);
   a.holdPresentation();a.menu(true);final int menuStart=started();waitFor(()->started()>menuStart);check(Manager.players.get(Manager.players.size()-1).loops==-1,"Menu music blocked by gameplay gate");
   a.menu(false);Thread.sleep(150);check(a.presentationPending(),"Returning from menu acknowledged an unseen frame");
  }finally{a.close();waitFor(()->!worker.isAlive());}
  PilotsMidlet app=new PilotsMidlet();PilotsMidlet.GameCanvas canvas=app.new GameCanvas();set(app,"canvas",canvas);set(app,"display",Display.getDisplay(app));
  try{
   canvas.startLocation(13);GameAudio attached=(GameAudio)get(canvas,"audio");attached.active(true);attached.play(6);int starts=started();
   Graphics g=new Graphics(128,160);g.setClip(24,32,12,12);canvas.paint(g);Thread.sleep(150);
   check(attached.presentationPending()&&started()==starts,"Cursor-only paint released room audio");
   g.setClip(0,0,128,160);canvas.paint(g);check(!attached.presentationPending(),"Full room paint did not release audio");waitFor(()->started()>starts);
   for(int kind=0;kind<3;kind++){
    attached.play(7);check(attached.presentationPending(),"Cue did not request presentation");
    Canvas detail;
    if(kind==0){GameState s=new GameState();s.scene=9;HotelWorld world=new HotelWorld(s);s.variables[59]=2;FridgeCanvas f=new FridgeCanvas(world,new FridgeCanvas.Host(){public void back(){}public void active(boolean v){}});f.presentationAudio(attached);detail=f;}
    else if(kind==1){GameState s=new GameState();s.scene=13;HotelWorld world=new HotelWorld(s);s.variables[59]=2;SwitchboardCanvas f=new SwitchboardCanvas(world,new SwitchboardCanvas.Host(){public void back(){}public void active(boolean v){}public void error(java.io.IOException e){throw new RuntimeException(e);}});f.presentationAudio(attached);detail=f;}
    else{GameState s=new GameState();s.scene=15;HotelWorld world=new HotelWorld(s);world.basement.request();BasementCanvas f=new BasementCanvas(world,new BasementCanvas.Host(){public void back(){}public void active(boolean v){}public void error(java.io.IOException e){throw new RuntimeException(e);}});f.presentationAudio(attached);detail=f;}
    Method paint=detail.getClass().getDeclaredMethod("paint",Graphics.class);paint.setAccessible(true);g.setClip(0,0,128,160);paint.invoke(detail,g);
    check(!attached.presentationPending(),"Detail screen left audio gated "+kind);if(detail instanceof BasementCanvas)((BasementCanvas)detail).shutdown();
   }
  }finally{canvas.shutdown();}
  System.out.println("Audio presentation: first MIDI/cues wait for full paint; no busy spin; existing FX continue; pause/resume and menu bypass work; partial room paints cannot release cues; all three detail screens acknowledge drawing");
 }
}
