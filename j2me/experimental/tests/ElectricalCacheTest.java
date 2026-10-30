package pilots;
import javax.microedition.lcdui.*;
import java.lang.reflect.*;import java.security.MessageDigest;
/** Full electrical intro pixels/state + >32 small simultaneous raster keys, fixed pixel budget. */
public final class ElectricalCacheTest {
 static void set(Object o,String n,Object v)throws Exception{Field f=o.getClass().getDeclaredField(n);f.setAccessible(true);f.set(o,v);}
 static String hex(byte[] bytes){String s="";for(byte b:bytes)s+=Integer.toHexString((b&255)|256).substring(1);return s;}
 public static void main(String[] args)throws Exception {
  boolean reference=args.length>0;while(DebugHud.mode()!=0)DebugHud.toggle();
  PilotsMidlet app=new PilotsMidlet();PilotsMidlet.GameCanvas canvas=app.new GameCanvas();set(app,"canvas",canvas);set(app,"display",Display.getDisplay(app));
  MessageDigest pixels=MessageDigest.getInstance("SHA-256"),state=MessageDigest.getInstance("SHA-256");int before=Image.allocations;
  try {
   canvas.startLocation(13);Graphics g=new Graphics(128,160);Field sf=canvas.getClass().getDeclaredField("state");sf.setAccessible(true);
   for(int tick=0;tick<600;tick++){
    canvas.tick(100);
    AnimatedActor[] hidden=null;Scene room=null;
    if(reference && args[0].equals("instant-reference")) {
     Field roomField=canvas.getClass().getDeclaredField("scene");roomField.setAccessible(true);room=(Scene)roomField.get(canvas);hidden=new AnimatedActor[room.actors.length];
     for(int i=0;i<room.actors.length;i++){AnimatedActor actor=room.actors[i];if(actor!=null&&actor.id>=205&&actor.id<=210){hidden[i]=actor;room.actors[i]=null;}}
    }
    canvas.paint(g);if(hidden!=null)for(int i=0;i<hidden.length;i++)if(hidden[i]!=null)room.actors[i]=hidden[i];
    state.update(((GameState)sf.get(canvas)).encode());
    for(int y=0;y<160;y++)for(int x=0;x<128;x++){int rgb=g.image.getRGB(x,y);pixels.update((byte)(rgb>>16));pixels.update((byte)(rgb>>8));pixels.update((byte)rgb);}
    if(RasterCache.pixels()>65536)throw new Exception("Raster pixel budget exceeded");
   }
   int made=Image.allocations-before;String logic=hex(state.digest()),painted=hex(pixels.digest());
   System.out.println("electrical_intro ticks=600 images="+made+" state_sha256="+logic+" pixel_sha256="+painted);
   if(!reference && (!logic.equals("9425ad4062e4f6ca404dc5a697bd3c271f389e95ad24fb4e96ef57ce09cf7fa2") || !painted.equals("429e4f1af3c87cbc1b7765e7184f1e65ad9a27ecb19d11a10514cc3882e92606")))throw new Exception("Electrical pixels or game state differ from instant-light reference on 0.27.19");
   if(!reference && made>1000)throw new Exception("Electrical raster churn returned: "+made);
  }finally{canvas.shutdown();}
  if(!reference){
   FrameRasters.clear();RasterCache.budget(65536);int[] rgb=new int[100];
   for(int i=0;i<48;i++)FrameRasters.put(100000+i,0,Image.createRGBImage(rgb,10,10,true));
   for(int pass=0;pass<3;pass++)for(int i=0;i<48;i++)if(FrameRasters.get(100000+i,0,10,10)==null)throw new Exception("48-key working set evicted despite fitting pixel budget");
   if(RasterCache.pixels()!=4800)throw new Exception("Unexpected raster memory cost");FrameRasters.clear();if(RasterCache.pixels()!=0)throw new Exception("Raster images retained after clear");
   System.out.println("48 simultaneous raster keys stay warm within 4800 pixels; scene clear releases every cached image");
  }
 }
}
