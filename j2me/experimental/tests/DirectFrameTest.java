package pilots;
import java.lang.reflect.*;
/** Hot cels load only their own indexed bytes; equal-sized cache entries recycle buffers. */
public final class DirectFrameTest {
 public static void main(String[] args)throws Exception{
  int banks=TileStore.loads,direct=TileStore.directLoads;long raw=TileStore.rawDecoded;AnimationClip clip=new AnimationClip("/scenes/02-20032.anm");int size=clip.width*clip.height;if(TileStore.loads!=banks||TileStore.directLoads!=direct+1||(TileStore.rawDecoded-raw<size||TileStore.rawDecoded-raw>32768))throw new Exception("Hot frame inflated a tile bank");clip.close();
  Field fs=TileStore.class.getDeclaredField("frames"),ids=TileStore.class.getDeclaredField("frameIds"),ages=TileStore.class.getDeclaredField("frameAges");fs.setAccessible(true);ids.setAccessible(true);ages.setAccessible(true);byte[][] frames=(byte[][])fs.get(null);int[] names=(int[])ids.get(null);long[] timestamps=(long[])ages.get(null);for(int i=0;i<frames.length;i++){frames[i]=null;names[i]=-1;timestamps[i]=0;}
  Method remember=TileStore.class.getDeclaredMethod("remember",Integer.TYPE,byte[].class,Integer.TYPE);remember.setAccessible(true);byte[] data=new byte[512];for(int i=0;i<32;i++)remember.invoke(null,100000+i,data,data.length);byte[] old=frames[0];data[17]=42;remember.invoke(null,100032,data,data.length);if(frames[0]!=old||old[17]!=42||names[0]!=100032)throw new Exception("Cache entry was not recycled");
  System.out.println("Direct frames: hot cel uses a small linear bank and one bulk copy; zero tile-bank load; equal-sized cache buffer reused");
 }
}
