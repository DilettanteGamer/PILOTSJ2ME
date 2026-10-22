package pilots;

public final class BarrelSceneTest {
    private static void check(boolean ok,String message) {
        if(!ok) throw new RuntimeException(message);
    }
    private static void ticks(ZooScene scene,int count) throws Exception {
        for(int i=0;i<count;i++) scene.update(100);
    }
    public static void main(String[] args) throws Exception {
        GameState state=new GameState(); state.scene=2;
        Scene scene=new Scene(2);
        try {
            ZooScene zoo=new ZooScene(scene,state);
            check(scene.actors[5].visible && !scene.actors[7].visible,"initial kettle and absent cat");
            zoo.activate(90,300);
            check(scene.actors[2].id==75 && scene.actors[2].x==64 && scene.actors[2].y==320,"Chief pickup mark");
            ticks(zoo,6); check(scene.actors[5].visible,"kettle cannot disappear early");
            ticks(zoo,1); check(zoo.logic.hasKettle() && !scene.actors[5].visible,"pickup commits at step 8");
            state=GameState.decode(state.encode(),20); zoo=new ZooScene(scene,state);
            check(zoo.logic.hasKettle() && zoo.logic.step()==8,"resume pickup");
            ticks(zoo,2);
            zoo.logic.selectKettle(true); zoo.activate(150,315);
            check(scene.actors[2].id==69 && scene.actors[2].x==192 && scene.actors[2].y==320,"Chief kettle/barrel pose");
            ticks(zoo,23); check(!scene.actors[7].visible,"cat cannot appear before event");
            ticks(zoo,1); check(scene.actors[7].id==68 && scene.actors[7].visible,"cat map event at step 25");
            check(scene.actors[7].x==192 && scene.actors[7].y==176,"source roof mark");
            ticks(zoo,9); check(!zoo.logic.busy(),"Chief releases at step 34");
            ticks(zoo,38);
            check(!zoo.logic.catPresent() && !scene.actors[7].visible && zoo.logic.butterflyState()==0,
                  "wrong-order cat disappears; no butterfly reward");
            check(zoo.logic.hasKettle(),"kettle available for retry");
            zoo.logic.selectKettle(false); zoo.logic.select(1);
            zoo.activate(180,260); ticks(zoo,92);
            check(zoo.logic.crowState()==2 && scene.actors[0].visible,"original opening chain still works");
            zoo.logic.selectKettle(true); zoo.activate(150,315);
            check(scene.actors[3].id==84 && scene.actors[3].x==192 && scene.actors[3].y==320,
                  "Colleague item reaction binds to his interaction position");
            ticks(zoo,30); zoo.logic.select(0); zoo.activate(150,315);
            ticks(zoo,24); check(zoo.logic.catPresent(),"retry spawns cat");
            ticks(zoo,32);
            check(zoo.logic.crowState()==0 && scene.actors[4].visible && scene.actors[4].id==88 && scene.actors[7].id==89,
                  "cat chases crow only when present");
            state=GameState.decode(state.encode(),20);
            int catFrame=scene.actors[7].clip.index;
            int crowExitFrame=scene.actors[4].clip.index;
            zoo=new ZooScene(scene,state);
            check(scene.actors[7].clip.index==catFrame && scene.actors[4].visible && scene.actors[4].id==88 && scene.actors[4].clip.index==crowExitFrame,"resume chase after crow removal");
            ticks(zoo,13);
            check(zoo.logic.butterflyState()==1 && scene.actors[8].visible && scene.actors[8].id==90,"source butterfly spawn");
            ticks(zoo,28);
            check(!zoo.logic.catPresent() && scene.actors[8].id==91 && scene.actors[8].visible,"cat cleanup and persistent butterfly");
            check(scene.actors[0].visible && zoo.logic.hasKettle(),"window and inventory preserved across chain");
            check(!zoo.logic.crowExiting() && !scene.actors[4].visible,"catalog animation 18 completes and releases its exit actor");
        } finally { scene.close(); }
        System.out.println("BarrelScene: pickup, inventory use, wrong-order retry, cat/crow/butterfly and resumption passed");
    }
}
