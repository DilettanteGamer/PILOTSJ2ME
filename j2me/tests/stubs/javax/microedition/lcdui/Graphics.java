package javax.microedition.lcdui;

/** Desktop-only recording surface for production indexed panel rendering. Never packaged. */
public final class Graphics {
    public final java.awt.image.BufferedImage image;
    private int color;
    public int rgbCalls,imageCalls;
    public static long rgbPixels;
    public static int rgbTotalCalls;
    public static int copyCalls;
    public static boolean rejectCopyArea;
    public static boolean failCopyArea;
    public static boolean rejectDrawRegion;
    private int clipX,clipY,clipWidth,clipHeight;
    public Graphics(int width,int height) { image=new java.awt.image.BufferedImage(width,height,java.awt.image.BufferedImage.TYPE_INT_RGB);setClip(0,0,width,height); }
    Graphics(java.awt.image.BufferedImage bitmap) { image=bitmap;setClip(0,0,bitmap.getWidth(),bitmap.getHeight()); }
    public int getClipX() { return clipX; }
    public int getClipY() { return clipY; }
    public int getClipWidth() { return clipWidth; }
    public int getClipHeight() { return clipHeight; }
    public void setClip(int x,int y,int width,int height) {
        clipX=Math.max(0,x);clipY=Math.max(0,y);
        clipWidth=Math.max(0,Math.min(image.getWidth(),x+width)-clipX);
        clipHeight=Math.max(0,Math.min(image.getHeight(),y+height)-clipY);
    }
    public void clipRect(int x,int y,int width,int height) {
        int left=Math.max(x,clipX),top=Math.max(y,clipY);
        int right=Math.min(x+width,clipX+clipWidth),bottom=Math.min(y+height,clipY+clipHeight);
        setClip(left,top,Math.max(0,right-left),Math.max(0,bottom-top));
    }
    public void copyArea(int x,int y,int width,int height,int targetX,int targetY,int anchor) {
        if(failCopyArea) throw new OutOfMemoryError("simulated copyArea allocation failure");
        if(rejectCopyArea) throw new IllegalStateException("simulated unsupported mutable-image copyArea");
        if(x<0 || y<0 || x+width>image.getWidth() || y+height>image.getHeight()) throw new IllegalArgumentException("copy source");
        copyCalls++;
        int[] data=image.getRGB(x,y,width,height,null,0,width);
        for(int yy=0;yy<height;yy++) for(int xx=0;xx<width;xx++) pixel(targetX+xx,targetY+yy,data[yy*width+xx]);
    }
    public void fillRect(int x,int y,int width,int height) {
        for(int yy=y;yy<y+height;yy++) for(int xx=x;xx<x+width;xx++) pixel(xx,yy,color|0xff000000);
    }
    public void drawImage(Image source,int x,int y,int anchor) {
        blit(source,0,0,source.getWidth(),source.getHeight(),x,y);
    }
    public void drawRegion(Image source,int sx,int sy,int width,int height,int transform,int x,int y,int anchor) {
        if(rejectDrawRegion) throw new IllegalArgumentException("simulated unsupported drawRegion");
        if(sx<0 || sy<0 || sx+width>source.getWidth() || sy+height>source.getHeight() || transform!=0) throw new IllegalArgumentException("image region");
        blit(source,sx,sy,width,height,x,y);
    }
    private void blit(Image source,int sx,int sy,int width,int height,int x,int y) {
        imageCalls++;
        for(int yy=Math.max(0,clipY-y);yy<Math.min(height,clipY+clipHeight-y);yy++) for(int xx=Math.max(0,clipX-x);xx<Math.min(width,clipX+clipWidth-x);xx++) {
            int rgb=source.bitmap.getRGB(sx+xx,sy+yy);
            if((rgb>>>24)!=0) pixel(x+xx,y+yy,rgb);
        }
    }
    public void setColor(int rgb) { color=rgb; }
    public void drawLine(int x,int y,int x2,int y2) {
        int dx=Math.abs(x2-x),dy=-Math.abs(y2-y),sx=x<x2?1:-1,sy=y<y2?1:-1,error=dx+dy;
        while(true) {
            pixel(x,y,color);
            if(x==x2 && y==y2) break;
            int twice=error*2;
            if(twice>=dy) { error+=dy;x+=sx; }
            if(twice<=dx) { error+=dx;y+=sy; }
        }
    }
    public void drawRect(int x,int y,int width,int height) {
        for(int i=x;i<=x+width;i++) { pixel(i,y,color);pixel(i,y+height,color); }
        for(int j=y;j<=y+height;j++) { pixel(x,j,color);pixel(x+width,j,color); }
    }
    private void pixel(int x,int y,int rgb) { if(x>=clipX && y>=clipY && x<clipX+clipWidth && y<clipY+clipHeight) image.setRGB(x,y,rgb); }
    public void drawRGB(int[] data,int offset,int scan,int x,int y,int width,int height,boolean alpha) {
        rgbCalls++;
        rgbTotalCalls++;
        rgbPixels+=(long)width*height;
        // Model a driver which validates/copies a full stride for each row.
        // The desktop pixel loop alone misses this device compatibility hazard.
        if(offset<0 || scan<width || (long)offset+(long)height*scan>data.length)
            throw new ArrayIndexOutOfBoundsException("Device RGB stride: offset="+offset+", scan="+scan+", width="+width+", height="+height+", buffer="+data.length);
        for(int row=0;row<height;row++) for(int col=0;col<width;col++) {
            int rgb=data[offset+row*scan+col];if(!alpha || (rgb>>>24)!=0) pixel(x+col,y+row,rgb);
        }
    }
}
