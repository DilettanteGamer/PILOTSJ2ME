package pilots;

/** Integration regressions for walking, deferred catalog 29 and all speech bindings. */
public final class ZooPresentationTest {
    private static void check(boolean ok,String message) { if(!ok) throw new RuntimeException(message); }
    private static void ticks(ZooScene zoo,int count) throws Exception { for(int i=0;i<count;i++) zoo.update(100); }
    private static void settle(ZooScene zoo) throws Exception {
        int budget=1500;
        while(zoo.logic.busy() && budget-->0) zoo.update(100);
        check(budget>0,"sequence terminates");
    }
    private static boolean gait(int id,int hero,int direction) {
        if(hero==0) return direction==1?id==20020:direction==2?id==20021:direction==3?id==20024||id==20025:id==20022||id==20023;
        return direction==1?id==20032||id==20033:direction==2?id==20028||id==20029:direction==3?id==20030||id==20031:id==20026||id==20027;
    }
    public static void main(String[] args) throws Exception {
        Scene scene=new Scene(2);
        try {
            for(int hero=0;hero<2;hero++) for(int direction=1;direction<=4;direction++) {
                GameState state=new GameState();state.scene=2;ZooLogic init=new ZooLogic(state);
                init.placeHero(hero,400,368);init.select(hero);
                ZooScene zoo=new ZooScene(scene,state);
                int x=direction==1?336:direction==2?464:400;
                int y=direction==3?304:direction==4?416:368;
                check(zoo.request(x,y),"manual walk request");
                ticks(zoo,5);
                check(gait(scene.actors[2+hero].id,hero,direction),"original directional gait");
                check(scene.actors[2+hero].clip.index>0,"walking frames advance");
                int px=zoo.logic.heroX(hero),py=zoo.logic.heroY(hero),id=scene.actors[2+hero].id,frame=scene.actors[2+hero].clip.index;
                state=GameState.decode(state.encode(),20);zoo=new ZooScene(scene,state);
                check(scene.actors[2+hero].id==id && scene.actors[2+hero].clip.index==frame,"resume exact gait half and frame");
                check(zoo.logic.heroX(hero)==px && zoo.logic.heroY(hero)==py,"resume actual walk position");
                settle(zoo);
                check(zoo.logic.heroX(hero)==x && zoo.logic.heroY(hero)==y,"walking reaches queued point");
                check(scene.actors[2+hero].id>=20012 && scene.actors[2+hero].id<=20019,"idle facing replaces gait on arrival");
            }
            GameState state=new GameState();state.scene=2;ZooLogic init=new ZooLogic(state);
            state.inventory[1]=true;init.selectItem(2);
            ZooScene zoo=new ZooScene(scene,state);zoo.activate(580,230);
            ticks(zoo,7);
            check(scene.actors[12].id==73 && !scene.actors[13].visible,"catalog 10 starts without catalog 29");
            ticks(zoo,25);state=GameState.decode(state.encode(),20);zoo=new ZooScene(scene,state);
            check(scene.actors[12].id==73 && !scene.actors[13].visible,"save during wall collapse cannot reveal rock");
            int budget=100;
            while(scene.actors[12].id==73 && budget-->0) {
                check(!scene.actors[13].visible,"rock hidden through entire collapse");zoo.update(100);
            }
            check(budget>0 && scene.actors[12].id==74 && scene.actors[13].id==104 && scene.actors[13].visible,
                  "catalog 29 begins after catalog 10 completes");
            check(scene.actors[13].clip.index==0 && zoo.layer(13)<zoo.layer(12),"rock begins at first frame in front of wall");
            ticks(zoo,3);state=GameState.decode(state.encode(),20);zoo=new ZooScene(scene,state);
            check(scene.actors[13].visible && scene.actors[13].clip.index==3,"resume falling item");
            ticks(zoo,30);check(!scene.actors[13].visible,"falling item expires");

            for(int hero=0;hero<2;hero++) for(int branch=0;branch<2;branch++) {
                state=new GameState();state.scene=2;init=new ZooLogic(state);state.variables[61]=1;
                init.select(hero);state.variables[hero==0?51:46]=branch;
                zoo=new ZooScene(scene,state);zoo.activate(580,230); // bare hand at wall -> original refusal
                if(branch==0) { ticks(zoo,13);check(zoo.logic.message()==null,"silent refusal has no invented dialogue"); }
                else {
                    if(hero==1) { ticks(zoo,11);check(zoo.logic.message()==null,"Colleague speech not early");ticks(zoo,1); }
                    check(zoo.logic.message()!=null && zoo.logic.message().indexOf("Ничего не понимаю")>=0,"both spoken refusals captioned");
                    state=GameState.decode(state.encode(),20);zoo=new ZooScene(scene,state);
                    check(zoo.logic.message()!=null,"subtitle persists through save");
                    ticks(zoo,35);check(zoo.logic.message()==null,"caption expires even after action ends");
                }
            }
            state=new GameState();state.scene=2;init=new ZooLogic(state);state.variables[0]=2;
            zoo=new ZooScene(scene,state);
            check(zoo.logic.message().indexOf("Откройте")>=0,"Chief arrival speech");
            check(zoo.request(0,0) && zoo.logic.message().indexOf("Доступ ограничен")>=0,"guard refusal speech");
            zoo.request(0,0);state.inventory[2]=true;zoo.logic.selectItem(3);
            check(zoo.activate(330,220),"original payment begins");
            ticks(zoo,28);check(zoo.logic.step()==29 && zoo.logic.message()==null,"payment speech not early");
            ticks(zoo,1);
            check(zoo.logic.message()!=null && zoo.logic.message().indexOf("В виде исключения")>=0,"guard payment cue 36 at step 30");
            state=GameState.decode(state.encode(),20);zoo=new ZooScene(scene,state);
            check(zoo.logic.message().indexOf("В виде исключения")>=0,"resume payment caption");
            ticks(zoo,35);check(zoo.logic.message()==null,"payment caption clears");
            settle(zoo);check(zoo.logic.gateState()==2 && state.scene==2,"presentation changes preserve paid gate and first location");
            check(ZooSubtitles.text(33)==null && ZooSubtitles.text(31)==null,"gate/footstep sounds are not fabricated speech");
        } finally { scene.close(); }
        System.out.println("Presentation: 8 directional walks/save, collapse-to-item sequence, all 5 speech cues/timing/save passed");
    }
}
