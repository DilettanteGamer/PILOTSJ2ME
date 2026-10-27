package pilots;
import java.lang.reflect.*;
import java.util.Arrays;
import javax.microedition.lcdui.*;
/** Cold entry plus deterministic intro/camera workload; never a phone FPS estimate. */
public final class LocationProfile {
 static Object get(Object object,String name)throws Exception{Field f=object.getClass().getDeclaredField(name);f.setAccessible(true);return f.get(object);}
 static void set(Object object,String name,Object value)throws Exception{Field f=object.getClass().getDeclaredField(name);f.setAccessible(true);f.set(object,value);}
 static long cache(String name)throws Exception{Field f=TileStore.class.getDeclaredField(name);f.setAccessible(true);long total=0;for(byte[] b:(byte[][])f.get(null))if(b!=null)total+=b.length;return total;}
 static double ms(long ns){return Math.round(ns/1000.0)/1000.0;}
 static double percentile(long[] values,int count,int percent){long[] copy=new long[count];System.arraycopy(values,0,copy,0,count);Arrays.sort(copy);return ms(copy[Math.min(count-1,(count-1)*percent/100)]);}
 public static void main(String[] args)throws Exception{
  int requested=Integer.parseInt(args[0]),limit=Integer.parseInt(args[2]);boolean moving=args[1].equals("camera");
  PilotsMidlet app=new PilotsMidlet();PilotsMidlet.GameCanvas canvas=app.new GameCanvas();set(app,"canvas",canvas);set(app,"display",Display.getDisplay(app));
  try{
   // Constructor has loaded scene 2: clear resource state so every requested entry is cold.
   canvas.shutdown();
   for(String name:new String[]{"banks","frames"}){Field f=TileStore.class.getDeclaredField(name);f.setAccessible(true);byte[][] data=(byte[][])f.get(null);for(int i=0;i<data.length;i++)data[i]=null;}
   for(String name:new String[]{"bankIds","frameIds"}){Field f=TileStore.class.getDeclaredField(name);f.setAccessible(true);Arrays.fill((int[])f.get(null),-1);}
   for(String name:new String[]{"palettes","maps","olderMaps","sampledMap"}){Field f=TileStore.class.getDeclaredField(name);f.setAccessible(true);f.set(null,null);}
   for(String name:new String[]{"mapId","olderMapId"}){Field f=TileStore.class.getDeclaredField(name);f.setAccessible(true);f.setInt(null,-1);}
   for(String name:new String[]{"metas","data"}){Field f=PackedResources.class.getDeclaredField(name);f.setAccessible(true);byte[][] data=(byte[][])f.get(null);for(int i=0;i<data.length;i++)data[i]=null;}
   for(String name:new String[]{"metaNames"}){Field f=PackedResources.class.getDeclaredField(name);f.setAccessible(true);Arrays.fill((Object[])f.get(null),null);}
   Field ids=PackedResources.class.getDeclaredField("ids");ids.setAccessible(true);Arrays.fill((int[])ids.get(null),-1);
   FrameRasters.clear();
   long bytes=TileStore.rawDecoded;int banks=TileStore.loads+TileStore.directLoads,opens=PackedResources.opens,images=Image.allocations;
   long begin=System.nanoTime();canvas.startLocation(requested);long entry=System.nanoTime()-begin;
   Graphics graphics=new Graphics(128,160);long[] updates=new long[limit],paints=new long[limit];long sumU=0,sumP=0,peakBanks=0,peakFrames=0,peakRasters=0;int done=0,endScene=requested,lastBanks=0,lastOpens=0,lastImages=0;long lastBytes=0;
   begin=System.nanoTime();canvas.paint(graphics);long firstPaint=System.nanoTime()-begin;
   peakBanks=cache("banks");peakFrames=cache("frames");peakRasters=RasterCache.pixels();
   for(int tick=0;tick<limit;tick++){
    long t=System.nanoTime();canvas.tick(100);updates[done]=System.nanoTime()-t;
    GameState state=(GameState)get(canvas,"state");endScene=state.scene;if(endScene!=requested)break;sumU+=updates[done];
    Viewport view=(Viewport)get(canvas,"view");
    if(moving){int phase=tick%160;view.cursorX=(phase<80?phase:160-phase)*(view.worldWidth-1)/80;view.cursorY=view.worldHeight/2;view.update();}
    graphics.setClip(0,0,128,160);view.paintClip(0,0,view.width,view.height);
    t=System.nanoTime();canvas.paint(graphics);paints[done]=System.nanoTime()-t;sumP+=paints[done];done++;
    peakBanks=Math.max(peakBanks,cache("banks"));peakFrames=Math.max(peakFrames,cache("frames"));peakRasters=Math.max(peakRasters,RasterCache.pixels());lastBanks=TileStore.loads+TileStore.directLoads-banks;lastOpens=PackedResources.opens-opens;lastImages=Image.allocations-images;lastBytes=TileStore.rawDecoded-bytes;
   }
   if(done==0)throw new Exception("No sampled ticks");
   System.out.println("PROFILE {\"scene\":"+requested+",\"mode\":\""+args[1]+"\",\"ticks\":"+done+",\"end_scene\":"+endScene+",\"entry_ms\":"+ms(entry)+",\"first_paint_ms\":"+ms(firstPaint)+",\"update_total_ms\":"+ms(sumU)+",\"paint_total_ms\":"+ms(sumP)+",\"update_p95_ms\":"+percentile(updates,done,95)+",\"paint_p95_ms\":"+percentile(paints,done,95)+",\"paint_max_ms\":"+percentile(paints,done,100)+",\"bank_reads\":"+lastBanks+",\"resource_opens\":"+lastOpens+",\"graphics_bytes\":"+lastBytes+",\"image_allocations\":"+lastImages+",\"peak_bank_bytes\":"+peakBanks+",\"peak_frame_bytes\":"+peakFrames+",\"peak_raster_pixels\":"+peakRasters+"}");
  }finally{canvas.shutdown();}
 }
}
