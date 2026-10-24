package pilots;
import java.io.*;
import java.util.*;

/** Execute packaged source rules through actual input, then save/resume complete quests. */
public final class AnimalWorldTest {
    static GameState state;static AnimalWorld world;
    static final Set<Integer> actions=new TreeSet<Integer>();
    static void check(boolean condition,String text) { if(!condition) throw new RuntimeException(text); }
    static void settle() {
        int budget=2000;
        while(world.busy() && budget-->0) { actions.add(world.action());world.tick(); }
        check(budget>0,"Animal sequence hangs: action "+world.action()+" step "+world.step()+" walk "+world.walkHero);
    }
    static void fresh() throws Exception {
        state=new GameState();state.scene=4;world=new AnimalWorld(state);settle();
        check(world.heroes[0].x==192 && world.heroes[0].y==304 && world.heroes[1].x==64 && world.heroes[1].y==208,"original entry sequence ends at source positions");
    }
    static void save() throws Exception {
        world.syncSave();byte[] bytes=state.encode();
        state=GameState.decode(bytes,20);world=new AnimalWorld(state);
        check(Arrays.equals(bytes,state.encode()),"Animal save is not lossless");
    }
    static void act(int hero,int item,int entity) throws Exception {
        check(world.select(hero),"Cannot select hero "+hero);
        check(world.selectItem(item),"Cannot select item "+item);
        check(world.interact(entity),"Cannot approach entity "+entity);
        for(int i=0;i<5 && world.busy();i++) world.tick();save();settle();save();
    }
    static boolean exists(int id) {
        for(AnimalWorld.Thing thing:world.cells) if(thing!=null && thing.id==id) return true;return false;
    }
    static void prepareTree() throws Exception {
        fresh();act(0,0,27);act(1,0,26);act(0,0,25);
        check(world.hasItem(1)&&world.hasItem(2)&&world.hasItem(3),"source inventory pickups");
        act(1,1,22);check(!exists(18),"Colleague's wrong watering action grew bananas");
        act(0,1,22);check(exists(18),"Chief's watering did not create bananas");
        act(1,0,18);check(exists(21),"first attempt did not expose animal path");
        act(0,2,21);check(!world.hasItem(2) && exists(17),"rake/animal path event did not consume the rake and create rhino");
        check(world.select(0)&&world.move(448,208),"Chief returns to cooperation mark");settle();
        act(1,0,18);check(exists(13)&&exists(29)&&world.g(0x18)==3&&!world.heroes[1].active,"Colleague's tree form was not created");
    }
    static void fountain(int turns,boolean blocker) throws Exception {
        prepareTree();
        for(int i=0;i<turns;i++) act(0,0,24);
        if(blocker && (turns==0 || turns==2)) {
            check(world.select(0)&&world.move(turns==0?320:64,turns==0?288:192),"Chief landing blocker setup");settle();
        }
        act(1,3,29);
        check(!exists(13) && !exists(29),"cut bananas did not complete tree removal");
        if(turns==0) check(world.heroes[1].active && world.heroes[1].x==320 && world.heroes[1].y==288,"first fountain destination");
        if(turns==2) check(world.heroes[1].active && world.heroes[1].x==64 && world.heroes[1].y==192,"third fountain destination");
        if(turns==1) {
            act(0,0,24);check(exists(19),"tap/fountain chain did not move kangaroo");
            act(0,0,19);check(world.completed && world.heroes[0].x==576 && world.heroes[0].y==368,"kangaroo exit did not finish the third location");
            check(SceneFlow.next(4,world.completed)==5,"third location exit did not open garden");save();check(world.completed,"completion lost on save");
        }
    }
    public static void main(String[] args) throws Exception {
        fresh();check(world.interact(27),"walking snapshot setup");
        AnimalWorld original=world;GameState replayState=GameState.decode(state.encode(),20);AnimalWorld replay=new AnimalWorld(replayState);
        for(int i=0;i<150;i++) {
            original.tick();replay.tick();
            check(Arrays.equals(original.state.encode(),replay.state.encode()),"save/resume changed walking, action timing or selected hero at tick "+i);
            replayState=GameState.decode(replay.state.encode(),20);replay=new AnimalWorld(replayState);
        }
        for(int h=0;h<2;h++) {
            fresh();for(int entity=25;entity<=27;entity++) { act(h,0,entity);check(world.hero()==h,"aside changed the selected hero"); }
            check(world.hasItem(1)&&world.hasItem(2)&&world.hasItem(3),"pickup coverage for hero "+h);
        }
        for(int turns=0;turns<4;turns++) fountain(turns,false);
        fountain(0,true);fountain(2,true);
        AnimationCatalog catalog=new AnimationCatalog();check(catalog.scene==15 && catalog.ids.length==35,"new preview list");
        check(AnimalWorldTest.class.getResourceAsStream("/scenes/02.animations")==null && AnimalWorldTest.class.getResourceAsStream("/scenes/03.animations")==null,"old preview lists survived");
        check(actions.containsAll(Arrays.asList(51,59,62,65,66,68,71,72,73,74,75,76,78,79,57)),"Missing source action coverage "+actions);
        System.out.println("AnimalWorld: full quest, four fountain states, blocked landings, source action coverage "+actions+", save/resume and third-only previews passed");
    }
}
