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

public final class IndexedBackground {
    public final int width;
    public final int height;
    public final int worldWidth;
    public final int worldHeight;
    private final int[] palette = new int[256];
    private final byte[] pixels;
    private int[] row;
    private final RasterCache raster=new RasterCache();
    private int cachedWidth,cachedHeight,cachedLeft,cachedTop,cachedDrawWidth,cachedDrawHeight,cachedPanel;

    public IndexedBackground(InputStream inputStream) throws IOException {
        if (inputStream == null) {
            throw new IOException("Missing scene resource");
        }
        DataInputStream dataInputStream = new DataInputStream(inputStream);
        try {
            if (dataInputStream.readInt() != 1345864263) {
                throw new IOException("Invalid P8BG magic");
            }
            this.width = dataInputStream.readUnsignedShort();
            this.height = dataInputStream.readUnsignedShort();
            this.worldWidth = dataInputStream.readUnsignedShort();
            this.worldHeight = dataInputStream.readUnsignedShort();
            if (this.width < 1 || this.height < 1 || this.width > 640 || this.height > 480 || this.worldWidth < 1 || this.worldWidth > 2048 || this.worldHeight < 1 || this.worldHeight > 2048) {
                throw new IOException("Invalid image dimensions");
            }
            int n = 0;
            byte[] colors=new byte[768];dataInputStream.readFully(colors);
            while (n < 256) {
                int p=n*3;
                this.palette[n] = 0xFF000000 | (colors[p]&255) << 16 | (colors[p+1]&255) << 8 | (colors[p+2]&255);
                ++n;
            }
            this.pixels = new byte[this.width * this.height];
            dataInputStream.readFully(this.pixels);
            if (dataInputStream.read() != -1) {
                throw new IOException("Trailing image bytes");
            }
        }
        finally {
            dataInputStream.close();
        }
    }

    public void paint(Graphics graphics, Viewport viewport) {
        Image image=raster.get();
        if(image!=null && cachedPanel==0 && cachedWidth==viewport.width && cachedHeight==viewport.height &&
           cachedLeft==viewport.left && cachedTop==viewport.top && cachedDrawWidth==viewport.drawWidth && cachedDrawHeight==viewport.drawHeight) {
            graphics.drawImage(image,0,0,20);return;
        }
        raster.clear();
        int area=viewport.width*viewport.height;
        if(RasterCache.allows(area)) {
            try {
                raster.prepare(area);
                image=Image.createImage(viewport.width,viewport.height);
                Graphics target=image.getGraphics();target.setColor(0);target.fillRect(0,0,viewport.width,viewport.height);
                paintRows(target,viewport);raster.store(image);
                cachedPanel=0;cachedWidth=viewport.width;cachedHeight=viewport.height;
                cachedLeft=viewport.left;cachedTop=viewport.top;cachedDrawWidth=viewport.drawWidth;cachedDrawHeight=viewport.drawHeight;
                graphics.drawImage(image,0,0,20);return;
            } catch(OutOfMemoryError exhausted) { RasterCache.failed(); }
              catch(IllegalArgumentException unsupported) { RasterCache.failed(); }
        }
        paintRows(graphics,viewport);
    }

    public void clearCache() { raster.clear(); }

    private void paintRows(Graphics graphics, Viewport viewport) {
        int n = Math.max(0, viewport.left);
        int n2 = Math.min(viewport.width, viewport.left + viewport.drawWidth);
        int n3 = Math.max(0, viewport.top);
        int n4 = Math.min(viewport.height, viewport.top + viewport.drawHeight);
        int n5 = n2 - n;
        if (n5 <= 0) {
            return;
        }
        if (this.row == null || this.row.length != n5) {
            this.row = new int[n5];
        }
        int n6 = n3;
        while (n6 < n4) {
            int n7 = (n6 - viewport.top) * this.height / viewport.drawHeight * this.width;
            int n8 = n;
            while (n8 < n2) {
                this.row[n8 - n] = this.palette[this.pixels[n7 + (n8 - viewport.left) * this.width / viewport.drawWidth] & 0xFF];
                ++n8;
            }
            graphics.drawRGB(this.row, 0, n5, n, n6, n5, 1, false);
            ++n6;
        }
    }

    public void paintPanel(Graphics graphics, int n, int n2, int n3) {
        if(n<1 || n3<1) return;
        Image image=raster.get();
        if(image!=null && cachedPanel==1 && cachedWidth==n && cachedHeight==n3) {
            graphics.drawImage(image,0,n2-n3,20);return;
        }
        raster.clear();
        if(RasterCache.allows(n*n3)) {
            try {
                raster.prepare(n*n3);image=Image.createImage(n,n3);
                paintPanelRows(image.getGraphics(),n,n3,n3);raster.store(image);
                cachedPanel=1;cachedWidth=n;cachedHeight=n3;
                graphics.drawImage(image,0,n2-n3,20);return;
            } catch(OutOfMemoryError exhausted) { RasterCache.failed(); }
              catch(IllegalArgumentException unsupported) { RasterCache.failed(); }
        }
        paintPanelRows(graphics,n,n2,n3);
    }

    private void paintPanelRows(Graphics graphics, int n, int n2, int n3) {
        if (this.row == null || this.row.length != n) {
            this.row = new int[n];
        }
        int n4 = 0;
        while (n4 < n3) {
            int n5 = n4 * this.height / n3 * this.width;
            int n6 = 0;
            while (n6 < n) {
                this.row[n6] = this.palette[this.pixels[n5 + n6 * this.width / n] & 0xFF];
                ++n6;
            }
            graphics.drawRGB(this.row, 0, n, 0, n2 - n3 + n4, n, 1, false);
            ++n4;
        }
    }
}
