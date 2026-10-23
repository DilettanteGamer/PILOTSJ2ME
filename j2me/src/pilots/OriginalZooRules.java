package pilots;

import java.io.DataInputStream;
import java.io.InputStream;
import java.io.IOException;

/** Compiled from the pinned EXE's predicates; no guessed fallback actions. */
public final class OriginalZooRules {
    private static byte[] routes;
    private static int[] actions;
    private static synchronized void load() {
        if (routes != null) return;
        InputStream stream = OriginalZooRules.class.getResourceAsStream("/scenes/02.rules");
        if (stream == null) throw new IllegalStateException("Missing original zoo rules");
        DataInputStream input = new DataInputStream(stream);
        try {
            if (input.readInt() != 0x5a525431) throw new IOException("Invalid original rules");
            byte[] table = new byte[1024]; input.readFully(table);
            int count = input.readUnsignedShort();
            if (count > 64) throw new IOException("Invalid action count");
            int[] variants = new int[count * 4];
            for (int i=0;i<count;i++) {
                variants[i*4] = input.readUnsignedByte();
                variants[i*4+1] = input.readUnsignedByte();
                variants[i*4+2] = input.readUnsignedByte();
                int clip = input.readUnsignedShort();
                variants[i*4+3] = ((clip < 46 ? 20000 + clip : clip) << 16) | input.readUnsignedShort();
            }
            if (input.read() != -1) throw new IOException("Trailing original rules");
            actions = variants; routes = table;
        } catch (IOException failure) { throw new IllegalStateException(failure.toString()); }
        finally { try { input.close(); } catch (IOException ignored) {} }
    }
    public static int route(int target, int hero, int item, int flags) {
        load();
        if (target<1 || target>8 || hero<0 || hero>1 || item<0 || item>3 || flags<0 || flags>15) return 0;
        return routes[(((target-1)*2+hero)*4+item)*16+flags] & 255;
    }
    public static int variant(int action, int hero, int branch) {
        load();
        for (int i=0;i<actions.length;i+=4)
            if (actions[i]==action && actions[i+1]==hero && actions[i+2]==(action==13?branch:0)) return actions[i+3];
        return 0;
    }
}
