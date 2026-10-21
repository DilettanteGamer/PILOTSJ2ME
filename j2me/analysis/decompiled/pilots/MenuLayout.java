/*
 * Decompiled with CFR 0.152.
 */
package pilots;

import pilots.Viewport;

public final class MenuLayout {
    public int width;
    public int height;
    public int header;
    public int footer;
    public int rowHeight;
    public int visible;
    public int first;
    public int selected;
    public final int count;

    public MenuLayout(int n) {
        if (n < 1) {
            throw new IllegalArgumentException();
        }
        this.count = n;
    }

    public void resize(int n, int n2, int n3) {
        this.width = Math.max(1, n);
        this.height = Math.max(1, n2);
        this.header = Math.min(n2 / 3, n3 + 6);
        this.footer = Math.min(n2 / 3, n3 + 2);
        int n4 = Math.max(1, n2 - this.header - this.footer);
        this.rowHeight = Math.min(n4, Math.max(n3 + 2, n2 / 10));
        this.visible = Math.min(this.count, Math.max(1, n4 / this.rowHeight));
        this.reveal();
    }

    public void move(int n) {
        this.selected = (this.selected + n % this.count + this.count) % this.count;
        this.reveal();
    }

    public void select(int n) {
        this.selected = Viewport.clamp(n, 0, this.count - 1);
        this.reveal();
    }

    private void reveal() {
        this.first = Viewport.clamp(this.first, 0, Math.max(0, this.count - this.visible));
        if (this.selected < this.first) {
            this.first = this.selected;
        }
        if (this.selected >= this.first + this.visible) {
            this.first = this.selected - this.visible + 1;
        }
    }

    public int itemAt(int n) {
        if (n < this.header || n >= this.header + this.visible * this.rowHeight) {
            return -1;
        }
        return this.first + (n - this.header) / this.rowHeight;
    }
}

