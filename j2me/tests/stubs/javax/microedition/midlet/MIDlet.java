package javax.microedition.midlet;
/** Desktop lifecycle harness; never packaged in the game. */
public abstract class MIDlet {
    public MIDlet() {}
    public String getAppProperty(String name) { return null; }
    public boolean destroyed;
    public void notifyDestroyed() {destroyed=true;}
    public void notifyPaused() {}
    public void resumeRequest() {}
    protected abstract void startApp();
    protected abstract void pauseApp();
    protected abstract void destroyApp(boolean unconditional);
}
