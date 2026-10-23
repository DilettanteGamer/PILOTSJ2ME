package pilots;

import javax.microedition.lcdui.Image;

/** Shared, bounded LRU for disposable screen-sized rasters (four bytes/pixel budget). */
public final class RasterCache {
    private static RasterCache first,last;
    private static int used,limit=65536;
    private RasterCache previous,next;
    private Image image;
    private int cost;

    public static void budget(int pixels) {
        limit=Math.max(0,pixels);
        while(used>limit && first!=null) first.clear();
    }
    public static int pixels() { return used; }
    public static boolean allows(int pixels) { return pixels>0 && pixels<=limit; }
    public static void failed() { budget(0); }
    public Image get() {
        if(image==null) return null;
        Image result=image;
        if(this!=last) { unlink();link(); }
        return result;
    }
    private void unlink() {
        if(previous==null) first=next;else previous.next=next;
        if(next==null) last=previous;else next.previous=previous;
        previous=next=null;
    }
    private void link() {
        previous=last;
        if(last==null) first=this;else last.next=this;
        last=this;
    }
    public void clear() {
        if(image==null) return;
        unlink();used-=cost;cost=0;image=null;
    }
    public void prepare(int pixels) {
        clear();
        while(used+pixels>limit && first!=null) first.clear();
    }
    public void store(Image value) {
        int pixels=value.getWidth()*value.getHeight();
        prepare(pixels);
        if(!allows(pixels)) return;
        image=value;cost=pixels;used+=pixels;link();
    }
}
