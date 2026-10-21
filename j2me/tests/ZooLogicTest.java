package pilots;

import java.io.BufferedReader;
import java.io.FileReader;

/** Compare the port against cases evaluated directly from original EXE rule bytecode. */
public final class ZooLogicTest {
    private static void check(boolean ok, String message) {
        if (!ok) throw new RuntimeException(message);
    }
    public static void main(String[] args) throws Exception {
        BufferedReader input = new BufferedReader(new FileReader(args[0]));
        String line;
        int cases = 0;
        try {
            while ((line = input.readLine()) != null) {
                String[] values = line.split(",");
                int hero = Integer.parseInt(values[0]), opened = Integer.parseInt(values[1]);
                int branch = Integer.parseInt(values[2]), originalClip = Integer.parseInt(values[4]);
                int end = Integer.parseInt(values[5]);
                GameState state = new GameState(); state.scene = 2;
                state.variables[61] = opened; state.variables[51] = branch;
                ZooLogic logic = new ZooLogic(state);
                logic.select(hero);
                int clip = logic.beginWindow();
                check(clip == (originalClip < 46 ? 20000 + originalClip : originalClip), "EXE clip mismatch: " + line);
                check(logic.endStep() == end, "EXE release step mismatch");
                check(!logic.select(1 - hero) && logic.beginWindow() == 0, "busy lock");
                for (int step = 2; step < end; step++) {
                    logic.advance();
                    check(logic.busy(), "premature release");
                    if (clip == 64) {
                        check(logic.windowOpen() == (step >= 13), "shutters event step " + step);
                        check(logic.shuttersVisible() == (step >= 13), "shutters visibility step " + step);
                        check(logic.crowState() == (step >= 38 ? 1 : 0), "crow event step " + step);
                    } else check(logic.windowOpen() == (opened == 1), "wrong window mutation");
                    if (step == 20 && clip == 64) {
                        state = GameState.decode(state.encode(),20);
                        logic = new ZooLogic(state);
                        check(logic.pendingClip() == 64 && logic.step() == 20, "resume script step");
                    }
                }
                logic.advance();
                check(!logic.busy(), "release at source step");
                check(state.encode().length == 299, "legacy save envelope");
                cases++;
            }
        } finally { input.close(); }
        check(cases == 8, "missing source oracle cases");
        GameState legacy = new GameState(); legacy.variables[61] = 1;
        check(new ZooLogic(legacy).crowState() == 2, "0.4.0 completed opening migration");
        check(ZooLogic.atWindow(150,224) && ZooLogic.atWindow(217,289)
            && !ZooLogic.atWindow(218,289) && !ZooLogic.atWindow(150,290), "hit area");
        System.out.println("ZooLogic: 8 original-bytecode cases, intermediate events and save migration passed");
    }
}
