package pilots;

/** Window rules transcribed from EXE objects 17/18/23/24; see window-original-rules.json. */
public final class ZooLogic {
    public static final int CHIEF = 0, COLLEAGUE = 1;
    public static final int GLOBAL_CHIEF_IDLE = 20012, GLOBAL_COLLEAGUE_IDLE = 20016;
    public static final int GLOBAL_CHIEF_REFUSAL = 20036, GLOBAL_CHIEF_REPLY = 20034;
    private final GameState state;

    public ZooLogic(GameState state) {
        this.state = state;
        if (state.variables[60] != COLLEAGUE) state.variables[60] = CHIEF;
        state.variables[61] = state.variables[61] == 1 ? 1 : 0;
        if (state.variables[58] != 2) {
            // 0.4.0 only saved completed opening: its crow event has already happened.
            state.variables[62] = windowOpen() ? 2 : 0;
            state.variables[52] = state.variables[53] = state.variables[59] = 0;
            state.variables[54] = 448; state.variables[55] = 336;
            state.variables[56] = 512; state.variables[57] = 336;
            state.variables[58] = 2;
        }
    }

    public int hero() { return state.variables[60]; }
    public boolean windowOpen() { return state.variables[61] == 1; }
    public int pendingClip() { return state.variables[52]; }
    public int step() { return state.variables[53]; }
    public boolean busy() { return pendingClip() != 0; }
    public int crowState() { return state.variables[62]; }
    public int crowFrame() { return state.variables[59]; }
    public void crowFrame(int frame) { state.variables[59] = frame; }
    public void perchCrow() { state.variables[62] = 2; state.variables[59] = 0; }
    public int heroX(int hero) { return state.variables[54 + hero * 2]; }
    public int heroY(int hero) { return state.variables[55 + hero * 2]; }
    public void placeHero(int hero, int x, int y) {
        state.variables[54 + hero * 2] = x;
        state.variables[55 + hero * 2] = y;
    }
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
        // Primary verb 2. Closed object 23 routes to action 18 for both heroes.
        // Open object 24 routes Chief to generic action 13, Colleague to action 19.
        int clip = hero() == CHIEF
            ? (windowOpen() ? (state.variables[51] == 1 ? GLOBAL_CHIEF_REPLY : GLOBAL_CHIEF_REFUSAL) : 86)
            : (windowOpen() ? 87 : 64);
        state.variables[52] = clip;
        state.variables[53] = 1;
        return clip;
    }
    public int endStep() {
        int clip = pendingClip();
        return clip == 64 ? 88 : clip == 86 ? 13 : clip == 87 ? 14 : 36;
    }
    public boolean shuttersVisible() { return pendingClip() != 64 || step() >= 13; }
    public void advance() {
        if (!busy()) return;
        state.variables[53]++;
        if (pendingClip() == 64) {
            // 18.17 -> native 14(6) replaces the removed window with open object 24.
            if (step() >= 13) state.variables[61] = 1;
            // 18.18 -> native 14(7), entity 15 enters with clip 66 at (320,112).
            if (step() >= 38 && crowState() == 0) state.variables[62] = 1;
        }
        if (step() >= endStep()) {
            state.variables[52] = 0;
            state.variables[53] = 0;
        }
    }
}
