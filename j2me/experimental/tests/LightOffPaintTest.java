package pilots;
import java.lang.reflect.*;import java.security.*;import javax.microedition.lcdui.*;
/** Simulate an evicted background exactly at the first instant-darkness stage. */
public final class LightOffPaintTest {
 static Object get(Object o,String n)throws Exception{Field f=o.getClass().getDeclaredField(n);f.setAccessible(true);return f.get(o);}
 static void set(Object o,String n,Object v)throws Exception{Field f=o.getClass().getDeclaredField(n);f.setAccessible(true);f.set(o,v);}
 static boolean first(HotelWorld w){for(HotelWorld.Thing[] group:new HotelWorld.Thing[][]{w.cells,w.heroes,w.effects})for(HotelWorld.Thing t:group)if(t!=null&&t.active&&t.clip==205)return true;return false;}
 public static void main(String[] args)throws Exception{
  while(DebugHud.mode()!=0)DebugHud.toggle();PilotsMidlet app=new PilotsMidlet();PilotsMidlet.GameCanvas c=app.new GameCanvas();set(app,"canvas",c);set(app,"display",Display.getDisplay(app));
  try{c.startLocation(13);Graphics g=new Graphics(128,160);
   for(int tick=0;tick<250;tick++){
    c.tick(100);boolean dark=first(((HotelScene)get(c,"zoo")).animals.world);
    if(dark)((Scene)get(c,"scene")).background.clearCache();
    int calls=Graphics.rgbTotalCalls;c.paint(g);
    if(dark){MessageDigest hash=MessageDigest.getInstance("SHA-256");for(int y=0;y<160;y++)for(int x=0;x<128;x++){int rgb=g.image.getRGB(x,y);hash.update((byte)(rgb>>16));hash.update((byte)(rgb>>8));hash.update((byte)rgb);}
     String hex="";for(byte b:hash.digest())hex+=Integer.toHexString((b&255)|256).substring(1);
     System.out.println("light_off_tick="+tick);System.out.println("light_off_driver_calls="+(Graphics.rgbTotalCalls-calls));System.out.println("light_off_pixels_sha256="+hex);return;
    }
   }throw new Exception("Light-off stage not reached");
  }finally{c.shutdown();}
 }
}
