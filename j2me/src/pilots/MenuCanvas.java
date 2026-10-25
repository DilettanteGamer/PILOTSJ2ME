/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  javax.microedition.lcdui.Canvas
 *  javax.microedition.lcdui.Command
 *  javax.microedition.lcdui.CommandListener
 *  javax.microedition.lcdui.Displayable
 *  javax.microedition.lcdui.Font
 *  javax.microedition.lcdui.Graphics
 */
package pilots;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import pilots.MenuLayout;

public final class MenuCanvas
extends Canvas
implements CommandListener {
    private static final String[] DEFAULT_ITEMS={"Продолжить","Новая игра","Инвентарь","Сохранить","Загрузить","Справка","Анимации","Локации","Звук: вкл/выкл","Выход"};
    private final Host host;
    private final MenuLayout layout;
    private final String[] items;
    private final String title;
    private final boolean mainMenu;
    private Font font;
    private final Command select = new Command("\u0412\u044b\u0431\u0440\u0430\u0442\u044c", 4, 1);
    private final Command back = new Command("\u041d\u0430\u0437\u0430\u0434", 2, 2);

    public MenuCanvas(Host host) {
        this(host, "\u0411\u0440\u0430\u0442\u044c\u044f \u041f\u0438\u043b\u043e\u0442\u044b", DEFAULT_ITEMS);
    }

    public MenuCanvas(Host host, String string, String[] stringArray) {
        this.host = host;
        this.title = string;
        this.items = stringArray;
        this.mainMenu = stringArray == DEFAULT_ITEMS;
        this.layout = new MenuLayout(this.items.length);
        this.setFullScreenMode(true);
        this.addCommand(this.select);
        this.addCommand(this.back);
        this.setCommandListener(this);
        this.resizeMenu(this.getWidth(), this.getHeight());
    }

    private void resizeMenu(int n, int n2) {
        this.font = Font.getFont((int)0, (int)0, (int)(n < 200 ? 8 : 0));
        this.layout.resize(n, n2, this.font.getHeight());
    }

    protected synchronized void sizeChanged(int n, int n2) {
        if (this.layout != null) {
            this.resizeMenu(n, n2);
        }
        this.repaint();
    }

    protected synchronized void showNotify() {
        this.resizeMenu(this.getWidth(), this.getHeight());
        this.repaint();
    }

    private String fit(String string, int n) {
        if (this.font.stringWidth(string) <= n) {
            return string;
        }
        String string2 = "...";
        if (this.font.stringWidth(string2) > n) {
            return "";
        }
        int n2 = string.length();
        while (n2 > 0 && this.font.stringWidth(String.valueOf(string.substring(0, n2)) + string2) > n) {
            --n2;
        }
        return String.valueOf(string.substring(0, n2)) + string2;
    }

    private String label(int n) {
        if (this.mainMenu && this.layout.width < 200) {
            if (n == 1) {
                return "Новая игра";
            }
            if (n == 2) {
                return "Инвентарь";
            }
        }
        return this.items[n];
    }

    protected synchronized void paint(Graphics graphics) {
        int n;
        int n2;
        int n3 = this.getWidth();
        int n4 = this.getHeight();
        if (this.layout.width != n3 || this.layout.height != n4) {
            this.resizeMenu(n3, n4);
        }
        graphics.setColor(1582367);
        graphics.fillRect(0, 0, n3, n4);
        graphics.setFont(this.font);
        graphics.setColor(16045977);
        graphics.drawString(this.fit(this.title, n3 - 16), 8, (this.layout.header - this.font.getHeight()) / 2, 20);
        graphics.setColor(6583900);
        graphics.drawLine(8, this.layout.header - 3, n3 - 9, this.layout.header - 3);
        int n5 = n3 - 22;
        int n6 = 0;
        while (n6 < this.layout.visible) {
            n2 = this.layout.first + n6;
            n = this.layout.header + n6 * this.layout.rowHeight;
            if (n2 == this.layout.selected) {
                graphics.setColor(15254655);
                graphics.fillRect(5, n + 1, n5, this.layout.rowHeight - 2);
                graphics.setColor(1582367);
            } else {
                graphics.setColor(15066575);
            }
            graphics.drawString(this.fit(this.label(n2), n5 - 12), 11, n + (this.layout.rowHeight - this.font.getHeight()) / 2, 20);
            ++n6;
        }
        if (this.layout.visible < this.layout.count) {
            n6 = this.layout.visible * this.layout.rowHeight;
            graphics.setColor(3491383);
            graphics.fillRect(n3 - 10, this.layout.header, 3, n6);
            n2 = Math.max(4, n6 * this.layout.visible / this.layout.count);
            n = this.layout.header + (n6 - n2) * this.layout.first / (this.layout.count - this.layout.visible);
            graphics.setColor(15254655);
            graphics.fillRect(n3 - 10, n, 3, n2);
        }
        String string = String.valueOf(this.layout.selected + 1) + "/" + this.layout.count;
        n2 = this.font.stringWidth(string);
        n = n4 - this.layout.footer + (this.layout.footer - this.font.getHeight()) / 2;
        graphics.setColor(12043183);
        graphics.drawString(this.fit("5: OK  0: \u041d\u0430\u0437\u0430\u0434", n3 - n2 - 22), 8, n, 20);
        graphics.drawString(string, n3 - 8, n, 24);
    }

    protected void keyPressed(int n) {
        this.handleKey(n, false);
    }

    protected void keyRepeated(int n) {
        this.handleKey(n, true);
    }

    private void handleKey(int n, boolean bl) {
        int n2 = 0;
        try {
            n2 = this.getGameAction(n);
        }
        catch (IllegalArgumentException illegalArgumentException) {}
        if (n == 50 || n2 == 1) {
            this.move(-1);
        } else if (n == 56 || n2 == 6) {
            this.move(1);
        } else if (!bl && n == 48) {
            this.host.choose(0);
        } else if (!(bl || n != 53 && n2 != 8)) {
            this.host.choose(this.selected());
        }
    }

    public synchronized void select(int n) {
        this.layout.select(n);
        this.repaint();
    }

    private synchronized int selected() {
        return this.layout.selected;
    }

    private synchronized void move(int n) {
        this.layout.move(n);
        this.repaint();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void pointerPressed(int n, int n2) {
        int n3;
        MenuCanvas menuCanvas = this;
        synchronized (menuCanvas) {
            if (n < 0 || n >= this.getWidth()) {
                return;
            }
            if (n >= this.getWidth() - 16 && this.layout.visible < this.layout.count) {
                if (n2 >= this.layout.header && n2 < this.layout.header + this.layout.visible * this.layout.rowHeight) {
                    this.move(n2 < this.layout.header + this.layout.visible * this.layout.rowHeight / 2 ? -1 : 1);
                }
                return;
            }
            n3 = this.layout.itemAt(n2);
            if (n3 < 0) {
                return;
            }
            this.layout.select(n3);
        }
        this.host.choose(n3);
    }

    public void commandAction(Command command, Displayable displayable) {
        this.host.choose(command == this.back ? 0 : this.selected());
    }

    public static interface Host {
        public void choose(int var1);
    }
}
