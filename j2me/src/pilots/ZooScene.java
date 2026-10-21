package pilots;

import java.io.IOException;

/** Resource adapter for the first window interaction; all positions use world coordinates. */
public final class ZooScene {
    public final ZooLogic logic;
    private final Scene scene;
    private AnimatedActor action;
    private int chiefX = 448, chiefY = 336, colleagueX = 512, colleagueY = 336;

    public ZooScene(Scene scene, GameState state) throws IOException {
        this.scene = scene;
        logic = new ZooLogic(state);
        rebuild();
    }

    private AnimatedActor actor(int id, int x, int y, boolean playing) throws IOException {
        AnimatedActor result = new AnimatedActor(id, "/scenes/02-" + id + ".anm", x, y);
        result.playing = playing;
        return result;
    }

    private void rebuild() throws IOException {
        AnimatedActor[] next = new AnimatedActor[4];
        try {
            // Closed/open shutters persist independently of the character effect.
            next[0] = actor(logic.windowOpen() ? 63 : 62, 128, 224, false);
            next[1] = actor(59, 256, 128, false);
            next[2] = actor(75, chiefX, chiefY, false);
            next[3] = actor(76, colleagueX, colleagueY, false);
        } catch (IOException failure) {
            for (int i = 0; i < next.length; i++) if (next[i] != null) next[i].close();
            throw failure;
        }
        scene.close();
        scene.actors = next;
        action = null;
    }

    public boolean activate(int x, int y) throws IOException {
        if (!ZooLogic.atWindow(x, y) || logic.busy()) return false;
        int id = logic.beginWindow();
        int slot = logic.hero() == ZooLogic.CHIEF ? 2 : 3;
        int other = slot == 2 ? 3 : 2;
        AnimatedActor next = null, idle;
        try {
            next = actor(id, 192, 304, true);
            idle = actor(other == 2 ? 75 : 76, other == 2 ? 448 : 512, 336, false);
        } catch (IOException failure) {
            if (next != null) next.close();
            logic.cancel();
            throw failure;
        }
        // Until walking is ported, keep the other hero at his starting mark.
        if (slot == 2) { colleagueX = 512; colleagueY = 336; }
        else { chiefX = 448; chiefY = 336; }
        scene.actors[other].close();
        scene.actors[other] = idle;
        scene.actors[slot].close();
        scene.actors[slot] = next;
        action = next;
        // Opening frames contain the shutters themselves; avoid a closed pane behind them.
        scene.actors[0].visible = id != 64;
        if (slot == 2) { chiefX = 192; chiefY = 304; }
        else { colleagueX = 192; colleagueY = 304; }
        return true;
    }

    public boolean finishIfReady() throws IOException {
        if (action == null || action.clip.index != action.clip.count - 1) return false;
        logic.complete();
        rebuild();
        return true;
    }
}
