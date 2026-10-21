package pilots;

import java.io.IOException;

/** Fixed-step adapter for the source window sequence; drawing never commits game state. */
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

    private void rebuild() throws IOException {
        AnimatedActor[] next = new AnimatedActor[5];
        try {
            next[0] = actor(logic.windowOpen() ? 63 : 62, 128, 224, false, 0);
            next[0].visible = logic.shuttersVisible();
            next[1] = actor(59, 256, 128, false, 0);
            next[2] = hero(ZooLogic.CHIEF);
            next[3] = hero(ZooLogic.COLLEAGUE);
            next[4] = crow();
        } catch (IOException failure) {
            for (int i = 0; i < next.length; i++) if (next[i] != null) next[i].close();
            throw failure;
        }
        scene.close();
        scene.actors = next;
    }

    public boolean activate(int x, int y) throws IOException {
        if (!ZooLogic.atWindow(x, y) || logic.busy()) return false;
        int[] previous = new int[state.variables.length];
        System.arraycopy(state.variables, 0, previous, 0, previous.length);
        try {
            int clip = logic.beginWindow();
            if (clip == 64 || clip == 86 || clip == 87) {
                int other = 1 - logic.hero();
                if (logic.heroX(other) == 192 && logic.heroY(other) == 304)
                    logic.placeHero(other, other == 0 ? 448 : 512, 336);
                logic.placeHero(logic.hero(), 192, 304);
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
                if (scene.actors[4].clip.index == scene.actors[4].clip.count - 1) {
                    logic.perchCrow();
                    AnimatedActor next = crow();
                    scene.actors[4].close(); scene.actors[4] = next;
                    changed = true;
                }
            }
            int oldClip = logic.pendingClip();
            boolean oldOpen = logic.windowOpen();
            int oldCrow = logic.crowState();
            logic.advance();
            if (oldClip != 0) changed = true;
            if (oldClip != logic.pendingClip()) rebuild();
            else {
                if (oldOpen != logic.windowOpen()) {
                    AnimatedActor next = actor(63, 128, 224, false, 0);
                    scene.actors[0].close(); scene.actors[0] = next;
                }
                scene.actors[0].visible = logic.shuttersVisible();
                if (oldCrow != logic.crowState()) {
                    AnimatedActor next = crow();
                    scene.actors[4].close(); scene.actors[4] = next;
                }
            }
        }
        return changed;
    }
}
