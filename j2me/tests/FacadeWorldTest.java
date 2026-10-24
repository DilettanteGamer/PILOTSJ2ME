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
 static void systemRest()throws Exception {
  for(int i=0;i<90;i++){boolean lift=false,rope=false;for(HotelWorld.Thing t:world.cells)if(t!=null){if(t.id==21)lift=t.clip==51&&!t.finished;if(t.id==18)rope=t.clip==54&&!t.finished;}
   check(lift&&rope,"lift/rope vanished after reaction");tick();}
 }
 static void act(int hero,int item,int id)throws Exception{check(world.select(hero)&&replay.select(hero),"ledge hero");check(world.selectItem(item)&&replay.selectItem(item),"ledge item");check(world.interact(id)&&replay.interact(id),"ledge target "+id);settle();}
 static void liftOnce(int hero,boolean manual)throws Exception {
  final List<String> sounds=new ArrayList<String>();world.sound(new SoundSink(){public void play(int cue){sounds.add(cue+":"+world.g(4));}});sounds.clear();
  check(world.select(hero)&&replay.select(hero),"lift actor select");
  if(manual)check(world.move(256,320)&&replay.move(256,320),"unsafe manual lift route");
  else check(world.interact(15)&&replay.interact(15),"unsafe window route");
  int episodes=0,starts=0,withdrawals=0;boolean was=false;
  for(int i=0;i<180;i++) {
   boolean reaction=world.action()==53||world.action()==54;
   if(reaction&&!was)episodes++;was=reaction;
   for(HotelWorld.Thing t:world.cells)if(t!=null&&t.id==21&&t.clip==53&&t.frame==0)starts++;
   if(manual&&reaction&&world.g(4)<5)check(world.heroes[hero].clip>=20&&world.heroes[hero].clip<=33,"lift interrupted the current gait frame");
   if(reaction&&world.g(4)==5){check(world.walkHero==-1&&world.heroes[hero].clip==(hero==0?74:72),"lift reaction missed walking cancellation/withdrawal");withdrawals++;}
   if(reaction&&world.g(4)==6)check(world.heroes[hero].clip==(hero==0?75:73),"lift response animation missing");
   tick();
  }
  check(episodes==1&&starts==1&&withdrawals==1,"duplicate/incomplete lift sequence "+episodes+"/"+starts+"/"+withdrawals);
  check(!world.busy()&&world.walkHero==-1&&world.heroes[hero].x==384,"lift continued original route");
  List<String> relevant=new ArrayList<String>();for(String cue:sounds)if(cue.startsWith("18:")||cue.startsWith("20:")||cue.startsWith("21:"))relevant.add(cue);
  check(relevant.equals(hero==0?Arrays.asList("18:8","18:36"):Arrays.asList("21:5","18:8","20:21","18:36")),"lift voice/effect timing "+relevant);
 }
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
  for(int i=0;i<85;i++){
   for(HotelWorld.Thing t:world.cells)if(t!=null&&t.clip==76)intro[t.frame]=true;
   if(i>=66)for(HotelWorld.Thing h:world.heroes)check(h!=null&&h.active&&h.clip>=0,"characters missing at intro handoff "+i);
   tick();
  }
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
  for(int hero=0;hero<2;hero++)for(int mode=0;mode<2;mode++){
   world=new HotelWorld(GameState.decode(initial,12));replay=resume(world);liftOnce(hero,mode==0);systemRest();quest(0,0);
  }
  for(int exit=0;exit<2;exit++){
   world=new HotelWorld(GameState.decode(initial,12));replay=resume(world);quest(exit,exit);
  }
  // A bad approach must leave a recoverable puzzle, including after saving.
  world=new HotelWorld(GameState.decode(initial,12));replay=resume(world);act(0,0,15);check(!world.completed&&!world.hasItem(1),"unsafe ledge bypass");
  for(int i=0;i<5;i++)tick();systemRest();quest(0,0);
  for(int fixture=0;fixture<args.length;fixture++){
   world=new HotelWorld(GameState.decode(java.nio.file.Files.readAllBytes(java.nio.file.Paths.get(args[fixture])),12));replay=resume(world);
   if(fixture==0)for(HotelWorld.Thing h:world.heroes)check(h!=null&&h.clip>=0,"legacy intro gap not repaired");
   settle();systemRest();quest(0,0);
  }
  check(SceneFlow.next(10,true)==11&&SceneFlow.next(11,true)==12,"ledge chapter routing");
  System.out.println("FacadeWorld: all intro frames, both heroes' branches, fall/recovery, window/shard/rope puzzle, both exit leaders and "+ticks+" every-tick saves passed "+actions);
 }
}
