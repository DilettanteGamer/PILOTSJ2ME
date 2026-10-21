/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  javax.microedition.lcdui.Graphics
 */
package pilots;

import java.io.IOException;
import javax.microedition.lcdui.Graphics;
import pilots.AnimationClip;
import pilots.Viewport;

public final class AnimatedActor {
    public final AnimationClip clip;
    public final int id;
    public int x;
    public int y;
    private long elapsed;
    public boolean playing = true;
    public boolean visible = true;

    public AnimatedActor(int n, String string, int n2, int n3) throws IOException {
        this.id = n;
        this.x = n2;
        this.y = n3;
        this.clip = new AnimationClip(string);
    }

    public boolean update(long l) throws IOException {
        if (!playing || l <= 0L || this.clip.index == this.clip.count - 1) {
            return false;
        }
        this.elapsed += Math.min(l, (long)this.clip.duration * 4L);
        boolean bl = false;
        while (this.elapsed >= (long)this.clip.duration) {
            this.elapsed -= (long)this.clip.duration;
            bl = this.clip.advance() || bl;
        }
        return bl;
    }

    public void restart() throws IOException {
        this.elapsed = 0L;
        this.clip.restart();
    }

    public void paint(Graphics graphics, Viewport viewport) {
        if (visible) this.clip.paint(graphics, viewport, this.x, this.y);
    }

    public void close() {
        this.clip.close();
    }
}
