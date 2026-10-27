package pilots;
/** Covers crop origins, changing source dimensions, zoom and buffer reuse. */
public final class PixelSamplingTest {
 public static void main(String[] args)throws Exception {
  int[] reuse=null;long checked=0,oldDivisions=0,newDivisions=0;
  for(int source=1;source<=2048;source+=7)for(int draw=1;draw<=512;draw+=11) {
   int start=draw/3,count=Math.min(128,draw-start);
   int[] before=reuse;reuse=PixelSampling.columns(reuse,source,draw,start,count);
   if(before!=null && before.length>=count && before!=reuse)throw new Exception("Reusable columns allocated again");
   for(int x=0;x<count;x++){if(reuse[x]!=(start+x)*source/draw)throw new Exception("Sampling changed");checked++;}
   oldDivisions+=(long)count*64;newDivisions+=count;
  }
  System.out.println("exact_columns="+checked+" same_size_buffer_reused=true representative_64_row_pixel_divisions="+oldDivisions+"->"+newDivisions);
 }
}
