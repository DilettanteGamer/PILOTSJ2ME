package pilots;
import java.util.Arrays;
/** Section 16 quest, including original alternative actors inside the basket. */
public final class RoofWorldTest {
 static HotelWorld world,replay;static int ticks;static int[] cues=new int[128];
 static void check(boolean b,String m){if(!b)throw new RuntimeException(m);}
 static HotelWorld copy(HotelWorld w)throws Exception{w.syncSave();byte[] b=w.state.encode();GameState s=GameState.decode(b,16);HotelWorld c=new HotelWorld(s);check(Arrays.equals(b,s.encode()),"roof restore mutated snapshot");return c;}
 static void tick()throws Exception{world.tick();replay.tick();check(Arrays.equals(world.state.encode(),replay.state.encode()),"roof replay "+ticks);replay=copy(replay);ticks++;}
 static void settle()throws Exception{int n=2500;while(world.busy()&&n-->0)tick();check(n>0,"roof lock "+world.action()+"/"+world.step()+" busyFlag="+world.g(0x88)+" hero="+world.hero()+" walk="+world.walkHero+" aux="+world.g(0x1e1b6c)+" seat="+world.g(0x1e1b60)+","+world.g(0x1e1b5c)+" restore="+world.g(0x1e1b80)+","+world.g(0x1e1b84));tick();}
 static void act(int hero,int item,int id)throws Exception{check(world.select(hero)&&replay.select(hero),"roof hero "+hero);check(world.selectItem(item)&&replay.selectItem(item),"roof item "+item);check(world.interact(id)&&replay.interact(id),"roof target "+id);settle();}
 static void move(int hero,int x,int y)throws Exception{check(world.select(hero)&&replay.select(hero),"move select");check(world.move(x,y)&&replay.move(x,y),"roof move");settle();}
 static boolean find(int id){for(HotelWorld.Thing t:world.cells)if(t!=null&&t.id==id)return true;return false;}
 static void idle(int n)throws Exception{for(int i=0;i<n;i++)tick();check(!world.busy(),"roof idle stranded action "+world.action()+"/"+world.step());}
 static void latePaint()throws Exception{
  GameState s=new GameState();s.scene=14;world=new HotelWorld(s);replay=copy(world);settle();
  act(0,0,15);act(0,0,23);act(0,0,28);move(0,384,384);act(1,0,27);act(0,4,18);act(0,0,39);act(0,0,17);
  idle(1200);check(find(47)&&world.heroes[1].active,"Chief waiting in basket changed actors");
  act(1,1,22);idle(1200);act(1,0,17);idle(1200);act(1,3,35);check(world.completed,"late green paint blocks final flight");
 }
 public static void main(String[] args)throws Exception{
  GameState s=new GameState();s.scene=14;world=new HotelWorld(s);replay=copy(world);world.sound(new SoundSink(){public void play(int c){cues[c]++;}});
  check(world.action()==99&&!world.select(1),"intro accepted input");settle();check(cues[12]==1,"original entrance sound");
  check(world.heroes[0].active&&world.heroes[1].active,"entrance lost heroes");
  act(0,0,22);check(!world.hasItem(1),"Chief acquired paint from antagonist");
  act(0,0,15);act(1,0,16);check(world.hasItem(1)&&world.hasItem(2),"both source paint pickups");
  act(1,2,22);check(world.g(0x1e1b7c)==0&&!world.hasItem(2),"red traffic light branch");act(0,0,16);
  act(1,1,22);check(world.g(0x1e1b7c)==1&&!world.hasItem(1),"green traffic light branch");
  act(0,0,23);act(0,0,28);check(world.hasItem(3)&&world.hasItem(4),"scissors/ladder HUD mapping");
  act(1,0,27);check(world.g(0x1e1b70)==0,"tap bypassed Chief placement");
  move(0,384,384);check(world.g(0x1e1b64)==1,"Chief hose stance");act(1,0,27);check(world.g(0x1e1b70)==1,"cooperative tap");
  act(0,4,18);check(!world.hasItem(4)&&find(17),"source ladder installation");
  act(0,0,39);check(world.g(0x1e1b78)==1&&world.g(0x1e1b74)==0,"hose did not inflate balloon");
  act(0,0,17);check(find(47)&&!world.heroes[0].active,"Chief basket pose");
  move(0,256,208);check(world.heroes[0].active&&!find(47),"Chief cannot leave basket through original descent");
  act(0,0,17);act(1,0,17);check(find(46)&&find(47)&&!world.heroes[1].active,"both basket poses");
  check(world.select(0)&&replay.select(0)&&world.select(1)&&replay.select(1),"basket actors unselectable");
  byte[] basket=world.state.encode();
  act(1,2,35);check(find(46)&&find(47)&&!world.completed,"wrong paint on rope blocked basket actor");
  act(0,0,15);check(world.heroes[0].active&&world.hasItem(1),"basket item click did not descend and resume source action");
  world=new HotelWorld(GameState.decode(basket,16));replay=copy(world);world.sound(new SoundSink(){public void play(int c){cues[c]++;}});
  act(1,3,35);check(world.completed&&SceneFlow.next(15,true)==14&&SceneFlow.next(14,true)==16,"source flight/scene boundary");
  for(HotelWorld.Thing t:world.cells)if(t!=null&&t.id==46)check(t.clip<0,"departed Colleague returned to basket rest pose");
  check(cues[44]==1,"final flight voice missing");
  latePaint();
  if(args.length>0){
   world=new HotelWorld(GameState.decode(java.nio.file.Files.readAllBytes(java.nio.file.Paths.get(args[0])),16));replay=copy(world);
   check(world.action()==0&&!world.busy()&&find(47),"legacy orphaned idle did not recover");
   act(1,1,22);idle(600);act(1,0,17);idle(600);act(1,3,35);check(world.completed,"recovered 0.20.0 save cannot finish");
  }
  System.out.println("RoofWorld: original intro, incorrect actions, both paints, green/red branch, ladder, scissors, hose cooperation, both basket actors, descent/reentry, flight and "+ticks+" every-tick save replays passed");
 }
}
