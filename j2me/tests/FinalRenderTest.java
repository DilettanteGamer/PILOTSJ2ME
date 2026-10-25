package pilots;
import java.io.File;import javax.imageio.ImageIO;import javax.microedition.lcdui.Graphics;
/** Production opaque hotspots and complete final sequence through HotelScene. */
public final class FinalRenderTest implements FinalTestRoute.Driver {
 final HotelScene h;final Scene scene;final ZooPanel panel;final int width,height,bar;final String out;
 FinalRenderTest(int w,int height,String out)throws Exception{this.width=w;this.height=height;this.out=out;bar=(w*63+320)/640;GameState s=new GameState();s.scene=17;scene=new Scene(17);h=new HotelScene(scene,s);panel=new ZooPanel(17);}
 public HotelWorld world(){return h.animals.world;}
 public void tick()throws Exception{h.update(100);
  int chief=0,partner=0;for(HotelWorld.Thing t:world().cells)if(t!=null){if(t.id==25)chief++;if(t.id==24)partner++;}
  FinalTestRoute.check(chief<=1&&partner<=1,"duplicate final hero after physical click");
 }
 public void act(int id)throws Exception{
  int x=-1,y=-1;
  for(int yy=0;yy<417&&x<0;yy++)for(int xx=0;xx<640;xx++)if(h.targetAt(xx,yy)==id){x=xx;y=yy;break;}
  FinalTestRoute.check(x>=0,"final opaque hotspot "+id);FinalTestRoute.check(h.hoverText(x,y)!=null,"final hover "+id);
  FinalTestRoute.check(h.request(x,y),"final scene action "+id);
 }
 public void move(int x,int y)throws Exception{FinalTestRoute.check(h.request(x,y),"final point movement");}
 public void milestone(String name)throws Exception{
  Viewport v=new Viewport(width,height-bar,640,417);v.cursorX=h.animals.heroX(world().hero());v.cursorY=h.animals.heroY(world().hero());if(world().completed){v.cursorX=380;v.cursorY=340;}v.update();Graphics g=new Graphics(width,height);scene.background.paint(g,v);h.paint(g,v);panel.paint(g,h.animals,width,height,bar);ImageIO.write(g.image,"png",new File(out+"-"+name+".png"));
 }
 public static void main(String[] a)throws Exception{
  FinalRenderTest test=new FinalRenderTest(Integer.parseInt(a[0]),Integer.parseInt(a[1]),a[2]);
  try{FinalTestRoute.play(test);FinalTestRoute.check(test.world().caption().indexOf("Игра пройдена")>=0,"final caption");}
  finally{test.panel.close();test.scene.close();}
  System.out.println("FinalRender: original opaque hotspots/labels, five inventory slots, tree planes, cooperative puzzle and full source ending at "+a[0]+"x"+a[1]+" passed");
 }
}
