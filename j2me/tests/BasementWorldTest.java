package pilots;
import java.util.*;import java.nio.file.*;
/** Keyboard segment, visual shot delay, reversible exit, old-save recovery and source ending. */
public final class BasementWorldTest {
 static HotelWorld world,replay;static int ticks;
 static void check(boolean b,String m){if(!b)throw new RuntimeException(m);}
 static HotelWorld copy(HotelWorld w)throws Exception{w.syncSave();byte[] b=w.state.encode();HotelWorld c=new HotelWorld(GameState.decode(b,16));c.syncSave();check(Arrays.equals(b,c.state.encode()),"crate roundtrip");return c;}
 static void same()throws Exception{check(Arrays.equals(world.state.encode(),replay.state.encode()),"crate replay divergence at "+ticks);replay=copy(replay);}
 static void tick(boolean detail)throws Exception{if(detail){world.basement.tick();replay.basement.tick();}else{world.tick();replay.tick();}same();ticks++;}
 static void steps(int n,boolean detail)throws Exception{for(int i=0;i<n;i++)tick(detail);}
 static void key(int key)throws Exception{check(world.basement.input(key)==replay.basement.input(key),"key result mismatch");same();}
 static void open()throws Exception{check(world.select(0)&&replay.select(0),"Chief select");check(world.requestBasement(null,400,350)&&replay.requestBasement(null,400,350),"crate trigger");check(world.basementRequested(),"crate screen absent");world.basement.open();replay.basement.open();same();}
 static void close()throws Exception{world.basement.close();replay.basement.close();same();}
 static void move(int key)throws Exception{key(key);steps(22,true);}
 static void fire(int n)throws Exception{for(int i=0;i<n;i++){int before=world.basement.crates(world.basement.column());key(5);check(world.basement.shotTick()==BasementGame.SHOT_TICKS&&world.basement.crates(world.basement.column())==before,"box disappeared on shot");steps(BasementGame.SHOT_TICKS-1,true);check(world.basement.crates(world.basement.column())==before,"box removed before complete animation");steps(1,true);check(world.basement.crates(world.basement.column())==before-1,"box not removed after animation");}}
 static final BasementTestRoute.Driver route=new BasementTestRoute.Driver(){
  public HotelWorld world(){return world;}public void press(int k)throws Exception{key(k);}public void ticks(int n)throws Exception{steps(n,true);}
 };
 static void solve()throws Exception{
  BasementTestRoute.stairs(route);
  key(2);steps(4,true);check(!world.basement.safe(),"UP did not enter crate lane");key(5);check(world.basement.shotTick()==0,"gun fired on unsafe lane");
  for(int i=0;i<3;i++){key(4);steps(4,true);}
  check(world.basement.solved()&&!world.basementRequested(),"top platform did not finish segment");close();int limit=250;while(!world.completed&&limit-->0)tick(false);check(world.completed,"source celebration/ladder/both exits failed");
 }
 public static void main(String[] args)throws Exception{
  GameState s=new GameState();s.scene=15;world=new HotelWorld(s);replay=copy(world);steps(800,false);
  check(!world.busy()&&world.basement.throwsCount()==0,"spawning/idle started throws");
  for(HotelWorld.Thing t:world.cells)if(t!=null&&t.id==36)check(t.clip==86,"servant throws before movement");
  byte[] fresh=world.state.encode();
  open();key(2);steps(22,true);check(world.basement.safe()&&world.basement.crates(0)==2,"fall hit must recover onto safe lane");steps(4,true);key(6);steps(4,true);check(world.basement.column()==1,"movement locked after hit");
  world=new HotelWorld(GameState.decode(fresh,16));replay=copy(world);
  open();steps(80,true);check(world.basement.throwsCount()==0,"opening/standing starts barrage");key(5);check(world.basement.shotTick()==0,"phantom gun");
  int throwsBefore=world.basement.throwsCount();key(6);steps(4,true);key(4);steps(4,true);check(world.basement.throwsCount()==throwsBefore&&world.basement.fallingColumn()<0,"safe walking starts throws");
  key(2);steps(4,true);key(8);steps(22,true);check(world.basement.fallingColumn()<0&&world.basement.crates(0)==1,"windup did not cancel after retreat");
  key(6);steps(4,true);key(2);steps(12,true);close();int paused=world.basement.fallingTick();steps(80,false);check(world.basement.fallingTick()==paused,"room exit did not pause segment");
  byte[] withoutGun=world.state.encode();
  check(world.interact(14)&&replay.interact(14),"Chief should approach the gun after leaving detail");steps(80,false);
  check(!world.busy()&&world.hasItem(1)&&world.hero()==0,"Chief did not take the gun");
  world=new HotelWorld(GameState.decode(withoutGun,16));replay=copy(world);
  check(world.move(256,336)&&replay.move(256,336),"Chief walk to the shared approach");steps(40,false);
  check(world.select(1)&&replay.select(1)&&world.interact(14)&&replay.interact(14),"gun pickup after exit");steps(100,false);check(world.hasItem(1),"gun absent");open();steps(22,true);
  key(8);steps(4,true);check(world.basement.safe()&&world.basement.crates(1)==1,"safe lane was hit or drop failed");key(4);steps(22,true);
  solve();
  if(args.length>0){world=new HotelWorld(GameState.decode(Files.readAllBytes(Paths.get(args[0])),16));replay=copy(world);check(world.hasItem(1)&&!world.busy(),"old interrupted shot lost gun/stayed locked");open();key(6);steps(22,true);check(world.basement.column()==1,"old shot migration cannot move");close();}
  if(args.length>1){world=new HotelWorld(GameState.decode(Files.readAllBytes(Paths.get(args[1])),16));replay=copy(world);check(world.basement.solved()&&world.busy()&&!world.select(0),"old exit restarted the adapted puzzle");steps(250,false);check(world.completed,"old source exit migration failed");}
  if(args.length>2){world=new HotelWorld(GameState.decode(Files.readAllBytes(Paths.get(args[2])),16));replay=copy(world);check(!world.busy()&&!world.hasItem(1),"0.19 return left a busy Chief");check(world.interact(14)&&replay.interact(14),"legacy Chief approach");steps(80,false);check(!world.busy()&&world.hasItem(1),"legacy Chief did not collect the gun");
   world=new HotelWorld(GameState.decode(Files.readAllBytes(Paths.get(args[2])),16));replay=copy(world);check(world.select(1)&&replay.select(1)&&world.interact(14)&&replay.interact(14),"legacy Colleague pickup");steps(100,false);check(world.hasItem(1),"legacy gun not collected");}
  check(SceneFlow.next(13,true)==15&&SceneFlow.next(15,true)==15,"scene bounds");
  System.out.println("BasementWorld: no spawn barrage, warned movement throws, safe lane, gun gating, full destruction before removal, recoverable input, pause/exit/pickup/re-entry, auto jumps, original ending and "+ticks+" every-tick save replays passed");
 }
}
