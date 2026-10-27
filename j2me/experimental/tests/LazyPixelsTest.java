package pilots;
import java.lang.reflect.Field;import java.util.Arrays;import java.io.IOException;
import javax.microedition.lcdui.*;
/** Deferred metadata, offscreen culling, shared images, exact hit masks and IO propagation. */
public final class LazyPixelsTest {
 private static void check(boolean ok,String why)throws Exception{if(!ok)throw new Exception(why);}
 private static Object pixels(AnimationClip c)throws Exception{Field f=AnimationClip.class.getDeclaredField("pixels");f.setAccessible(true);return f.get(c);}
 public static void main(String[] args)throws Exception {
  AnimationClip eager=new AnimationClip("/scenes/04-20032.anm");int calls=TileStore.frameCalls;
  AnimationClip lazy=new AnimationClip("/scenes/04-20032.anm",true);
  lazy.seek(lazy.count-1);lazy.seek(0);lazy.seek(lazy.count/2);
  check(TileStore.frameCalls==calls && pixels(lazy)==null,"Metadata seeks decoded pixels");
  eager.seek(lazy.index);calls=TileStore.frameCalls;
  Viewport view=new Viewport(128,148,640,417);view.update();Graphics a=new Graphics(128,160),b=new Graphics(128,160);
  lazy.paint(a,view,-10000,0);check(TileStore.frameCalls==calls,"Offscreen paint fetched source pixels");
  int x=320-lazy.offsetX-lazy.width/2,y=208-lazy.offsetY-lazy.height/2;
  FrameRasters.clear();lazy.paint(a,view,x,y);check(TileStore.frameCalls==calls+1,"Visible paint did not fetch source pixels exactly once");
  eager.paint(b,view,x,y);check(Arrays.equals(a.image.getRGB(0,0,128,160,null,0,128),b.image.getRGB(0,0,128,160,null,0,128)),"Deferred painting differs");
  AnimationClip shared=new AnimationClip("/scenes/04-20032.anm",true);shared.seek(lazy.index);calls=TileStore.frameCalls;shared.paint(a,view,x,y);
  check(TileStore.frameCalls==calls && pixels(shared)==null,"Shared raster still fetched source pixels");
  for(int frame=0;frame<lazy.count;frame++) {
   lazy.seek(frame);eager.seek(frame);
   check(lazy.index==eager.index && lazy.width==eager.width && lazy.height==eager.height && lazy.offsetX==eager.offsetX && lazy.offsetY==eager.offsetY,"Deferred frame metadata differs");
   for(int yy=-1;yy<=lazy.height;yy++)for(int xx=-1;xx<=lazy.width;xx++)check(lazy.contains(lazy.offsetX+xx,lazy.offsetY+yy)==eager.contains(eager.offsetX+xx,eager.offsetY+yy),"Deferred hit mask differs");
  }
  AnimationClip bad=new AnimationClip("/lazy-invalid.anm",true);bad.paint(a,view,0,0);boolean reported=false;
  try{bad.seek(0);}catch(IOException expected){reported=true;}check(reported,"Paint IO failure was swallowed instead of reported on update");
  eager.close();lazy.close();shared.close();bad.close();
  System.out.println("Deferred pixels: zero loads for metadata/offscreen/shared-raster use; exact visible pixels and all walking hit masks; failed pixel IO reported to next update");
 }
}
