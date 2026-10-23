package pilots;
/** Gameplay exits are committed once by the canvas, after the source action completes. */
public final class SceneFlow {
    public static int next(int scene,boolean completed) {
        return completed && (scene==2 || scene==3)?scene+1:scene;
    }
}
