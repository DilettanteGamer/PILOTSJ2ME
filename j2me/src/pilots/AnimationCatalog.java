/*
 * Decompiled with CFR 0.152.
 */
package pilots;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

public final class AnimationCatalog {
    public final int scene = 14;
    public final int[] ids;
    public final int[] x;
    public final int[] y;
    public final int[] focusX;
    public final int[] focusY;
    public final String[] labels;

    public AnimationCatalog() throws IOException {
        InputStream inputStream = this.getClass().getResourceAsStream("/scenes/14.animations");
        if (inputStream == null) {
            throw new IOException("Missing animation catalog");
        }
        DataInputStream dataInputStream = new DataInputStream(inputStream);
        try {
            if (dataInputStream.readInt() != 1095648050) {
                throw new IOException("Invalid animation catalog");
            }
            int n = dataInputStream.readUnsignedShort();
            if (n < 1 || n > 254) {
                throw new IOException("Invalid animation count");
            }
            this.ids = new int[n];
            this.x = new int[n];
            this.y = new int[n];
            this.focusX = new int[n];
            this.focusY = new int[n];
            this.labels = new String[n + 1];
            this.labels[0] = "\u0412\u0435\u0440\u043d\u0443\u0442\u044c\u0441\u044f \u0432 \u0438\u0433\u0440\u0443";
            int n2 = 0;
            while (n2 < n) {
                this.ids[n2] = dataInputStream.readUnsignedShort();
                this.x[n2] = dataInputStream.readShort();
                this.y[n2] = dataInputStream.readShort();
                this.focusX[n2] = dataInputStream.readUnsignedShort();
                this.focusY[n2] = dataInputStream.readUnsignedShort();
                this.labels[n2 + 1] = dataInputStream.readUTF();
                if (n2 > 0 && this.ids[n2] <= this.ids[n2 - 1] || this.x[n2] < 0 || this.x[n2] >= 640 || this.y[n2] < 0 || this.y[n2] >= 417 || this.focusX[n2] >= 640 || this.focusY[n2] >= 417 || this.labels[n2 + 1].length() > 80) {
                    throw new IOException("Invalid animation entry");
                }
                ++n2;
            }
            if (dataInputStream.read() != -1) {
                throw new IOException("Trailing animation catalog");
            }
        }
        finally {
            dataInputStream.close();
        }
    }
}
