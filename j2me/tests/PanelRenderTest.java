package pilots;

import javax.microedition.lcdui.Graphics;
import java.io.File;
import javax.imageio.ImageIO;

public final class PanelRenderTest {
    private static void check(boolean ok,String message) { if(!ok) throw new RuntimeException(message); }
    public static void main(String[] args) throws Exception {
        int width=Integer.parseInt(args[0]),height=Integer.parseInt(args[1]);
        int panelHeight=(width*63+320)/640;
        Scene scene=new Scene(2); ZooPanel panel=new ZooPanel();
        try {
            GameState state=new GameState();state.scene=2;ZooLogic logic=new ZooLogic(state);
            Graphics baseline=new Graphics(width,height),painted=new Graphics(width,height);
            scene.panel.paintPanel(baseline,width,height,panelHeight);
            scene.panel.paintPanel(painted,width,height,panelHeight);
            Graphics empty=new Graphics(width,height);
            scene.panel.paintPanel(empty,width,height,panelHeight);
            panel.paint(empty,logic,width,height,panelHeight);
            for(int slot=4;slot<=6;slot++) for(int x=slot*width/10;x<(slot+1)*width/10;x++)
                for(int y=height-panelHeight;y<height;y++)
                    check(empty.image.getRGB(x,y)==baseline.image.getRGB(x,y),"unowned item is not drawn");
            state.inventory[0]=state.inventory[1]=state.inventory[2]=true;logic.selectItem(3);
            panel.paint(painted,logic,width,height,panelHeight);
            int[] selectedRoots={64,384};
            for(int root:selectedRoots) {
                int left=(root+2)*width/640,top=height-panelHeight+4*panelHeight/63;
                int right=(root+61)*width/640-1,bottom=height-panelHeight+59*panelHeight/63-1;
                check((painted.image.getRGB(left,top)&0xffffff)==0xE8D43F,"selection top-left follows sprite frame");
                check((painted.image.getRGB(right,bottom)&0xffffff)==0xE8D43F,"selection bottom-right follows sprite frame");
                if(top>height-panelHeight) check((painted.image.getRGB(left,height-panelHeight)&0xffffff)!=0xE8D43F,"selection must not touch panel edge");
            }
            int[] slots={0,1,3,4,5,6,9};
            for(int index=0;index<slots.length;index++) {
                int slot=slots[index],x=(slot*64+31)*width/640;
                check(ZooPanel.slotAt(x,height-1,width,height,panelHeight)==slot,"cell hit mapping");
                int changed=0;
                for(int sx=slot*width/10;sx<(slot+1)*width/10;sx++)
                    for(int y=height-panelHeight;y<height;y++)
                        if(painted.image.getRGB(sx,y)!=baseline.image.getRGB(sx,y)) changed++;
                check(changed>=3,"missing icon in original panel cell "+slot);
            }
            for(int y=0;y<height-panelHeight;y++) for(int x=0;x<width;x++)
                check(painted.image.getRGB(x,y)==baseline.image.getRGB(x,y),"panel icons must not paint over scene");
            check(ZooPanel.slotAt(1,height-panelHeight-1,width,height,panelHeight)==-1,"scene click cannot select panel");
            check(ZooPanel.slotAt(width/4,height-1,width,height,panelHeight)==-1,"panel gap is inert");
            ImageIO.write(painted.image.getSubimage(0,height-panelHeight,width,panelHeight),"png",new File(args[2]));
        } finally { panel.close();scene.close(); }
        System.out.println("Panel: production icon rendering and cell selection passed at "+width+"x"+height);
    }
}
