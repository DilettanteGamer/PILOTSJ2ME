package pilots;
import java.io.IOException;
import java.lang.reflect.Field;
import javax.microedition.lcdui.Display;

/** Exercises the shipped MIDlet/Canvas entry routes, not just scene constructors. */
public final class LocationEntryTest {
    static void check(boolean ok,String message) { if(!ok) throw new RuntimeException(message); }
    static Object get(Object object,String name)throws Exception {
        Field f=object.getClass().getDeclaredField(name);f.setAccessible(true);return f.get(object);
    }
    static void set(Object object,String name,Object value)throws Exception {
        Field f=object.getClass().getDeclaredField(name);f.setAccessible(true);f.set(object,value);
    }
    static void garden(PilotsMidlet.GameCanvas canvas,String route)throws Exception {
        check(((GameState)get(canvas,"state")).scene==5 && get(canvas,"zoo") instanceof GardenScene,route);
    }
    static void street(PilotsMidlet.GameCanvas canvas,String route)throws Exception {
        check(((GameState)get(canvas,"state")).scene==6 && get(canvas,"zoo") instanceof StreetScene,route);
    }
    public static void main(String[] args)throws Exception {
        PilotsMidlet app=new PilotsMidlet();PilotsMidlet.GameCanvas canvas=app.new GameCanvas();
        try {
            set(app,"canvas",canvas);set(app,"display",Display.getDisplay(app));
            // Menu item 10 must actually enter scene 5 (errors normally become alerts).
            canvas.startLocation(5);
            app.choose(10);garden(canvas,"fourth location menu entry");
            canvas.startLocation(4);
            ((AnimalScene)get(canvas,"zoo")).animals.world.completed=true;
            canvas.tick(100);garden(canvas,"third location completion transition");
            canvas.save();canvas.startLocation(2);canvas.load();garden(canvas,"RMS garden restore");
            // Enter the fifth location from its menu and from garden completion.
            canvas.startLocation(6);app.choose(11);street(canvas,"fifth location menu entry");
            canvas.startLocation(5);((GardenScene)get(canvas,"zoo")).animals.world.completed=true;
            canvas.tick(100);street(canvas,"fourth-to-fifth transition");
            canvas.save();canvas.startLocation(2);canvas.load();street(canvas,"RMS street restore");
            // Re-enter, restore a street save, and verify its actual loader.
            GameState saved=GameState.decode(((GameState)get(canvas,"state")).encode(),7);
            canvas.startLocation(2);set(canvas,"state",saved);canvas.goToScene(saved.scene);
            street(canvas,"restored fifth location entry");
            // Garden previews must load from another scene and return to it.
            canvas.startLocation(2);set(app,"animationCatalog",new AnimationCatalog());
            canvas.preview(0);check(((GameState)get(canvas,"state")).scene==6,"preview scene");
            canvas.finishPreview();check(((GameState)get(canvas,"state")).scene==2,"preview return");
            Object original=get(canvas,"zoo");
            try { canvas.goToScene(7);throw new RuntimeException("unsupported scene accepted"); }
            catch(IOException expected) {}
            check(get(canvas,"zoo")==original,"invalid scene damaged active scene");
        } finally { canvas.shutdown(); }
        System.out.println("LocationEntry: MIDlet menus, third/fourth/fifth transitions, real Canvas RMS save/load, preview/return and invalid catalog ID passed");
    }
}
