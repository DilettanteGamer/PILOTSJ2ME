package pilots;
import java.io.IOException;
import javax.microedition.lcdui.*;

/** Source electrical puzzle in a readable detail view; a solution returns to the room. */
public final class SwitchboardCanvas extends Canvas implements CommandListener {
    public interface Host { void back();void active(boolean active);void error(IOException error); }
    private final HotelWorld world;
    private final Host host;
    private final SwitchboardPuzzle puzzle;
    private final Command back=new Command("Назад",Command.BACK,1);
    private int selected;
    private boolean closed;
    public SwitchboardCanvas(HotelWorld world,Host host) throws IOException {
        this.world=world;this.host=host;puzzle=new SwitchboardPuzzle(world);world.beginBoard();
        setFullScreenMode(true);addCommand(back);setCommandListener(this);
    }
    protected void showNotify() {host.active(true);repaint();}
    protected void hideNotify() {host.active(false);}
    protected void paint(Graphics g) {
        g.setColor(0x514b35);g.fillRect(0,0,getWidth(),getHeight());g.setColor(0xffe4a5);
        g.setFont(Font.getFont(Font.FACE_SYSTEM,Font.STYLE_PLAIN,Font.SIZE_SMALL));
        g.drawString("Соедините провода",getWidth()/2,1,Graphics.TOP|Graphics.HCENTER);
        try{puzzle.paint(g,getWidth(),getHeight(),selected);}catch(IOException e){host.error(e);return;}
        g.setColor(0xffe4a5);g.drawString("2/4/6/8 выбор",getWidth()/2,getHeight()-27,Graphics.TOP|Graphics.HCENTER);
        g.drawString("5 поворот  0 назад",getWidth()/2,getHeight()-13,Graphics.TOP|Graphics.HCENTER);
    }
    protected void keyPressed(int key){input(key);}
    protected void keyRepeated(int key){int a=action(key);if(key!=KEY_NUM5 && key!=KEY_NUM0 && a!=FIRE)input(key);}
    private int action(int key){try{return getGameAction(key);}catch(IllegalArgumentException ignored){return 0;}}
    public void input(int key) {
        if(closed)return;int a=action(key);
        if(key==KEY_NUM0){leave();return;}
        if(key==KEY_NUM2 || a==UP)selected=puzzle.select(selected,0,-1);
        else if(key==KEY_NUM8 || a==DOWN)selected=puzzle.select(selected,0,1);
        else if(key==KEY_NUM4 || a==LEFT)selected=puzzle.select(selected,-1,0);
        else if(key==KEY_NUM6 || a==RIGHT)selected=puzzle.select(selected,1,0);
        else if(key==KEY_NUM5 || a==FIRE){turn();return;}
        repaint();
    }
    protected void pointerPressed(int x,int y){if(closed)return;int hit=puzzle.hit(x,y);if(hit>=0){selected=hit;turn();}}
    private void turn(){if(world.turnBoard(selected)&&!world.boardRequested()){leave();return;}repaint();}
    private void leave(){if(closed)return;closed=true;puzzle.close();world.endBoard();host.back();}
    public void commandAction(Command command,Displayable display){if(command==back)leave();}
}
