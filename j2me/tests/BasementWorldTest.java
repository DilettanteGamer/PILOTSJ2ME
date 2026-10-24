package pilots;
import java.util.*;
import java.nio.file.*;
/** Legal source-map inputs only: climb, falling crates, gun, both exits and tick-by-tick resume. */
public final class BasementWorldTest {
 static HotelWorld world,replay;static int ticks,maxHeight;static boolean sawGun,sawHit,sawExit,climbedLadder;
 static final Set<Integer> cues=new HashSet<Integer>(),clips=new HashSet<Integer>(),directions=new HashSet<Integer>();
 static void check(boolean b,String m){if(!b)throw new RuntimeException(m);}
 static HotelWorld copy(HotelWorld w)throws Exception{
  w.syncSave();byte[] bytes=w.state.encode();GameState s=GameState.decode(bytes,16);HotelWorld c=new HotelWorld(s);c.syncSave();
  check(Arrays.equals(bytes,s.encode()),"basement save roundtrip");return c;
 }
 static HotelWorld.Thing at(HotelWorld w,int[] a){if(a[3]<0)return null;HotelWorld.Thing t=w.cells[a[5]/16*10+a[4]/64];check(t!=null&&t.id==a[3],"route target moved: "+Arrays.toString(a));return t;}
 static void tick()throws Exception{
  world.tick();replay.tick();check(Arrays.equals(world.state.encode(),replay.state.encode()),"basement replay diverged at "+ticks);replay=copy(replay);ticks++;
  maxHeight=Math.max(maxHeight,world.g(0x1e1ba8));sawGun|=world.hasItem(1);sawExit|=world.g(0x1e1bb0)==5;
  for(HotelWorld.Thing t:world.cells)if(t!=null){clips.add(t.clip);if(t.clip==80)directions.add(t.direction);sawHit|=t.id==20;climbedLadder|=t.id==15&&t.y<200;}
 }
 static void steps(int n)throws Exception{for(int i=0;i<n;i++)tick();}
 static void request(int[] a)throws Exception{
  check(world.select(0)&&replay.select(0)&&world.selectItem(a[2])&&replay.selectItem(a[2]),"basement selection");
  check(world.requestBasement(at(world,a),a[0],a[1])&&replay.requestBasement(at(replay,a),a[0],a[1]),"basement click rejected");
  // Resume before the two independent source click channels have been consumed.
  replay=copy(replay);tick();for(int i=0;i<90&&(world.busy()||i<4);i++)tick();check(!world.busy(),"basement action hangs");
 }
 static List<int[]> route(String path)throws Exception{
  List<int[]> out=new ArrayList<int[]>();for(String line:Files.readAllLines(Paths.get(path))){String[] parts=line.split(",");int[] row=new int[6];for(int i=0;i<6;i++)row[i]=Integer.parseInt(parts[i]);out.add(row);}return out;
 }
 public static void main(String[] args)throws Exception{
  GameState s=new GameState();s.scene=15;world=new HotelWorld(s);replay=copy(world);world.sound(new SoundSink(){public void play(int id){cues.add(id);}});
  steps(80);
  check(!world.busy()&&!world.hasItem(1),"basement intro/phantom gun");
  check(world.select(1)&&replay.select(1)&&world.interact(14)&&replay.interact(14),"gun pickup");steps(80);check(world.hasItem(1),"gun not in inventory");
  byte[] ready=world.state.encode();List<int[]> route=route(args[0]);
  for(int[] a:route)if(a[1]<0)steps(a[0]);else request(a);
  int limit=250;while(!world.completed&&limit-->0)tick();
  check(world.completed&&maxHeight==4&&sawGun&&sawExit&&climbedLadder,"incomplete original basement quest");
  check(sawHit&&clips.contains(72)&&clips.contains(88)&&clips.contains(80),"missing hit, gun or ending branch: "+clips);
  check(cues.contains(3)&&cues.contains(23),"missing source footsteps/ending audio: "+cues);
  check(SceneFlow.next(13,true)==15&&SceneFlow.next(15,true)==15,"basement scene boundary");
  // The same legal quest with another timing reaches the ending outside ambient laugh 700.
  world=new HotelWorld(GameState.decode(ready,16));replay=copy(world);
  for(int i=0;i<route.size();i++){if(i==26)steps(50);int[] a=route.get(i);if(a[1]<0)steps(a[0]);else request(a);}
  limit=250;while(!world.completed&&limit-->0)tick();check(world.completed,"alternate-timing ending failed");
  System.out.println("BasementWorld: legal crate/gun quest, four heights, damage, ladder, both exits and "+ticks+" every-tick save/replay checks passed; aim directions "+directions);
 }
}
