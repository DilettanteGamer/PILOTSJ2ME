package pilots;
import java.util.*;
/** Source section 15: original rotations, electrical completion, extinguisher and both exits. */
public final class SwitchboardWorldTest {
 static HotelWorld world,replay;static int ticks,turns;
 static void check(boolean b,String m){if(!b)throw new RuntimeException(m);}
 static HotelWorld copy(HotelWorld w)throws Exception {w.syncSave();byte[] bytes=w.state.encode();GameState s=GameState.decode(bytes,14);HotelWorld c=new HotelWorld(s);c.syncSave();check(Arrays.equals(bytes,s.encode()),"switchboard save changed");return c;}
 static void tick()throws Exception {world.tick();replay.tick();ticks++;check(Arrays.equals(world.state.encode(),replay.state.encode()),"switchboard replay divergence");replay=copy(replay);}
 static void settle()throws Exception {int limit=3000;while(world.busy()&&!world.boardRequested()&&limit-->0)tick();check(limit>0,"switchboard lock "+world.action()+"/"+world.step());}
 static void act(int hero,int item,int id)throws Exception {check(world.select(hero)&&replay.select(hero),"switchboard select");check(world.selectItem(item)&&replay.selectItem(item),"switchboard item");check(world.interact(id)&&replay.interact(id),"switchboard target "+id);settle();}
 static void turn(int i)throws Exception {
  int old=world.boardOrientation(i);int[] others=new int[27];for(int n=0;n<27;n++)others[n]=world.boardOrientation(n);
  check(world.turnBoard(i)&&replay.turnBoard(i),"source turn rejected");turns++;
  check(world.boardOrientation(i)==old%(i>=4&&i<=11?2:4)+1,"source orientation cycle "+i+":"+old+"->"+world.boardOrientation(i)+" action="+world.action());for(int n=0;n<27;n++)if(n!=i)check(world.boardOrientation(n)==others[n],"turn changed another wire");
  check(Arrays.equals(world.state.encode(),replay.state.encode()),"source turn replay");replay=copy(replay);
 }
 public static void main(String[] args)throws Exception {
  GameState s=new GameState();s.scene=13;world=new HotelWorld(s);replay=copy(world);settle();
  check(world.heroes[0].clip>=0&&world.heroes[1].clip>=0,"intro lost heroes");
  check(world.heroes[0].layer==30-world.heroes[0].y/8,"source room depth");
  act(1,0,48);act(1,0,16);check(!world.boardRequested(),"Colleague opened Chief puzzle");
  act(0,0,16);check(world.boardRequested()&&world.action()==0,"detail opened before source approach");
  world.beginBoard();replay.beginBoard();byte[] paused=world.state.encode();tick();check(Arrays.equals(paused,world.state.encode()),"detail did not pause room");
  check(!world.select(1)&&!world.move(192,192),"room accepted input under detail");
  turn(0);turn(0);turn(0);turn(0);world.endBoard();replay.endBoard();act(0,0,16);world.beginBoard();replay.beginBoard();
  world=copy(world);replay=copy(world);check(world.boardRequested(),"partial detail save did not reopen");
  for(int i=0;i<27;i++){int limit=4;while(world.boardOrientation(i)!=1&&limit-->0)turn(i);check(limit>=0,"wire did not solve");}
  check(!world.boardRequested()&&world.action()==97,"solution did not resume original electrical scene");settle();
  act(0,0,51);check(world.hasItem(1),"extinguisher pickup");act(0,1,52);act(0,0,52);
  check(world.completed&&world.g(0x1e1bcc)==1&&world.g(0x1e1bd0)==1,"source elevator exit requires both brothers");
  check(SceneFlow.next(12,true)==13&&SceneFlow.next(13,true)==13,"switchboard scene boundaries");
  System.out.println("SwitchboardWorld: original intro, 27 wires, "+turns+" source turns, detail pause/reopen, full quest, both exits and "+ticks+" save replays passed");
 }
}
