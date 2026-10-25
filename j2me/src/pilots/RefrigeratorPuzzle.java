package pilots;
import javax.microedition.lcdui.Graphics;
import java.io.IOException;

/** Nine readable handles. A move flips its row and column, counting their intersection once. */
public final class RefrigeratorPuzzle {
    public static int turn(int mask,int index) {
        if(index<0 || index>=9) throw new IllegalArgumentException();
        for(int i=0;i<9;i++) if(i/3==index/3 || i%3==index%3) mask^=1<<i;
        return mask&511;
    }
    public static int initial(int seed) {
        int mask=0,moves=(seed^(seed>>>9))&511;
        for(int i=0;i<9;i++) if((moves&(1<<i))!=0) mask=turn(mask,i);
        return mask==0?turn(0,4):mask;
    }
    private final AnimationClip horizontal,vertical;
    private final Viewport view=new Viewport(1,1,1,1);
    public RefrigeratorPuzzle() throws IOException {
        horizontal=new AnimationClip("/scenes/09-116.anm");
        vertical=new AnimationClip("/scenes/09-132.anm");
    }
    public void paint(Graphics g,int x,int y,int size,int mask,int selected) {
        int cell=size/3;
        // Original metal plate and handles, enlarged with nearest-neighbour pixels.
        g.setColor(0xb6b6b6);g.fillRect(x,y,size,size);
        view.width=x+size;view.height=y+size;view.worldWidth=view.worldHeight=48;
        view.drawWidth=view.drawHeight=size;view.left=x;view.top=y;
        view.paintClip(x,y,size,size);
        for(int i=0;i<9;i++) {
            int left=x+i%3*cell,top=y+i/3*cell;
            if(i==selected) { g.setColor(0xffe46b);g.fillRect(left,top,cell,cell); }
            AnimationClip clip=(mask&(1<<i))==0?horizontal:vertical;
            clip.paint(g,view,i%3*16+8-clip.width/2-clip.offsetX,i/3*16+8-clip.height/2-clip.offsetY);
        }
    }
}
