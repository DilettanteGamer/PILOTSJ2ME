package pilots;

/** Headless resource wiring checks. Completion is injected; frames are not replayed. */
public final class ZooSceneTest {
    private static void check(boolean ok, String message) {
        if (!ok) throw new RuntimeException(message);
    }
    public static void main(String[] args) throws Exception {
        GameState state = new GameState(); state.scene = 2;
        Scene scene = new Scene(2);
        try {
            ZooScene zoo = new ZooScene(scene, state);
            check(scene.actors[0].id == 62, "closed shutters on entry");
            check(!scene.actors[2].playing && !scene.actors[3].playing, "entry does not trigger actions");
            check(!zoo.activate(0,0) && !zoo.logic.busy(), "background click has no effect");
            zoo.logic.select(1);
            check(zoo.activate(180,260), "window hit");
            check(scene.actors[3].id == 64 && scene.actors[3].x == 192 && scene.actors[3].y == 304,
                  "opening uses confirmed character anchor");
            check(!scene.actors[0].visible, "opening owns shutter rendering");
            check(!zoo.finishIfReady() && !zoo.logic.windowOpen(), "cannot finish at start");
            check(!zoo.activate(180,260), "cannot restart busy action");
            scene.actors[3].clip.index = scene.actors[3].clip.count - 1;
            check(zoo.finishIfReady() && zoo.logic.windowOpen(), "completion commits state");
            check(scene.actors[0].id == 63 && scene.actors[0].visible, "open shutters persist");
            zoo.activate(180,260);
            check(scene.actors[3].id == 87, "next interaction looks");
            scene.actors[3].clip.index = scene.actors[3].clip.count - 1;
            zoo.finishIfReady();
            zoo.logic.select(0); zoo.activate(180,260);
            check(scene.actors[2].id == 86 && scene.actors[3].x == 512, "Chief knocks; other hero moves off action mark");
            scene.actors[2].clip.index = scene.actors[2].clip.count - 1;
            zoo.finishIfReady();
            GameState loaded = GameState.decode(state.encode(),20);
            ZooScene restored = new ZooScene(scene, loaded);
            check(restored.logic.windowOpen() && scene.actors[0].id == 63, "restored scene renders saved state");
            check(scene.actors[0].x == 128 && scene.actors[0].y == 224, "original shutter root");
        } finally { scene.close(); }
        System.out.println("ZooScene: resource wiring, input lock, completion and restored shutters passed");
    }
}
