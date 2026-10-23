package pilots;

/** Integration regression tests for the window object's lifetime and script sequence. */
public final class ZooSceneTest {
    private static void check(boolean ok, String message) {
        if (!ok) throw new RuntimeException(message);
    }
    public static void main(String[] args) throws Exception {
        GameState state = new GameState(); state.scene = 2;
        Scene scene = new Scene(2);
        try {
            ZooScene zoo = new ZooScene(scene, state);
            check(scene.actors[0].id == 62 && scene.actors[0].visible, "closed shutters initially");
            check(scene.actors[2].id == 20012 && scene.actors[3].id == 20016, "original idle resources");
            check(!scene.actors[4].visible, "crow not spawned early");
            check(!zoo.activate(0,0), "background click");
            zoo.logic.select(1); zoo.activate(180,260);
            check(!scene.actors[0].visible, "rule 5 removes closed object during opening frames");
            check(!zoo.activate(180,260), "busy lock");
            for (int step = 2; step <= 12; step++) zoo.update(100);
            check(!scene.actors[0].visible && !zoo.logic.windowOpen(), "before open event");
            zoo.update(100);
            check(scene.actors[0].id == 63 && scene.actors[0].visible, "step 13 restores open shutters");
            check(scene.actors[3].clip.index == 12, "shutters synchronize with source step 13");
            check(scene.actors[0].x == 128 && scene.actors[0].y == 224, "shutter root");
            GameState loaded = GameState.decode(state.encode(),20);
            zoo = new ZooScene(scene, loaded);
            check(zoo.logic.step() == 13 && scene.actors[3].clip.index == 12 && scene.actors[0].visible,
                  "resume middle of opening without disappearance");
            for (int step = 14; step <= 38; step++) zoo.update(100);
            check(scene.actors[4].id == 66 && scene.actors[4].visible, "source crow event");
            check(scene.actors[4].x == 320 && scene.actors[4].y == 112, "source crow mark");
            zoo.update(100); zoo.update(100);
            loaded = GameState.decode(loaded.encode(),20);
            int savedCrowFrame = scene.actors[4].clip.index;
            zoo = new ZooScene(scene, loaded);
            check(zoo.logic.step() == 40 && scene.actors[4].clip.index == savedCrowFrame,
                  "resume crow flight and character timeline together");
            check(scene.actors[0].visible, "restoring crow cannot hide shutters");
            for (int step = 41; step < 88; step++) zoo.update(100);
            check(zoo.logic.busy() && scene.actors[0].visible, "hold through source script tail");
            zoo.update(100);
            check(!zoo.logic.busy() && scene.actors[4].id == 66, "release while crow flight continues");
            for (int step = 89; step <= 93; step++) zoo.update(100);
            check(scene.actors[4].id == 65, "source crow completion switches to perched pose");
            zoo.logic.select(0); zoo.activate(180,260);
            check(scene.actors[2].id == 20036 && scene.actors[2].id != 86,
                  "Chief does not knock on open shutters");
            check(scene.actors[0].visible && scene.actors[0].id == 63, "refusal preserves shutters");
            for (int i = 1; i < 36; i++) zoo.update(100);
            zoo.logic.select(1); zoo.activate(180,260);
            check(scene.actors[3].id == 87, "Colleague looks through open window");
        } finally { scene.close(); }
        System.out.println("ZooScene: shutters lifetime, source events, resumption, crow and Chief response passed");
    }
}
