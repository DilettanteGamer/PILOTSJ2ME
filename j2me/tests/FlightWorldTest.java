package pilots;
import java.util.Arrays;
/** Original three height bands, finite ballast, crow timing and all three source encounters. */
public final class FlightWorldTest {
 static HotelWorld world,replay;static int ticks;
 static void check(boolean b,String m){if(!b)throw new RuntimeException(m);}
 static HotelWorld copy(HotelWorld w)throws Exception{w.syncSave();byte[] b=w.state.encode();GameState s=GameState.decode(b,17);HotelWorld c=new HotelWorld(s);check(Arrays.equals(b,s.encode()),"flight roundtrip changed");return c;}
 static void tick()throws Exception{world.tick();replay.tick();ticks++;check(Arrays.equals(world.state.encode(),replay.state.encode()),"flight replay "+ticks);replay=copy(replay);}
 static void settle()throws Exception{int n=250;while(world.busy()&&n-->0)tick();check(n>0,"flight lock "+world.action()+"/"+world.step());}
 static void key(int key)throws Exception{int id=key==2?17:key==5?16:15;check(world.interact(id)&&replay.interact(id),"flight band rejected "+key);}
 static void start()throws Exception{GameState s=new GameState();s.scene=16;world=new HotelWorld(s);replay=copy(world);}
 static void encounter(int band,int initial,int key,int movie)throws Exception{
  start();if(initial==1){key(8);settle();}
  int n=2000;
  while(n-->0 && !(world.g(0x1e1b58)==band*100&&world.g(0x1e1b5c)==5&&!world.busy())){
   if(world.g(0x1e1b58)<50&&!world.busy()&&world.g(0x1e1b54)!=initial){key(initial==1?8:5);}
   tick();
  }
  check(n>0,"source crow did not approach band "+band);key(key);
  int frames=0;while(!world.completed&&frames++<150){tick();if(world.action()>=41&&world.action()<=43)check(world.action()==movie,"wrong source encounter branch");}
  check(world.completed,"source encounter did not exit "+band);
  for(HotelWorld.Thing t:world.cells)if(t!=null&&t.id==13)check(t.clip<0,"balloon resurrected after encounter");
 }
 public static void main(String[] args)throws Exception{
  start();check(world.g(0x1e1b54)==2&&world.g(0x94)==3,"source initial altitude/ballast");
  key(2);check(world.g(0x94)==2,"upper band did not consume source ballast");settle();check(world.g(0x1e1b54)==3,"upper band");
  key(8);settle();check(world.g(0x1e1b54)==1,"lower band");key(5);settle();check(world.g(0x1e1b54)==2,"middle band");
  encounter(1,2,8,41);encounter(2,1,5,42);encounter(3,2,2,43);
  check(SceneFlow.next(14,true)==16&&SceneFlow.next(16,true)==17,"flight boundary");
  System.out.println("FlightWorld: original height controls, ballast, all three timed crow encounters/exits and "+ticks+" every-tick save replays passed");
 }
}
