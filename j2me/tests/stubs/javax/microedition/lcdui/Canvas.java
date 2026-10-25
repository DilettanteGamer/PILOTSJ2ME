package javax.microedition.lcdui;
public abstract class Canvas extends Displayable {
    public void setFullScreenMode(boolean full) {}
    public void repaint() {}
    public void repaint(int x,int y,int width,int height) {}
    public void serviceRepaints() {}
    public int getGameAction(int code) { return code==-1?1:code==-2?6:code==-3?2:code==-4?5:code==-5?8:0; }
    protected abstract void paint(Graphics graphics);
}
