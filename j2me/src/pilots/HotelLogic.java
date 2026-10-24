package pilots;
import java.io.IOException;

/** Hotel facade for the shared input and inventory panel. */
public final class HotelLogic extends ZooLogic {
    public final HotelWorld world;
    public HotelLogic(GameState state) throws IOException { super(state);world=new HotelWorld(state); }
    public int hero() { return world.hero(); }
    private HotelWorld.Thing character(int h) {
        if(world.scene==14 && !world.heroes[h].active){for(int i=0;i<world.cells.length;i++)if(world.cells[i]!=null && world.cells[i].id==(h==0?47:46))return world.cells[i];}
        if(world.scene!=15)return world.heroes[h];
        for(int i=0;i<world.cells.length;i++) {
            HotelWorld.Thing t=world.cells[i];
            if(t!=null && (h==0?t.id>=16&&t.id<=20:t.id==15||t.id==12||t.id==13))return t;
        }
        return null;
    }
    public int heroX(int h) { HotelWorld.Thing t=character(h);return t==null?128:t.x; }
    public int heroY(int h) { HotelWorld.Thing t=character(h);return t==null?208:t.y; }
    public boolean select(int h) { return world.select(h); }
    public boolean busy() { return world.busy(); }
    public boolean walking() { return world.walkHero>=0; }
    public int pendingAction() { return world.action(); }
    public int pendingClip() { HotelWorld.Thing t=character(hero());return t==null?-1:t.clip; }
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
