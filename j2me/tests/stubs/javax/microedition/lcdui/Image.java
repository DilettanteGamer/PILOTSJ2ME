package javax.microedition.lcdui;

/** Desktop implementation of the MIDP image operations used by the raster cache. */
public final class Image {
    final java.awt.image.BufferedImage bitmap;
    public static boolean failAllocations;
    public static int allocations;
    private Image(int width,int height) {
        if(failAllocations) throw new OutOfMemoryError("simulated image heap exhaustion");
        allocations++;
        bitmap=new java.awt.image.BufferedImage(width,height,java.awt.image.BufferedImage.TYPE_INT_ARGB);
    }
    public static Image createImage(int width,int height) { return new Image(width,height); }
    public static Image createRGBImage(int[] rgb,int width,int height,boolean alpha) {
        Image result=new Image(width,height);
        for(int y=0;y<height;y++) for(int x=0;x<width;x++)
            result.bitmap.setRGB(x,y,alpha?rgb[y*width+x]:rgb[y*width+x]|0xff000000);
        return result;
    }
    public Graphics getGraphics() { return new Graphics(bitmap); }
    public int getWidth() { return bitmap.getWidth(); }
    public int getHeight() { return bitmap.getHeight(); }
}
