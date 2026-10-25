package pilots;
import java.io.File;import javax.imageio.ImageIO;import javax.microedition.lcdui.Graphics;
/** Production opaque hotspots and complete final sequence through HotelScene. */
public final class FinalRenderTest implements FinalTestRoute.Driver {
 final HotelScene h;final Scene scene;final ZooPanel panel;final int width,height,bar;final String out;boolean upperBend;int endpointClicks;
 FinalRenderTest(int w,int height,String out)throws Exception{this.width=w;this.height=height;this.out=out;bar=(w*63+320)/640;GameState s=new GameState();s.scene=17;scene=new Scene(17);h=new HotelScene(scene,s);panel=new ZooPanel(17);}
 public HotelWorld world(){return h.animals.world;}
 public void tick()throws Exception{h.update(100);
  int chief=0,partner=0;for(HotelWorld.Thing t:world().cells)if(t!=null){if(t.id==25)chief++;if(t.id==24)partner++;}
  FinalTestRoute.check(chief<=1&&partner<=1,"duplicate final hero after physical click");
  if(world().flag(0x1e1b57)==4)for(int i=0;i<300;i++){
   HotelWorld.Thing t=world().cells[i];AnimatedActor a=scene.actors[i];
   if(t!=null&&t.id==25&&a!=null&&t.clip>=20&&t.clip<=33&&a.x>=320&&a.x<=448&&t.y==64){
    FinalTestRoute.check(a.y==64+(64-Math.abs(a.x-384))/4,"Chief above supporting upper bough");
    if(a.x==384){upperBend=true;milestone("upper-bend");}
   }
  }
 }
 public void act(int id)throws Exception{
  if(id==36||id==37){int bx=id==36?576:320,by=id==36?224:64;FinalTestRoute.check("Конец ветки".equals(h.hoverText(bx+44,by+26))&&h.targetAt(bx+44,by+26)==102,"upper branch endpoint label "+id);}
  int x=-1,y=-1;
  for(int yy=0;yy<417&&x<0;yy++)for(int xx=0;xx<640;xx++)if(h.targetAt(xx,yy)==id){x=xx;y=yy;break;}
  FinalTestRoute.check(x>=0,"final opaque hotspot "+id);FinalTestRoute.check(h.hoverText(x,y)!=null,"final hover "+id);
  FinalTestRoute.check(h.request(x,y),"final scene action "+id);
 }
 public void move(int x,int y)throws Exception{
  if((x==192&&y==272 || x==0&&y==240) && "Конец ветки".equals(h.hoverText(x+44,y+26))){
   FinalTestRoute.check(h.targetAt(x+44,y+26)==102,"branch end enlarged hotspot");
   endpointClicks++;x+=44;y+=26;
  }
  FinalTestRoute.check(h.request(x,y),"final point movement");
 }
 public void milestone(String name)throws Exception{
  Viewport v=new Viewport(width,height-bar,640,417);v.cursorX=h.animals.heroX(world().hero());v.cursorY=h.animals.heroY(world().hero());if(world().completed){v.cursorX=380;v.cursorY=340;}v.update();Graphics g=new Graphics(width,height);scene.background.paint(g,v);h.paint(g,v);panel.paint(g,h.animals,width,height,bar);ImageIO.write(g.image,"png",new File(out+"-"+name+".png"));
 }
 public static void main(String[] a)throws Exception{
  FinalRenderTest test=new FinalRenderTest(Integer.parseInt(a[0]),Integer.parseInt(a[1]),a[2]);
  try{FinalTestRoute.play(test);FinalTestRoute.check(test.upperBend&&test.endpointClicks>=3,"branch markers/bend not exercised");FinalTestRoute.check(test.world().caption().indexOf("Игра пройдена")>=0,"final caption");}
  finally{test.panel.close();test.scene.close();}
  System.out.println("FinalRender: enlarged labelled branch ends, supported upper bend, original opaque hotspots/labels, five inventory slots, cooperative puzzle and full source ending at "+a[0]+"x"+a[1]+" passed");
 }
}
