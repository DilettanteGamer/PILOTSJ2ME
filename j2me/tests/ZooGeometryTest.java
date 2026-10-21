package pilots;

import javax.microedition.lcdui.Graphics;
import java.io.File;
import javax.imageio.ImageIO;

/** Regression checks for reported placement defects, using the production painter. */
public final class ZooGeometryTest {
    private static void check(boolean ok,String message) { if(!ok) throw new RuntimeException(message); }
    private static void settle(ZooScene zoo) throws Exception {
        int budget=1000;
        while(zoo.logic.busy() && budget-->0) zoo.update(100);
        check(budget>0,"interaction terminates");
    }
    public static void main(String[] args) throws Exception {
        Scene scene=new Scene(2);
        try {
            GameState state=new GameState();state.scene=2;ZooLogic init=new ZooLogic(state);
            init.placeHero(0,192,304);init.placeHero(1,256,336);init.select(1);
            ZooScene zoo=new ZooScene(scene,state);
            check(zoo.request(180,260),"Colleague requests occupied window position");
            zoo.update(100);
            check(zoo.logic.hero()==0 && zoo.logic.walking(),"blocking Chief steps aside first");
            check(Math.abs(zoo.logic.walkX()-192)+Math.abs(zoo.logic.walkY()-304)<=64,
                  "step aside stays within one source horizontal cell");
            state=GameState.decode(state.encode(),20);zoo=new ZooScene(scene,state);settle(zoo);
            check(zoo.logic.hero()==1 && zoo.logic.windowOpen(),"resume aside queue, restore actor and open window");
            check(zoo.logic.heroX(1)==192 && zoo.logic.heroY(1)==304,"requesting hero occupies action point");
            check(Math.abs(zoo.logic.heroX(0)-192)+Math.abs(zoo.logic.heroY(0)-304)<=64,
                  "previous hero remains near window, not at initial position");

            state=new GameState();state.scene=2;init=new ZooLogic(state);
            state.variables[7]=2;state.variables[42]=2;
            init.placeHero(0,384,304);init.placeHero(1,512,336);
            zoo=new ZooScene(scene,state);
            AnimationClip coin=scene.actors[8].clip;
            int cx=scene.actors[8].x+coin.offsetX+coin.width/2;
            int cy=scene.actors[8].y+coin.offsetY+coin.height/2;
            check(cx==313 && cy==316,"settled coin source coordinates");
            for(int y=313;y<320;y++) for(int x=307;x<319;x++)
                check(zoo.logic.targetAt(x,y)==ZooLogic.COIN,"entire coin sprite is selectable");
            check(zoo.logic.targetAt(280,285)!=ZooLogic.COIN,"stale coin hotspot removed");
            Viewport view=new Viewport(640,417,640,417);
            Graphics full=new Graphics(640,417);scene.background.paint(full,view);zoo.paint(full,view);
            // Every opaque pixel shared by these heroes and the wall must show the hero.
            Graphics wall=new Graphics(640,417);scene.actors[12].paint(wall,view);
            for(int h=0;h<2;h++) {
                Graphics hero=new Graphics(640,417);scene.actors[2+h].paint(hero,view);
                int overlap=0;
                for(int y=0;y<417;y++) for(int x=0;x<640;x++) {
                    int hp=hero.image.getRGB(x,y),wp=wall.image.getRGB(x,y);
                    if((hp&0xffffff)!=0 && (wp&0xffffff)!=0) {
                        overlap++;check(full.image.getRGB(x,y)==hp,"wall must not clip hero "+h);
                    }
                }
                check(overlap>100,"occlusion regression covers overlapping sprites");
            }
            check(zoo.layer(12)==17 && zoo.layer(2)==14 && zoo.layer(3)==10,"source standing layers");
            init.placeHero(0,448,240);
            check(zoo.layer(2)>zoo.layer(11),"hero entering zoo passes behind gate");
            init.placeHero(0,384,304);
            Graphics without=new Graphics(640,417);scene.background.paint(without,view);
            scene.actors[14].visible=false;zoo.paint(without,view);scene.actors[14].visible=true;
            int filled=0;
            for(int y=190;y<270;y++) for(int x=398;x<418;x++)
                if(full.image.getRGB(x,y)!=without.image.getRGB(x,y)) filled++;
            check(filled>100,"original right column fills gate-wall gap");
            check(scene.actors[14].id==52 && scene.actors[14].x==576 && scene.actors[14].y==48,
                  "column uses original initial-map root");
            check(scene.actors[15].id==99 && scene.actors[15].x==384 && scene.actors[15].y==96,
                  "original gate sign restored");
            int width=Integer.parseInt(args[0]),height=Integer.parseInt(args[1]);
            Viewport phone=new Viewport(width,height-(width*63+320)/640,640,417);
            phone.cursorX=480;phone.cursorY=304;phone.update();
            Graphics rendered=new Graphics(width,height);
            scene.background.paint(rendered,phone);zoo.paint(rendered,phone);
            int bar=(width*63+320)/640;scene.panel.paintPanel(rendered,width,height,bar);
            ZooPanel panel=new ZooPanel();
            try { panel.paint(rendered,zoo.logic,width,height,bar); } finally { panel.close(); }
            ImageIO.write(rendered.image,"png",new File(args[2]));
            check(zoo.request(cx,cy),"click actual coin endpoint");settle(zoo);
            check(zoo.logic.hasItem(3) && zoo.logic.butterflyState()==0,"real coin hotspot completes pickup");
        } finally { scene.close(); }
        System.out.println("Geometry: local aside/save, gate column/sign, hero occlusion and visible coin pickup passed");
    }
}
