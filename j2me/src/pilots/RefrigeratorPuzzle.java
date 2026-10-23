package pilots;
import javax.microedition.lcdui.Graphics;

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
    public static void paint(Graphics g,int x,int y,int size,int mask,int selected) {
        int cell=size/3;
        for(int i=0;i<9;i++) {
            int left=x+i%3*cell,top=y+i/3*cell;
            g.setColor(i==selected?0xffe46b:0xb6b5a7);g.fillRect(left+1,top+1,cell-2,cell-2);
            g.setColor(0x454838);g.drawRect(left+1,top+1,cell-3,cell-3);
            int cx=left+cell/2,cy=top+cell/2,r=Math.max(2,cell/3),thick=Math.max(2,cell/8);
            g.setColor(0x282d27);
            if((mask&(1<<i))==0) g.fillRect(cx-r,cy-thick/2,2*r+1,thick);
            else g.fillRect(cx-thick/2,cy-r,thick,2*r+1);
            g.setColor(0xf5efc8);g.fillRect(cx-1,cy-1,2,2);
        }
    }
}
