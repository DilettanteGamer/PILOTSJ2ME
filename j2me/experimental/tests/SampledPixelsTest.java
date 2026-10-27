package pilots;
import java.io.*;import java.lang.reflect.*;import java.util.Arrays;
import javax.microedition.lcdui.*;
/** Every packaged variant vs exact original indices, then render/crop/zoom/fallback paths. */
public final class SampledPixelsTest {
 private static void check(boolean ok,String message)throws Exception{if(!ok)throw new Exception(message);}
 public static void main(String[] args)throws Exception {
  InputStream resource=SampledPixelsTest.class.getResourceAsStream("/tiles/sampled");DataInputStream in=new DataInputStream(resource);int count=in.readUnsignedShort();
  int[] clips={53,54,111};int variants=0,renders=0;boolean[] seen=new boolean[8630];
  Field id=AnimationClip.class.getDeclaredField("tileImage"),raw=AnimationClip.class.getDeclaredField("pixels");id.setAccessible(true);raw.setAccessible(true);
  for(int c:clips){AnimationClip original=new AnimationClip("/scenes/04-"+c+".anm");
   for(int frame=0;frame<original.count;frame++){original.seek(frame);int image=id.getInt(original);byte[] pixels=(byte[])raw.get(original);if(seen[image])continue;seen[image]=true;
    for(int w=Math.max(1,original.width*228/640);w<=Math.max(1,(original.width*228+639)/640);w++)for(int h=Math.max(1,original.height*148/417);h<=Math.max(1,(original.height*148+416)/417);h++){
     byte[] sampled=TileStore.sampled(image,w,h,null);check(sampled!=null,"Missing variant");
     for(int y=0;y<h;y++)for(int x=0;x<w;x++)check(sampled[y*w+x]==pixels[(y*original.height/h)*original.width+x*original.width/w],"Sampled index differs");
     int reads=TileStore.sampleReads,hits=TileStore.sampleHits;byte[] again=TileStore.sampled(image,w,h,sampled);check(again==sampled && TileStore.sampleReads==reads && TileStore.sampleHits==hits+1,"Sample cache/reuse failed");NativeCacheTest.bounds();variants++;
    }
   }original.close();
  }
  check(variants==count,"Variant coverage differs");in.close();
  for(int budget:new int[]{65536,0}){RasterCache.budget(budget);
   for(int c:clips){AnimationClip original=new AnimationClip("/scenes/04-"+c+".anm"),lazy=new AnimationClip("/scenes/04-"+c+".anm",true);
    for(int frame=0;frame<original.count;frame++){original.seek(frame);lazy.seek(frame);
     for(int mode=0;mode<8;mode++){
      Viewport v=new Viewport(128,mode==6?146:148,640,417);v.cursorX=mode%2==0?160:480;v.zoom=mode==7;v.update();
      int x=(mode==0?-30:mode==1?570:320)-original.offsetX,y=mode==2?390-original.offsetY:140-original.offsetY;
      Graphics a=new Graphics(128,160),b=new Graphics(128,160);
      if(mode==3){a.setClip(30,30,21,60);b.setClip(30,30,21,60);v.paintClip(30,30,21,60);}
      FrameRasters.clear();lazy.paint(a,v,x,y);FrameRasters.clear();original.paint(b,v,x,y);
      check(Arrays.equals(a.image.getRGB(0,0,128,160,null,0,128),b.image.getRGB(0,0,128,160,null,0,128)),"Sample rendering differs "+c+":"+frame+":"+mode+":"+budget);renders++;
     }
    }original.close();lazy.close();
   }
  }
  RasterCache.budget(65536);System.out.println("Sampled indices exact: "+variants+" variants; "+renders+" renders with crop/camera/rounding/zoom/other viewport and uncached fallback; source frames retained");
 }
}
