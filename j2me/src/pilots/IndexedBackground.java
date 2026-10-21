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
import pilots.Viewport;

public final class IndexedBackground {
    public final int width;
    public final int height;
    public final int worldWidth;
    public final int worldHeight;
    private final int[] palette = new int[256];
    private final byte[] pixels;
    private int[] row;

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
            while (n < 256) {
                this.palette[n] = 0xFF000000 | dataInputStream.readUnsignedByte() << 16 | dataInputStream.readUnsignedByte() << 8 | dataInputStream.readUnsignedByte();
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
