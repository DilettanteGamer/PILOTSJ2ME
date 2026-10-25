package pilots;
import java.util.*;
/** Original section 8, including the autonomous Chief and timed street controllers. */
public final class StreetWorldTest {
    static StreetWorld world,replay;static Set<Integer> actions=new TreeSet<Integer>();
    static List<Integer> cues=new ArrayList<Integer>();static boolean hiddenColleague;static boolean[] jumpFrames=new boolean[17];
    static void check(boolean ok,String message){if(!ok)throw new RuntimeException(message);}
    static StreetWorld resume(StreetWorld w)throws Exception {
        w.syncSave();byte[] before=w.state.encode();GameState state=GameState.decode(before,7);
        StreetWorld copy=new StreetWorld(state);copy.syncSave();
        check(Arrays.equals(before,state.encode()),"street snapshot changed");return copy;
    }
    static void tick()throws Exception {
        actions.add(world.action());world.tick();replay.tick();
        check(Arrays.equals(world.state.encode(),replay.state.encode()),"street replay diverged at "+world.action()+"/"+world.step());
        replay=resume(replay);
        if(!world.heroes[1].active){check(world.action()>=61 || world.completed,"Colleague vanished before sewer exit");hiddenColleague=true;}
        if(world.action()==61 && world.step()>=21 && world.step()<=37) {
            StreetWorld.Thing jump=null;for(StreetWorld.Thing effect:world.effects)if(effect!=null && effect.id==11 && effect.clip==79)jump=effect;
            check(jump!=null && jump.frame==world.step()-21,"Colleague exit animation missing at sewer step "+world.step());
            jumpFrames[jump.frame]=true;
        }
        check(world.heroes[0].active && world.heroes[0].clip>=0,"autonomous Chief vanished");
        if(world.heroes[1].active && world.action()!=61)check(world.heroes[1].clip>=0,"Colleague lost visible pose");
    }
    static void settle()throws Exception {int n=1600;while(world.busy()&&n-->0)tick();check(n>0,"street action hangs "+world.action()+"/"+world.step());}
    static void act(int id)throws Exception {check(world.interact(id)&&replay.interact(id),"street target rejected "+id);settle();}
    static void item(int id)throws Exception {check(world.selectItem(id)&&replay.selectItem(id),"street item rejected");}
    public static void main(String[] args)throws Exception {
        GameState state=new GameState();state.scene=6;world=new StreetWorld(state);
        world.sound(new SoundSink(){public void play(int cue){cues.add(cue);}});replay=resume(world);
        check(world.inGroup(0,18) && world.inGroup(0,20) && !world.inGroup(0,11) && !world.inGroup(0,12),"street source class 0 changed");
        check(!world.move(320,240),"manual movement bypassed the scripted road crossing");
        check(world.hero()==1 && world.heroes[0].id==12 && world.heroes[1].id==11,"source street hero roles");
        check(!world.select(0) && !world.interact(18),"Chief manual control or phantom corkscrew");
        // The player can act while the Chief independently walks to his source bench mark.
        check(!world.move(448,368),"manual walk stole the Chief's approach marker");
        check(world.move(320,400) && replay.move(320,400),"manual walk during Chief approach");settle();
        act(19);check(world.at(5,24)==18,"corkscrew was not found in the bin");
        check(world.heroes[0].x==448 && world.heroes[0].y==368,"Chief did not reach his source waiting point");
        act(18);check(world.hasItem(1),"corkscrew pickup failed");act(21);item(1);
        act(19);check(world.hasItem(1),"wrong item reaction consumed corkscrew");
        check(actions.contains(13),"wrong action branch missing");
        act(21);check(!world.completed && world.at(3,21)!=20,"failed cover attempt completed the puzzle");item(0);
        act(17);check(actions.contains(51) && !world.completed,"first crossing/traffic reaction missing");
        check(world.at(3,21)!=20,"opened hatch exists before successful crossing");
        int budget=1200;while(world.at(9,18)!=25 && budget-->0)tick();check(budget>0,"source pedestrian timer did not fire");
        act(16);check(actions.contains(60) && world.at(3,22)==20,"timed crossing did not open the sewer route");
        act(20);check(world.completed && actions.contains(61) && actions.contains(62) && actions.contains(63),"sewer finale incomplete "+actions);
        for(int frame=0;frame<jumpFrames.length;frame++)check(jumpFrames[frame],"missing jump frame "+frame);
        check(hiddenColleague && !world.heroes[1].active && world.heroes[0].x==256 && world.heroes[0].y==352,"source sewer exit positions");
        check(cues.contains(31) && cues.contains(27) && cues.contains(25) && cues.contains(26),"finale or traffic sound missing "+cues);
        check(SceneFlow.next(5,true)==6 && SceneFlow.next(6,true)==7 && SceneFlow.next(7,true)==8,"fifth location transition boundary");
        System.out.println("StreetWorld: autonomous Chief, bin/corkscrew, wrong item, cover, timed crossing, traffic, sewer finale/audio and every-tick SWS1 replay passed "+actions);
    }
}
