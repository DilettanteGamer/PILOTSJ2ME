/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  javax.microedition.lcdui.Alert
 *  javax.microedition.lcdui.AlertType
 *  javax.microedition.lcdui.Canvas
 *  javax.microedition.lcdui.Command
 *  javax.microedition.lcdui.CommandListener
 *  javax.microedition.lcdui.Display
 *  javax.microedition.lcdui.Displayable
 *  javax.microedition.lcdui.Graphics
 *  javax.microedition.midlet.MIDlet
 *  javax.microedition.rms.RecordStore
 */
package pilots;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Graphics;
import javax.microedition.midlet.MIDlet;
import javax.microedition.rms.RecordStore;
import pilots.AnimatedActor;
import pilots.AnimationCatalog;
import pilots.GameState;
import pilots.Hotspot;
import pilots.MenuCanvas;
import pilots.Scene;
import pilots.ScriptVM;
import pilots.Viewport;

public final class PilotsMidlet
extends MIDlet
implements CommandListener,
MenuCanvas.Host {
    private final Command menuCommand = new Command("\u041c\u0435\u043d\u044e", 1, 1);
    private Display display;
    private GameCanvas canvas;
    private MenuCanvas menu;
    private MenuCanvas animationMenu;
    private AnimationCatalog animationCatalog;

    protected void startApp() {
        this.display = Display.getDisplay((MIDlet)this);
        if (this.canvas == null) {
            try {
                this.canvas = new GameCanvas();
            }
            catch (IOException iOException) {
                this.fatal(iOException.toString());
                return;
            }
            catch (OutOfMemoryError outOfMemoryError) {
                this.fatal("Not enough heap for this profile");
                return;
            }
            this.showMenu();
        } else if (this.display.getCurrent() == null) {
            this.showMenu();
        }
        this.canvas.pauseAnimation(false);
    }

    protected void pauseApp() {
        if (this.canvas != null) {
            this.canvas.pauseAnimation(true);
        }
    }

    protected void destroyApp(boolean bl) {
        if (this.canvas != null) {
            this.canvas.shutdown();
        }
    }

    private void fatal(String string) {
        Alert alert = new Alert("Cannot start", string, null, AlertType.ERROR);
        alert.setTimeout(-2);
        Command command = new Command("Exit", 7, 1);
        alert.addCommand(command);
        alert.setCommandListener(new CommandListener(){

            public void commandAction(Command command, Displayable displayable) {
                PilotsMidlet.this.notifyDestroyed();
            }
        });
        this.display.setCurrent((Displayable)alert);
    }

    private void notice(String string, String string2) {
        Alert alert = new Alert(string, string2, null, AlertType.INFO);
        alert.setTimeout(-2);
        this.display.setCurrent(alert, (Displayable)this.canvas);
    }

    public void commandAction(Command command, Displayable displayable) {
        if (command == this.menuCommand) {
            if (this.canvas.isPreview()) {
                this.showAnimationMenu();
            } else {
                this.showMenu();
            }
        }
    }

    private void showMenu() {
        if (this.menu == null) {
            this.menu = new MenuCanvas(this);
        }
        this.display.setCurrent((Displayable)this.menu);
    }

    private void showAnimationMenu() {
        try {
            if (this.animationCatalog == null) {
                this.animationCatalog = new AnimationCatalog();
            }
            if (this.animationMenu == null) {
                this.animationMenu = new MenuCanvas(new MenuCanvas.Host(){

                    public void choose(int n) {
                        try {
                            if (n == 0) {
                                PilotsMidlet.this.canvas.finishPreview();
                            } else {
                                PilotsMidlet.this.canvas.preview(n - 1);
                            }
                            PilotsMidlet.this.display.setCurrent((Displayable)PilotsMidlet.this.canvas);
                        }
                        catch (IOException iOException) {
                            PilotsMidlet.this.notice("\u0410\u043d\u0438\u043c\u0430\u0446\u0438\u044f", iOException.toString());
                        }
                    }
                }, "\u0410\u043d\u0438\u043c\u0430\u0446\u0438\u0438", this.animationCatalog.labels);
            }
            if (this.canvas.isPreview()) {
                this.animationMenu.select(this.canvas.previewIndex + 1);
            }
            this.display.setCurrent((Displayable)this.animationMenu);
        }
        catch (IOException iOException) {
            this.notice("\u0410\u043d\u0438\u043c\u0430\u0446\u0438\u044f", iOException.toString());
        }
    }

    private void showInventory() {
        if (this.canvas.zoo == null) {
            this.notice("Инвентарь", "Предметы доступны на игровой сцене у зоопарка.");
            return;
        }
        final String[] items = this.canvas.zoo.logic.hasKettle()
            ? new String[]{"Назад", "Без предмета", "Чайник"}
            : new String[]{"Назад", "Без предмета"};
        MenuCanvas inventory = new MenuCanvas(new MenuCanvas.Host() {
            public void choose(int choice) {
                if (choice != 0) PilotsMidlet.this.canvas.selectKettle(choice == 2);
                PilotsMidlet.this.display.setCurrent((Displayable)PilotsMidlet.this.canvas);
            }
        }, "Инвентарь", items);
        inventory.select(this.canvas.zoo.logic.kettleSelected() ? 2 : 1);
        this.display.setCurrent((Displayable)inventory);
    }

    public void choose(int n) {
        if (n == 8) { this.showInventory(); return; }
        if (n == 6) {
            this.showAnimationMenu();
            return;
        }
        this.display.setCurrent((Displayable)this.canvas);
        try {
            if (n == 1) {
                this.canvas.changeScene(-1);
            } else if (n == 2) {
                this.canvas.changeScene(1);
            } else if (n == 3) {
                this.canvas.save();
                this.notice("\u0421\u043e\u0445\u0440\u0430\u043d\u0435\u043d\u0438\u0435", "\u041f\u043e\u0437\u0438\u0446\u0438\u044f \u0441\u043e\u0445\u0440\u0430\u043d\u0435\u043d\u0430");
            } else if (n == 4) {
                this.canvas.load();
                this.notice("\u0417\u0430\u0433\u0440\u0443\u0437\u043a\u0430", "\u041f\u043e\u0437\u0438\u0446\u0438\u044f \u0432\u043e\u0441\u0441\u0442\u0430\u043d\u043e\u0432\u043b\u0435\u043d\u0430");
            } else if (n == 5) {
                this.help();
            } else if (n == 7) {
                this.destroyApp(true);
                this.notifyDestroyed();
            }
        }
        catch (Exception exception) {
            this.notice("\u041e\u0448\u0438\u0431\u043a\u0430", exception.toString());
        }
    }

    private void help() {
        this.notice("Управление", "2/4/6/8: курсор. 5 или касание: действие. 1: Шеф, 3: Коллега. #: инвентарь; выберите чайник, затем укажите объект. Без предмета — обычное действие. Меню инвентаря открывается и касанием нижней панели. *: масштаб. 0: меню, сохранение и загрузка. 7/9: просмотр сцен. В списке анимаций 1/3: выбор, 5: повтор. Доступны окно, чайник и бочка. Ходьба и головоломка ворот ещё переносятся.");
    }

    final class GameCanvas
    extends Canvas
    implements Runnable,
    ScriptVM.Host {
        private Scene scene;
        private ZooScene zoo;
        private GameState state = new GameState();
        private Viewport view;
        private final int sceneCount;
        private final int[] sceneIds;
        private Thread animationThread;
        private boolean shown;
        private boolean appPaused;
        private boolean stopped;
        private long lastTick;
        private GameState previewReturnState;
        private int previewIndex = -1;

        GameCanvas() throws IOException {
            this.setFullScreenMode(true);
            this.addCommand(PilotsMidlet.this.menuCommand);
            this.setCommandListener(PilotsMidlet.this);
            InputStream inputStream = this.getClass().getResourceAsStream("/catalog.bin");
            if (inputStream == null) {
                throw new IOException("Missing scene catalog");
            }
            DataInputStream dataInputStream = new DataInputStream(inputStream);
            try {
                if (dataInputStream.readInt() != 1347179570) {
                    throw new IOException("Invalid scene catalog");
                }
                this.sceneCount = dataInputStream.readUnsignedShort();
                if (this.sceneCount < 1 || this.sceneCount > 254) {
                    throw new IOException("Invalid scene count");
                }
                this.sceneIds = new int[this.sceneCount];
                int n = 0;
                while (n < this.sceneCount) {
                    this.sceneIds[n] = dataInputStream.readUnsignedByte();
                    if (this.sceneIds[n] > 253 || n > 0 && this.sceneIds[n] <= this.sceneIds[n - 1]) {
                        throw new IOException("Invalid scene IDs");
                    }
                    ++n;
                }
                if (dataInputStream.read() != -1) {
                    throw new IOException("Trailing catalog data");
                }
            }
            finally {
                dataInputStream.close();
            }
            this.state.x = 180;
            this.state.y = 260;
            this.goToScene(2);
        }

        public synchronized void goToScene(int n) throws IOException {
            if (this.sceneIndex(n) < 0) {
                throw new IOException("Invalid scene ID");
            }
            if (this.scene != null) {
                this.scene.close();
            }
            this.scene = null;
            try {
                this.scene = new Scene(n);
            }
            catch (OutOfMemoryError outOfMemoryError) {
                throw new IOException("Scene exceeds available heap");
            }
            this.state.scene = n;
            this.zoo = n == 2 ? new ZooScene(this.scene, this.state) : null;
            this.view = new Viewport(Math.max(1, this.getWidth()), this.sceneHeight(this.getWidth(), this.getHeight()), this.scene.background.worldWidth, this.scene.background.worldHeight);
            this.view.cursorX = this.state.x;
            this.view.cursorY = this.state.y;
            this.view.zoom = this.state.zoom;
            this.view.update();
            this.lastTick = 0L;
            this.repaint();
        }

        synchronized boolean isPreview() {
            return this.previewReturnState != null;
        }

        synchronized void preview(int n) throws IOException {
            if (PilotsMidlet.this.animationCatalog == null || n < 0 || n >= ((PilotsMidlet)PilotsMidlet.this).animationCatalog.ids.length) {
                throw new IOException("Invalid preview selection");
            }
            if (this.zoo != null && this.zoo.logic.busy()) {
                throw new IOException("Finish the current action before preview");
            }
            if (this.previewReturnState == null) {
                this.syncState();
                GameState gameState = GameState.decode(this.state.encode(), this.sceneIds[this.sceneCount - 1] + 1);
                this.goToScene(2);
                this.previewReturnState = gameState;
            }
            this.zoo = null;
            this.scene.close();
            this.scene.actors = new AnimatedActor[0];
            int n2 = ((PilotsMidlet)PilotsMidlet.this).animationCatalog.ids[n];
            this.scene.actors = new AnimatedActor[]{new AnimatedActor(n2, "/scenes/02-" + n2 + ".anm", ((PilotsMidlet)PilotsMidlet.this).animationCatalog.x[n], ((PilotsMidlet)PilotsMidlet.this).animationCatalog.y[n])};
            this.previewIndex = n;
            this.view.cursorX = ((PilotsMidlet)PilotsMidlet.this).animationCatalog.focusX[n];
            this.view.cursorY = ((PilotsMidlet)PilotsMidlet.this).animationCatalog.focusY[n];
            this.view.zoom = false;
            this.view.update();
            this.lastTick = 0L;
            this.repaint();
        }

        synchronized void finishPreview() throws IOException {
            if (this.previewReturnState == null) {
                return;
            }
            this.state = this.previewReturnState;
            this.previewReturnState = null;
            this.previewIndex = -1;
            this.goToScene(this.state.scene);
        }

        protected synchronized void showNotify() {
            this.shown = true;
            this.lastTick = 0L;
            if (this.animationThread == null && !this.stopped) {
                this.animationThread = new Thread(this);
                this.animationThread.start();
            }
            this.notifyAll();
        }

        protected synchronized void hideNotify() {
            this.shown = false;
            this.lastTick = 0L;
        }

        synchronized void pauseAnimation(boolean bl) {
            this.appPaused = bl;
            this.lastTick = 0L;
            this.notifyAll();
        }

        synchronized void shutdown() {
            this.stopped = true;
            this.notifyAll();
            if (this.scene != null) {
                this.scene.close();
            }
            if (this.animationThread != null) {
                this.animationThread.interrupt();
            }
        }

        // Display/menu pause preserves the action; scene replacement resumes saved script steps.
        public void run() {
            while (true) {
                GameCanvas gameCanvas;
                try {
                    while (true) {
                        gameCanvas = this;
                        synchronized (gameCanvas) {
                            while (!(this.stopped || this.shown && !this.appPaused)) {
                                this.wait();
                            }
                            if (this.stopped) {
                                return;
                            }
                            long l = System.currentTimeMillis();
                            if (this.lastTick != 0L) {
                                this.tick(Math.max(0L, l - this.lastTick));
                            }
                            this.lastTick = l;
                        }
                        Thread.sleep(40L);
                    }
                }
                catch (InterruptedException interruptedException) {
                    gameCanvas = this;
                    synchronized (gameCanvas) {
                        if (this.stopped) {
                            return;
                        }
                        continue;
                    }
                }
                catch (IOException iOException) {
                    GameCanvas gameCanvas2 = this;
                    synchronized (gameCanvas2) {
                        this.appPaused = true;
                    }
                    PilotsMidlet.this.notice("\u0410\u043d\u0438\u043c\u0430\u0446\u0438\u044f", iOException.toString());
                    continue;
                }
            }
        }

        synchronized void tick(long l) throws IOException {
            if (this.scene == null) {
                return;
            }
            boolean bl = false;
            if (this.zoo != null) {
                bl = this.zoo.update(l);
            } else {
                for (int n = 0; n < this.scene.actors.length; n++)
                    bl = this.scene.actors[n].update(l) || bl;
            }
            if (bl) {
                this.repaint();
            }
        }

        private void replayActor(int n) throws IOException {
            int n2 = 0;
            while (n2 < this.scene.actors.length) {
                if (this.scene.actors[n2].id == n) {
                    this.scene.actors[n2].restart();
                }
                ++n2;
            }
            this.lastTick = 0L;
        }

        private int sceneIndex(int n) {
            int n2 = 0;
            while (n2 < this.sceneCount) {
                if (this.sceneIds[n2] == n) {
                    return n2;
                }
                ++n2;
            }
            return -1;
        }

        synchronized void changeScene(int n) throws IOException {
            if (this.isPreview()) {
                this.preview((this.previewIndex + n + ((PilotsMidlet)PilotsMidlet.this).animationCatalog.ids.length) % ((PilotsMidlet)PilotsMidlet.this).animationCatalog.ids.length);
                return;
            }
            this.syncState();
            this.goToScene(this.sceneIds[(this.sceneIndex(this.state.scene) + n + this.sceneCount) % this.sceneCount]);
        }

        public void message(int n) {
            PilotsMidlet.this.notice("Action", "Message " + n);
        }

        protected synchronized void sizeChanged(int n, int n2) {
            if (this.view != null && this.scene != null) {
                this.view.resize(Math.max(1, n), this.sceneHeight(n, n2), this.scene.background.worldWidth, this.scene.background.worldHeight);
                this.repaint();
            }
        }

        private int panelHeight(int n, int n2) {
            if (this.scene == null || this.scene.panel == null) {
                return 0;
            }
            return Math.min(Math.max(0, n2 - 1), Math.max(1, (n * this.scene.panel.worldHeight + this.scene.panel.worldWidth / 2) / this.scene.panel.worldWidth));
        }

        private int sceneHeight(int n, int n2) {
            return Math.max(1, n2 - this.panelHeight(n, n2));
        }

        protected synchronized void paint(Graphics graphics) {
            graphics.setColor(0);
            graphics.fillRect(0, 0, this.getWidth(), this.getHeight());
            if (this.scene == null || this.view == null) {
                graphics.setColor(0xFFFFFF);
                graphics.drawString("Use Menu to retry", 0, 0, 20);
                return;
            }
            graphics.setClip(0, 0, this.getWidth(), this.view.height);
            this.scene.background.paint(graphics, this.view);
            int n = 0;
            while (n < this.scene.actors.length) {
                this.scene.actors[n].paint(graphics, this.view);
                ++n;
            }
            n = this.view.screenX(this.view.cursorX);
            int n2 = this.view.screenY(this.view.cursorY);
            graphics.setColor(0);
            graphics.drawLine(n - 4, n2 + 1, n + 4, n2 + 1);
            graphics.drawLine(n + 1, n2 - 4, n + 1, n2 + 4);
            graphics.setColor((this.zoo != null ? this.zoo.logic.targetAt(this.view.cursorX, this.view.cursorY) != 0 : this.scene.at(this.view.cursorX, this.view.cursorY) != null) ? 0xFFFF00 : 0xFFFFFF);
            graphics.drawLine(n - 4, n2, n + 4, n2);
            graphics.drawLine(n, n2 - 4, n, n2 + 4);
            graphics.setClip(0, 0, this.getWidth(), this.getHeight());
            if (this.scene.panel != null) {
                this.scene.panel.paintPanel(graphics, this.getWidth(), this.getHeight(), this.panelHeight(this.getWidth(), this.getHeight()));
            }
            if (this.zoo != null) {
                graphics.setFont(javax.microedition.lcdui.Font.getFont(0, 0, 8));
                graphics.setColor(0);
                String label = this.zoo.logic.hero() == ZooLogic.CHIEF ? "1: Шеф" : "3: Коллега";
                if (this.zoo.logic.busy()) label += " ...";
                else label += this.zoo.logic.kettleSelected() ? " + чайник" : " # вещи";
                graphics.drawString(label, 2, this.view.height, 20);
            }
        }

        protected void keyPressed(int n) {
            this.handleKey(n, false);
        }

        protected void keyRepeated(int n) {
            this.handleKey(n, true);
        }

        private synchronized void handleKey(int n, boolean bl) {
            if (this.view == null || this.scene == null) {
                return;
            }
            try {
                int n2 = 0;
                try {
                    n2 = this.getGameAction(n);
                }
                catch (IllegalArgumentException illegalArgumentException) {}
                int n3 = Math.max(1, (this.view.worldWidth * (bl ? 4 : 2) + this.view.drawWidth - 1) / this.view.drawWidth);
                if (n == 50 || n2 == 1) {
                    this.view.move(0, -n3);
                } else if (n == 56 || n2 == 6) {
                    this.view.move(0, n3);
                } else if (n == 52 || n2 == 2) {
                    this.view.move(-n3, 0);
                } else if (n == 54 || n2 == 5) {
                    this.view.move(n3, 0);
                } else if (!bl && n == 49) {
                    if (this.isPreview()) {
                        this.changeScene(-1);
                    } else {
                        if (this.zoo != null) this.zoo.logic.select(ZooLogic.CHIEF);
                        else this.replayActor(95);
                    }
                } else if (!bl && n == 51) {
                    if (this.isPreview()) {
                        this.changeScene(1);
                    } else {
                        if (this.zoo != null) this.zoo.logic.select(ZooLogic.COLLEAGUE);
                        else this.replayActor(64);
                    }
                } else if (!bl && n == 42) {
                    this.view.zoom = !this.view.zoom;
                    this.view.update();
                } else {
                    if (!bl && n == 35) {
                        if (this.zoo != null) PilotsMidlet.this.showInventory();
                        else PilotsMidlet.this.help();
                        return;
                    }
                    if (!bl && n == 48) {
                        if (this.isPreview()) {
                            PilotsMidlet.this.showAnimationMenu();
                        } else {
                            PilotsMidlet.this.showMenu();
                        }
                        return;
                    }
                    if (!bl && n == 55) {
                        this.changeScene(-1);
                        return;
                    }
                    if (!bl && n == 57) {
                        this.changeScene(1);
                        return;
                    }
                    if (!(bl || n != 53 && n2 != 8)) {
                        this.activate();
                        return;
                    }
                }
                this.syncState();
                this.repaint();
            }
            catch (Exception exception) {
                PilotsMidlet.this.notice("Error", exception.toString());
            }
        }

        protected synchronized void pointerPressed(int n, int n2) {
            if (this.zoo != null && n2 >= this.view.height && n2 < this.getHeight()) {
                PilotsMidlet.this.showInventory();
                return;
            }
            if (this.view == null || this.scene == null || n < this.view.left || n >= this.view.left + this.view.drawWidth || n2 < this.view.top || n2 >= Math.min(this.view.height, this.view.top + this.view.drawHeight)) {
                return;
            }
            this.view.cursorX = this.view.worldX(n);
            this.view.cursorY = this.view.worldY(n2);
            this.view.update();
            try {
                this.activate();
            }
            catch (IOException iOException) {
                PilotsMidlet.this.notice("Error", iOException.toString());
            }
            this.repaint();
        }

        synchronized void selectKettle(boolean selected) {
            if (this.zoo != null) this.zoo.logic.selectKettle(selected);
            this.repaint();
        }

        private void activate() throws IOException {
            if (this.isPreview()) {
                if (this.scene.actors.length != 0) {
                    this.scene.actors[0].restart();
                }
                this.lastTick = 0L;
                this.repaint();
                return;
            }
            this.syncState();
            if (this.zoo != null) {
                this.zoo.activate(this.view.cursorX, this.view.cursorY);
                this.lastTick = 0L;
                this.repaint();
                return;
            }
            Hotspot hotspot = this.scene.at(this.view.cursorX, this.view.cursorY);
            if (hotspot == null) {
                PilotsMidlet.this.notice("\u041f\u0440\u043e\u0441\u043c\u043e\u0442\u0440 \u0441\u0446\u0435\u043d\u044b", "\u0421\u0446\u0435\u043d\u0430 " + (this.sceneIndex(this.state.scene) + 1) + "/" + this.sceneCount + ", \u043f\u043e\u0437\u0438\u0446\u0438\u044f " + this.view.cursorX + "," + this.view.cursorY + ". \u0412\u0437\u0430\u0438\u043c\u043e\u0434\u0435\u0439\u0441\u0442\u0432\u0438\u044f \u043e\u0440\u0438\u0433\u0438\u043d\u0430\u043b\u0430 \u043f\u043e\u043a\u0430 \u043d\u0435 \u043f\u0435\u0440\u0435\u043d\u0435\u0441\u0435\u043d\u044b.");
            } else {
                ScriptVM.execute(hotspot.action, this.state, this);
            }
            this.repaint();
        }

        private void syncState() {
            if (this.view != null && this.previewReturnState == null) {
                this.state.x = this.view.cursorX;
                this.state.y = this.view.cursorY;
                this.state.zoom = this.view.zoom;
            }
        }

        synchronized void save() throws Exception {
            this.syncState();
            byte[] byArray = (this.previewReturnState == null ? this.state : this.previewReturnState).encode();
            RecordStore recordStore = RecordStore.openRecordStore((String)"pilots-port-v1", (boolean)true);
            try {
                if (recordStore.getNumRecords() == 0) {
                    recordStore.addRecord(byArray, 0, byArray.length);
                } else {
                    recordStore.setRecord(1, byArray, 0, byArray.length);
                }
            }
            finally {
                recordStore.closeRecordStore();
            }
        }

        synchronized void load() throws Exception {
            GameState gameState;
            this.finishPreview();
            RecordStore recordStore = RecordStore.openRecordStore((String)"pilots-port-v1", (boolean)false);
            try {
                gameState = GameState.decode(recordStore.getRecord(1), this.sceneIds[this.sceneCount - 1] + 1);
            }
            finally {
                recordStore.closeRecordStore();
            }
            if (gameState.scene == 1) {
                gameState.scene = this.sceneIds[0];
            }
            GameState gameState2 = this.state;
            this.state = gameState;
            try {
                this.goToScene(gameState.scene);
            }
            catch (IOException iOException) {
                this.state = gameState2;
                throw iOException;
            }
        }
    }
}
