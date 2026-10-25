package pilots;

import java.io.IOException;
import javax.microedition.lcdui.Graphics;

/** Original panel cells: Colleague, Chief, hand, kettle, pickaxe, coin and menu. */
public final class ZooPanel {
    private static final int[] IDS={20004,20005,20006,20007,20008,20009,77,78,79,80,81,82,20044};
    private final AnimationClip[] icons=new AnimationClip[IDS.length+4];
    private Viewport view;
    private final int sceneId;
    public ZooPanel() throws IOException { this(false); }
    public ZooPanel(boolean animals) throws IOException { this(animals?4:2); }
    public ZooPanel(int scene) throws IOException {
        sceneId=scene;boolean animals=scene==4;
        try {
            for(int i=0;i<IDS.length;i++) {
                int id=scene==17 && i>=4 && i<=11?(i<6?94+i%2:i<8?96+i%2:i<10?99+i%2:101+i%2):scene==14 && i>=6 && i<=11?(i<8?51-i%2:i<10?53-i%2:86-i%2):scene==15 && i>=6 && i<=11?(i%2==0?83:84):scene==12 && i>=6 && i<=11?(i<8?(i%2==0?52:53):(i%2==0?79:80)):scene>=10 && i>=6 && i<=11?(i%2==0?47:48):scene==9 && i>=6 && i<=11?(i<8?(i%2==0?75:78):i<10?(i%2==0?74:77):(i%2==0?76:79)):scene==6 && i>=6 && i<=11?(i%2==0?47:48):scene==5 && i>=6 && i<=11?81:animals&&i>=6&&i<=11?(i<8?62:i<10?60:64)+(i%2==0?1:0):IDS[i];
                icons[i]=new AnimationClip((scene==17?"/scenes/17-":scene==14?"/scenes/14-":scene==15?"/scenes/15-":scene==13?"/scenes/13-":scene==12?"/scenes/12-":scene==11?"/scenes/11-":scene==10?"/scenes/10-":scene==9?"/scenes/09-":scene==6?"/scenes/06-":scene==5?"/scenes/05-":animals?"/scenes/04-":"/scenes/02-")+id+".anm");
            }
            if(scene==17)for(int i=0;i<4;i++)icons[13+i]=new AnimationClip("/scenes/17-"+(i<2?103+i:99+i-2)+".anm");
            if(scene==14){icons[13]=new AnimationClip("/scenes/14-55.anm");icons[14]=new AnimationClip("/scenes/14-54.anm");}
            if(scene==12)for(int i=0;i<4;i++)icons[13+i]=new AnimationClip("/scenes/12-"+(54+i)+".anm");
        } catch(IOException failure) { close();throw failure; }
    }
    public void close() { for(int i=0;i<icons.length;i++) if(icons[i]!=null) icons[i].close(); }
    public static int slotAt(int x,int y,int width,int height,int panelHeight) {
        if(width<1 || x<0 || x>=width || y<height-panelHeight || y>=height) return -1;
        int slot=(x*640/width)/64;
        return slot==0 || slot==1 || slot==3 || slot==4 || slot==5 || slot==6 || slot==7 || slot==8 || slot==9 ? slot : -1;
    }
    public void paint(Graphics graphics,ZooLogic logic,int width,int height,int panelHeight) {
        if(panelHeight<1) return;
        if(view==null) view=new Viewport(width,height,640,63);
        view.width=width;view.height=height;view.worldWidth=640;view.worldHeight=63;
        view.drawWidth=width;view.drawHeight=panelHeight;view.left=0;view.top=height-panelHeight;
        view.paintClip(0,0,width,height);
        icons[logic.hero()==1?0:1].paint(graphics,view,0,31);
        icons[logic.hero()==0?2:3].paint(graphics,view,64,31);
        icons[logic.selectedItem()==0?4:5].paint(graphics,view,192,31);
        for(int item=1;item<=(sceneId==17?5:sceneId==14?4:3);item++) if(logic.hasItem(item))
            icons[itemIcon(logic,item,logic.selectedItem()==item)].paint(graphics,view,(item+3)*64,31);
        icons[12].paint(graphics,view,576,31);
        graphics.setColor(0xE8D43F);
        outline(graphics,icons[logic.hero()==1?0:2],logic.hero()==1?0:64);
        int item=logic.selectedItem();
        outline(graphics,icons[item==0?4:itemIcon(logic,item,true)],item==0?192:(item+3)*64);
    }
    private int itemIcon(ZooLogic logic,int item,boolean selected) {
        if(sceneId==17 && item>=4)return 13+(item-4)*2+(selected?0:1);
        if(sceneId==14 && item==4)return selected?13:14;
        if(item==1 && logic instanceof HotelLogic) {
            HotelWorld world=((HotelLogic)logic).world;
            if(world.scene==12) { int timer=world.g(0x1e1b54);if(timer>0 && timer<400)return (timer<200?13:15)+(selected?0:1); }
        }
        return 6+(item-1)*2+(selected?0:1);
    }
    private void outline(Graphics graphics,AnimationClip icon,int x) {
        int left=view.screenX(x+icon.offsetX),top=view.screenY(31+icon.offsetY);
        int right=view.screenX(x+icon.offsetX+icon.width),bottom=view.screenY(31+icon.offsetY+icon.height);
        graphics.drawRect(left,top,Math.max(0,right-left-1),Math.max(0,bottom-top-1));
    }
}
