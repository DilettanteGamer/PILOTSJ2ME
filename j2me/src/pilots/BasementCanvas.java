package pilots;
import java.io.IOException;
import javax.microedition.lcdui.*;

/** Direct controls and a pausable timer, driven by the MIDlet's existing game thread. */
public final class BasementCanvas extends Canvas implements CommandListener {
    public interface Host{void back();void active(boolean active);void error(IOException error);}
    private final HotelWorld world;
    private final SpeechTrack speech;
    private final Host host;
    private final BasementStage stage;
    private final Command back=new Command("Назад",Command.BACK,1);
    private volatile boolean shown;
    private boolean closed;
    private long elapsed;
    public BasementCanvas(HotelWorld world,Host host)throws IOException{
        this(world,host,null);
    }
    public BasementCanvas(HotelWorld world,Host host,SpeechTrack speech)throws IOException{
        this.speech=speech;this.world=world;this.host=host;stage=new BasementStage(world.basement);world.basement.open();
        setFullScreenMode(true);addCommand(back);setCommandListener(this);
    }
    public boolean visible(){return shown&&!closed;}
    protected void showNotify(){shown=true;elapsed=0;host.active(true);repaint();}
    protected void hideNotify(){shown=false;elapsed=0;host.active(false);}
    protected void paint(Graphics g){g.setColor(0x231d16);g.fillRect(0,0,getWidth(),getHeight());try{synchronized(world){stage.paint(g,getWidth(),getHeight());if(speech!=null)CaptionPanel.paint(g,getWidth(),getHeight(),speech.text(),speech.age());}}catch(IOException error){host.error(error);}}
    public void update(long delta){
        if(closed || !shown || delta<=0)return;
        synchronized(world){elapsed+=Math.min(delta,400);while(elapsed>=100&&!world.basement.solved()){elapsed-=100;world.basement.tick();}}
        if(world.basement.solved()){leave();return;}repaint();
    }
    protected void keyPressed(int key){input(key);}
    protected void keyRepeated(int key){if(key!=KEY_NUM5 && action(key)!=FIRE)input(key);}
    private int action(int key){try{return getGameAction(key);}catch(IllegalArgumentException error){return 0;}}
    public void input(int key){
        if(closed)return;if(key==KEY_NUM0){leave();return;}
        int a=action(key),command=key==KEY_NUM2||a==UP?2:key==KEY_NUM8||a==DOWN?8:key==KEY_NUM4||a==LEFT?4:key==KEY_NUM6||a==RIGHT?6:key==KEY_NUM5||a==FIRE?5:0;
        synchronized(world){world.basement.input(command);}repaint();
    }
    protected void pointerPressed(int x,int y){if(y>getHeight()-20){leave();return;}if(y<getHeight()/3)input(KEY_NUM2);else if(y>getHeight()*2/3)input(KEY_NUM8);else input(x<getWidth()/3?KEY_NUM4:x>getWidth()*2/3?KEY_NUM6:KEY_NUM5);}
    private void leave(){if(closed)return;closed=true;shown=false;synchronized(world){world.basement.close();stage.close();}host.back();}
    public void shutdown(){closed=true;shown=false;stage.close();}
    public void commandAction(Command command,Displayable display){if(command==back)leave();}
}
