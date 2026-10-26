package pilots;
import javax.microedition.lcdui.Image;
/** Reuses scaled cels inside the existing RasterCache budget; never owns raw Image references. */
public final class FrameRasters {
 private static final int COUNT=32,LIMIT=32768;
 private static final RasterCache[] rasters=new RasterCache[COUNT];
 private static final int[] images=new int[COUNT],palettes=new int[COUNT],widths=new int[COUNT],heights=new int[COUNT];
 private static final long[] ages=new long[COUNT];private static long clock;
 public static int hits,stores;
 public static synchronized Image get(int image,int palette,int width,int height){for(int i=0;i<COUNT;i++)if(rasters[i]!=null&&images[i]==image&&palettes[i]==palette&&widths[i]==width&&heights[i]==height){Image found=rasters[i].get();if(found!=null){ages[i]=++clock;hits++;return found;}}return null;}
 public static synchronized void put(int image,int palette,Image bitmap){
  int area=bitmap.getWidth()*bitmap.getHeight();if(area>LIMIT)return;int slot=-1,total=0;
  for(int i=0;i<COUNT;i++){if(rasters[i]==null||rasters[i].get()==null){if(slot<0)slot=i;}else total+=widths[i]*heights[i];}
  if(slot<0){slot=0;for(int i=1;i<COUNT;i++)if(ages[i]<ages[slot])slot=i;total-=widths[slot]*heights[slot];rasters[slot].clear();}
  while(total+area>LIMIT){int oldest=-1;for(int i=0;i<COUNT;i++)if(i!=slot&&rasters[i]!=null&&rasters[i].get()!=null&&(oldest<0||ages[i]<ages[oldest]))oldest=i;if(oldest<0)break;total-=widths[oldest]*heights[oldest];rasters[oldest].clear();}
  if(rasters[slot]==null)rasters[slot]=new RasterCache();rasters[slot].store(bitmap);images[slot]=image;palettes[slot]=palette;widths[slot]=bitmap.getWidth();heights[slot]=bitmap.getHeight();ages[slot]=++clock;stores++;
 }
 public static synchronized void clear(){for(int i=0;i<COUNT;i++)if(rasters[i]!=null)rasters[i].clear();}
}
