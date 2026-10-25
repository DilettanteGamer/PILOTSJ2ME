package pilots;
import java.io.IOException;
import javax.microedition.lcdui.Graphics;

/** Enlarged source panel: original positions, palette and connected wire sprites. */
public final class SwitchboardPuzzle {
    private final HotelWorld world;
    private final IndexedBackground plate;
    private final AnimationClip[] wires=new AnimationClip[27];
    private final int[] ids=new int[27];
    private final Viewport view=new Viewport(128,112,128,112);
    private int left,top,width,height;
    public SwitchboardPuzzle(HotelWorld world) throws IOException {
        this.world=world;plate=new IndexedBackground(getClass().getResourceAsStream("/scenes/13.board"));
        for(int i=0;i<ids.length;i++)ids[i]=-1;
        refresh();
    }
    private void refresh() throws IOException {
        for(int i=0;i<wires.length;i++) {
            HotelWorld.Thing t=world.boardThing(i);int id=t==null?-1:t.clip;
            if(id==ids[i])continue;
            if(wires[i]!=null)wires[i].close();wires[i]=null;ids[i]=id;
            if(id>=0)wires[i]=new AnimationClip("/scenes/13-"+id+".anm");
        }
    }
    public void close() { for(int i=0;i<wires.length;i++)if(wires[i]!=null)wires[i].close(); }
    private int cx(int i) { HotelWorld.Thing t=world.boardThing(i);return t.x+wires[i].offsetX+wires[i].width/2-124; }
    private int cy(int i) { HotelWorld.Thing t=world.boardThing(i);return t.y+wires[i].offsetY+wires[i].height/2-24; }
    public int select(int selected,int dx,int dy) {
        int best=selected,score=2147483647;
        for(int i=0;i<27;i++)if(i!=selected && wires[i]!=null) {
            int x=cx(i)-cx(selected),y=cy(i)-cy(selected),ahead=x*dx+y*dy,side=x*dy-y*dx;
            if(ahead<=0)continue;int distance=ahead*ahead+side*side*4;
            if(distance<score) { score=distance;best=i; }
        }
        return best;
    }
    public int hit(int x,int y) {
        if(x<left || y<top || x>=left+width || y>=top+height)return -1;
        int px=(x-left)*128/width,py=(y-top)*112/height,best=-1,score=2147483647;
        for(int i=0;i<27;i++)if(wires[i]!=null) {
            int dx=px-cx(i),dy=py-cy(i),distance=dx*dx+dy*dy;
            if(distance<score){score=distance;best=i;}
        }
        return best;
    }
    public void paint(Graphics g,int screenWidth,int screenHeight,int selected) throws IOException {
        refresh();width=Math.max(1,Math.min(screenWidth-8,(screenHeight-44)*128/112));height=Math.max(1,width*112/128);
        left=(screenWidth-width)/2;top=17+(screenHeight-44-height)/2;
        view.width=screenWidth;view.height=screenHeight;view.drawWidth=width;view.drawHeight=height;view.left=left;view.top=top;
        view.paintClip(left,top,width,height);plate.paint(g,view);
        if(wires[selected]!=null) {
            HotelWorld.Thing t=world.boardThing(selected);AnimationClip clip=wires[selected];
            int x=view.screenX(t.x+clip.offsetX-125),y=view.screenY(t.y+clip.offsetY-25);
            g.setColor(0xffe46b);g.fillRect(x,y,Math.max(1,(clip.width+2)*width/128),Math.max(1,(clip.height+2)*height/112));
        }
        for(int i=0;i<27;i++)if(wires[i]!=null) {
            HotelWorld.Thing t=world.boardThing(i);wires[i].paint(g,view,t.x-124,t.y-24);
        }
    }
}
