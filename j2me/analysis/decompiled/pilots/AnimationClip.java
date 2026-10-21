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

    /*
     * Unable to fully structure code
     */
    public void paint(Graphics var1_1, Viewport var2_2, int var3_3, int var4_4) {
        if (this.pixels == null) {
            return;
        }
        var5_5 = var2_2.screenX(var3_3 + this.offsetX);
        var6_6 = var2_2.screenY(var4_4 + this.offsetY);
        var7_7 = var2_2.screenX(var3_3 + this.offsetX + this.width);
        var8_8 = var2_2.screenY(var4_4 + this.offsetY + this.height);
        var9_9 = Math.max(1, var7_7 - var5_5);
        var10_10 = Math.max(1, var8_8 - var6_6);
        var11_11 = Math.max(0, var5_5);
        var12_12 = Math.min(var2_2.width, var5_5 + var9_9);
        var13_13 = Math.max(0, var6_6);
        var14_14 = Math.min(var2_2.height, var6_6 + var10_10);
        if (var12_12 <= var11_11 || var14_14 <= var13_13) {
            return;
        }
        var15_15 = var12_12 - var11_11;
        if (this.row == null || this.row.length < var15_15) {
            this.row = new int[var15_15];
        }
        var16_16 = var13_13;
        while (var16_16 < var14_14) {
            var17_17 = (var16_16 - var6_6) * this.height / var10_10 * this.width;
            var18_18 = var11_11;
            while (var18_18 < var12_12) {
                this.row[var18_18 - var11_11] = this.palette[this.pixels[var17_17 + (var18_18 - var5_5) * this.width / var9_9] & 255];
                ++var18_18;
            }
            var18_18 = 0;
            ** GOTO lbl37
            {
                ++var18_18;
                do {
                    if (var18_18 < var15_15 && this.row[var18_18] == 0) continue block2;
                    var19_19 = var18_18;
                    while (var18_18 < var15_15 && this.row[var18_18] != 0) {
                        ++var18_18;
                    }
                    if (var18_18 <= var19_19) continue;
                    var1_1.drawRGB(this.row, var19_19, var15_15, var11_11 + var19_19, var16_16, var18_18 - var19_19, 1, false);
lbl37:
                    // 3 sources

                } while (var18_18 < var15_15);
            }
            ++var16_16;
        }
    }
}

