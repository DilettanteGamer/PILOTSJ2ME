package pilots;
import java.util.*;
/** Section 14: original timed plunger, cat/soap cooperation and both exits. */
public final class BathroomWorldTest {
 static HotelWorld world,replay;static Set<Integer> actions=new TreeSet<Integer>();static int ticks;
 static void check(boolean b,String m){if(!b)throw new RuntimeException(m);}
 static HotelWorld resume(HotelWorld w)throws Exception{w.syncSave();byte[] bytes=w.state.encode();GameState s=GameState.decode(bytes,13);HotelWorld c=new HotelWorld(s);c.syncSave();check(Arrays.equals(bytes,s.encode()),"bathroom save changed");return c;}
 static void tick()throws Exception{actions.add(world.action());world.tick();replay.tick();ticks++;check(Arrays.equals(world.state.encode(),replay.state.encode()),"bathroom replay divergence "+world.action()+"/"+world.step());replay=resume(replay);}
 static void settle()throws Exception{int n=3000;while(world.busy()&&n-->0)tick();check(n>0,"bathroom lock "+world.action()+"/"+world.step());}
 static boolean has(int id){for(HotelWorld.Thing t:world.cells)if(t!=null&&t.id==id)return true;return false;}
 static void act(int hero,int item,int id)throws Exception{check(world.select(hero)&&replay.select(hero),"bathroom hero");check(world.selectItem(item)&&replay.selectItem(item),"bathroom item "+item);check(world.interact(id)&&replay.interact(id),"bathroom target "+id);settle();}
 static void ready()throws Exception {
  act(0,0,21);check(world.hasItem(1),"plunger pickup");act(1,0,17);check(world.hasItem(2),"soap pickup");
  act(0,0,18);check(world.state.variables[56]>0,"Chief did not wake cat");
  // Waiting closes the first opportunity; the original chain must be repeatable.
  for(int i=0;i<115;i++)tick();settle();check(world.state.variables[56]==0,"cat window did not expire");
  act(0,0,18);act(1,2,28);check(world.state.variables[55]==1&&!world.hasItem(2),"Colleague did not soap cat");
  act(0,1,26);check(world.g(0x34)==1&&!world.hasItem(1)&&has(25),"Chief plunger setup");
  act(1,0,20);check(world.g(0x38)==1,"cooperative flush condition");
  act(0,0,25);check(world.hasItem(1)&&world.state.variables[54]>0&&has(22),"timed plunger recovery");
 }
 public static void main(String[] args)throws Exception {
  if(args.length>0) {
   world=new HotelWorld(GameState.decode(java.nio.file.Files.readAllBytes(java.nio.file.Paths.get(args[0])),13));replay=resume(world);
   check(world.heroes[1].x==256&&world.heroes[1].y==320&&world.walkHero==1&&world.busy(),"legacy sink pose must visibly step back");
   settle();check(world.heroes[1].x==256&&world.heroes[1].y==336,"legacy sink clearance");
  }
  GameState s=new GameState();s.scene=12;world=new HotelWorld(s);replay=resume(world);
  check(!world.busy()&&world.heroes[0].id==12&&world.heroes[1].id==11,"bathroom entry");
  check(world.heroes[0].layer==78-world.heroes[0].y*3/16&&world.heroes[1].layer==78-world.heroes[1].y*3/16,"source bathroom planes");
  byte[] initial=world.state.encode();
  for(int hero=0;hero<2;hero++)for(int target=15;target<=31;target++) {
   world=new HotelWorld(GameState.decode(initial,13));replay=resume(world);world.select(hero);replay.select(hero);
   boolean a=world.interact(target),b=replay.interact(target);check(a==b,"bathroom target replay");if(a)settle();
  }
  world=new HotelWorld(GameState.decode(initial,13));replay=resume(world);
  act(0,0,21);act(0,1,15);check(!world.completed,"dry plunger bypassed exit");
  world=new HotelWorld(GameState.decode(initial,13));replay=resume(world);ready();byte[] prepared=world.state.encode();
  // The second icon phase and expiry are driven by the original 400-tick timer.
  while(world.state.variables[54]<200)tick();check(world.hasItem(1),"plunger lost during timer phase");
  while(world.state.variables[54]!=0)tick();check(world.hasItem(1),"plunger lost on expiry");
  act(0,1,15);check(!world.completed,"expired plunger bypassed exit");
  world=new HotelWorld(GameState.decode(prepared,13));replay=resume(world);act(0,1,15);
  check(world.completed&&actions.contains(60)&&world.state.variables[57]==1,"bathroom ending / partner walk");
  check(SceneFlow.next(11,true)==12&&SceneFlow.next(12,true)==13,"bathroom chapter bounds");
  System.out.println("BathroomWorld: both character branches, dry/timed plunger, soap/cat timeout/retry, cooperation, both exits and "+ticks+" every-tick save replays passed "+actions);
 }
}
