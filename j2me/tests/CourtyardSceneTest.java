package pilots;

public final class CourtyardSceneTest {
    private static void check(boolean ok,String message) { if(!ok) throw new RuntimeException(message); }
    private static void ticks(CourtyardScene courtyard,int count) throws Exception { for(int i=0;i<count;i++) courtyard.update(100); }
    private static void settle(CourtyardScene courtyard) throws Exception {
        int budget=2000;while(courtyard.logic.busy() && budget-->0) courtyard.update(100);
        check(budget>0,"courtyard queue terminates");
    }
    private static CourtyardScene restore(Scene scene,GameState state) throws Exception { return new CourtyardScene(scene,state); }
    public static void main(String[] args) throws Exception {
        Scene scene=new Scene(3);
        try {
            GameState state=new GameState();state.scene=3;CourtyardScene courtyard=restore(scene,state);
            check(courtyard.logic.heroX(0)==128 && courtyard.logic.heroX(1)==64,"original hero entry cells");
            check(scene.actors[2].x==256 && scene.actors[2].y==288 && courtyard.layer(2)==23,"original clock root/layer");
            check(scene.actors[8].id==52 && scene.actors[8].x==576 && scene.actors[11].id==66,"piano and first score");
            check("Рояль в кустах".equals(courtyard.hoverText(550,120)),"original Russian piano hover label");
            check(!scene.actors[12].visible && !scene.actors[13].visible && !scene.actors[14].visible,"no early cat/exit/bubbles");
            check(CourtyardWalk.plan(128,272,512,176).length>0,"source walk map connects Chief to piano");
            check(CourtyardWalk.plan(64,272,320,288).length>0,"source walk map connects Colleague to arrow position");
            check(CourtyardWalk.plan(128,272,576,384).length>0,"source walk map connects entrance to fence");
            check(!CourtyardWalk.allowed(256,288),"post cell cannot be walked through");
            check(CourtyardWalk.animalSees(64,224) && CourtyardWalk.animalSees(128,272),"original animal trigger cells");
            check(!CourtyardWalk.animalSees(128,224) && !CourtyardWalk.animalSees(128,304),"query 29 excludes two rectangle corners");
            check(CourtyardWalk.plan(128,272,192,384).length==0,"pond interior is isolated by original blocked cells");
            AnimationCatalog catalog=new AnimationCatalog();
            check(catalog.scene==14 && catalog.ids.length==32,"only balcony previews");
            check(CourtyardSceneTest.class.getResourceAsStream("/scenes/09.animations")==null && CourtyardSceneTest.class.getResourceAsStream("/scenes/03.animations")==null && CourtyardSceneTest.class.getResourceAsStream("/scenes/02.animations")==null,"old preview list removed");
            ticks(courtyard,100);check(courtyard.courtyard.animalPhase()==1 && scene.actors[12].id==74,"cat entry at source 100 ticks");
            ticks(courtyard,17);check(scene.actors[12].id==72 && courtyard.courtyard.animalPhase()==3,"cat retreats when a hero is in original watched cells");
            ticks(courtyard,13);check(scene.actors[14].visible && scene.actors[14].id==75,"original bubble event at tick 130");
            // Colleague cycles all three original scores and says cue 12 at action start.
            for(int i=0;i<3;i++) {
                courtyard.logic.select(1);check(courtyard.request(550,120),"score request");
                int budget=1000;while(courtyard.logic.pendingAction()==0 && budget-->0) courtyard.update(100);
                check(budget>0 && courtyard.logic.pendingClip()==60+i,"source score animation");
                check(courtyard.logic.message()!=null && courtyard.logic.message().indexOf("Маэстро")>=0,"spoken score selection caption");
                ticks(courtyard,7);check(courtyard.courtyard.sheet()==i,"score does not change before step nine");
                ticks(courtyard,1);check(courtyard.courtyard.sheet()==(i+1)%3,"score changes at source step nine");
                state=GameState.decode(state.encode(),20);courtyard=restore(scene,state);settle(courtyard);
            }
            courtyard.logic.select(0);courtyard.request(550,120);settle(courtyard);
            check(!courtyard.courtyard.arrowTurned(),"first melody does not solve the arrow");
            courtyard.logic.select(1);courtyard.request(550,120);settle(courtyard);
            check(courtyard.courtyard.sheet()==1,"second melody selected");
            courtyard.logic.select(0);courtyard.request(550,120);settle(courtyard);
            check(!courtyard.courtyard.arrowTurned(),"right melody with Colleague at piano cannot solve puzzle");
            courtyard.logic.select(1);check(courtyard.request(320,288),"position Colleague at original arrow interaction mark");settle(courtyard);
            courtyard.logic.select(0);courtyard.request(550,120);
            int budget=1000;while(courtyard.logic.pendingAction()==0 && budget-->0) courtyard.update(100);
            ticks(courtyard,11);check(!courtyard.courtyard.arrowTurned(),"no early arrow event");
            ticks(courtyard,1);check(courtyard.courtyard.arrowState()==1 && scene.actors[10].id==56 && scene.actors[13].visible,"original map 23 opens path at step 13");
            check(scene.actors[1].id==58,"Colleague reaction is coupled to second melody");
            state=GameState.decode(state.encode(),20);courtyard=restore(scene,state);settle(courtyard);
            check(courtyard.courtyard.arrowState()==2 && scene.actors[10].id==55,"rotated arrow holds final pose after resume");
            check(courtyard.logic.targetAt(580,390)==0,"original map 23 disables old fence interaction");
            check(courtyard.request(50,220),"original left exit is active");settle(courtyard);
            check(courtyard.logic.completed() && state.scene==3,"second location completes before the canvas transition");
            check(SceneFlow.next(state.scene,courtyard.logic.completed())==4,"courtyard exit schedules animal yard");
            check(courtyard.logic.heroX(0)==0 && courtyard.logic.heroY(0)==288 && courtyard.logic.heroY(1)==272,"original exit formation");
            state=GameState.decode(state.encode(),20);courtyard=restore(scene,state);
            check(courtyard.logic.completed(),"second-location completion persists");
            GameState swapped=new GameState();swapped.scene=3;courtyard=restore(scene,swapped);
            swapped.variables[1]=2;courtyard.logic.select(1);courtyard.request(50,220);settle(courtyard);
            check(courtyard.logic.heroY(1)==288 && courtyard.logic.heroY(0)==272,"exit formation follows selected hero as source map 32");
            GameState resting=new GameState();resting.scene=3;courtyard=restore(scene,resting);
            courtyard.logic.placeHero(0,512,176);courtyard.logic.placeHero(1,320,288);ticks(courtyard,130);
            check(courtyard.courtyard.animalPhase()==2 && scene.actors[12].id==73,"cat waits if neither hero approaches");
            courtyard.logic.placeHero(0,64,224);courtyard.update(100);
            check(courtyard.courtyard.animalPhase()==3,"exact source trigger starts retreat");
            GameState fence=new GameState();fence.scene=3;courtyard=restore(scene,fence);courtyard.logic.select(1);
            check(courtyard.request(580,390),"fence remains usable before arrow puzzle");
            budget=2000;while(courtyard.logic.pendingAction()!=54 && budget-->0) courtyard.update(100);
            check(budget>0 && !scene.actors[0].visible && !scene.actors[1].visible,"source fence crossing removes both actors temporarily");
            fence=GameState.decode(fence.encode(),20);courtyard=restore(scene,fence);settle(courtyard);
            check(courtyard.logic.heroX(0)==576 && courtyard.logic.heroY(0)==96 && courtyard.logic.heroY(1)==80,"original fence landing formation");
            check(scene.actors[0].visible && courtyard.layer(0)==60 && courtyard.layer(1)==63,"landing depth restored");
        } finally { scene.close(); }
        System.out.println("Courtyard: source objects, 3 scores, companion positions, arrow, cat, bubbles, fence, exit and saves passed");
    }
}
