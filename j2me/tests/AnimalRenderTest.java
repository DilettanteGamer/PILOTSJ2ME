package pilots;
import java.io.File;
import javax.imageio.ImageIO;
import javax.microedition.lcdui.Graphics;

/** Exercise the production painter, native roots/layers and scene-specific inventory. */
public final class AnimalRenderTest {
    static void settle(AnimalScene scene) throws Exception {
        int budget=2000;while(scene.logic.busy() && budget-->0) scene.update(100);
        if(budget<=0) throw new RuntimeException("Animal render sequence hangs");
    }
    static void capture(Scene scene,AnimalScene location,int width,int height,String file) throws Exception {
        int bar=(width*63+320)/640;
        Viewport view=new Viewport(width,height-bar,640,417);view.cursorX=320;view.cursorY=240;view.update();
        Graphics surface=new Graphics(width,height);scene.background.paint(surface,view);location.paint(surface,view);scene.panel.paintPanel(surface,width,height,bar);
        ZooPanel panel=new ZooPanel(true);try { panel.paint(surface,location.logic,width,height,bar); } finally { panel.close(); }
        ImageIO.write(surface.image,"png",new File(file));
    }
    public static void main(String[] args) throws Exception {
        Scene scene=new Scene(4);
        try {
            GameState state=new GameState();state.scene=4;AnimalScene location=new AnimalScene(scene,state);settle(location);
            capture(scene,location,Integer.parseInt(args[0]),Integer.parseInt(args[1]),args[2]+"-entry.png");
            capture(scene,location,640,480,args[2]+"-world.png");
            location.animals.world.interact(27);settle(location);location.animals.world.interact(26);settle(location);location.animals.world.interact(25);settle(location);
            location.logic.selectItem(1);location.animals.world.interact(22);settle(location);
            capture(scene,location,Integer.parseInt(args[0]),Integer.parseInt(args[1]),args[2]+"-palm.png");
            capture(scene,location,640,480,args[2]+"-palm-world.png");
        } finally { scene.close(); }
        System.out.println("AnimalRender: source placement, entry, grown palm and new inventory icons captured");
    }
}
