package pilots;
import java.util.*;
/** Original section 7 predicates, physical pursuit, capture, key dialogue and GWS1 resume. */
public final class GardenWorldTest {
    static void check(boolean ok,String text) { if(!ok) throw new RuntimeException(text); }
    static boolean exists(GardenWorld world,int id) { for(GardenWorld.Thing t:world.cells) if(t!=null && t.id==id) return true;return false; }
    static GardenWorld resume(GardenWorld world) throws Exception {
        world.syncSave();byte[] bytes=world.state.encode();
        GameState state=GameState.decode(bytes,6);GardenWorld copy=new GardenWorld(state);copy.syncSave();
        check(Arrays.equals(bytes,state.encode()),"garden snapshot changed state");return copy;
    }
    public static void main(String[] args) throws Exception {
        GameState state=new GameState();state.scene=5;GardenWorld world=new GardenWorld(state);
        check(world.heroes[0].x==128 && world.heroes[1].x==64 && exists(world,14),"original garden entry positions");
        check(!world.hasItem(1) && !world.hasItem(2) && !world.hasItem(3),"old scene inventory carried into garden");
        GardenWorld replay=resume(world);check(world.select(0) && replay.select(0),"Chief selection");
        int[][] route={{384,368},{512,272}};Set<Integer> actions=new TreeSet<Integer>();
        boolean caption=false,waiting=false;List<Integer> cues=new ArrayList<Integer>();
        SoundSink sound=new SoundSink(){ public void play(int cue){cues.add(cue);} };world.sound(sound);
        for(int[] goal:route) {
            check(world.move(goal[0],goal[1]),"garden approach rejected");replay.move(goal[0],goal[1]);
            int budget=2000;
            while(world.busy() && budget-->0) {
                actions.add(world.action());
                if(world.action()==51) {
                    waiting=true;check(!world.completed,"completed before confirmation");
                    check(world.interact(14) && replay.interact(14),"key dialogue confirmation");
                }
                world.tick();replay.tick();
                for(GardenWorld.Thing t:world.cells) if(t!=null && t.id==14) check(t.clip>=0,"monkey disappeared between walking clips");
                if(world.caption()!=null && world.caption().indexOf("Карбофоса")>=0)caption=true;
                check(Arrays.equals(world.state.encode(),replay.state.encode()),"garden replay diverged");
                replay=resume(replay);
            }
            check(budget>0,"garden action hangs "+world.action()+"/"+world.step());
        }
        check(world.hasItem(1),"key missing from the inventory panel");
        check(world.completed && !exists(world,14) && waiting && caption && cues.contains(21),"capture, key line, sound or finish missing "+actions);
        check(world.heroes[0].x==320 && world.heroes[0].y==384 && world.heroes[1].x==256 && world.heroes[1].y==384,"original final regrouping positions");
        check(SceneFlow.next(4,true)==5 && SceneFlow.next(5,true)==5,"garden transitions");
        // Exercise Colleague's wrong approaches, including source action 8 recovery.
        state=new GameState();state.scene=5;world=new GardenWorld(state);Random random=new Random(71);
        for(int i=0;i<60 && !world.completed;i++) {
            world.select(random.nextInt(2));if(!world.move((1+random.nextInt(8))*64,(9+random.nextInt(16))*16))continue;
            int budget=2000;while(world.busy() && budget-->0){if(world.action()==51)world.interact(14);world.tick();if(!world.completed && world.action()<11)check(exists(world,14),"monkey lost before capture");}
            check(budget>0,"wrong hero reaction hangs");
        }
        System.out.println("GardenWorld: original entry/gates, pursuit, capture, regrouping, key caption/audio, confirmation, every-tick save/replay and Colleague reactions passed");
    }
}
