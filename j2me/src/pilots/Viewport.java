/*
 * Decompiled with CFR 0.152.
 */
package pilots;

public final class Viewport {
    public int width;
    public int height;
    public int worldWidth;
    public int worldHeight;
    public int cursorX;
    public int cursorY;
    public int cameraX;
    public int cameraY;
    public int drawWidth;
    public int drawHeight;
    public int left;
    public int top;
    public boolean zoom;
    public int paintLeft,paintTop,paintRight,paintBottom;
    public void paintClip(int x,int y,int width,int height) {
        paintLeft=Math.max(0,x);paintTop=Math.max(0,y);
        paintRight=Math.min(this.width,x+width);paintBottom=Math.min(this.height,y+height);
    }

    public Viewport(int n, int n2, int n3, int n4) {
        this.resize(n, n2, n3, n4);
        this.cursorX = n3 / 2;
        this.cursorY = n4 / 2;
        this.update();
    }

    public void resize(int n, int n2, int n3, int n4) {
        if (n <= 0 || n2 <= 0 || n3 <= 0 || n4 <= 0) {
            throw new IllegalArgumentException();
        }
        this.width = n;
        this.height = n2;
        this.worldWidth = n3;
        this.worldHeight = n4;
        paintClip(0,0,n,n2);
        this.update();
    }

    public void update() {
        this.cursorX = Viewport.clamp(this.cursorX, 0, this.worldWidth - 1);
        this.cursorY = Viewport.clamp(this.cursorY, 0, this.worldHeight - 1);
        this.drawHeight = this.height;
        this.drawWidth = (this.height * this.worldWidth + this.worldHeight - 1) / this.worldHeight;
        if (this.zoom) {
            this.drawWidth *= 2;
            this.drawHeight *= 2;
        }
        this.drawWidth = Math.max(1, this.drawWidth);
        this.drawHeight = Math.max(1, this.drawHeight);
        this.cameraX = Viewport.clamp(this.cursorX * this.drawWidth / this.worldWidth - this.width / 2, 0, Math.max(0, this.drawWidth - this.width));
        this.cameraY = Viewport.clamp(this.cursorY * this.drawHeight / this.worldHeight - this.height / 2, 0, Math.max(0, this.drawHeight - this.height));
        this.left = Math.max(0, (this.width - this.drawWidth) / 2) - this.cameraX;
        this.top = Math.max(0, (this.height - this.drawHeight) / 2) - this.cameraY;
    }

    public void move(int n, int n2) {
        this.cursorX += n;
        this.cursorY += n2;
        this.update();
    }

    public int screenX(int n) {
        return this.left + n * this.drawWidth / this.worldWidth;
    }

    public int screenY(int n) {
        return this.top + n * this.drawHeight / this.worldHeight;
    }

    public int worldX(int n) {
        return Viewport.clamp((n - this.left) * this.worldWidth / this.drawWidth, 0, this.worldWidth - 1);
    }

    public int worldY(int n) {
        return Viewport.clamp((n - this.top) * this.worldHeight / this.drawHeight, 0, this.worldHeight - 1);
    }

    public static int clamp(int n, int n2, int n3) {
        return Math.max(n2, Math.min(n, n3));
    }
}
