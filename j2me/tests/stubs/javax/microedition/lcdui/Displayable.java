package javax.microedition.lcdui;
public abstract class Displayable {
    public int getWidth() { return Integer.getInteger("pilots.test.width",240).intValue(); }
    public int getHeight() { return Integer.getInteger("pilots.test.height",320).intValue(); }
    public final java.util.List<Command> commands=new java.util.ArrayList<Command>();
    public void addCommand(Command command) {if(!commands.contains(command))commands.add(command);}
    public void removeCommand(Command command) {commands.remove(command);}
    public void setCommandListener(CommandListener listener) {}
}
