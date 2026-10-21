/*
 * Decompiled with CFR 0.152.
 */
package pilots;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import pilots.AnimatedActor;
import pilots.Hotspot;
import pilots.IndexedBackground;

public final class Scene {
    public final IndexedBackground background;
    public final IndexedBackground panel;
    public final Hotspot[] hotspots;
    public AnimatedActor[] actors = new AnimatedActor[0];

    public Scene(int n) throws IOException {
        String string = "/scenes/" + (n < 10 ? "0" : "") + n;
        try {
            int n2;
            int n3;
            int n4;
            int n5;
            InputStream inputStream;
            this.background = new IndexedBackground(this.getClass().getResourceAsStream(String.valueOf(string) + ".p8"));
            InputStream inputStream2 = this.getClass().getResourceAsStream(String.valueOf(string) + ".bar");
            this.panel = inputStream2 == null ? null : new IndexedBackground(inputStream2);
            InputStream inputStream3 = this.getClass().getResourceAsStream(String.valueOf(string) + ".act");
            if (inputStream3 != null) {
                inputStream = new DataInputStream(inputStream3);
                try {
                    try {
                        if (((DataInputStream)inputStream).readInt() != 1094931505) {
                            throw new IOException("Invalid actor catalog");
                        }
                        int n6 = ((DataInputStream)inputStream).readUnsignedByte();
                        if (n6 > 8) {
                            throw new IOException("Too many actors");
                        }
                        this.actors = new AnimatedActor[n6];
                        n5 = 0;
                        while (n5 < n6) {
                            n4 = ((DataInputStream)inputStream).readUnsignedShort();
                            n3 = ((DataInputStream)inputStream).readShort();
                            n2 = ((DataInputStream)inputStream).readShort();
                            if (n3 < 0 || n3 >= this.background.worldWidth || n2 < 0 || n2 >= this.background.worldHeight) {
                                throw new IOException("Invalid actor anchor");
                            }
                            this.actors[n5] = new AnimatedActor(n4, String.valueOf(string) + "-" + n4 + ".anm", n3, n2);
                            ++n5;
                        }
                        if (inputStream.read() != -1) {
                            throw new IOException("Trailing actor catalog");
                        }
                    }
                    catch (IOException iOException) {
                        this.close();
                        throw iOException;
                    }
                }
                finally {
                    inputStream.close();
                }
            }
            if ((inputStream = this.getClass().getResourceAsStream(String.valueOf(string) + ".hot")) == null) {
                this.hotspots = new Hotspot[0];
                return;
            }
            DataInputStream dataInputStream = new DataInputStream(inputStream);
            try {
                if (dataInputStream.readInt() != 1213158449) {
                    throw new IOException("Invalid hotspot pack");
                }
                n5 = dataInputStream.readUnsignedByte();
                if (n5 > 64) {
                    throw new IOException("Too many hotspots");
                }
                this.hotspots = new Hotspot[n5];
                n4 = 0;
                while (n4 < n5) {
                    n3 = dataInputStream.readUnsignedShort();
                    n2 = dataInputStream.readUnsignedShort();
                    int n7 = dataInputStream.readUnsignedShort();
                    int n8 = dataInputStream.readUnsignedShort();
                    int n9 = dataInputStream.readUnsignedShort();
                    if (n7 < 1 || n8 < 1 || n3 + n7 > this.background.worldWidth || n2 + n8 > this.background.worldHeight || n9 < 1 || n9 > 1024) {
                        throw new IOException("Invalid hotspot");
                    }
                    byte[] byArray = new byte[n9];
                    dataInputStream.readFully(byArray);
                    this.hotspots[n4] = new Hotspot(n3, n2, n7, n8, byArray);
                    ++n4;
                }
                if (dataInputStream.read() != -1) {
                    throw new IOException("Trailing hotspot data");
                }
            }
            finally {
                dataInputStream.close();
            }
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

    public void close() {
        int n = 0;
        while (n < this.actors.length) {
            if (this.actors[n] != null) {
                this.actors[n].close();
            }
            ++n;
        }
    }

    public Hotspot at(int n, int n2) {
        int n3 = this.hotspots.length - 1;
        while (n3 >= 0) {
            if (this.hotspots[n3].contains(n, n2)) {
                return this.hotspots[n3];
            }
            --n3;
        }
        return null;
    }
}
