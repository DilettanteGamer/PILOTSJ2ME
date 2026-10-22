package javax.microedition.lcdui;

/** Desktop-only recording surface for production indexed panel rendering. Never packaged. */
public final class Graphics {
    public final java.awt.image.BufferedImage image;
    private int color;
    public int rgbCalls,imageCalls;
    public Graphics(int width,int height) { image=new java.awt.image.BufferedImage(width,height,java.awt.image.BufferedImage.TYPE_INT_RGB); }
    Graphics(java.awt.image.BufferedImage bitmap) { image=bitmap; }
    public void fillRect(int x,int y,int width,int height) {
        for(int yy=y;yy<y+height;yy++) for(int xx=x;xx<x+width;xx++) pixel(xx,yy,color|0xff000000);
    }
    public void drawImage(Image source,int x,int y,int anchor) {
        imageCalls++;
        for(int yy=0;yy<source.getHeight();yy++) for(int xx=0;xx<source.getWidth();xx++) {
            int rgb=source.bitmap.getRGB(xx,yy);
            if((rgb>>>24)!=0) pixel(x+xx,y+yy,rgb);
        }
    }
    public void setColor(int rgb) { color=rgb; }
    public void drawRect(int x,int y,int width,int height) {
        for(int i=x;i<=x+width;i++) { pixel(i,y,color);pixel(i,y+height,color); }
        for(int j=y;j<=y+height;j++) { pixel(x,j,color);pixel(x+width,j,color); }
    }
    private void pixel(int x,int y,int rgb) { if(x>=0 && y>=0 && x<image.getWidth() && y<image.getHeight()) image.setRGB(x,y,rgb); }
    public void drawRGB(int[] data,int offset,int scan,int x,int y,int width,int height,boolean alpha) {
        rgbCalls++;
        // Model a driver which validates/copies a full stride for each row.
        // The desktop pixel loop alone misses this device compatibility hazard.
        if(offset<0 || scan<width || (long)offset+(long)height*scan>data.length)
            throw new ArrayIndexOutOfBoundsException("Device RGB stride: offset="+offset+", scan="+scan+", width="+width+", height="+height+", buffer="+data.length);
        for(int row=0;row<height;row++) for(int col=0;col<width;col++) {
            int rgb=data[offset+row*scan+col];if(!alpha || (rgb>>>24)!=0) pixel(x+col,y+row,rgb);
        }
    }
}
