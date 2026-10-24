package pilots;
import java.util.*;
/** Original section-13 ledge quest and failure paths, with every-tick save replay. */
public final class FacadeWorldTest {
 static HotelWorld world,replay;static Set<Integer> actions=new TreeSet<Integer>();static int ticks;
 static void check(boolean b,String m){if(!b)throw new RuntimeException(m);}
 static HotelWorld resume(HotelWorld w)throws Exception{w.syncSave();byte[] bytes=w.state.encode();GameState s=GameState.decode(bytes,12);HotelWorld copy=new HotelWorld(s);copy.syncSave();check(Arrays.equals(bytes,s.encode()),"ledge save changed");return copy;}
 static void tick()throws Exception{actions.add(world.action());world.tick();replay.tick();ticks++;check(Arrays.equals(world.state.encode(),replay.state.encode()),"ledge replay diverged "+world.action()+"/"+world.step());replay=resume(replay);}
 static void settle()throws Exception{int n=2000;while(world.busy()&&n-->0)tick();check(n>0,"ledge action locked "+world.action()+"/"+world.step());}
 static boolean has(int id){for(HotelWorld.Thing t:world.cells)if(t!=null&&t.id==id)return true;return false;}
 static void act(int hero,int item,int id)throws Exception{check(world.select(hero)&&replay.select(hero),"ledge hero");check(world.selectItem(item)&&replay.selectItem(item),"ledge item");check(world.interact(id)&&replay.interact(id),"ledge target "+id);settle();}
 static void quest(int pickupHero,int exitHero)throws Exception{
  act(1,0,16);check(has(17),"Colleague did not open right window");
  act(0,0,17);check(has(19)&&has(23),"Chief did not reveal glass shard");
  act(pickupHero,0,19);check(world.hasItem(1)&&has(20)&&!has(19),"shard pickup");
  act(0,1,18);check(!has(18)&&!has(21),"rope not cut by Chief with shard");
  act(exitHero,0,15);check(world.completed&&world.state.variables[60]==2&&actions.contains(60),"cooperative left-window exit");
 }
 public static void main(String[] args)throws Exception{
  GameState s=new GameState();s.scene=11;world=new HotelWorld(s);replay=resume(world);
  check(world.heroes[0]==null&&world.heroes[1]==null,"intro duplicates characters");
  boolean[] intro=new boolean[66];
  for(int i=0;i<85;i++){for(HotelWorld.Thing t:world.cells)if(t!=null&&t.clip==76)intro[t.frame]=true;tick();}
  for(int i=0;i<intro.length;i++)check(intro[i],"ledge intro frame missing "+i);
  check(!world.busy()&&world.heroes[0].id==12&&world.heroes[1].id==11,"ledge intro controller roles");
  check(world.heroes[0].layer==46-world.heroes[0].y/8&&world.heroes[1].layer==46-world.heroes[1].y/8,"source ledge planes");
  byte[] initial=world.state.encode();
  for(int hero=0;hero<2;hero++)for(int id=15;id<=20;id++){
   world=new HotelWorld(GameState.decode(initial,12));replay=resume(world);world.select(hero);replay.select(hero);boolean a=world.interact(id),b=replay.interact(id);check(a==b,"initial branch replay");if(a)settle();
  }
  check(actions.contains(53)||actions.contains(54),"missing ledge fall failure");
  for(int hero=0;hero<2;hero++){
   world=new HotelWorld(GameState.decode(initial,12));replay=resume(world);world.select(hero);replay.select(hero);
   check(world.move(448,320)&&replay.move(448,320),"manual ledge route");settle();check(world.heroes[hero].layer==46-world.heroes[hero].y/8,"manual ledge depth");
  }
  for(int exit=0;exit<2;exit++){
   world=new HotelWorld(GameState.decode(initial,12));replay=resume(world);quest(exit,exit);
  }
  // A bad approach must leave a recoverable puzzle, including after saving.
  world=new HotelWorld(GameState.decode(initial,12));replay=resume(world);act(0,0,15);check(!world.completed&&!world.hasItem(1),"unsafe ledge bypass");quest(0,0);
  check(SceneFlow.next(10,true)==11&&SceneFlow.next(11,true)==11,"ledge chapter routing");
  System.out.println("FacadeWorld: all intro frames, both heroes' branches, fall/recovery, window/shard/rope puzzle, both exit leaders and "+ticks+" every-tick saves passed "+actions);
 }
}
