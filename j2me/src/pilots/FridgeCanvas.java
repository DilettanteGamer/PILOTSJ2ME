package pilots;
import javax.microedition.lcdui.*;

/** A full screen detail view; the room and its timers pause until Back. */
public final class FridgeCanvas extends Canvas implements CommandListener {
    public interface Host { void back(); void active(boolean active); }
    private final HotelWorld world;
    private final Host host;
    private final Command back=new Command("Назад",Command.BACK,1);
    private int selected;
    public FridgeCanvas(HotelWorld world,Host host) {
        this.world=world;this.host=host;
        world.beginFridge();setFullScreenMode(true);addCommand(back);setCommandListener(this);
    }
    protected void showNotify() { host.active(true);repaint(); }
    protected void hideNotify() { host.active(false); }
    private int size() { return Math.max(3,Math.min(getWidth()-8,getHeight()-46)/3*3); }
    private int left() { return (getWidth()-size())/2; }
    private int top() { return 17+Math.max(0,(getHeight()-46-size())/2); }
    protected void paint(Graphics g) {
        g.setColor(0x262d28);g.fillRect(0,0,getWidth(),getHeight());
        g.setFont(Font.getFont(Font.FACE_SYSTEM,Font.STYLE_PLAIN,Font.SIZE_SMALL));g.setColor(0xffefb4);
        g.drawString(world.fridgeMask()==0?"Готово!":"Ручки горизонтально",getWidth()/2,1,Graphics.TOP|Graphics.HCENTER);
        RefrigeratorPuzzle.paint(g,left(),top(),size(),world.fridgeMask(),selected);
        g.setColor(0xffffff);g.drawString("2/4/6/8 выбор",getWidth()/2,getHeight()-28,Graphics.TOP|Graphics.HCENTER);
        g.drawString("5 ход  0 назад",getWidth()/2,getHeight()-14,Graphics.TOP|Graphics.HCENTER);
    }
    protected void keyPressed(int key) { input(key); }
    protected void keyRepeated(int key) {
        int action=0;try { action=getGameAction(key); }catch(IllegalArgumentException ignored) {}
        if(key!=KEY_NUM5 && key!=KEY_NUM0 && action!=FIRE)input(key);
    }
    public void input(int key) {
        int action=0;try { action=getGameAction(key); }catch(IllegalArgumentException ignored) {}
        if(key==KEY_NUM0) { leave();return; }
        if(key==KEY_NUM2 || action==UP) selected=selected>=3?selected-3:selected;
        else if(key==KEY_NUM8 || action==DOWN) selected=selected<6?selected+3:selected;
        else if(key==KEY_NUM4 || action==LEFT) selected=selected%3>0?selected-1:selected;
        else if(key==KEY_NUM6 || action==RIGHT) selected=selected%3<2?selected+1:selected;
        else if(key==KEY_NUM5 || action==FIRE) world.turnFridge(selected);
        repaint();
    }
    protected void pointerPressed(int x,int y) {
        int size=size(),left=left(),top=top();
        if(x<left || y<top || x>=left+size || y>=top+size)return;
        selected=(y-top)/(size/3)*3+(x-left)/(size/3);world.turnFridge(selected);repaint();
    }
    private void leave() { world.endFridge();host.back(); }
    public void commandAction(Command command,Displayable display) { if(command==back)leave(); }
}
