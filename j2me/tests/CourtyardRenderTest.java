package pilots;
import java.io.File;
import javax.imageio.ImageIO;
import javax.microedition.lcdui.Graphics;

/** Real courtyard painter on the desktop recording surface. */
public final class CourtyardRenderTest {
    private static void check(boolean ok,String message) { if(!ok) throw new RuntimeException(message); }
    private static void settle(CourtyardScene location) throws Exception {
        int budget=2000;while(location.logic.busy() && budget-->0) location.update(100);
        check(budget>0,"render sequence terminates");
    }
    private static void capture(Scene scene,CourtyardScene location,int width,int height,String name) throws Exception {
        int bar=(width*63+320)/640;
        Viewport view=new Viewport(width,height-bar,640,417);view.cursorX=400;view.cursorY=224;view.update();
        Graphics surface=new Graphics(width,height);scene.background.paint(surface,view);location.paint(surface,view);
        scene.panel.paintPanel(surface,width,height,bar);ZooPanel panel=new ZooPanel();
        try { panel.paint(surface,location.logic,width,height,bar); } finally { panel.close(); }
        ImageIO.write(surface.image,"png",new File(name));
    }
    public static void main(String[] args) throws Exception {
        int width=Integer.parseInt(args[0]),height=Integer.parseInt(args[1]);
        Scene scene=new Scene(3);
        try {
            GameState state=new GameState();state.scene=3;CourtyardScene location=new CourtyardScene(scene,state);
            capture(scene,location,width,height,args[2]+"-entry.png");
            capture(scene,location,640,480,args[2]+"-world.png");
            location.logic.select(1);location.request(550,120);settle(location);
            location.request(320,288);settle(location);
            location.logic.select(0);location.request(550,120);
            int budget=1000;while(location.logic.pendingAction()==0 && budget-->0) location.update(100);
            while(location.logic.step()<13) location.update(100);
            check(location.courtyard.arrowState()==1 && scene.actors[0].id==64 && scene.actors[1].id==58,"real source cooperative animation in renderer");
            capture(scene,location,width,height,args[2]+"-arrow.png");
            capture(scene,location,640,480,args[2]+"-arrow-world.png");
        } finally { scene.close(); }
        System.out.println("CourtyardRender: entry and cooperative piano/arrow scene captured at "+width+"x"+height);
    }
}
