package pilots;
/** Gameplay exits are committed once by the canvas, after the source action completes. */
public final class SceneFlow {
    public static int next(int scene,boolean completed) {
        if(completed && scene==6) return 8;
        if(completed && scene==12) return 13;
        if(completed && scene==11) return 12;
        if(completed && scene==10) return 11;
        if(completed && scene==9) return 10;
        if(completed && scene==8) return 9;
        return completed && (scene==2 || scene==3 || scene==4 || scene==5)?scene+1:scene;
    }
}
