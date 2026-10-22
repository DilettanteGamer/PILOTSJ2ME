package pilots;

import java.io.IOException;
import javax.microedition.lcdui.Graphics;

/** Original panel cells: Colleague, Chief, hand, kettle, pickaxe, coin and menu. */
public final class ZooPanel {
    private static final int[] IDS={20004,20005,20006,20007,20008,20009,77,78,79,80,81,82,20044};
    private final AnimationClip[] icons=new AnimationClip[IDS.length];
    private Viewport view;
    public ZooPanel() throws IOException { this(false); }
    public ZooPanel(boolean animals) throws IOException {
        try {
            for(int i=0;i<IDS.length;i++) {
                int id=animals&&i>=6&&i<=11?(i<8?62:i<10?60:64)+(i%2==0?1:0):IDS[i];
                icons[i]=new AnimationClip((animals?"/scenes/04-":"/scenes/02-")+id+".anm");
            }
        } catch(IOException failure) { close();throw failure; }
    }
    public void close() { for(int i=0;i<icons.length;i++) if(icons[i]!=null) icons[i].close(); }
    public static int slotAt(int x,int y,int width,int height,int panelHeight) {
        if(width<1 || x<0 || x>=width || y<height-panelHeight || y>=height) return -1;
        int slot=(x*640/width)/64;
        return slot==0 || slot==1 || slot==3 || slot==4 || slot==5 || slot==6 || slot==9 ? slot : -1;
    }
    public void paint(Graphics graphics,ZooLogic logic,int width,int height,int panelHeight) {
        if(panelHeight<1) return;
        if(view==null) view=new Viewport(width,height,640,63);
        view.width=width;view.height=height;view.worldWidth=640;view.worldHeight=63;
        view.drawWidth=width;view.drawHeight=panelHeight;view.left=0;view.top=height-panelHeight;
        icons[logic.hero()==1?0:1].paint(graphics,view,0,31);
        icons[logic.hero()==0?2:3].paint(graphics,view,64,31);
        icons[logic.selectedItem()==0?4:5].paint(graphics,view,192,31);
        for(int item=1;item<=3;item++) if(logic.hasItem(item))
            icons[6+(item-1)*2+(logic.selectedItem()==item?0:1)].paint(graphics,view,(item+3)*64,31);
        icons[12].paint(graphics,view,576,31);
        graphics.setColor(0xE8D43F);
        int[] selected={logic.hero()==1?0:1,logic.selectedItem()==0?3:logic.selectedItem()+3};
        for(int i=0;i<selected.length;i++) graphics.drawRect(selected[i]*width/10+1,height-panelHeight,Math.max(1,width/10-3),Math.max(1,panelHeight-1));
    }
}
