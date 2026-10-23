package pilots;
import java.util.*;
/** Original section 7 predicates, early passage-directed loop pursuit and tree ambush, capture, key dialogue and GWS2 resume and GWS1 migration. */
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
        Set<Integer> actions=new TreeSet<Integer>();
        boolean caption=false,waiting=false,early=false,loop=false;
        List<Integer> cues=new ArrayList<Integer>();
        SoundSink sound=new SoundSink(){ public void play(int cue){cues.add(cue);} };world.sound(sound);
        // Place the Chief at the original map-41 tree, then drive the monkey with
        // the Colleague. Pursuit must not require either hero to overlap the monkey.
        check(world.move(384,272) && replay.move(384,272),"tree approach");
        int commands=0,oldX=-1,oldY=-1;
        for(int tick=0;tick<4000 && !world.completed;tick++) {
            if(!world.busy()) {
                if(commands==0) {
                    check(world.heroes[0].x==384 && world.heroes[0].y==272,"Chief missed the ambush tree");
                    check(world.select(1) && replay.select(1),"Colleague chase selection");
                }
                GardenWorld.Thing monkey=null;
                for(GardenWorld.Thing t:world.cells) if(t!=null && t.id==14)monkey=t;
                if(monkey!=null) {check(world.move(monkey.x,monkey.y) && replay.move(monkey.x,monkey.y),"pursuit input");commands++;}
            }
            actions.add(world.action());
            if(world.action()==51) {
                waiting=true;check(!world.completed,"completed before confirmation");
                check(world.interact(14) && replay.interact(14),"key dialogue confirmation");
            }
            world.tick();replay.tick();
            for(GardenWorld.Thing t:world.cells) if(t!=null && t.id==14) {
                check(t.clip>=0,"monkey disappeared between walking clips");
                for(int h=0;h<2;h++) check(t.x!=world.heroes[h].x || t.y!=world.heroes[h].y,"monkey ran into a hero");
                if(oldX>=0 && (t.x!=oldX || t.y!=oldY)) {
                    int distance=10000;
                    for(int h=0;h<2;h++)distance=Math.min(distance,Math.abs(t.fromX-world.heroes[h].x)/64+Math.abs(t.fromY-world.heroes[h].y)/16);
                    if(distance>=3)early=true;
                    GardenWorld.Thing pursuer=world.heroes[1];
                    int dx=pursuer.x-t.fromX,dy=pursuer.y-t.fromY;
                    if(world.hero()==1 && distance<=3 && (dx==0 && t.x==t.fromX || dy==0 && t.y==t.fromY))
                        check(Math.abs(t.x-pursuer.x)+Math.abs(t.y-pursuer.y)>=Math.abs(dx)+Math.abs(dy),"monkey ran toward nearby Colleague");
                }
                if(t.x==512 && t.y==144)loop=true;oldX=t.x;oldY=t.y;
            }
            if(world.action()==12)check(world.hero()==1 && world.heroes[0].x==384 && world.heroes[0].y==288,"capture did not start from Chief's tree");
            if(world.caption()!=null && world.caption().indexOf("Карбофоса")>=0)caption=true;
            check(Arrays.equals(world.state.encode(),replay.state.encode()),"garden replay diverged");
            replay=resume(replay);
        }
        check(early && loop && commands>1,"monkey did not flee early around the garden");
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
        if(args.length>0) {
            java.io.FileInputStream in=new java.io.FileInputStream(args[0]);
            byte[] bytes=new byte[(int)new java.io.File(args[0]).length()];new java.io.DataInputStream(in).readFully(bytes);in.close();
            world=new GardenWorld(GameState.decode(bytes,6));
            check(world.heroes[0].x%64==0 && world.walkHero==0,"legacy walking save did not migrate");
            int budget=2000;while(world.busy() && budget-->0)world.tick();
            check(budget>0 && world.heroes[0].x==384 && world.heroes[0].y==272,"migrated tree approach failed");resume(world);
        }
        System.out.println("GardenWorld: early passage-directed loop pursuit, tree ambush, no head-on chase, key caption/audio, every-tick GWS2 replay and GWS1 migration passed");
    }
}
