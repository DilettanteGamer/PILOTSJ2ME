package pilots;
import java.io.IOException;

/** Fifth-location facade for the shared input and inventory panel. */
public final class StreetLogic extends ZooLogic {
    public final StreetWorld world;
    public StreetLogic(GameState state) throws IOException { super(state);world=new StreetWorld(state); }
    public int hero() { return world.hero(); }
    public int heroX(int h) { return world.heroes[h].x; }
    public int heroY(int h) { return world.heroes[h].y; }
    public boolean select(int h) { return world.select(h); }
    public boolean busy() { return world.busy(); }
    public boolean walking() { return world.walkHero>=0; }
    public int pendingAction() { return world.action(); }
    public int pendingClip() { return world.heroes[hero()].clip; }
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
