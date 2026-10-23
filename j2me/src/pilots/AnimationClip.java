/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  javax.microedition.lcdui.Graphics
 */
package pilots;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import pilots.Viewport;

public final class AnimationClip {
    private final String resource;
    private DataInputStream input;
    private final int[] palette = new int[256];
    private byte[] pixels;
    private int[] row;
    public int index;
    public int count;
    public int duration;
    public int offsetX;
    public int offsetY;
    public int width;
    public int height;
    private int transparent;
    private boolean packed;
    private byte[] encoded;
    private final RasterCache raster=new RasterCache();
    private int cachedWidth,cachedHeight,cachedX,cachedY,cachedCropWidth,cachedCropHeight;
    private static boolean regionSupported=true;

    public AnimationClip(String string) throws IOException {
        this.resource = string;
        this.restart();
    }

    public void restart() throws IOException {
        this.close();
        InputStream inputStream = WalkStreams.open(this.resource);
        if (inputStream == null) {
            throw new IOException("Missing animation " + this.resource);
        }
        this.input = new DataInputStream(inputStream);
        try {
            int magic=this.input.readInt();
            packed=magic==0x414e4d33;
            if (magic != 1095650609 && !packed) {
                throw new IOException("Invalid animation magic");
            }
            this.count = this.input.readUnsignedShort();
            this.duration = this.input.readUnsignedShort();
            this.transparent = this.input.readUnsignedByte();
            if (this.count < 1 || this.count > 1024 || this.duration < 20 || this.duration > 1000) {
                throw new IOException("Invalid animation header");
            }
            int n = 0;
            byte[] colors=new byte[768];input.readFully(colors);
            while (n < 256) {
                int p=n*3;
                this.palette[n] = 0xFF000000 | (colors[p]&255) << 16 | (colors[p+1]&255) << 8 | (colors[p+2]&255);
                ++n;
            }
            this.palette[this.transparent] = 0;
            this.index = -1;
            this.advance();
        }
        catch (IOException iOException) {
            this.close();
            throw iOException;
        }
        catch (OutOfMemoryError outOfMemoryError) {
            this.close();
            throw outOfMemoryError;
        }
    }

    public boolean advance() throws IOException {
        if (this.input == null) {
            return false;
        }
        raster.clear();
        try {
            short s = this.input.readShort();
            short s2 = this.input.readShort();
            int n = this.input.readUnsignedShort();
            int n2 = this.input.readUnsignedShort();
            if (n < 1 || n > 640 || n2 < 1 || n2 > 480 || s < -2048 || s > 2048 || s2 < -2048 || s2 > 2048) {
                throw new IOException("Invalid animation frame");
            }
            if (this.pixels == null || this.pixels.length < n * n2) {
                this.pixels = new byte[n * n2];
            }
            int size=n*n2;
            int stored=packed?input.readInt():0;
            if(stored<0 || stored>=size && stored!=0) throw new IOException("Invalid packed frame size");
            if(stored==0) this.input.readFully(this.pixels, 0, size);
            else {
                if(encoded==null || encoded.length<stored) encoded=new byte[stored];
                input.readFully(encoded,0,stored);
                int source=0,target=0;
                while(source<stored) {
                    int control=encoded[source++]&255;
                    int length=control<128?control+1:(control&127)+3;
                    if(target+length>size) throw new IOException("Packed frame overflow");
                    if(control<128) {
                        if(source+length>stored) throw new IOException("Truncated literal run");
                        System.arraycopy(encoded,source,pixels,target,length);source+=length;target+=length;
                    } else {
                        if(source>=stored) throw new IOException("Truncated repeated run");
                        byte value=encoded[source++];
                        for(int end=target+length;target<end;target++) pixels[target]=value;
                    }
                }
                if(target!=size) throw new IOException("Incomplete packed frame");
            }
            this.width = n;
            this.height = n2;
            this.offsetX = s;
            this.offsetY = s2;
            ++this.index;
            if (this.index == this.count - 1) {
                if (this.input.read() != -1) {
                    throw new IOException("Trailing animation data");
                }
                this.close();
            }
            return true;
        }
        catch (IOException iOException) {
            this.close();
            this.pixels = null;
            throw iOException;
        }
    }

    public void close() {
        raster.clear();
        encoded=null;
        if (this.input != null) {
            try {
                this.input.close();
            }
            catch (IOException iOException) {}
            this.input = null;
        }
    }

    public boolean contains(int x,int y) {
        x-=offsetX;y-=offsetY;
        return pixels!=null && x>=0 && y>=0 && x<width && y<height && (pixels[y*width+x]&255)!=transparent;
    }

