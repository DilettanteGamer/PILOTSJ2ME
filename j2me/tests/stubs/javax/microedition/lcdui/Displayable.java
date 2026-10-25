package javax.microedition.lcdui;
public abstract class Displayable {
    public int getWidth() { return Integer.getInteger("pilots.test.width",240).intValue(); }
    public int getHeight() { return Integer.getInteger("pilots.test.height",320).intValue(); }
    public void addCommand(Command command) {}
    public void setCommandListener(CommandListener listener) {}
}
