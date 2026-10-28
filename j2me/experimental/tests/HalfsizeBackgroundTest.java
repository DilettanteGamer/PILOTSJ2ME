package pilots;
import javax.microedition.lcdui.*;import java.security.*;
/** Normal-scale background pixels must stay identical across different storage sizes. */
public final class HalfsizeBackgroundTest {
 public static void main(String[] args)throws Exception{
  MessageDigest digest=MessageDigest.getInstance("SHA-256");
  for(int scene=2;scene<=17;scene++){
   IndexedBackground bg=new IndexedBackground(PackedResources.open("/scenes/"+(scene<10?"0":"")+scene+".p8"));
   Viewport v=new Viewport(128,148,bg.worldWidth,bg.worldHeight);Graphics g=new Graphics(128,148);
   for(int cursor:new int[]{0,bg.worldWidth/2,bg.worldWidth-1}){
    v.cursorX=cursor;v.update();bg.paint(g,v);
    for(int y=0;y<148;y++)for(int x=0;x<128;x++){int rgb=g.image.getRGB(x,y);digest.update((byte)(rgb>>16));digest.update((byte)(rgb>>8));digest.update((byte)rgb);}
   }bg.clearCache();
  }
  String hex="";for(byte b:digest.digest())hex+=Integer.toHexString((b&255)|256).substring(1);
  System.out.println("background_normal_scale_sha256="+hex);
 }
}
