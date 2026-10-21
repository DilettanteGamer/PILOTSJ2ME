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

    public AnimationClip(String string) throws IOException {
        this.resource = string;
        this.restart();
    }

    public void restart() throws IOException {
        this.close();
        InputStream inputStream = this.getClass().getResourceAsStream(this.resource);
        if (inputStream == null) {
            throw new IOException("Missing animation " + this.resource);
        }
        this.input = new DataInputStream(inputStream);
        try {
            if (this.input.readInt() != 1095650609) {
                throw new IOException("Invalid animation magic");
            }
            this.count = this.input.readUnsignedShort();
            this.duration = this.input.readUnsignedShort();
            this.transparent = this.input.readUnsignedByte();
            if (this.count < 1 || this.count > 1024 || this.duration < 20 || this.duration > 1000) {
                throw new IOException("Invalid animation header");
            }
            int n = 0;
            while (n < 256) {
                this.palette[n] = 0xFF000000 | this.input.readUnsignedByte() << 16 | this.input.readUnsignedByte() << 8 | this.input.readUnsignedByte();
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
            this.input.readFully(this.pixels, 0, n * n2);
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
        if (this.input != null) {
            try {
                this.input.close();
            }
            catch (IOException iOException) {}
            this.input = null;
        }
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
                if (column > run) graphics.drawRGB(row, run, size, x1 + run, sy, column - run, 1, false);
            }
        }
    }
}
