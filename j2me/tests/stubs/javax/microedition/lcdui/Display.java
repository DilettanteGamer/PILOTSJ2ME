package javax.microedition.lcdui;
import javax.microedition.midlet.MIDlet;
/** Does not send showNotify: the test advances GameCanvas ticks explicitly. */
public class Display {
    private static final Display instance=new Display();
    private Displayable current;
    public static Display getDisplay(MIDlet app) { return instance; }
    public void setCurrent(Displayable screen) { current=screen; }
    public Displayable getCurrent() { return current; }
}
