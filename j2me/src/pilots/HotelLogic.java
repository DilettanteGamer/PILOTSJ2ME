package pilots;
import java.io.IOException;

/** Hotel facade for the shared input and inventory panel. */
public final class HotelLogic extends ZooLogic {
    public final HotelWorld world;
    public HotelLogic(GameState state) throws IOException { super(state);world=new HotelWorld(state); }
    public int hero() { return world.hero(); }
    public int heroX(int h) { return world.heroes[h]==null?128:world.heroes[h].x; }
    public int heroY(int h) { return world.heroes[h]==null?208:world.heroes[h].y; }
    public boolean select(int h) { return world.select(h); }
    public boolean busy() { return world.busy(); }
    public boolean walking() { return world.walkHero>=0; }
    public int pendingAction() { return world.action(); }
    public int pendingClip() { return world.heroes[hero()]==null?-1:world.heroes[hero()].clip; }
    public int step() { return world.step(); }
    public boolean hasItem(int item) { return world.hasItem(item); }
    public boolean hasKettle() { return false; }
    public int selectedItem() { return world.item(); }
    public boolean selectItem(int item) { return world.selectItem(item); }
    public void setSound(SoundSink sink) { world.sound(sink); }
    public boolean continueTalk() { return false; }
    public String message() { return world.caption(); }
    public boolean completed() { return world.completed; }
}
