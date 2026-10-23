package pilots;
import java.io.File;
import javax.imageio.ImageIO;
import javax.microedition.lcdui.Graphics;
/** Physical scene hit testing, source pivots/layers and both independent walkers. */
public final class StreetRenderTest {
    static Scene scene;static GameState state;static StreetScene street;
    static void check(boolean ok,String text){if(!ok)throw new RuntimeException(text);}
    static void capture(int width,int height,String path)throws Exception {
        int bar=(width*63+320)/640;Viewport view=new Viewport(width,height-bar,640,417);
        view.cursorX=street.animals.world.heroes[1].x;view.cursorY=street.animals.world.heroes[1].y;view.update();
        Graphics g=new Graphics(width,height);scene.background.paint(g,view);street.paint(g,view);scene.panel.paintPanel(g,width,height,bar);
        ZooPanel panel=new ZooPanel(6);try{panel.paint(g,street.logic,width,height,bar);}finally{panel.close();}
        ImageIO.write(g.image,"png",new File(path));
    }
    static void act(int id)throws Exception {
        for(int i=0;i<scene.actors.length;i++) {
            StreetWorld.Thing t=i<300?street.animals.world.cells[i]:i<302?street.animals.world.heroes[i-300]:street.animals.world.effects[i-302];
            AnimatedActor actor=scene.actors[i];if(t==null || t.id!=id || actor==null)continue;
            for(int y=Math.max(0,actor.y+actor.clip.offsetY);y<Math.min(417,actor.y+actor.clip.offsetY+actor.clip.height);y++)
                for(int x=Math.max(0,actor.x+actor.clip.offsetX);x<Math.min(640,actor.x+actor.clip.offsetX+actor.clip.width);x++)
                    if(street.targetAt(x,y)==id){check(street.hoverText(x,y)!=null && street.request(x,y),"physical street target "+id);return;}
        }
        throw new RuntimeException("no visible street hotspot "+id);
    }
    static void settle()throws Exception {int n=1600;while(street.logic.busy()&&n-->0)street.update(100);check(n>0,"street render action hangs");}
    public static void main(String[] a)throws Exception {
        int width=Integer.parseInt(a[0]),height=Integer.parseInt(a[1]);scene=new Scene(6);state=new GameState();state.scene=6;
        try {
            street=new StreetScene(scene,state);capture(width,height,a[2]+"-entry.png");capture(640,480,a[2]+"-world.png");
            act(19);settle();act(18);settle();check(street.logic.hasItem(1),"physical corkscrew pickup");
            capture(width,height,a[2]+"-inventory.png");capture(640,480,a[2]+"-waiting-world.png");
            act(17);settle();int budget=1200;
            while(street.animals.world.at(9,18)!=25 && budget-->0)street.update(100);
            check(budget>0,"pedestrian render timer");act(16);
            boolean cover=false;budget=1600;
            while(street.logic.busy()&&budget-->0) {
                street.update(100);
                if(!cover && street.animals.world.action()==60 && street.animals.world.step()==100){capture(640,480,a[2]+"-cover-world.png");cover=true;}
            }
            check(cover && budget>0,"full crossing scene not rendered");act(20);settle();
            check(street.logic.completed(),"physical street finale");capture(width,height,a[2]+"-end.png");
        }finally{scene.close();}
        System.out.println("StreetRender: actual opaque hotspots, autonomous waiting point, corkscrew panel, crossing/cover and sewer finale painted");
    }
}
