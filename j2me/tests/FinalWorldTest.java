package pilots;
import java.util.*;
/** Full unmodified section-19 quest and every-tick snapshots of its packed byte flags. */
public final class FinalWorldTest implements FinalTestRoute.Driver {
 HotelWorld w,replay;int ticks;boolean ladderArt,endingArt;Set<Integer> actions=new TreeSet<Integer>();Set<Integer> cues=new TreeSet<Integer>();
 FinalWorldTest()throws Exception{GameState s=new GameState();s.scene=17;w=new HotelWorld(s);w.sound(new SoundSink(){public void play(int n){cues.add(n);}});replay=copy(w);}
 HotelWorld copy(HotelWorld world)throws Exception{world.syncSave();byte[] encoded=world.state.encode();GameState state=GameState.decode(encoded,18);HotelWorld result=new HotelWorld(state);FinalTestRoute.check(Arrays.equals(encoded,state.encode()),"final save mutated on load");return result;}
 public HotelWorld world(){return w;}
 public void tick()throws Exception{w.tick();replay.tick();ticks++;actions.add(w.action());for(HotelWorld.Thing t:w.effects)if(t!=null){if(t.clip==114)ladderArt=true;if(t.clip==117)endingArt=true;}FinalTestRoute.check(Arrays.equals(w.state.encode(),replay.state.encode()),"final replay tick "+ticks);replay=copy(replay);}
 public void act(int id)throws Exception{FinalTestRoute.check(w.interact(id),"final action "+id);replay=copy(w);}
 public void move(int x,int y)throws Exception{FinalTestRoute.check(w.move(x,y),"final move");replay=copy(w);}
 public void milestone(String name)throws Exception{replay=copy(w);}
 public static void main(String[] a)throws Exception{
  FinalWorldTest test=new FinalWorldTest();
  // Byte writes and unaligned 32-bit values must preserve neighbouring flags.
  test.w.g(0x1e1b79,0x12345678);FinalTestRoute.check(test.w.flag(0x1e1b78)==0&&test.w.g(0x1e1b79)==0x12345678,"unaligned final bank");test.w.g(0x1e1b79,0);test.replay=test.copy(test.w);
  FinalTestRoute.play(test);
  FinalTestRoute.check(test.ladderArt&&test.endingArt,"original unbound movies missing");
  boolean finalPose=false;for(HotelWorld.Thing t:test.w.effects)if(t!=null&&t.clip==118)finalPose=true;FinalTestRoute.check(finalPose,"original final hold missing");
  FinalTestRoute.check(SceneFlow.next(16,true)==17&&SceneFlow.next(17,true)==17,"final boundary");
  FinalTestRoute.check(test.cues.contains(45)&&test.cues.contains(42)&&test.cues.contains(35),"final voices missing");
  System.out.println("FinalWorld: stool, both heroes, bird/servant/hollow, all four flutes, ladder, original ending, cues "+test.cues+" and "+test.ticks+" every-tick save replays passed "+test.actions);
 }
}