    // Nearest-neighbour indexed rendering, with transparent runs skipped.
    public void paint(Graphics graphics, Viewport view, int x, int y) {
        if (pixels == null) return;
        int left = view.screenX(x + offsetX);
        int top = view.screenY(y + offsetY);
        int drawWidth = Math.max(1, view.screenX(x + offsetX + width) - left);
        int drawHeight = Math.max(1, view.screenY(y + offsetY + height) - top);
        int x1 = Math.max(0, left), x2 = Math.min(view.width, left + drawWidth);
        int y1 = Math.max(0, top), y2 = Math.min(view.height, top + drawHeight);
        if (x2 <= x1 || y2 <= y1) return;
        if(x2<=view.paintLeft || x1>=view.paintRight || y2<=view.paintTop || y1>=view.paintBottom) return;
        int cropWidth=x2-x1,cropHeight=y2-y1;
        // Cache the whole scaled sprite when it fits, so panning across an edge
        // changes only the drawRegion source rectangle, not the cached image.
        boolean whole=drawWidth*drawHeight<=cropWidth*cropHeight+256 && RasterCache.allows(drawWidth*drawHeight*2);
        int rasterX=whole?0:x1-left,rasterY=whole?0:y1-top;
        int rasterWidth=whole?drawWidth:cropWidth,rasterHeight=whole?drawHeight:cropHeight;
        Image cached=raster.get();
        if(cached!=null && cachedWidth==drawWidth && cachedHeight==drawHeight &&
           cachedX<=x1-left && cachedY<=y1-top && cachedX+cachedCropWidth>=x2-left && cachedY+cachedCropHeight>=y2-top) {
            paintRaster(graphics,cached,x1,y1,x1-left-cachedX,y1-top-cachedY,cropWidth,cropHeight);return;
        }
        raster.clear();
        int area=rasterWidth*rasterHeight;
        if(RasterCache.allows(area*2)) {
            try {
                raster.prepare(area*2);
                int[] rgb=new int[area];
                for(int yy=0;yy<rasterHeight;yy++) {
                    int sourceRow=(rasterY+yy)*height/drawHeight*width;
                    for(int xx=0;xx<rasterWidth;xx++)
                        rgb[yy*rasterWidth+xx]=palette[pixels[sourceRow+(rasterX+xx)*width/drawWidth]&255];
                }
                Image bitmap=Image.createRGBImage(rgb,rasterWidth,rasterHeight,true);
                raster.store(bitmap);
                cachedWidth=drawWidth;cachedHeight=drawHeight;cachedX=rasterX;cachedY=rasterY;
                cachedCropWidth=rasterWidth;cachedCropHeight=rasterHeight;
                paintRaster(graphics,bitmap,x1,y1,x1-left-rasterX,y1-top-rasterY,cropWidth,cropHeight);return;
            } catch(OutOfMemoryError exhausted) { RasterCache.failed(); }
              catch(IllegalArgumentException unsupported) { RasterCache.failed(); }
        }
        x1=Math.max(x1,view.paintLeft);x2=Math.min(x2,view.paintRight);
        y1=Math.max(y1,view.paintTop);y2=Math.min(y2,view.paintBottom);
        int size = x2 - x1;
        if (row == null || row.length < size) row = new int[size];
        for (int sy = y1; sy < y2; sy++) {
            int sourceRow = (sy - top) * height / drawHeight * width;
            for (int sx = x1; sx < x2; sx++) {
                row[sx - x1] = palette[pixels[sourceRow + (sx - left) * width / drawWidth] & 255];
            }
            int column = 0;
            while (column < size) {
                while (column < size && row[column] == 0) column++;
                int run = column;
                while (column < size && row[column] != 0) column++;
                if (column > run) {
                    int length = column - run;
                    // Some phone drivers validate offset + scanlength * height.
                    // Compact this run so both the offset and unused stride vanish.
                    // The destination ends before column: later runs stay intact.
                    if (run != 0) System.arraycopy(row, run, row, 0, length);
                    graphics.drawRGB(row, 0, length, x1 + run, sy, length, 1, false);
                }
            }
        }
    }
    private void paintRaster(Graphics graphics,Image image,int x,int y,int sx,int sy,int width,int height) {
        if(sx==0 && sy==0 && width==image.getWidth() && height==image.getHeight()) graphics.drawImage(image,x,y,20);
        else {
            if(regionSupported) {
                try { graphics.drawRegion(image,sx,sy,width,height,0,x,y,20);return; }
                catch(RuntimeException unsupported) { regionSupported=false; }
            }
            int cx=graphics.getClipX(),cy=graphics.getClipY(),cw=graphics.getClipWidth(),ch=graphics.getClipHeight();
            try { graphics.clipRect(x,y,width,height);graphics.drawImage(image,x-sx,y-sy,20); }
            finally { graphics.setClip(cx,cy,cw,ch); }
        }
    }
}
