package pilots;
import javax.microedition.lcdui.Image;
/** Scratch conversion evicts old sprite rasters before discarding the painted background. */
public final class RasterReservationTest {
 private static Image bitmap(int width,int height){return Image.createRGBImage(new int[width*height],width,height,true);}
 private static void check(boolean ok,String why)throws Exception{if(!ok)throw new Exception(why);}
 public static void main(String[] args)throws Exception {
  RasterCache.budget(100);RasterCache a=new RasterCache(),b=new RasterCache(),c=new RasterCache();
  a.store(bitmap(5,6));b.store(bitmap(5,6));check(a.peek()!=null,"peek lost image");c.prepare(50);
  check(a.peek()==null && b.peek()!=null,"Accounting peek changed LRU order");a.clear();b.clear();c.clear();
  RasterCache background=new RasterCache();background.store(bitmap(4,5));FrameRasters.put(123,0,bitmap(5,5));
  FrameRasters.prepare(70);check(background.peek()!=null && RasterCache.pixels()==20,"Scratch reservation evicted background before old cel");
  check(FrameRasters.get(123,0,5,5)==null,"Evicted frame still referenced");
  FrameRasters.prepare(101);check(background.peek()!=null,"Oversized reservation unexpectedly cleared background");
  background.clear();FrameRasters.clear();RasterCache.budget(65536);
  System.out.println("Raster LRU: accounting peek does not mark use; old sprite freed before background for scratch; fixed budget and oversized fallback preserved");
 }
}
