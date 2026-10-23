package pilots;
import java.io.File;
import javax.imageio.ImageIO;
import javax.microedition.lcdui.Graphics;
public final class GardenRenderTest {
    static Scene scene;static GameState state;static GardenScene garden;
    static void check(boolean ok,String s){if(!ok)throw new RuntimeException(s);}
    static void capture(int width,int height,String path)throws Exception {
        int bar=(width*63+320)/640;Viewport view=new Viewport(width,height-bar,640,417);
        view.cursorX=garden.animals.world.heroes[0].x;view.cursorY=garden.animals.world.heroes[0].y;view.update();
        Graphics g=new Graphics(width,height);scene.background.paint(g,view);garden.paint(g,view);scene.panel.paintPanel(g,width,height,bar);
        ZooPanel panel=new ZooPanel(5);try{panel.paint(g,garden.logic,width,height,bar);}finally{panel.close();}
        ImageIO.write(g.image,"png",new File(path));
    }
    static void settle()throws Exception {
        int n=2000;while(garden.logic.busy()&&n-->0) {
            if(garden.animals.world.action()==51)check(garden.request(320,384),"final confirmation");garden.update(100);
        }check(n>0,"render sequence hangs");
    }
    public static void main(String[]a)throws Exception {
        int width=Integer.parseInt(a[0]),height=Integer.parseInt(a[1]);scene=new Scene(5);state=new GameState();state.scene=5;
        try {
            garden=new GardenScene(scene,state);settle();
            check(scene.actors[230+8]!=null || garden.animals.world.cells[238]!=null,"monkey map position");
            capture(width,height,a[2]+"-entry.png");capture(640,480,a[2]+"-world.png");
            check(garden.logic.select(0) && garden.request(384,368),"first physical approach");settle();
            check(garden.request(512,272),"second physical approach");
            boolean key=false;int budget=2000;
            while(garden.logic.busy()&&budget-->0) {
                if(garden.animals.world.action()==51)garden.request(320,384);garden.update(100);
                if(!key && garden.animals.world.action()==50 && garden.animals.world.step()==30){capture(640,480,a[2]+"-key-world.png");key=true;}
            }
            check(budget>0 && garden.logic.completed() && key && garden.logic.hasItem(1),"physical capture/key sequence failed");
            capture(width,height,a[2]+"-end.png");
        } finally {scene.close();}
        System.out.println("GardenRender: physical input, source roots/layers, entry, final key caption and inventory captured");
    }
}
