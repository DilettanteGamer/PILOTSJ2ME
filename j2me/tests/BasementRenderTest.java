package pilots;
import java.nio.file.*;
import javax.microedition.lcdui.Graphics;
import javax.imageio.ImageIO;
import java.io.File;
/** Exact production silhouettes: separate sprite/map clicks and all four gun directions. */
public final class BasementRenderTest {
 static void check(boolean b,String m){if(!b)throw new RuntimeException(m);}
 static void ticks(HotelScene h,int n)throws Exception{for(int i=0;i<n;i++)h.update(100);}
 static void capture(HotelScene h,int width,int height,String path)throws Exception{
  int bar=(width*63+320)/640;Viewport v=new Viewport(width,height-bar,640,417);v.cursorX=440;v.cursorY=240;v.update();
  Graphics g=new Graphics(width,height);h.scene.background.paint(g,v);h.paint(g,v);h.scene.panel.paintPanel(g,width,height,bar);
  ZooPanel p=new ZooPanel(15);try{p.paint(g,h.logic,width,height,bar);}finally{p.close();}ImageIO.write(g.image,"png",new File(path));
 }
 public static void main(String[]args)throws Exception{
  int width=Integer.parseInt(args[0]),height=Integer.parseInt(args[1]);Scene scene=new Scene(15);GameState s=new GameState();s.scene=15;
  try{HotelScene h=new HotelScene(scene,s);HotelWorld w=h.animals.world;ticks(h,80);
   capture(h,width,height,args[2]+"-entry.png");capture(h,640,480,args[2]+"-entry-world.png");
   BalconyRenderTest.label(h,14,"Ящик с автоматом");check(w.select(1),"Colleague select");HotelRenderTest.act(h,14);ticks(h,80);check(w.hasItem(1),"physical gun pickup");
   capture(h,width,height,args[2]+"-gun.png");
   int row=0,max=0;for(String line:Files.readAllLines(Paths.get(args[3]))){String[] p=line.split(",");int[] a=new int[6];for(int i=0;i<6;i++)a[i]=Integer.parseInt(p[i]);
    if(a[1]<0)ticks(h,a[0]);else{
     check(w.select(0)&&w.selectItem(a[2]),"physical selection "+row);int hit=h.targetAt(a[0],a[1]);check(hit==(a[3]<0?0:a[3]),"physical route target "+row+": "+hit+" instead of "+a[3]);
     check(h.request(a[0],a[1]),"physical click "+row);h.update(100);for(int n=0;n<90&&(w.busy()||n<4);n++)h.update(100);
    }
    if(w.g(0x1e1ba8)>max){max=w.g(0x1e1ba8);capture(h,640,480,args[2]+"-height-"+max+"-world.png");}
    row++;
   }
   int limit=250;boolean ladder=false;while(!w.completed&&limit-->0){h.update(100);for(HotelWorld.Thing t:w.cells)if(t!=null&&t.id==15&&t.y<200)ladder=true;}
   check(w.completed&&ladder,"physical basement exit failed");capture(h,width,height,args[2]+"-exit.png");capture(h,640,480,args[2]+"-exit-world.png");
   // Probe each authored direction of sprite 80 without changing any game rule.
   GameState pose=new GameState();pose.scene=15;HotelWorld probe=new HotelWorld(pose);HotelWorld.Thing aim=new HotelWorld.Thing(19,384,304,999);aim.clip=80;aim.layer=10;
   for(int i=0;i<probe.cells.length;i++)probe.cells[i]=null;probe.cells[196]=aim;probe.syncSave();
   HotelScene poses=new HotelScene(scene,pose);HotelWorld.Thing actual=poses.animals.world.cells[196];
   int[] dirs={1,3,5,7};for(int i=0;i<4;i++){actual.direction=dirs[i];poses.refreshState();check(scene.actors[196].clip.index==i,"directional chief sprite "+dirs[i]);}
  }finally{scene.close();}
  System.out.println("BasementRender: real silhouettes, exact crate instances, original gun HUD, four authored aim poses, climb and both exits at "+width+"x"+height+" passed");
 }
}
