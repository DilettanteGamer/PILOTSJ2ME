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
 public static void main(String[] args)throws Exception{
  SewerLogic fail=new SewerLogic(state());check(!fail.input(2)&&!fail.input(5)&&!fail.input(8),"extra controls");
  for(int i=0;i<100&&fail.fall==0;i++){fail.input(4);fail.tick();}
  check(fail.fall>0,"rats never catch hurried crossing");SewerLogic replay=copy(fail);int falls=fail.falls;
  while(fail.fall>0){check(!fail.input(6),"fall accepts walk");fail.tick();replay.tick();check(Arrays.equals(snapshot(fail),snapshot(replay)),"fall RMS replay");replay=copy(replay);}
  check(fail.x==448&&fail.motion==0&&fail.falls==falls,"automatic start respawn");
  int[] keys=solution();SewerLogic s=new SewerLogic(state());replay=copy(s);
  for(int key:keys){check(s.input(key)==replay.input(key),"saved input");s.tick();replay.tick();check(Arrays.equals(snapshot(s),snapshot(replay)),"crossing RMS replay");replay=copy(replay);}
  check(s.completed()&&s.x==128&&s.hero()==ZooLogic.COLLEAGUE&&!s.hasItem(1),"single Colleague crossing");
  check(SceneFlow.next(6,true)==7&&SceneFlow.next(7,true)==8,"street/sewer/hotel boundary");
  GameState save=state();save.location=snapshot(s);check(GameState.decode(save.encode(),18).location.length>0,"scene7 extension");
  System.out.println("SewerWorld: original rat windows, direct 4/6 commands, fall/automatic retry, one Colleague, timed solution and every-tick save replay passed in "+keys.length+" ticks "+Arrays.toString(keys));
 }
}
