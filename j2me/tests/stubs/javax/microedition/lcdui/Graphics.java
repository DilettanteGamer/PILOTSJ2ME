package javax.microedition.lcdui;

/** Desktop-only recording surface for production indexed panel rendering. Never packaged. */
public final class Graphics {
    public final java.awt.image.BufferedImage image;
    private int color;
    public Graphics(int width,int height) { image=new java.awt.image.BufferedImage(width,height,java.awt.image.BufferedImage.TYPE_INT_RGB); }
    public void setColor(int rgb) { color=rgb; }
    public void drawRect(int x,int y,int width,int height) {
        for(int i=x;i<=x+width;i++) { pixel(i,y,color);pixel(i,y+height,color); }
        for(int j=y;j<=y+height;j++) { pixel(x,j,color);pixel(x+width,j,color); }
    }
    private void pixel(int x,int y,int rgb) { if(x>=0 && y>=0 && x<image.getWidth() && y<image.getHeight()) image.setRGB(x,y,rgb); }
    public void drawRGB(int[] data,int offset,int scan,int x,int y,int width,int height,boolean alpha) {
        for(int row=0;row<height;row++) for(int col=0;col<width;col++) {
            int rgb=data[offset+row*scan+col];if(!alpha || (rgb>>>24)!=0) pixel(x+col,y+row,rgb);
        }
    }
}
