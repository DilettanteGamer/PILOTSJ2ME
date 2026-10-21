package pilots;

public final class FirstLocationSceneTest {
    private static void check(boolean ok,String message) { if(!ok) throw new RuntimeException(message); }
    private static void ticks(ZooScene zoo,int n) throws Exception { for(int i=0;i<n;i++) zoo.update(100); }
    private static void settle(ZooScene zoo) throws Exception {
        int budget=2500;
        while(zoo.logic.busy() && budget-->0) {
            if(zoo.logic.guardPhase()==2) zoo.request(0,0);
            else zoo.update(100);
        }
        check(budget>0,"queued action must terminate");
    }
    public static void main(String[] args) throws Exception {
        GameState state=new GameState();state.scene=2;
        Scene scene=new Scene(2);
        try {
            ZooScene zoo=new ZooScene(scene,state);
            check(!zoo.logic.guardPresent() && scene.actors[11].id==51,"original closed gate and absent guard");
            zoo.request(90,300);ticks(zoo,5);
            check(zoo.logic.walking() && !zoo.logic.hasKettle(),"approach precedes pickup");
            state=GameState.decode(state.encode(),20);zoo=new ZooScene(scene,state);settle(zoo);
            check(zoo.logic.hasItem(1),"resume approach then pick up kettle");
            zoo.request(90,260);settle(zoo);check(zoo.logic.hasItem(2),"pickaxe pickup distinct from kettle");
            zoo.logic.select(1);zoo.request(180,260);settle(zoo);
            check(zoo.logic.windowOpen(),"open window after queued approach");
            zoo.logic.select(0);zoo.logic.selectItem(1);zoo.request(150,315);settle(zoo);
            for(int i=0;i<100 && zoo.logic.catPresent();i++) zoo.update(100);
            check(zoo.logic.butterflyState()!=0,"source cat chain produces coin");
            zoo.logic.selectItem(0);zoo.request(313,316);settle(zoo);
            check(zoo.logic.hasItem(3) && zoo.logic.butterflyState()==0,"coin goes into third inventory slot");
            zoo.logic.selectItem(2);zoo.request(580,230);settle(zoo);
            check(zoo.logic.wallBroken() && zoo.logic.gateState()==0,"pickaxe changes wall, not gate");
            zoo.request(580,230);settle(zoo);check(!zoo.logic.completed(),"blocked wall is not an invented exit");
            zoo.logic.selectItem(3);zoo.request(330,220);settle(zoo);
            check(zoo.logic.gateState()==0,"coin cannot open gate without guard");
            zoo.logic.selectItem(0);zoo.request(455,230);settle(zoo);
            check(zoo.logic.guardPhase()==4 && zoo.logic.guardPresent(),"bell, arrival and original refusal dialogue");
            zoo.logic.selectItem(3);zoo.request(330,220);
            int budget=1000;
            while(zoo.logic.pendingAction()!=30 && budget-->0) zoo.update(100);
            check(budget>0,"payment begins after approach");
            while(zoo.logic.step()<52) zoo.update(100);
            check(zoo.logic.gateState()!=0,"gate opens at source payment event");
            state=GameState.decode(state.encode(),20);zoo=new ZooScene(scene,state);settle(zoo);
            check(zoo.logic.gateState()==2 && zoo.logic.hasItem(3),"resume payment without invented coin consumption");
            check(!zoo.request(330,220) && !zoo.logic.busy(),"open gate does not accept duplicate payment");
            zoo.logic.selectItem(0);zoo.request(330,220);settle(zoo);
            check(zoo.logic.completed() && state.scene==2,"first location completes without scene transition");
            check(zoo.logic.heroX(0)==448 && zoo.logic.heroY(0)==240 && zoo.logic.heroX(1)==448 && zoo.logic.heroY(1)==256,
                  "both heroes reach original exit formation");
            state=GameState.decode(state.encode(),20);zoo=new ZooScene(scene,state);
            check(zoo.logic.completed() && state.scene==2,"completed state persists locally");
            GameState waiting=new GameState();waiting.scene=2;ZooLogic init=new ZooLogic(waiting);
            waiting.variables[0]=4;waiting.variables[2]=249;
            zoo=new ZooScene(scene,waiting);zoo.update(100);
            check(!zoo.logic.guardPresent(),"original guard timeout clears payment eligibility");
        } finally { scene.close(); }
        System.out.println("FirstLocation: queued approaches, all items, wall, guard, payment, completion and saves passed");
    }
}
