package pilots;
import javax.microedition.lcdui.*;import java.security.*;
/** Three independent loops, real scaling/alpha, camera/clip changes and allocation pressure. */
public final class RenderBurstTest {
 public static void main(String[] args)throws Exception{
  RasterCache.budget(65536);Viewport view=new Viewport(128,148,640,480);view.cursorX=320;view.update();Graphics g=new Graphics(128,160);
  AnimationClip[] actors={new AnimationClip("/scenes/02-20032.anm"),new AnimationClip("/scenes/02-20028.anm"),new AnimationClip("/scenes/02-65.anm")};int[] x={250,330,370},y={310,310,200};MessageDigest hash=MessageDigest.getInstance("SHA-256");long start=System.nanoTime();int warmed=0;int first=Image.allocations;
  for(int t=0;t<300;t++){
   if(t==150)warmed=Image.allocations;
   if(t==200&&args.length>0){RasterCache.budget(0);Graphics.rejectDrawRegion=true;}
   g.setClip(0,0,128,160);g.setColor(0x224466);g.fillRect(0,0,128,160);view.paintClip(0,0,128,148);
   if(t>=220){view.cursorX=t%2==0?100:550;view.zoom=t>=260;view.update();}
   if(t>=200&&t<220){g.setClip(23,35,60,72);view.paintClip(23,35,60,72);}
   for(int i=0;i<actors.length;i++){if(!actors[i].advance())actors[i].restart();actors[i].paint(g,view,x[i],y[i]);}
   for(int yy=0;yy<160;yy++)for(int xx=0;xx<128;xx++){int pixel=g.image.getRGB(xx,yy);hash.update((byte)(pixel>>16));hash.update((byte)(pixel>>8));hash.update((byte)pixel);}
   if(RasterCache.pixels()>65536)throw new Exception("Raster budget exceeded");
   if(t==199){int count=Image.allocations-warmed;System.out.println("warm_50_frames_allocations="+count);if(count!=0)throw new Exception("Warmed loops allocate images");}
  }
  String sha="";for(byte b:hash.digest())sha+=Integer.toHexString((b&255)|256).substring(1);System.out.println("render_sha256="+sha+" allocations="+(Image.allocations-first)+" total_ms="+(System.nanoTime()-start)/1000000);if(!sha.equals("d36b28b215de60908f819632a488a446cedc8285080cc064756f7fd667eb98f7"))throw new Exception("Rendering differs from 0.27.2 reference");for(AnimationClip a:actors)a.close();
 }
}
