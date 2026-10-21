package pilots;

public final class ZooLogicTest {
    private static void check(boolean ok, String message) {
        if (!ok) throw new RuntimeException(message);
    }
    public static void main(String[] args) throws Exception {
        GameState state = new GameState();
        state.scene = 2;
        ZooLogic logic = new ZooLogic(state);
        check(logic.hero() == ZooLogic.CHIEF && !logic.windowOpen(), "initial state");
        check(logic.beginWindow() == 86, "Chief knocks");
        check(!logic.select(1) && logic.beginWindow() == 0, "busy action is locked");
        logic.complete();
        check(!logic.windowOpen(), "knocking cannot open the window");
        check(logic.select(1) && !logic.select(7), "hero selection bounds");
        check(logic.beginWindow() == 64 && !logic.windowOpen(), "opening is deferred");
        GameState during = GameState.decode(state.encode(), 20);
        check(!new ZooLogic(during).windowOpen(), "mid-action save stores committed state only");
        logic.cancel();
        check(!logic.windowOpen() && !logic.busy(), "cancellation cannot commit");
        logic.beginWindow(); logic.complete();
        check(logic.windowOpen(), "opening completes");
        byte[] save = state.encode();
        check(save.length == 299, "save compatibility");
        ZooLogic loaded = new ZooLogic(GameState.decode(save, 20));
        check(loaded.hero() == 1 && loaded.windowOpen(), "persistent hero and shutters");
        check(loaded.beginWindow() == 87, "repeat action looks through open window");
        loaded.complete(); loaded.select(0); loaded.beginWindow(); loaded.complete();
        check(loaded.windowOpen(), "Chief cannot reset open shutters");
        GameState legacy = new GameState();
        check(!new ZooLogic(GameState.decode(legacy.encode(),20)).windowOpen(), "legacy save defaults");
        check(ZooLogic.atWindow(150,224) && ZooLogic.atWindow(217,289), "window bounds");
        check(!ZooLogic.atWindow(149,224) && !ZooLogic.atWindow(218,289)
            && !ZooLogic.atWindow(150,223) && !ZooLogic.atWindow(150,290), "outside window");
        System.out.println("ZooLogic: selection, actions, cancellation, persistence and hotspot bounds passed");
    }
}
