package pilots;
import java.io.IOException;
/** Input selects the same three source height bands as the PC mouse. */
public final class FlightLogic extends ZooLogic {
 public final HotelWorld world;
 public FlightLogic(GameState state)throws IOException{super(state);world=new HotelWorld(state);}
 public boolean command(int key){return key==2?world.interact(17):key==5?world.interact(16):key==8?world.interact(15):false;}
 public int altitude(){return world.g(0x1e1b54);}
 public int ballast(){return world.g(0x94);}
 public boolean busy(){return world.busy();}
 public boolean completed(){return world.completed;}
 public boolean hasItem(int item){return item==0;}
 public boolean hasKettle(){return false;}
 public int selectedItem(){return 0;}
 public boolean selectItem(int i){return false;}
 public boolean select(int h){return false;}
 public void setSound(SoundSink sink){world.sound(sink);}
 public String message(){return world.caption();}
}
