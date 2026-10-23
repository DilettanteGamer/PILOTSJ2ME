package pilots;
import java.util.*;
/** Source-driven hotel entrance, pickups, switch puzzle and finale, including save/replay. */
public final class HotelWorldTest {
 static HotelWorld world,replay;static Set<Integer> actions=new TreeSet<Integer>();static List<Integer> cues=new ArrayList<Integer>();static int restoredMusic;
 static void check(boolean ok,String text){if(!ok)throw new RuntimeException(text);}
 static HotelWorld resume(HotelWorld w)throws Exception{w.syncSave();byte[] before=w.state.encode();GameState s=GameState.decode(before,10);HotelWorld copy=new HotelWorld(s);copy.syncSave();check(Arrays.equals(before,s.encode()),"hotel snapshot changed");return copy;}
 static void tick()throws Exception{
  if(world.scene==9 && (world.completed || world.action()==87 || world.action()==84 && world.step()>=10)) {
   HotelWorld saved=resume(world);restoredMusic=0;saved.sound(new MusicSink(){public void play(int n){}public void music(int n){restoredMusic=n;}});check(restoredMusic==2,"ending music not restored");
  }
  actions.add(world.action());world.tick();replay.tick();check(Arrays.equals(world.state.encode(),replay.state.encode()),"hotel replay diverged "+world.action()+"/"+world.step());replay=resume(replay);}
 static void settle()throws Exception{int n=1800;while(world.busy()&&n-->0)tick();check(n>0,"hotel hangs "+world.action()+"/"+world.step());}
 static void act(int hero,int item,int id)throws Exception{check(world.select(hero)&&replay.select(hero),"hero selection");check(world.selectItem(item)&&replay.selectItem(item),"inventory selection "+item);check(world.interact(id)&&replay.interact(id),"target rejected "+id);settle();}
 static int mask(HotelWorld w){int value=0;for(int i=0;i<16;i++)if(w.at(2+i%4*2,3+i/4*2)>=80)value|=1<<i;return value;}
 public static void main(String[] args)throws Exception{
  GameState state=new GameState();state.scene=8;world=new HotelWorld(state);replay=resume(world);world.sound(new SoundSink(){public void play(int n){cues.add(n);}});
  boolean[] seen=new boolean[217];boolean first=false,second=false;int budget=240;
  while(!world.completed&&budget-->0){for(HotelWorld.Thing t:world.cells)if(t!=null&&t.clip==46)seen[t.frame]=true;first|="Похоже Мы у цели".equals(world.caption());second|="Попробуйте ключем".equals(world.caption());tick();}
  check(budget>0&&first&&second,"entrance dialogue/timeline");for(int i=0;i<217;i++)check(seen[i],"missing entrance frame "+i);
  check(cues.equals(Arrays.asList(12,14,14,14,5,13,15)),"entrance audio "+cues);
  check(SceneFlow.next(6,true)==8 && SceneFlow.next(8,true)==9 && SceneFlow.next(9,true)==9 && SceneFlow.next(6,false)==6,"sewer exclusion/exit boundaries");
  state=new GameState();state.scene=9;world=new HotelWorld(state);replay=resume(world);settle();
  check(world.heroes[0].id==13&&world.heroes[1].id==12,"hotel hero roles");check(!world.hasItem(1)&&!world.hasItem(2)&&!world.hasItem(3),"phantom inventory");
  byte[] initial=state.encode();
  // Every initially present interactive object has both character branches; none may lock input.
  for(int h=0;h<2;h++)for(int id=20;id<=37;id++){world=new HotelWorld(GameState.decode(initial,10));replay=resume(world);world.select(h);replay.select(h);boolean a=world.interact(id),b=replay.interact(id);check(a==b,"action acceptance replay");if(a)settle();}
  world=new HotelWorld(GameState.decode(initial,10));replay=resume(world);
  act(0,0,26);check(world.hasItem(1),"Chief shaving cream pickup");act(1,0,34);check(world.hasItem(2),"Colleague key pickup");
  act(1,1,29);act(0,1,27); // reveal the monster, then scare it away using the cream.
  act(1,0,31);act(1,0,25);check(world.hasItem(3),"sugar bowl reveals screwdriver; pile pickup");
  act(0,2,33);check(world.at(6,16)==254,"Chief must unlock the refrigerator");act(0,0,35);check(!world.completed,"unsolved refrigerator opened");
  int original=mask(world);byte[] base=world.state.encode();int[] delta=new int[16];
  for(int i=0;i<16;i++){world=new HotelWorld(GameState.decode(base,10));replay=resume(world);act(0,0,world.at(2+i%4*2,3+i/4*2));delta[i]=original^mask(world);int expected=0;for(int j=0;j<16;j++)if(j/4==i/4||j%4==i%4)expected|=1<<j;check(delta[i]==expected,"source row/column toggle "+i);}
  int solution=-1;for(int choice=0;choice<65536;choice++){int m=original;for(int i=0;i<16;i++)if((choice&(1<<i))!=0)m^=delta[i];if(m==0){solution=choice;break;}}
  check(solution>=0,"unsolvable source refrigerator");world=new HotelWorld(GameState.decode(base,10));replay=resume(world);
  for(int i=0;i<16;i++)if((solution&(1<<i))!=0)act(0,0,world.at(2+i%4*2,3+i/4*2));
  check(mask(world)==0,"refrigerator solution did not persist");act(0,0,35);check(world.completed&&actions.contains(84)&&actions.contains(87),"hotel finale and both heroes' exit");
  System.out.println("HotelWorld: all 217 entrance frames/voices, both hero action branches, three pickups, original 16 row/column switches, locked/solved refrigerator, finale and every-tick HWS1 replay passed "+actions);
 }
}
