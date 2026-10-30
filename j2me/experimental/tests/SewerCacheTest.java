package pilots;
import java.lang.reflect.*;import java.security.*;import javax.microedition.lcdui.*;
/** Same screen/state through idle, a real fall, save/load, swimming and recovery. */
public final class SewerCacheTest {
 static Object get(Object o,String n)throws Exception{Field f=o.getClass().getDeclaredField(n);f.setAccessible(true);return f.get(o);}
 static void set(Object o,String n,Object v)throws Exception{Field f=o.getClass().getDeclaredField(n);f.setAccessible(true);f.set(o,v);}
 static int rasterSetting(String name)throws Exception{Field f=FrameRasters.class.getDeclaredField(name);f.setAccessible(true);return f.getInt(null);}
 static String hex(MessageDigest d){String s="";for(byte b:d.digest())s+=Integer.toHexString((b&255)|256).substring(1);return s;}
 static void sample(PilotsMidlet.GameCanvas c,Graphics g,MessageDigest pixels,MessageDigest state)throws Exception{
  c.paint(g);state.update(((GameState)get(c,"state")).encode());
  for(int y=0;y<160;y++)for(int x=0;x<128;x++){int rgb=g.image.getRGB(x,y);pixels.update((byte)(rgb>>16));pixels.update((byte)(rgb>>8));pixels.update((byte)rgb);}
  if(RasterCache.pixels()>65536)throw new Exception("Sewer exceeded shared pixel budget");
 }
 public static void main(String[] args)throws Exception{
  boolean reference=args.length>0;while(DebugHud.mode()!=0)DebugHud.toggle();PilotsMidlet app=new PilotsMidlet();PilotsMidlet.GameCanvas c=app.new GameCanvas();set(app,"canvas",c);set(app,"display",Display.getDisplay(app));
  try{c.startLocation(7);Graphics g=new Graphics(128,160);MessageDigest pixels=MessageDigest.getInstance("SHA-256"),state=MessageDigest.getInstance("SHA-256");int images=0,banks=0;
   if(!reference)for(AnimatedActor a:((Scene)get(c,"scene")).actors)if(get(a.clip,"pixels")!=null)throw new Exception("Sewer entry decoded unpainted frames");
   for(int tick=0;tick<400;tick++){
    c.tick(100);sample(c,g,pixels,state);if(tick==199){images=Image.allocations;banks=TileStore.loads+TileStore.directLoads;}
   }
   int warmImages=Image.allocations-images,warmBanks=TileStore.loads+TileStore.directLoads-banks;
   System.out.println("sewer_idle_pixels="+hex(pixels));System.out.println("sewer_idle_state="+hex(state));
   System.out.println("sewer_warm_images="+warmImages);System.out.println("sewer_warm_banks="+warmBanks);
   if(!reference){
    if(rasterSetting("activeCount")!=192||rasterSetting("LIMIT")!=45056)throw new Exception("Sewer working set not enabled");
    if(warmImages>20||warmBanks>2)throw new Exception("Sewer warm loops still churn: images="+warmImages+" banks="+warmBanks);
   }
   pixels=MessageDigest.getInstance("SHA-256");state=MessageDigest.getInstance("SHA-256");SewerScene sewer=(SewerScene)get(c,"zoo");
   for(int i=0;i<100&&sewer.sewer.fall==0;i++){sewer.input(4);c.tick(100);sample(c,g,pixels,state);}
   if(sewer.sewer.fall==0)throw new Exception("Real fall not reached");c.save();c.load();sewer=(SewerScene)get(c,"zoo");
   boolean swam=false,spoke=false;int budget=100;
   while(sewer.sewer.fall>0&&budget-->0){c.tick(100);sample(c,g,pixels,state);swam|=sewer.sewer.fall>=21&&sewer.sewer.fall<=50;spoke|=sewer.sewer.fall>=76&&sewer.sewer.fall<=86;}
   if(budget<=0||!swam||!spoke||sewer.sewer.x!=448)throw new Exception("Saved fall/recovery failed");
   System.out.println("sewer_recovery_pixels="+hex(pixels));System.out.println("sewer_recovery_state="+hex(state));
   c.startLocation(4);if(!reference&&(rasterSetting("activeCount")!=64||rasterSetting("LIMIT")!=32768))throw new Exception("Sewer partition leaked to next location");
  }finally{c.shutdown();}
 }
}
