package pilots;
import java.util.*;
/** Source-driven hotel entrance, pickups, switch puzzle and finale, including save/replay. */
public final class HotelWorldTest {
 static HotelWorld world,replay;static Set<Integer> actions=new TreeSet<Integer>();static List<Integer> cues=new ArrayList<Integer>();static int restoredMusic;static int[] exitFrames=new int[2];
 static void check(boolean ok,String text){if(!ok)throw new RuntimeException(text);}
 static HotelWorld resume(HotelWorld w)throws Exception{w.syncSave();byte[] before=w.state.encode();GameState s=GameState.decode(before,10);HotelWorld copy=new HotelWorld(s);copy.syncSave();check(Arrays.equals(before,s.encode()),"hotel snapshot changed");return copy;}
 static void tick()throws Exception{
  if(world.scene==9 && world.action()==87)for(int h=0;h<2;h++) {
   HotelWorld.Thing t=world.heroes[h];if(t.active&&t.clip>=20&&t.clip<=33)exitFrames[h]|=1<<t.frame;
  }
  if(world.scene==9 && (world.completed || world.action()==87 || world.action()==84 && world.step()>=10)) {
   HotelWorld saved=resume(world);restoredMusic=0;saved.sound(new MusicSink(){public void play(int n){}public void music(int n){restoredMusic=n;}});check(restoredMusic==2,"ending music not restored");
  }
  actions.add(world.action());world.tick();replay.tick();check(Arrays.equals(world.state.encode(),replay.state.encode()),"hotel replay diverged "+world.action()+"/"+world.step());replay=resume(replay);}
 static void settle()throws Exception{int n=1800;while(world.busy()&&n-->0)tick();check(n>0,"hotel hangs "+world.action()+"/"+world.step());if(world.scene==9&&!world.completed)for(HotelWorld.Thing h:world.heroes)check(h.clip>=12&&h.clip<=19,"partial hero pose left after action: "+h.id+" clip "+h.clip+" finished "+h.finished);}
 static void act(int hero,int item,int id)throws Exception{check(world.select(hero)&&replay.select(hero),"hero selection");check(world.selectItem(item)&&replay.selectItem(item),"inventory selection "+item);check(world.interact(id)&&replay.interact(id),"target rejected "+id);settle();}
 static void closeFridge(){world.endFridge();replay.endFridge();}
 static void turn(int i)throws Exception{check(world.turnFridge(i)&&replay.turnFridge(i),"fridge input rejected");replay=resume(replay);check(Arrays.equals(world.state.encode(),replay.state.encode()),"fridge replay");}
 static int solve(int mask){for(int c=0;c<512;c++){int value=mask;for(int i=0;i<9;i++)if((c&(1<<i))!=0)value=RefrigeratorPuzzle.turn(value,i);if(value==0)return c;}return -1;}
 public static void main(String[] args)throws Exception{
  GameState state=new GameState();state.scene=8;world=new HotelWorld(state);replay=resume(world);world.sound(new SoundSink(){public void play(int n){cues.add(n);}});
  boolean[] seen=new boolean[217];boolean first=false,second=false;int budget=240;
  while(!world.completed&&budget-->0){for(HotelWorld.Thing t:world.cells)if(t!=null&&t.clip==46)seen[t.frame]=true;first|="Похоже Мы у цели".equals(world.caption());second|="Попробуйте ключем".equals(world.caption());tick();}
  check(budget>0&&first&&second,"entrance dialogue/timeline");for(int i=0;i<217;i++)check(seen[i],"missing entrance frame "+i);
  check(cues.equals(Arrays.asList(12,14,14,14,5,13,15)),"entrance audio "+cues);
  check(SceneFlow.next(6,true)==7 && SceneFlow.next(7,true)==8 && SceneFlow.next(8,true)==9 && SceneFlow.next(9,true)==10 && SceneFlow.next(6,false)==6,"sewer exclusion/exit boundaries");
  state=new GameState();state.scene=9;world=new HotelWorld(state);replay=resume(world);settle();
  check(world.heroes[0].id==13&&world.heroes[1].id==12,"hotel hero roles");check(!world.hasItem(1)&&!world.hasItem(2)&&!world.hasItem(3),"phantom inventory");
  byte[] initial=state.encode();
  // Every initially present interactive object has both character branches; none may lock input.
  for(int h=0;h<2;h++)for(int id=20;id<=37;id++){world=new HotelWorld(GameState.decode(initial,10));replay=resume(world);world.select(h);replay.select(h);boolean a=world.interact(id),b=replay.interact(id);check(a==b,"action acceptance replay");if(a)settle();}
  world=new HotelWorld(GameState.decode(initial,10));replay=resume(world);
  act(0,0,26);check(world.hasItem(1),"Chief shaving cream pickup");check(!world.interact(34)&&!replay.interact(34),"key picked through towel");
  act(1,1,29);check(!world.interact(34)&&!replay.interact(34),"key picked beside active crocodile");act(0,1,27);
  check(world.keyAvailable(),"Chief did not create a key window");
  for(int i=0;i<110;i++)tick();check(!world.keyAvailable()&&!world.interact(34)&&!replay.interact(34),"key window never expired");
  act(0,1,27);act(1,0,34);check(world.hasItem(2),"key pickup in second safe window"); // reveal the monster, then scare it away using the cream.
  act(1,0,31);act(1,0,25);check(world.hasItem(3),"sugar bowl reveals screwdriver; pile pickup");
  act(0,2,33);check(world.at(6,16)==254,"Chief must unlock the refrigerator");act(0,0,35);check(!world.completed,"unsolved refrigerator opened");
  check(world.fridgeRequested(),"detail screen did not wait for Chief approach");world.beginFridge();replay.beginFridge();
  int original=world.fridgeMask();check(original!=0 && original<512,"nine-handle initial state");
  for(int i=0;i<9;i++){int before=world.fridgeMask();turn(i);int expected=0;for(int j=0;j<9;j++)if(j/3==i/3||j%3==i%3)expected|=1<<j;check((before^world.fridgeMask())==expected,"3x3 row/column move");turn(i);}
  int solution=solve(original);check(solution>=0,"unreachable 3x3 combination");
  check(!world.select(1)&&!world.interact(34)&&!world.move(0,400),"room input leaked into detail screen");
  int frozen=world.g(4);tick();check(world.g(4)==frozen,"room timer ran under detail screen");
  for(int i=0;i<9;i++)if((solution&(1<<i))!=0)turn(i);
  check(world.fridgeMask()==0,"three-by-three solution");closeFridge();settle();act(0,0,35);check(world.completed&&actions.contains(84)&&actions.contains(87),"hotel finale and both heroes' exit");
  check(!world.heroes[0].active&&!world.heroes[1].active&&world.state.variables[63]==2,"hotel completed before both exits");
  check(exitFrames[0]==31&&exitFrames[1]==31,"missing native exit gait frames");
  for(HotelWorld.Thing t:world.cells)check(t==null||t.id!=97,"final hold pose left behind");
  for(int fixture=1;fixture<4;fixture++) {
   world=new HotelWorld(GameState.decode(java.nio.file.Files.readAllBytes(java.nio.file.Paths.get(args[fixture])),10));
   if(fixture==3)check(!world.heroes[1].active,"legacy hold pose has a duplicate live actor");
   check(!world.completed,"legacy final save was not resumed");replay=resume(world);settle();
   check(world.completed&&!world.heroes[0].active&&!world.heroes[1].active,"legacy final save did not finish both exits");
  }
  for(int seed=0;seed<65536;seed+=17)check(solve(RefrigeratorPuzzle.initial(seed))>=0,"unsolvable seeded puzzle");
  GameState legacy=GameState.decode(java.nio.file.Files.readAllBytes(java.nio.file.Paths.get(args[0])),10);
  world=new HotelWorld(legacy);for(int h=0;h<2;h++)check(world.g(h==0?0x40:0x44)==58-world.heroes[h].y/8,"legacy depth migration");check(world.hasItem(1)&&!world.hasItem(2)&&solve(world.fridgeMask())>=0,"0.12 save migration");replay=resume(world);
  // Manual walk uses the same per-character depth increments as original native walking.
  for(int h=0;h<2;h++){world.select(h);replay.select(h);boolean moved=false;for(int y=400;y>=272&&!moved;y-=16)for(int x=576;x>=0&&!moved;x-=64){if(world.move(x,y)){check(replay.move(x,y),"walk replay");moved=true;settle();check(world.heroes[h].layer==58-world.heroes[h].y/8,"manual walking depth");}}check(moved,"no room walking route");}
  System.out.println("HotelWorld: all 217 entrance frames/voices, both hero action branches, three pickups, 3x3 detail switches and towel/crocodile key window, locked/solved refrigerator, finale and every-tick HWS1 replay passed "+actions);
 }
}
