package pilots;
import java.util.*;
/** Physical commands, exact source timing windows, retry, save/replay and one crossing. */
public final class SewerWorldTest{
 static void check(boolean value,String message){if(!value)throw new RuntimeException(message);}
 static GameState state() {GameState s=new GameState();s.scene=7;return s;}
 static SewerLogic copy(SewerLogic s)throws Exception{GameState g=state();g.location=snapshot(s);return new SewerLogic(g);}
 static byte[] snapshot(SewerLogic s)throws Exception{s.sync();java.lang.reflect.Field f=ZooLogic.class.getDeclaredField("state");f.setAccessible(true);return ((GameState)f.get(s)).location;}
 static final class Node{SewerLogic s;int[] keys;Node(SewerLogic s,int[] keys){this.s=s;this.keys=keys;}}
 public static int[] solution()throws Exception{
  ArrayDeque<Node> queue=new ArrayDeque<Node>();Set<String> seen=new HashSet<String>();queue.add(new Node(new SewerLogic(state()),new int[0]));
  while(!queue.isEmpty()){
   Node n=queue.remove();if(n.s.completed())return n.keys;if(n.keys.length>=100)continue;
   for(int key:new int[]{0,4,6}){SewerLogic s=copy(n.s);s.input(key);s.tick();if(s.fall>0)continue;String id=s.phase()+":"+s.x+":"+s.from+":"+s.motion;if(!seen.add(id))continue;int[] path=Arrays.copyOf(n.keys,n.keys.length+1);path[path.length-1]=key;queue.add(new Node(s,path));}
  }throw new RuntimeException("sewer has no timed solution");
 }
 static SewerLogic middle()throws Exception{SewerLogic s=new SewerLogic(state());check(s.input(4),"first safe leg");for(int i=0;i<8;i++)s.tick();check(s.x==328&&s.motion==0&&s.fall==0,"middle safe stop");return s;}
 static void waitFor(SewerLogic s,int clock){while(s.clock%20!=clock)s.tick();}
 static void hit(SewerLogic s,int low,int high)throws Exception{check(s.input(4),"rat crossing");for(int i=0;i<16&&s.fall==0&&!s.completed();i++)s.tick();check(s.fall>0&&s.fallX>=low&&s.fallX<=high,"expected rat not hitting at "+s.fallX);}
 static byte[] legacy(int x,int from,int motion,int fall,boolean done)throws Exception{
  java.io.ByteArrayOutputStream b=new java.io.ByteArrayOutputStream();java.io.DataOutputStream o=new java.io.DataOutputStream(b);o.writeInt(0x53575231);o.writeShort(8);o.writeShort(x);o.writeShort(from);o.writeByte(motion);o.writeByte(-1);o.writeByte(fall);o.writeShort(256);o.writeInt(2);o.writeBoolean(done);o.close();return b.toByteArray();
 }
 public static void main(String[] args)throws Exception{
  for(int rat=0;rat<3;rat++){
   boolean struck=false;int center=new int[]{224,304,416}[rat];
   for(int wait=0;wait<20&&!struck;wait++){SewerLogic trial=rat==2?new SewerLogic(state()):middle();for(int i=0;i<wait;i++)trial.tick();trial.input(4);for(int i=0;i<16&&trial.fall==0&&!trial.completed();i++)trial.tick();struck=trial.fall>0&&Math.abs(trial.fallX+32-center)<=12;}
   check(struck,"rat "+rat+" cannot strike");
  }
  SewerLogic safe=middle();for(int i=0;i<200;i++)safe.tick();check(safe.fall==0&&safe.x==328,"safe stop inside rat trajectory");
  waitFor(safe,15);check(safe.input(6),"return to start");int destination=safe.x;check(!safe.input(4)&&safe.x==destination,"direction cancels crossing");
  while(safe.busy())safe.tick();check(safe.x==448&&safe.fall==0&&safe.falls==0,"rightward safe leg");
  for(int position:new int[]{192,256,384}){GameState old=state();old.location=legacy(position,position,0,0,false);SewerLogic migrated=new SewerLogic(old);check(migrated.x==448&&migrated.motion==0&&migrated.clock==8,"old unsafe stop migration");}
  GameState old=state();old.location=legacy(320,384,2,0,false);check(new SewerLogic(old).x==448,"old mid-step migration");
  old=state();old.location=legacy(256,320,0,9,false);SewerLogic falling=new SewerLogic(old);check(falling.fall==9&&falling.fallX==256,"old fall migration");
  old=state();old.location=legacy(128,192,0,0,true);check(new SewerLogic(old).completed(),"old completed save migration");
  old=state();byte[] oldRoute=legacy(320,320,0,0,false);oldRoute[3]=0x32;old.location=oldRoute;check(new SewerLogic(old).x==328,"SWR2 safe stop migration");
  SewerLogic fail=new SewerLogic(state());check(!fail.input(2)&&!fail.input(5)&&!fail.input(8),"extra controls");
  for(int i=0;i<100&&fail.fall==0;i++){fail.input(4);fail.tick();}
  check(fail.fall>0,"rats never catch hurried crossing");SewerLogic replay=copy(fail);int falls=fail.falls;final java.util.List<Integer> cues=new java.util.ArrayList<Integer>();fail.setSound(new SoundSink(){public void play(int n){cues.add(n);}});boolean swimming=false,returned=false;
  while(fail.fall>0){if(fail.fall>=21&&fail.fall<72)swimming=true;if(fail.fall>=72&&fail.x==448)returned=true;check(!fail.input(6),"fall accepts walk");fail.tick();replay.tick();check(Arrays.equals(snapshot(fail),snapshot(replay)),"fall RMS replay");replay=copy(replay);}
  check(swimming&&returned&&cues.contains(17),"missing swim/return/voice");
  check(fail.x==448&&fail.motion==0&&fail.falls==falls,"automatic start respawn");
  int[] keys=solution();SewerLogic s=new SewerLogic(state());replay=copy(s);
  for(int key:keys){check(s.input(key)==replay.input(key),"saved input");s.tick();replay.tick();if(s.motion==0&&s.fall==0)check(s.x==128||s.x==328||s.x==448,"stopped under a rat");check(Arrays.equals(snapshot(s),snapshot(replay)),"crossing RMS replay");replay=copy(replay);}
  check(s.completed()&&s.x==128&&s.hero()==ZooLogic.COLLEAGUE&&!s.hasItem(1),"single Colleague crossing");
  check(SceneFlow.next(6,true)==7&&SceneFlow.next(7,true)==8,"street/sewer/hotel boundary");
  GameState save=state();save.location=snapshot(s);check(GameState.decode(save.encode(),18).location.length>0,"scene7 extension");
  System.out.println("SewerWorld: all three rat hits, safe stops only, uninterruptible 4/6 legs, automatic retry, old-save migration, timed solution and every-tick save replay passed in "+keys.length+" ticks "+Arrays.toString(keys));
 }
}
