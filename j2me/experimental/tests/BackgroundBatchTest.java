package pilots;
import javax.microedition.lcdui.*;import java.lang.reflect.*;import java.security.*;
/** Exercise cold, cached, panned, clipped, zoomed and uncached original P8 pixels. */
public final class BackgroundBatchTest {
 public static void main(String[] args)throws Exception{
  boolean reference=args.length>0;MessageDigest digest=MessageDigest.getInstance("SHA-256");long calls=0;
  for(int scene:new int[]{2,4,7,13,16})for(int budget:new int[]{65536,0}){
   RasterCache.budget(budget);IndexedBackground bg=new IndexedBackground(PackedResources.open("/scenes/"+(scene<10?"0":"")+scene+".p8"));
   Viewport v=new Viewport(128,148,bg.worldWidth,bg.worldHeight);Graphics g=new Graphics(128,160);
   for(int t=0;t<16;t++){
    v.cursorX=t%2==0?bg.worldWidth/2:Math.min(bg.worldWidth-1,bg.worldWidth/2+t*2);v.zoom=t>=8;v.update();
    g.setClip(t%3==0?23:0,t%3==0?35:0,t%3==0?60:128,t%3==0?72:160);v.paintClip(g.getClipX(),g.getClipY(),g.getClipWidth(),g.getClipHeight());
    int before=Graphics.rgbTotalCalls;bg.paint(g,v);calls+=Graphics.rgbTotalCalls-before;
    if(!reference){Field f=IndexedBackground.class.getDeclaredField("row");f.setAccessible(true);int[] row=(int[])f.get(bg);if(row!=null&&row.length>1024)throw new Exception("Background scratch budget exceeded");}
    for(int y=0;y<160;y++)for(int x=0;x<128;x++){int rgb=g.image.getRGB(x,y);digest.update((byte)(rgb>>16));digest.update((byte)(rgb>>8));digest.update((byte)rgb);}
   }bg.clearCache();
  }
  String hex="";for(byte b:digest.digest())hex+=Integer.toHexString((b&255)|256).substring(1);
  System.out.println("background_cases_sha256="+hex);System.out.println("background_driver_calls="+calls);
 }
}
