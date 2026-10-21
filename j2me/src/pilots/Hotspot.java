/*
 * Decompiled with CFR 0.152.
 */
package pilots;

public final class Hotspot {
    public final int x;
    public final int y;
    public final int width;
    public final int height;
    public final byte[] action;

    public Hotspot(int n, int n2, int n3, int n4, byte[] byArray) {
        if (n < 0 || n2 < 0 || n3 <= 0 || n4 <= 0 || byArray == null) {
            throw new IllegalArgumentException();
        }
        this.x = n;
        this.y = n2;
        this.width = n3;
        this.height = n4;
        this.action = byArray;
    }

    public boolean contains(int n, int n2) {
        return n >= this.x && n2 >= this.y && n - this.x < this.width && n2 - this.y < this.height;
    }
}

