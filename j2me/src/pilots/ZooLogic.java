package pilots;

/** First playable interaction. Variable slots 60/61 are port-owned, not EXE globals. */
public final class ZooLogic {
    public static final int CHIEF = 0, COLLEAGUE = 1;
    private final GameState state;
    private int pending;

    public ZooLogic(GameState state) {
        this.state = state;
        if (state.variables[60] != COLLEAGUE) state.variables[60] = CHIEF;
        state.variables[61] = state.variables[61] == 1 ? 1 : 0;
    }

    public int hero() { return state.variables[60]; }
    public boolean windowOpen() { return state.variables[61] == 1; }
    public boolean busy() { return pending != 0; }
    public boolean select(int hero) {
        if (busy() || (hero != CHIEF && hero != COLLEAGUE)) return false;
        state.variables[60] = hero;
        return true;
    }
    public static boolean atWindow(int x, int y) {
        return x >= 150 && x < 218 && y >= 224 && y < 290;
    }
    public int beginWindow() {
        if (busy()) return 0;
        pending = hero() == CHIEF ? 86 : (windowOpen() ? 87 : 64);
        return pending;
    }
    public void complete() {
        if (pending == 64) state.variables[61] = 1;
        pending = 0;
    }
    public void cancel() { pending = 0; }
}
