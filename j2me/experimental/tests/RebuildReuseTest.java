package pilots;
import java.lang.reflect.*;import javax.microedition.lcdui.*;
/** Rebuild identical actors without I/O, preserve background, roll back on failed new resources. */
public final class RebuildReuseTest {
 public static void main(String[] args)throws Exception{GameState state=new GameState();state.scene=2;Scene scene=new Scene(2);RasterCache.budget(65536);try{ZooScene zoo=new ZooScene(scene,state);AnimatedActor[] old=scene.actors.clone();Graphics g=new Graphics(128,160);scene.background.paint(g,new Viewport(128,148,640,480));Field f=IndexedBackground.class.getDeclaredField("raster");f.setAccessible(true);Image background=((RasterCache)f.get(scene.background)).get();Method rebuild=ZooScene.class.getDeclaredMethod("rebuild");rebuild.setAccessible(true);int frames=TileStore.frameCalls,loads=TileStore.loads;rebuild.invoke(zoo);for(int i=0;i<old.length;i++)if(scene.actors[i]!=old[i])throw new Exception("Unchanged actor reallocated "+i);if(TileStore.frameCalls!=frames||TileStore.loads!=loads)throw new Exception("Rebuild reread unchanged cels");if(((RasterCache)f.get(scene.background)).get()!=background)throw new Exception("Unchanged background cache invalidated");
  state.variables[52]=64;state.variables[53]=1;state.variables[3]=65535;boolean visible=old[0].visible;try{rebuild.invoke(zoo);throw new Exception("Invalid resource accepted");}catch(InvocationTargetException expected){if(!(expected.getCause() instanceof java.io.IOException))throw expected;}if(scene.actors[0]!=old[0]||old[0].visible!=visible)throw new Exception("Failed rebuild mutated retained actor");
  System.out.println("Rebuild reuse: all 16 unchanged actors retained; zero frame reads; background retained; failed rebuild rolled back");
 }finally{scene.close();}}
}
