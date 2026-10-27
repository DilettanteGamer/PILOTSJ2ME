package pilots;

/** Exact nearest-neighbour columns, reused by every row of one scaled cel. */
public final class PixelSampling {
    private PixelSampling() { }
    public static int[] columns(int[] reuse,int sourceWidth,int drawWidth,int start,int count) {
        if(reuse==null || reuse.length<count) reuse=new int[count];
        for(int x=0;x<count;x++) reuse[x]=(start+x)*sourceWidth/drawWidth;
        return reuse;
    }
}
