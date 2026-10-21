/*
 * Decompiled with CFR 0.152.
 */
package pilots;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public final class GameState {
    public int scene;
    public int x = 320;
    public int y = 240;
    public boolean zoom;
    public final int[] variables = new int[64];
    public final boolean[] inventory = new boolean[32];

    public byte[] encode() throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
        dataOutputStream.writeInt(1347635761);
        dataOutputStream.writeShort(this.scene);
        dataOutputStream.writeShort(this.x);
        dataOutputStream.writeShort(this.y);
        dataOutputStream.writeBoolean(this.zoom);
        int n = 0;
        while (n < this.variables.length) {
            dataOutputStream.writeInt(this.variables[n]);
            ++n;
        }
        n = 0;
        while (n < this.inventory.length) {
            dataOutputStream.writeBoolean(this.inventory[n]);
            ++n;
        }
        dataOutputStream.close();
        return byteArrayOutputStream.toByteArray();
    }

    public static GameState decode(byte[] byArray, int n) throws IOException {
        if (byArray == null || byArray.length != 299) {
            throw new IOException("Invalid save size");
        }
        DataInputStream dataInputStream = new DataInputStream(new ByteArrayInputStream(byArray));
        if (dataInputStream.readInt() != 1347635761) {
            throw new IOException("Unsupported save version");
        }
        GameState gameState = new GameState();
        gameState.scene = dataInputStream.readUnsignedShort();
        gameState.x = dataInputStream.readUnsignedShort();
        gameState.y = dataInputStream.readUnsignedShort();
        gameState.zoom = dataInputStream.readBoolean();
        if (gameState.scene >= n || gameState.x >= 640 || gameState.y >= 480) {
            throw new IOException("Invalid saved location");
        }
        int n2 = 0;
        while (n2 < gameState.variables.length) {
            gameState.variables[n2] = dataInputStream.readInt();
            ++n2;
        }
        n2 = 0;
        while (n2 < gameState.inventory.length) {
            gameState.inventory[n2] = dataInputStream.readBoolean();
            ++n2;
        }
        return gameState;
    }
}

