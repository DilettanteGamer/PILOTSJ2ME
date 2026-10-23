package pilots;

import java.util.Arrays;

/** Check every painted frame, not just the final quest flags. */
public final class AnimalSequenceRenderTest {
    static boolean[] cut=new boolean[31],tail=new boolean[15],kangaroo=new boolean[100],fall=new boolean[36],monkey=new boolean[40];
    static int width,height;static String prefix;
    static void check(boolean ok,String text) { AnimalVisibilityTest.check(ok,text); }
    static AnimalWorld world() { return AnimalVisibilityTest.world(); }
    static boolean record(int clip,boolean[] frames) {
        boolean found=false;
        for(AnimatedActor actor:AnimalVisibilityTest.scene.actors) if(actor!=null && actor.id==clip && actor.visible) {
            check(actor.clip.index<frames.length,"unexpected source frame count");frames[actor.clip.index]=true;found=true;
        }
        return found;
    }
    static void capture(String name) throws Exception {
        AnimalRenderTest.capture(AnimalVisibilityTest.scene,AnimalVisibilityTest.location,640,480,prefix+"-"+name+"-world.png");
        AnimalRenderTest.capture(AnimalVisibilityTest.scene,AnimalVisibilityTest.location,width,height,prefix+"-"+name+".png");
    }
    static void save() throws Exception {
        world().syncSave();byte[] before=AnimalVisibilityTest.state.encode();AnimalVisibilityTest.save();world().syncSave();
        check(Arrays.equals(before,AnimalVisibilityTest.state.encode()),"save/resume changed cinematic frame or time");
    }
    static void all(boolean[] frames,String name) { for(int i=0;i<frames.length;i++) check(frames[i],name+" frame "+i+" never reached the painter"); }
    static AnimalWorld.Thing entity(int id) {
        for(AnimalWorld.Thing t:world().cells) if(t!=null && t.id==id) return t;
        throw new RuntimeException("missing entity "+id);
    }
    static void introduction() throws Exception {
        AnimalVisibilityTest.state=new GameState();AnimalVisibilityTest.state.scene=4;
        AnimalVisibilityTest.scene=new Scene(4);
        AnimalVisibilityTest.location=new AnimalScene(AnimalVisibilityTest.scene,AnimalVisibilityTest.state);
        boolean[] reaction=new boolean[18];int budget=2000;
        try {
            while(world().busy() && budget-->0) {
                AnimalVisibilityTest.location.update(100);
                check(entity(70).clip>=0,"giraffe head disappeared during introduction");record(131,reaction);
            }
            check(budget>0 && entity(70).clip==115,"giraffe standing pose did not return");all(reaction,"giraffe intro");
            save();
            for(int i=0;i<50;i++) AnimalVisibilityTest.location.update(100);
            check(entity(70).clip==115,"giraffe head disappeared while idle");capture("giraffe");
            int[] route=world().plan(128,288,320,144);check(route.length>0,"basin bypass disconnected tools");
            for(int i=0;i<route.length;i+=2) {
                int x=route[i],y=route[i+1];
                check(!(x>=176 && x<320 && y>=256 && y<304),"path crosses basin/tap segment");
            }
        } finally { AnimalVisibilityTest.scene.close(); }
    }
    public static void main(String[] args) throws Exception {
        width=Integer.parseInt(args[0]);height=Integer.parseInt(args[1]);prefix=args[2];
        introduction();AnimalVisibilityTest.prepare();
        try {
            for(AnimalWorld.Thing t:world().cells) if(t!=null && t.id==48) check(t.clip<0,"finished rhino attempt left a duplicate Chief");
            capture("rhino-recovered");
            int[] p=AnimalVisibilityTest.point(29);
            check("Бананы".equals(AnimalVisibilityTest.location.hoverText(p[0],p[1])),"grown bananas have the wrong hover label");
            AnimalVisibilityTest.act(0,0,24); // Correct fountain destination: kangaroo pouch.
            check(world().select(1) && world().selectItem(3),"secateurs setup");
            p=AnimalVisibilityTest.point(29);check(AnimalVisibilityTest.location.request(p[0],p[1]),"physical banana cut click rejected");
            boolean saved=false;int budget=2000;
            while(world().busy() && budget-->0) {
                AnimalVisibilityTest.location.update(100);record(97,cut);
                if(cut[10] && !saved) { capture("cut");save();record(97,cut);saved=true; }
            }
            check(budget>0 && saved,"cut scene did not finish");all(cut,"secateurs");
            for(AnimalWorld.Thing effect:world().effects) check(effect==null,"finished cutting effect retained");
            AnimalVisibilityTest.act(0,0,24);
            check(world().select(0) && world().selectItem(0),"tail setup");
            p=AnimalVisibilityTest.point(19);check(AnimalVisibilityTest.location.request(p[0],p[1]),"physical kangaroo click rejected");
            boolean savedFall=false,savedMonkey=false,savedWalk=false,bypassedCage=false,colleagueWalked=false;budget=2000;
            while(world().busy() && budget-->0) {
                AnimalVisibilityTest.location.update(100);
                if(world().walkHero==0) {
                    int x=world().heroes[0].x,y=world().heroes[0].y;
                    check(!(x>=16 && x<144 && y>=304 && y<400),"Chief crossed kangaroo bars");
                    if(x>=144 && y==400) bypassedCage=true;
                }
                if(world().g(4)>=91 && world().action()==57 && world().step()==1) {
                    check(world().heroes[1].active && AnimalVisibilityTest.scene.actors[301]!=null,"released Colleague disappeared");
                    check(entity(71).clip<0,"released Colleague has a duplicate composite sprite");
                }
                if(world().walkHero==1) {
                    colleagueWalked=true;
                    check(world().heroes[0].active && world().heroes[0].x==576 && world().heroes[0].y==368,"Colleague left before Chief reached the exit");
                    if(!savedWalk) { save();capture("following");savedWalk=true; }
                }
                record(123,tail);record(124,kangaroo);record(100,fall);record(125,monkey);
                if(world().action()==57 && world().step()==1 && world().g(4)<44)
                    check(world().walkHero<0,"Chief left before the source exit mark");
                if(fall[24] && !savedFall) { capture("fall");save();savedFall=true; }
                if(monkey[13] && !savedMonkey) { capture("monkey");save();savedMonkey=true; }
            }
            check(budget>0 && world().completed,"final scene failed to complete");
            check(bypassedCage && colleagueWalked && savedWalk,"missing cage bypass or companion walk");
            check(world().heroes[0].active && world().heroes[1].active && world().heroes[1].x==576 && world().heroes[1].y==368 && world().walkHero<0,"completion did not wait for both characters");
            all(tail,"Chief tail pull");all(kangaroo,"kangaroo");all(fall,"Colleague and key");all(monkey,"monkey steals key");
            check(SceneFlow.next(4,true)==5,"third location exit did not open garden");
        } finally { AnimalVisibilityTest.scene.close(); }
        System.out.println("AnimalSequences: banana hover, recovered rhino, all cutting/tail/kangaroo/fall/monkey frames painted, cinematic saves, giraffe fallback, basin/cage bypasses and both character exits passed");
    }
}
