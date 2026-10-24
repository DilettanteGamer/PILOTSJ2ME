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
    private FridgeCanvas fridge;
    private SwitchboardCanvas switchboard;
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
        if(this.fridge!=null || this.switchboard!=null)this.canvas.audio.active(true);
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

    private void showFridge(final HotelWorld world) throws IOException {
        this.fridge=new FridgeCanvas(world,new FridgeCanvas.Host() {
            public void back() { PilotsMidlet.this.fridge=null;PilotsMidlet.this.display.setCurrent(PilotsMidlet.this.canvas); }
            public void active(boolean active) { PilotsMidlet.this.canvas.audio.active(active && !PilotsMidlet.this.canvas.appPaused); }
        });
        this.display.setCurrent(this.fridge);
    }

    private void showSwitchboard(final HotelWorld world) throws IOException {
        this.switchboard=new SwitchboardCanvas(world,new SwitchboardCanvas.Host() {
            public void back() {
                PilotsMidlet.this.switchboard=null;
                try { ((HotelScene)PilotsMidlet.this.canvas.zoo).refreshState(); }
                catch(IOException error){error(error);return;}
                PilotsMidlet.this.display.setCurrent(PilotsMidlet.this.canvas);
            }
            public void active(boolean active){PilotsMidlet.this.canvas.audio.active(active && !PilotsMidlet.this.canvas.appPaused);}
            public void error(IOException error){PilotsMidlet.this.notice("Электрощит",error.toString());}
        });
        this.display.setCurrent(this.switchboard);
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
        if(this.canvas.zoo==null) return;
        final int[] values=new int[5];
        int count=2;values[0]=-1;values[1]=0;
        for(int item=1;item<=3;item++) if(this.canvas.zoo.logic.hasItem(item)) values[count++]=item;
        String[] labels=new String[count];labels[0]="Назад";labels[1]="Без предмета";
        for(int i=2;i<count;i++) labels[i]=this.canvas.state.scene==15?"Автомат":this.canvas.state.scene==13?"Огнетушитель":this.canvas.state.scene==12?(values[i]==1?"Вантуз":"Мыло"):this.canvas.state.scene==11?"Осколок":this.canvas.state.scene==10?"Мухобойка":this.canvas.state.scene==9?(values[i]==1?"Баллончик":values[i]==2?"Ключ":"Отвёртка"):this.canvas.state.scene==6?"Штопор":this.canvas.state.scene==5?"Ключ":this.canvas.state.scene==4?(values[i]==1?"Лейка":values[i]==2?"Грабли":"Секатор"):(values[i]==1?"Чайник":values[i]==2?"Кирка":"Монета");
        MenuCanvas inventory=new MenuCanvas(new MenuCanvas.Host() {
            public void choose(int choice) {
                if(choice>0) PilotsMidlet.this.canvas.selectItem(values[choice]);
                PilotsMidlet.this.display.setCurrent((Displayable)PilotsMidlet.this.canvas);
            }
        },"Инвентарь",labels);
        this.display.setCurrent((Displayable)inventory);
    }

    private void showLocations() {
        final int[] ids={-1,2,3,4,5,6,8,9,10,11,12,13,15};
        String[] labels={"Назад","У ворот","Двор зоопарка","Звериный двор","Сад","Улица","Гостиница: вступление","Гостиница: интерьер","Комната с балконом","Карниз гостиницы","Ванная комната","Электрощитовая","Подвал"};
        MenuCanvas locations=new MenuCanvas(new MenuCanvas.Host() {
            public void choose(int choice) {
                if(choice==0) { showMenu();return; }
                try { canvas.startLocation(ids[choice]);display.setCurrent(canvas); }
                catch(Exception error) { notice("Локация",error.toString()); }
            }
        },"Локации",labels);
        this.display.setCurrent(locations);
    }

    public void choose(int n) {
        if (n == 2) { this.showInventory(); return; }
        if (n == 7) { this.showLocations(); return; }
        if (n == 6) {
            this.showAnimationMenu();
            return;
        }
        this.display.setCurrent((Displayable)this.canvas);
        try {
            if (n == 1) {
                this.canvas.newGame();

            } else if (n == 3) {
                this.canvas.save();
                this.notice("\u0421\u043e\u0445\u0440\u0430\u043d\u0435\u043d\u0438\u0435", "\u041f\u043e\u0437\u0438\u0446\u0438\u044f \u0441\u043e\u0445\u0440\u0430\u043d\u0435\u043d\u0430");
            } else if (n == 4) {
                this.canvas.load();
                this.notice("\u0417\u0430\u0433\u0440\u0443\u0437\u043a\u0430", "\u041f\u043e\u0437\u0438\u0446\u0438\u044f \u0432\u043e\u0441\u0441\u0442\u0430\u043d\u043e\u0432\u043b\u0435\u043d\u0430");
            } else if (n == 5) {
                this.help();
            } else if (n == 8) {
                this.notice("Звук и музыка",this.canvas.audio.toggle()?"Включены":"Выключены");
            } else if (n == 9) {
                this.destroyApp(true);
                this.notifyDestroyed();
            }
        }
        catch (Exception exception) {
            this.notice("\u041e\u0448\u0438\u0431\u043a\u0430", exception.toString());
        }
    }

    private void help() {
        String location=this.canvas!=null && this.canvas.state.scene==15?
            "Подвал: Коллега достаёт автомат, Шеф прыгает по ящикам. Указывайте верх ящика или место следующего прыжка; автомат выбирается в нижней строке. Следите за Карбофосом и падающими ящиками. ":this.canvas!=null && this.canvas.state.scene==13?
            "Электрощитовая: открывайте щит Коллегой, соединяйте провода Шефом. В подробном виде 2/4/6/8 выбирают участок, 5 поворачивает, 0 возвращает в комнату. ":this.canvas!=null && this.canvas.state.scene==12?
            "Ванная комната: выбирайте вантуз и мыло в нижней строке. Для действий с бачком понадобятся оба брата; состояние вантуза ограничено по времени. ":this.canvas!=null && this.canvas.state.scene==11?
            "Карниз гостиницы: проверяйте действия обоих братьев у окон и троса; предметы выбираются в нижней строке. ":this.canvas!=null && this.canvas.state.scene==10?
            "Комната с балконом: исследуйте ящики, бумагу, телевизор и шнур. Действия братьев различаются; персонаж снаружи временно недоступен. ":this.canvas!=null && this.canvas.state.scene==9?
            "Гостиница: выбирайте героев и предметы в нижней строке. Холодильник открывает отдельный экран с ручками 3×3; поворачиваются строка и столбец. ":this.canvas!=null && this.canvas.state.scene==6?
            "Улица: управляйте Коллегой. Шеф действует самостоятельно у светофора. Исследуйте урну и люк; следите за машинами и прохожим на другой стороне. ":this.canvas!=null && this.canvas.state.scene==5?
            "Сад: выбирайте братьев в нижней строке и перекрывайте мартышке пути отхода. После сцены с ключом нажмите 5. ":this.canvas!=null && this.canvas.state.scene==4?
            "Звериный двор: выбирайте предметы в нижней строке и проверяйте действия обоих героев. ":this.canvas!=null && this.canvas.state.scene==3?
            "Двор зоопарка: Шеф играет на рояле, Коллега меняет ноты. Проверяйте реакции героев на мелодии и расположение указателей. ":
            "У ворот: выбирайте героев и предметы в нижней строке. После открытия ворот укажите проход. ";
        this.notice("Управление",location+"2/4/6/8: курсор; 5: действие; 1/3: герои; 7: предмет; 9: герой; #: инвентарь; *: масштаб; 0: меню. Подменю «Локации» открывает перенесённые сцены отдельно; сохранение на диске меняется только по команде «Сохранить».");
    }

    final class GameCanvas
    extends Canvas
    implements Runnable,
    ScriptVM.Host {
        private Scene scene;
        private GameAudio audio;
        private ZooScene zoo;
        private ZooPanel zooPanel;
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
            this.audio=new GameAudio();
            try { this.goToScene(2); }
            catch(IOException failure) { this.audio.close();throw failure; }
            catch(OutOfMemoryError failure) { this.audio.close();throw failure; }
        }

        public synchronized void goToScene(int n) throws IOException {
            if (this.sceneIndex(n) < 0) throw new IOException("Invalid scene ID");
            this.audio.active(false);
            int cachePixels=this.getWidth()<200?65536:131072;
            try {
                String configured=PilotsMidlet.this.getAppProperty("Pilots-Raster-Cache-Pixels");
                if(configured!=null) {
                    int value=Integer.parseInt(configured);
                    if(value>=0 && value<=131072) cachePixels=value;
                }
            } catch(NumberFormatException ignored) {}
            RasterCache.budget(cachePixels);
            WalkStreams.clear();
            if (this.zooPanel!=null) { this.zooPanel.close();this.zooPanel=null; }
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
            this.zoo = n>=8?new HotelScene(this.scene,this.state):n==6?new StreetScene(this.scene,this.state):n==5?new GardenScene(this.scene,this.state):n==4?new AnimalScene(this.scene,this.state):n==3?new CourtyardScene(this.scene,this.state):new ZooScene(this.scene,this.state);
            this.zooPanel = n==8?null:new ZooPanel(n);
            this.audio.scene(n);this.zoo.logic.setSound(this.audio);
            this.audio.active(this.shown && !this.appPaused && !this.isPreview());
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
                this.state=new GameState();
                this.state.scene=PilotsMidlet.this.animationCatalog.scene;
                this.previewReturnState = gameState;
                this.goToScene(PilotsMidlet.this.animationCatalog.scene);
            }
            this.audio.active(false);
            this.zoo = null;
            if(this.zooPanel!=null) { this.zooPanel.close();this.zooPanel=null; }
            this.scene.close();
            this.scene.actors = new AnimatedActor[0];
            int n2 = ((PilotsMidlet)PilotsMidlet.this).animationCatalog.ids[n];
            this.scene.actors = new AnimatedActor[]{new AnimatedActor(n2, "/scenes/" + (PilotsMidlet.this.animationCatalog.scene<10?"0":"") + PilotsMidlet.this.animationCatalog.scene + "-" + n2 + ".anm", ((PilotsMidlet)PilotsMidlet.this).animationCatalog.x[n], ((PilotsMidlet)PilotsMidlet.this).animationCatalog.y[n])};
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
            this.audio.active(!this.appPaused && !this.isPreview());
            this.lastTick = 0L;
            if (this.animationThread == null && !this.stopped) {
                this.animationThread = new Thread(this);
                this.animationThread.start();
            }
            this.notifyAll();
        }

        protected synchronized void hideNotify() {
            this.shown = false;
            this.audio.active(false);
            this.lastTick = 0L;
        }

        synchronized void pauseAnimation(boolean bl) {
            this.appPaused = bl;
            this.audio.active(this.shown && !bl && !this.isPreview());
            this.lastTick = 0L;
            this.notifyAll();
        }

        synchronized void shutdown() {
            this.stopped = true;
            this.audio.close();
            WalkStreams.clear();RasterCache.budget(0);
            this.notifyAll();
            if (this.scene != null) this.scene.close();
            if (this.zooPanel != null) this.zooPanel.close();
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
                        this.audio.active(false);
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
                int next=SceneFlow.next(this.state.scene,this.zoo.logic.completed());
                if(next!=this.state.scene) { this.startLocation(next);return; }
                if(PilotsMidlet.this.switchboard==null && this.zoo instanceof HotelScene && ((HotelScene)this.zoo).animals.world.boardRequested()) {
                    PilotsMidlet.this.showSwitchboard(((HotelScene)this.zoo).animals.world);return;
                }
                if(PilotsMidlet.this.fridge==null && this.zoo instanceof HotelScene && ((HotelScene)this.zoo).animals.world.fridgeRequested()) {
                    PilotsMidlet.this.showFridge(((HotelScene)this.zoo).animals.world);return;
                }
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
            // Gameplay locations change through their scripted exits; keys cycle previews only.
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
            int clipX=graphics.getClipX(),clipY=graphics.getClipY(),clipWidth=graphics.getClipWidth(),clipHeight=graphics.getClipHeight();
            graphics.setColor(0);
            graphics.fillRect(0, 0, this.getWidth(), this.getHeight());
            if (this.scene == null || this.view == null) {
                graphics.setColor(0xFFFFFF);
                graphics.drawString("Use Menu to retry", 0, 0, 20);
                return;
            }
            graphics.clipRect(0, 0, this.getWidth(), this.view.height);
            this.view.paintClip(clipX,clipY,clipWidth,clipHeight);
            this.scene.background.paint(graphics, this.view);
            int n = 0;
            if(this.zoo!=null) this.zoo.paint(graphics,this.view);
            else while (n < this.scene.actors.length) {
                this.scene.actors[n].paint(graphics, this.view);
                ++n;
            }
            n = this.view.screenX(this.view.cursorX);
            int n2 = this.view.screenY(this.view.cursorY);
            graphics.setColor(0);
            graphics.drawLine(n - 4, n2 + 1, n + 4, n2 + 1);
            graphics.drawLine(n + 1, n2 - 4, n + 1, n2 + 4);
            graphics.setColor((this.zoo != null ? this.zoo.targetAt(this.view.cursorX, this.view.cursorY) != 0 : this.scene.at(this.view.cursorX, this.view.cursorY) != null) ? 0xFFFF00 : 0xFFFFFF);
            graphics.drawLine(n - 4, n2, n + 4, n2);
            graphics.drawLine(n, n2 - 4, n, n2 + 4);
            graphics.setClip(clipX,clipY,clipWidth,clipHeight);
            if (this.scene.panel != null && clipY+clipHeight>this.view.height) {
                this.scene.panel.paintPanel(graphics, this.getWidth(), this.getHeight(), this.panelHeight(this.getWidth(), this.getHeight()));
            }
            if(this.zooPanel!=null && this.zoo!=null && clipY+clipHeight>this.view.height)
                this.zooPanel.paint(graphics,this.zoo.logic,this.getWidth(),this.getHeight(),this.panelHeight(this.getWidth(),this.getHeight()));
            String caption=this.zoo==null?null:this.zoo.logic.message();
            if(caption==null && this.zoo!=null) caption=this.zoo.hoverText(this.view.cursorX,this.view.cursorY);
            if(caption!=null) {
                javax.microedition.lcdui.Font font=javax.microedition.lcdui.Font.getFont(0,0,8);
                graphics.setFont(font);
                String remaining=caption;String[] lines=new String[6];int count=0;
                while(remaining.length()>0 && count<lines.length) {
                    int length=remaining.length();
                    while(length>1 && font.stringWidth(remaining.substring(0,length))>this.getWidth()-4) length--;
                    int space=remaining.substring(0,length).lastIndexOf(' ');
                    if(length<remaining.length() && space>0) length=space;
                    lines[count++]=remaining.substring(0,length);remaining=remaining.substring(length).trim();
                }
                graphics.setColor(0);graphics.fillRect(0,0,this.getWidth(),count*font.getHeight()+4);
                graphics.setColor(0xffffff);
                for(int i=0;i<count;i++) graphics.drawString(lines[i],2,2+i*font.getHeight(),20);
            }
        }

        protected void keyPressed(int n) {
            this.handleKey(n, false);
        }

        protected void keyRepeated(int n) {
            this.handleKey(n, true);
        }

        private String cursorCaption() {
            String text=this.zoo==null?null:this.zoo.logic.message();
            return text==null && this.zoo!=null?this.zoo.hoverText(this.view.cursorX,this.view.cursorY):text;
        }

        private void moveCursor(int dx,int dy) {
            int oldX=view.screenX(view.cursorX),oldY=view.screenY(view.cursorY),left=view.left,top=view.top;
            String caption=cursorCaption();
            view.move(dx,dy);syncState();
            String next=cursorCaption();
            if(left!=view.left || top!=view.top || !(caption==null?next==null:caption.equals(next))) repaint();
            else {
                int x=view.screenX(view.cursorX),y=view.screenY(view.cursorY);
                repaint(Math.min(oldX,x)-6,Math.min(oldY,y)-6,Math.abs(x-oldX)+13,Math.abs(y-oldY)+13);
            }
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
                    this.moveCursor(0, -n3);return;
                } else if (n == 56 || n2 == 6) {
                    this.moveCursor(0, n3);return;
                } else if (n == 52 || n2 == 2) {
                    this.moveCursor(-n3, 0);return;
                } else if (n == 54 || n2 == 5) {
                    this.moveCursor(n3, 0);return;
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
                    if (!bl && n == 55 && this.zoo!=null) {
                        int item=this.zoo.logic.selectedItem();
                        for(int i=0;i<4;i++) { item=(item+1)%4;if(this.zoo.logic.hasItem(item)) break; }
                        this.zoo.logic.selectItem(item);this.repaint();return;
                    }
                    if (!bl && n == 57 && this.zoo!=null) {
                        this.zoo.logic.select(1-this.zoo.logic.hero());this.repaint();return;
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
            if(this.zoo!=null && n2>=this.view.height && n2<this.getHeight()) {
                int slot=ZooPanel.slotAt(n,n2,this.getWidth(),this.getHeight(),this.panelHeight(this.getWidth(),this.getHeight()));
                if(slot==0 || slot==1) this.zoo.logic.select(slot==0?1:0);
                else if(slot==3) this.zoo.logic.selectItem(0);
                else if(slot>=4 && slot<=6) this.zoo.logic.selectItem(slot-3);
                else if(slot==9) PilotsMidlet.this.showMenu();
                this.repaint();return;
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

        synchronized void selectItem(int item) {
            if(this.zoo!=null) this.zoo.logic.selectItem(item);
            this.repaint();
        }
        synchronized void startCourtyard() throws IOException { startLocation(3); }
        synchronized void startLocation(int id) throws IOException {
            this.previewReturnState=null;this.previewIndex=-1;
            this.state=new GameState();this.state.scene=id;this.state.x=id==15?384:id==13?192:id==12?448:id==11?448:id==10?192:id==8?192:id==9?320:id==6?576:id==4?192:128;this.state.y=id==15?384:id==13?176:id==12?336:id==11?320:id==10?224:id==8?208:id==9?320:id==6?400:id==5?368:id==4?304:272;
            this.goToScene(id);
        }
        synchronized void newGame() throws IOException {
            this.previewReturnState=null;this.previewIndex=-1;
            this.state=new GameState();this.state.x=320;this.state.y=304;
            this.goToScene(2);
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
                this.zoo.request(this.view.cursorX,this.view.cursorY);
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
            if(this.sceneIndex(gameState.scene)<0) throw new IOException("Invalid saved scene ID");
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
