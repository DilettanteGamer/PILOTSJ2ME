package pilots;
import java.util.*;
/** Runs the original section-12 puzzle, replaying a fresh save after every tick. */
public final class BalconyWorldTest {
 static HotelWorld world,replay;static Set<Integer> actions=new TreeSet<Integer>();static int ticks;
 static void check(boolean b,String message){if(!b)throw new RuntimeException(message);}
 static HotelWorld resume(HotelWorld w)throws Exception {
  w.syncSave();byte[] bytes=w.state.encode();GameState state=GameState.decode(bytes,11);HotelWorld copy=new HotelWorld(state);copy.syncSave();
  check(Arrays.equals(bytes,state.encode()),"balcony snapshot changed");return copy;
 }
 static void tick()throws Exception {
  actions.add(world.action());world.tick();replay.tick();ticks++;
  check(Arrays.equals(world.state.encode(),replay.state.encode()),"balcony replay diverged "+world.action()+"/"+world.step());replay=resume(replay);
 }
 static void settle()throws Exception {int n=1800;while(world.busy()&&n-->0)tick();check(n>0,"balcony hangs "+world.action()+"/"+world.step());}
 static boolean has(int id){for(HotelWorld.Thing t:world.cells)if(t!=null&&t.id==id)return true;return false;}
 static void act(int hero,int item,int id)throws Exception {
  check(world.select(hero)&&replay.select(hero),"hero selection "+hero);
  check(world.selectItem(item)&&replay.selectItem(item),"item selection "+item);
  check(world.interact(id)&&replay.interact(id),"target rejected "+id);settle();
 }
 public static void main(String[] args)throws Exception {
  GameState state=new GameState();state.scene=10;world=new HotelWorld(state);replay=resume(world);settle();
  check(world.heroes[0].id==12&&world.heroes[1].id==11,"balcony hero roles");
  check(actions.contains(2)&&actions.contains(52),"balcony intro skipped");
  check(!world.hasItem(1)&&!world.hasItem(2),"phantom balcony inventory");
  byte[] initial=world.state.encode();
  for(int hero=0;hero<2;hero++)for(int target=15;target<=42;target++) {
   world=new HotelWorld(GameState.decode(initial,11));replay=resume(world);
   world.select(hero);replay.select(hero);boolean a=world.interact(target),b=replay.interact(target);check(a==b,"branch acceptance");if(a)settle();
  }
  world=new HotelWorld(GameState.decode(initial,11));replay=resume(world);
  for(int hero=0;hero<2;hero++) {
   world.select(hero);replay.select(hero);boolean moved=false;
   for(int y=320;y>=192&&!moved;y-=16)for(int x=384;x>=64&&!moved;x-=32)if(world.move(x,y)){check(replay.move(x,y),"manual walk replay");settle();moved=true;check(world.heroes[hero].layer==69-world.heroes[hero].y*3/16,"balcony walk plane");}
   check(moved,"no balcony walking route");
  }
  world=new HotelWorld(GameState.decode(initial,11));replay=resume(world);
  act(0,0,23);check(has(17)&&has(24),"drawer did not reveal swatter");
  act(0,0,17);check(world.hasItem(1)&&has(18)&&!has(17),"swatter pickup");
  act(1,0,28);check(has(26)&&has(27),"cupboard paper");
  act(0,0,30);check(has(29)&&!has(30),"cord preparation");
  act(1,0,29);check(world.g(20)==10&&!has(29),"Colleague cord sequence");
  act(1,0,16);check(!world.heroes[1].active&&has(35)&&has(36)&&has(37),"Colleague balcony departure");
  check(!world.select(1)&&!replay.select(1),"absent Colleague can be selected");
  act(0,0,16);check(!world.completed&&world.state.variables[57]==2&&has(38),"first balcony attempt must fail");
  act(0,1,38);check(world.state.variables[57]==1,"first switch reaction");
  act(0,1,38);check(world.state.variables[57]==0&&!has(38),"switch not reset");
  // The original fly timer closes the opportunity; the puzzle must remain recoverable.
  for(int i=0;i<190;i++)tick();check(has(38)&&world.state.variables[57]==1,"fly timeout did not restore switch");
  act(0,1,38);check(!has(38),"switch retry failed");
  act(0,0,16);check(world.completed&&actions.contains(78)&&actions.contains(79),"original balcony finale");
  check(SceneFlow.next(9,true)==10&&SceneFlow.next(10,true)==10,"chapter boundary");
  System.out.println("BalconyWorld: original intro, both hero branches, pickups, cord, balcony, fly timeout/retry, finale and "+ticks+" every-tick save replays passed "+actions);
 }
}
