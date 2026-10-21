/*
 * Decompiled with CFR 0.152.
 */
package pilots;

import java.io.IOException;
import pilots.GameState;

public final class ScriptVM {
    private static int next(byte[] byArray, int n) throws IOException {
        if (n < 0 || n >= byArray.length) {
            throw new IOException("Truncated script");
        }
        return byArray[n] & 0xFF;
    }

    public static void execute(byte[] byArray, GameState gameState, Host host) throws IOException {
        ScriptVM.validate(byArray, gameState);
        int n = 0;
        int n2 = 0;
        while (n2 < 256) {
            int n3;
            if ((n3 = ScriptVM.next(byArray, n++)) == 0) {
                return;
            }
            int n4 = ScriptVM.next(byArray, n++);
            if (n3 == 1 || n3 == 2) {
                int n5 = ScriptVM.next(byArray, n++) << 8 | ScriptVM.next(byArray, n++);
                if (n3 == 1) {
                    gameState.variables[n4] = n5;
                } else {
                    int n6 = ScriptVM.next(byArray, n++);
                    if (gameState.variables[n4] != n5) {
                        n += n6;
                    }
                }
            } else {
                if (n3 == 3) {
                    host.goToScene(n4);
                    return;
                }
                if (n3 == 4) {
                    gameState.inventory[n4] = ScriptVM.next(byArray, n++) != 0;
                } else if (n3 == 5) {
                    host.message(n4);
                }
            }
            ++n2;
        }
        throw new IOException("Script instruction budget exceeded");
    }

    private static void validate(byte[] byArray, GameState gameState) throws IOException {
        int n;
        int n2;
        if (byArray == null || byArray.length < 1 || byArray.length > 1024) {
            throw new IOException("Invalid script size");
        }
        boolean[] blArray = new boolean[byArray.length];
        int n3 = 0;
        int n4 = 0;
        boolean bl = false;
        while (n3 < byArray.length) {
            if (++n4 > 256) {
                throw new IOException("Script instruction budget exceeded");
            }
            blArray[n3] = true;
            if ((n2 = ScriptVM.next(byArray, n3++)) == 0) {
                if (n3 != byArray.length) {
                    throw new IOException("Bytes after END");
                }
                bl = true;
                break;
            }
            n = ScriptVM.next(byArray, n3++);
            if (n2 == 1 || n2 == 2) {
                if (n >= gameState.variables.length) {
                    throw new IOException("Invalid variable");
                }
                ScriptVM.next(byArray, n3++);
                ScriptVM.next(byArray, n3++);
                if (n2 != 2) continue;
                ScriptVM.next(byArray, n3++);
                continue;
            }
            if (n2 == 4) {
                if (n < gameState.inventory.length && ScriptVM.next(byArray, n3++) <= 1) continue;
                throw new IOException("Invalid inventory operand");
            }
            if (n2 == 3 || n2 == 5) continue;
            throw new IOException("Unknown adapter opcode");
        }
        if (!bl) {
            throw new IOException("Missing END");
        }
        n3 = 0;
        while (n3 < byArray.length) {
            if ((n2 = ScriptVM.next(byArray, n3++)) == 0) break;
            ++n3;
            if (n2 == 1) {
                n3 += 2;
                continue;
            }
            if (n2 == 2) {
                n3 += 2;
                if (n3 + (n = ScriptVM.next(byArray, n3++)) < byArray.length && blArray[n3 + n]) continue;
                throw new IOException("Jump does not target an instruction");
            }
            if (n2 != 4) continue;
            ++n3;
        }
    }

    public static interface Host {
        public void goToScene(int var1) throws IOException;

        public void message(int var1);
    }
}

