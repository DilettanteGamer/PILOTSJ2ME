package pilots;
import java.io.File;
import javax.imageio.ImageIO;
import javax.microedition.lcdui.Graphics;
/** Uses opaque production hotspots and renderer throughout the original puzzle. */
public final class BalconyRenderTest {
 static void check(boolean b,String s){if(!b)throw new RuntimeException(s);}
 static void settle(HotelScene h)throws Exception{int n=1800;while(h.logic.busy()&&n-->0)h.update(100);check(n>0,"balcony render hangs");}
 static void capture(HotelScene h,int width,int height,String path)throws Exception {
  int bar=(width*63+320)/640;Viewport v=new Viewport(width,height-bar,640,417);v.cursorX=352;v.cursorY=240;v.update();
  Graphics g=new Graphics(width,height);h.scene.background.paint(g,v);h.paint(g,v);h.scene.panel.paintPanel(g,width,height,bar);
  ZooPanel p=new ZooPanel(10);try{p.paint(g,h.logic,width,height,bar);}finally{p.close();}
  ImageIO.write(g.image,"png",new File(path));
 }
 static void act(HotelScene h,int hero,int item,int target)throws Exception {
  check(h.logic.select(hero)&&h.logic.selectItem(item),"balcony selection");HotelRenderTest.act(h,target);settle(h);
 }
 public static void main(String[] args)throws Exception {
  int width=Integer.parseInt(args[0]),height=Integer.parseInt(args[1]);Scene scene=new Scene(10);GameState state=new GameState();state.scene=10;
  try{HotelScene h=new HotelScene(scene,state);settle(h);capture(h,width,height,args[2]+"-room.png");capture(h,640,480,args[2]+"-world.png");
   act(h,0,0,23);act(h,0,0,17);act(h,1,0,28);capture(h,width,height,args[2]+"-inventory.png");
   act(h,0,0,30);act(h,1,0,29);act(h,1,0,16);capture(h,width,height,args[2]+"-balcony.png");
   act(h,0,0,16);act(h,0,1,38);act(h,0,1,38);act(h,0,0,16);
   check(h.logic.completed(),"physical balcony quest did not finish");capture(h,width,height,args[2]+"-finale.png");
  }finally{scene.close();}
  System.out.println("BalconyRender: production hotspots, source poses, inventory and complete balcony puzzle at "+width+"x"+height+" passed");
 }
}
