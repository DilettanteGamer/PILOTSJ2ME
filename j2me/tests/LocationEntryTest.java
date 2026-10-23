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
            ((StreetScene)get(canvas,"zoo")).animals.world.completed=true;
            canvas.tick(100);check(((GameState)get(canvas,"state")).scene==8,"street must skip sewer");
            canvas.save();canvas.startLocation(2);canvas.load();check(((GameState)get(canvas,"state")).scene==8,"cinematic save");
            for(int i=0;i<230;i++)canvas.tick(100);
            check(((GameState)get(canvas,"state")).scene==9,"entrance-to-interior transition");
            app.choose(12);check(((GameState)get(canvas,"state")).scene==8,"cinematic menu");
            app.choose(13);check(((GameState)get(canvas,"state")).scene==9,"hotel interior menu");
            canvas.save();canvas.startLocation(2);canvas.load();check(((GameState)get(canvas,"state")).scene==9,"interior save");
            HotelWorld hotel=((HotelScene)get(canvas,"zoo")).animals.world;
            for(int i=0;i<180;i++)canvas.tick(100);check(hotel.interact(101),"fridge approach");
            for(int i=0;i<180 && !(Display.getDisplay(app).getCurrent() instanceof FridgeCanvas);i++)canvas.tick(100);
            check(Display.getDisplay(app).getCurrent() instanceof FridgeCanvas,"detail canvas was not displayed");
            FridgeCanvas detail=(FridgeCanvas)Display.getDisplay(app).getCurrent();int before=hotel.fridgeMask();
            detail.input(54);detail.input(56);detail.input(53);check(hotel.fridgeMask()==RefrigeratorPuzzle.turn(before,4),"detail keyboard selection");
            int progress=hotel.fridgeMask();canvas.save();detail.input(48);check(Display.getDisplay(app).getCurrent()==canvas,"detail Back");
            canvas.startLocation(2);canvas.load();canvas.tick(100);check(Display.getDisplay(app).getCurrent() instanceof FridgeCanvas,"detail save did not reopen");
            check(((HotelScene)get(canvas,"zoo")).animals.world.fridgeMask()==progress,"detail save lost handles");
            detail=(FridgeCanvas)Display.getDisplay(app).getCurrent();
            int solution=-1;
            for(int moves=0;moves<512;moves++){int mask=progress;for(int i=0;i<9;i++)if((moves&(1<<i))!=0)mask=RefrigeratorPuzzle.turn(mask,i);if(mask==0){solution=moves;break;}}
            check(solution>0,"nontrivial detail solution");
            int selected=0;
            for(int i=0;i<9;i++)if((solution&(1<<i))!=0){
                while(selected/3<i/3){detail.input(56);selected+=3;}
                while(selected%3<i%3){detail.input(54);selected++;}
                while(selected%3>i%3){detail.input(52);selected--;}
                detail.input(53);
            }
            check(Display.getDisplay(app).getCurrent()==canvas,"solved puzzle did not return to room");
            check(!((HotelScene)get(canvas,"zoo")).animals.world.fridgeRequested(),"room stayed paused after solution");
            canvas.startLocation(6);
            // Re-enter, restore a street save, and verify its actual loader.
            GameState saved=GameState.decode(((GameState)get(canvas,"state")).encode(),7);
            canvas.startLocation(2);set(canvas,"state",saved);canvas.goToScene(saved.scene);
            street(canvas,"restored fifth location entry");
            // Garden previews must load from another scene and return to it.
            canvas.startLocation(2);set(app,"animationCatalog",new AnimationCatalog());
            canvas.preview(0);check(((GameState)get(canvas,"state")).scene==9,"preview scene");
            canvas.finishPreview();check(((GameState)get(canvas,"state")).scene==2,"preview return");
            Object original=get(canvas,"zoo");
            try { canvas.goToScene(7);throw new RuntimeException("unsupported scene accepted"); }
            catch(IOException expected) {}
            check(get(canvas,"zoo")==original,"invalid scene damaged active scene");
        } finally { canvas.shutdown(); }
        System.out.println("LocationEntry: MIDlet menus, third/fourth/fifth transitions, real Canvas RMS save/load, preview/return, fridge Canvas controls/RMS restore and invalid catalog ID passed");
    }
}
