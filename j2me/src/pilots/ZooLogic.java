package pilots;

/** Original zoo rules; evidence is recorded in window/barrel-original-rules.json. */
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
    public static final int WINDOW = 1, KETTLE = 2, BARREL = 3;
    public boolean hasKettle() { return state.inventory[0]; }
    public boolean kettleSelected() { return state.variables[50] == 1 && hasKettle(); }
    public boolean selectKettle(boolean selected) {
        if (busy() || (selected && !hasKettle())) return false;
        state.variables[50] = selected ? 1 : 0;
        return true;
    }
    public int targetAt(int x, int y) {
        if (atWindow(x, y)) return WINDOW;
        if (!hasKettle() && x >= 80 && x < 118 && y >= 284 && y < 320) return KETTLE;
        if (x >= 128 && x < 192 && y >= 282 && y < 350) return BARREL;
        return 0;
    }
    private int refusal() {
        if (hero() == CHIEF) return state.variables[51] == 1 ? GLOBAL_CHIEF_REPLY : GLOBAL_CHIEF_REFUSAL;
        return state.variables[46] == 1 ? 20035 : 20037;
    }
    public int beginWindow() { return begin(WINDOW); }
    public int begin(int target) {
        if (busy()) return 0;
        int clip = 0;
        if (target == WINDOW) {
            if (kettleSelected()) clip = hero() == CHIEF ? refusal() : 84;
            else clip = hero() == CHIEF ? (windowOpen() ? refusal() : 86) : (windowOpen() ? 87 : 64);
        } else if (target == KETTLE && !hasKettle() && !kettleSelected()) {
            clip = hero() == CHIEF ? 75 : 76;
        } else if (target == BARREL) {
            if (hero() == CHIEF) {
                if (!kettleSelected()) clip = refusal();
                else if (!catPresent()) clip = 69;
            } else {
                clip = catPresent() ? refusal() : kettleSelected() ? 84 : 83;
            }
        }
        if (clip != 0) {
            state.variables[52] = clip;
            state.variables[53] = 1;
        }
        return clip;
    }
    public int endStep() {
        int clip = pendingClip();
        return clip == 64 ? 88 : clip == 86 ? 13 : clip == 87 ? 14
            : clip == 75 ? 10 : clip == 76 ? 6 : clip == 69 ? 34 : clip == 83 ? 45
            : clip == 84 ? 31 : clip == 20037 ? 23 : clip == 20035 ? 53 : 36;
    }
    public boolean shuttersVisible() { return pendingClip() != 64 || step() >= 13; }
    public void advance() {
        if (!busy()) return;
        state.variables[53]++;
        if (pendingClip() == 64) {
            if (step() >= 13) state.variables[61] = 1;
            if (step() >= 38 && crowState() == 0) state.variables[62] = 1;
        }
        if ((pendingClip() == 75 && step() >= 8) || (pendingClip() == 76 && step() >= 4))
            state.inventory[0] = true; // Source map 41 replaces world kettle with inventory entity 33.
        if (pendingClip() == 69 && step() == 25) {
            state.variables[40] = 1; state.variables[41] = 0;
            state.variables[44] = 68; state.variables[45] = 0;
        }
        if (step() >= endStep()) {
            state.variables[52] = 0;
            state.variables[53] = 0;
        }
    }

    // Cat object 16: v94 branches on the presence of crow entity 15; v98 is its counter.
    public boolean catPresent() { return state.variables[40] != 0; }
    public int catMode() { return state.variables[40]; }
    public int catStep() { return state.variables[41]; }
    public int catClip() { return state.variables[44] == 0 ? 68 : state.variables[44]; }
    public int catFrame() { return state.variables[45]; }
    public void catFrame(int frame) { state.variables[45] = frame; }
    public int butterflyState() { return state.variables[42]; }
    public int butterflyFrame() { return state.variables[43]; }
    public void butterflyFrame(int frame) { state.variables[43] = frame; }
    public void settleButterfly() { state.variables[42] = 2; state.variables[43] = 0; }
    private void catClip(int id) { state.variables[44] = id; state.variables[45] = 0; }
    public void advanceCat() {
        if (!catPresent()) return;
        if (catStep() == 0 && crowState() != 0) state.variables[40] = 2;
        state.variables[41]++;
        if (catMode() == 3) {
            // Rule 16.13 has map effects in metadata 0x199 as well as its sound call.
            if (catStep() >= 16) {
                state.variables[40] = state.variables[41] = state.variables[45] = 0;
            }
            return;
        }
        if (catStep() == 15) catClip(67);
        if (catStep() == 32 && catMode() == 2) {
            state.variables[62] = state.variables[59] = 0; // map 10 removes the crow
            catClip(89);
        }
        if (catStep() == 45 && catMode() == 2) {
            state.variables[42] = 1; state.variables[43] = 0; // map 43 creates butterfly entity 30
        }
        if ((catMode() == 1 && catStep() == 32) || (catMode() == 2 && catStep() == 58)) {
            state.variables[40] = 3; state.variables[41] = 1;
            catClip(101);
        }
    }
}
