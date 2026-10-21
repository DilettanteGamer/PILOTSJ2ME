package pilots;

import java.io.IOException;

/** Fixed-step adapter for original zoo sequences; drawing never commits game state. */
public final class ZooScene {
    public final ZooLogic logic;
    private final Scene scene;
    private final GameState state;
    private long elapsed;

    public ZooScene(Scene scene, GameState state) throws IOException {
        this.scene = scene;
        this.state = state;
        logic = new ZooLogic(state);
        rebuild();
    }

    private AnimatedActor actor(int id, int x, int y, boolean playing, int frame) throws IOException {
        AnimatedActor result = new AnimatedActor(id, "/scenes/02-" + id + ".anm", x, y);
        try {
            if (frame < 0) throw new IOException("Invalid saved animation frame");
            int target = Math.min(frame, result.clip.count - 1);
            while (result.clip.index < target) result.clip.advance();
            result.playing = playing;
            return result;
        } catch (IOException failure) { result.close(); throw failure; }
    }

    private AnimatedActor hero(int hero) throws IOException {
        boolean active = logic.busy() && logic.hero() == hero;
        int id = active ? logic.pendingClip() : hero == ZooLogic.CHIEF
            ? ZooLogic.GLOBAL_CHIEF_IDLE : ZooLogic.GLOBAL_COLLEAGUE_IDLE;
        return actor(id, logic.heroX(hero), logic.heroY(hero), active, active ? logic.step() - 1 : 0);
    }

    private AnimatedActor crow() throws IOException {
        AnimatedActor result = actor(logic.crowState() == 1 ? 66 : 65, 320, 112,
            logic.crowState() == 1, logic.crowFrame());
        result.visible = logic.crowState() != 0;
        return result;
    }

    private AnimatedActor kettle() throws IOException {
        AnimatedActor result = actor(70, 64, 288, false, 0);
        result.visible = !logic.hasKettle();
        return result;
    }
    private AnimatedActor cat() throws IOException {
        AnimatedActor result = actor(logic.catClip(), 192, 176, logic.catPresent(), logic.catFrame());
        result.visible = logic.catPresent();
        return result;
    }
    private AnimatedActor butterfly() throws IOException {
        AnimatedActor result = actor(logic.butterflyState() == 1 ? 90 : 91, 256, 272,
            logic.butterflyState() == 1, logic.butterflyFrame());
        result.visible = logic.butterflyState() != 0;
        return result;
    }
    private void replace(int slot, AnimatedActor next) {
        scene.actors[slot].close(); scene.actors[slot] = next;
    }
    private void rebuild() throws IOException {
        AnimatedActor[] next = new AnimatedActor[9];
        try {
            next[0] = actor(logic.windowOpen() ? 63 : 62, 128, 224, false, 0);
            next[0].visible = logic.shuttersVisible();
            next[1] = actor(59, 256, 128, false, 0);
            next[2] = hero(ZooLogic.CHIEF);
            next[3] = hero(ZooLogic.COLLEAGUE);
            next[4] = crow();
            next[5] = kettle();
            next[6] = actor(55, 128, 320, false, 0);
            next[7] = cat();
            next[8] = butterfly();
        } catch (IOException failure) {
            for (int i = 0; i < next.length; i++) if (next[i] != null) next[i].close();
            throw failure;
        }
        scene.close();
        scene.actors = next;
    }

    public boolean activate(int x, int y) throws IOException {
        int target = logic.targetAt(x, y);
        if (target == 0 || logic.busy()) return false;
        int[] previous = new int[state.variables.length];
        System.arraycopy(state.variables, 0, previous, 0, previous.length);
        try {
            int clip = logic.begin(target);
            if (clip == 0) return false;
            // Reactions with no approach retain the hero's actual current position.
            if (clip < 20000) {
                int px = target == ZooLogic.KETTLE ? 64 : 192;
                int py = target == ZooLogic.WINDOW ? 304 : 320;
                int other = 1 - logic.hero();
                if (logic.heroX(other) == px && logic.heroY(other) == py)
                    logic.placeHero(other, other == 0 ? 448 : 512, 336);
                logic.placeHero(logic.hero(), px, py);
            }
            rebuild();
        } catch (IOException failure) {
            System.arraycopy(previous, 0, state.variables, 0, previous.length);
            throw failure;
        }
        elapsed = 0;
        return true;
    }

    public boolean update(long delta) throws IOException {
        if (delta <= 0) return false;
        elapsed += Math.min(delta, 400L);
        boolean changed = false;
        while (elapsed >= 100L) {
            elapsed -= 100L;
            for (int i = 0; i < scene.actors.length; i++) changed = scene.actors[i].update(100L) || changed;
            if (logic.crowState() == 1) {
                logic.crowFrame(scene.actors[4].clip.index);
                if (scene.actors[4].clip.index == scene.actors[4].clip.count - 1) logic.perchCrow();
            }
            if (logic.catPresent()) logic.catFrame(scene.actors[7].clip.index);
            if (logic.butterflyState() == 1) {
                logic.butterflyFrame(scene.actors[8].clip.index);
                if (scene.actors[8].clip.index == scene.actors[8].clip.count - 1) logic.settleButterfly();
            }
            int oldClip = logic.pendingClip();
            boolean catActive = logic.catPresent();
            logic.advanceCat();
            logic.advance();
            if (oldClip != 0 || catActive) changed = true;
            if (oldClip != logic.pendingClip()) rebuild();
            else {
                if (scene.actors[0].id != (logic.windowOpen() ? 63 : 62))
                    replace(0, actor(logic.windowOpen() ? 63 : 62, 128, 224, false, 0));
                scene.actors[0].visible = logic.shuttersVisible();
                if (scene.actors[4].id != (logic.crowState() == 1 ? 66 : 65)
                    || scene.actors[4].visible != (logic.crowState() != 0)) replace(4, crow());
                scene.actors[5].visible = !logic.hasKettle();
                if (scene.actors[7].id != logic.catClip() || scene.actors[7].visible != logic.catPresent()) replace(7, cat());
                if (scene.actors[8].id != (logic.butterflyState() == 1 ? 90 : 91)
                    || scene.actors[8].visible != (logic.butterflyState() != 0)) replace(8, butterfly());
            }
        }
        return changed;
    }
}
