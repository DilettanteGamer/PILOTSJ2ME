package pilots;
import java.io.File;
import javax.imageio.ImageIO;
import javax.microedition.lcdui.Graphics;
/** Actual hotel actor placement, input silhouettes and small-screen rendering. */
public final class HotelRenderTest {
 static void check(boolean b,String s){if(!b)throw new RuntimeException(s);}
 static void capture(HotelScene hotel,int width,int height,String path)throws Exception {
  int bar=(width*63+320)/640;Viewport view=new Viewport(width,height-bar,640,417);view.cursorX=320;view.cursorY=288;view.update();
  Graphics g=new Graphics(width,height);hotel.scene.background.paint(g,view);hotel.paint(g,view);hotel.scene.panel.paintPanel(g,width,height,bar);
  if(hotel.animals.world.scene==9){ZooPanel panel=new ZooPanel(9);try{panel.paint(g,hotel.logic,width,height,bar);}finally{panel.close();}}
  ImageIO.write(g.image,"png",new File(path));
 }
 static void noKeyHit(HotelScene hotel) {
  for(int i=0;i<300;i++){HotelWorld.Thing t=hotel.animals.world.cells[i];AnimatedActor a=hotel.scene.actors[i];if(t==null||t.id!=34||a==null)continue;
   for(int y=a.y+a.clip.offsetY;y<a.y+a.clip.offsetY+a.clip.height;y++)for(int x=a.x+a.clip.offsetX;x<a.x+a.clip.offsetX+a.clip.width;x++)check(hotel.targetAt(x,y)!=34,"key selected through occluder");
  }
 }
 static void act(HotelScene hotel,int id)throws Exception {
  if(id==101){for(int y=195;y<261;y++)for(int x=434;x<500;x++)if(hotel.targetAt(x,y)==101){check(hotel.request(x,y),"detail hotspot");return;}throw new RuntimeException("Missing refrigerator detail hotspot");}
  for(int i=0;i<302;i++) {
   HotelWorld.Thing t=i<300?hotel.animals.world.cells[i]:hotel.animals.world.heroes[i-300];AnimatedActor a=hotel.scene.actors[i];
   if(t==null||t.id!=id||a==null)continue;
   for(int y=Math.max(0,a.y+a.clip.offsetY);y<Math.min(417,a.y+a.clip.offsetY+a.clip.height);y++)for(int x=Math.max(0,a.x+a.clip.offsetX);x<Math.min(640,a.x+a.clip.offsetX+a.clip.width);x++)
    if(hotel.targetAt(x,y)==id){check(hotel.hoverText(x,y)!=null&&hotel.request(x,y),"hotel physical input "+id);return;}
  }
  throw new RuntimeException("Missing hotel hotspot "+id);
 }
 static void settle(HotelScene h)throws Exception {int n=1500;while(h.logic.busy()&&n-->0)h.update(100);check(n>0,"hotel render hangs");}
 public static void main(String[] a)throws Exception {
  int width=Integer.parseInt(a[0]),height=Integer.parseInt(a[1]);
  for(int n=8;n<=9;n++){Scene scene=new Scene(n);GameState state=new GameState();state.scene=n;
   try{HotelScene hotel=new HotelScene(scene,state);if(n==8){for(int i=0;i<110;i++)hotel.update(100);capture(hotel,width,height,a[2]+"-entrance.png");settle(hotel);}
   else{settle(hotel);capture(hotel,width,height,a[2]+"-room.png");capture(hotel,640,480,a[2]+"-world.png");
    noKeyHit(hotel);
    act(hotel,26);settle(hotel);check(hotel.logic.hasItem(1),"cream pickup");hotel.logic.select(1);hotel.logic.selectItem(1);act(hotel,29);settle(hotel);
    noKeyHit(hotel);hotel.logic.select(0);act(hotel,27);settle(hotel);hotel.logic.select(1);hotel.logic.selectItem(0);act(hotel,34);settle(hotel);check(hotel.logic.hasItem(2),"key pickup");hotel.logic.select(1);act(hotel,31);settle(hotel);act(hotel,25);settle(hotel);check(hotel.logic.hasItem(3),"screwdriver pickup");capture(hotel,width,height,a[2]+"-inventory.png");
    hotel.logic.select(0);hotel.logic.selectItem(2);act(hotel,33);settle(hotel);hotel.logic.selectItem(0);act(hotel,101);settle(hotel);
    check(hotel.animals.world.fridgeRequested(),"detail request did not finish approach");
    System.setProperty("pilots.test.width",String.valueOf(width));System.setProperty("pilots.test.height",String.valueOf(height));
    final HotelWorld world=hotel.animals.world;
    FridgeCanvas detail=new FridgeCanvas(world,new FridgeCanvas.Host(){public void back(){}public void active(boolean value){}});
    Graphics detailGraphics=new Graphics(width,height);detail.paint(detailGraphics);ImageIO.write(detailGraphics.image,"png",new File(a[2]+"-detail.png"));
    int mask=world.fridgeMask(),solution=-1;for(int c=0;c<512;c++){int value=mask;for(int i=0;i<9;i++)if((c&(1<<i))!=0)value=RefrigeratorPuzzle.turn(value,i);if(value==0){solution=c;break;}}
    check(solution>=0,"render puzzle solution");for(int i=0;i<9;i++)if((solution&(1<<i))!=0)world.turnFridge(i);
    detailGraphics=new Graphics(width,height);detail.paint(detailGraphics);ImageIO.write(detailGraphics.image,"png",new File(a[2]+"-detail-solved.png"));detail.input(48);
    for(int i=0;i<200;i++)hotel.update(100);settle(hotel);
    capture(hotel,640,480,a[2]+"-solved-world.png");act(hotel,35);for(int i=0;i<65;i++)hotel.update(100);capture(hotel,width,height,a[2]+"-finale.png");settle(hotel);check(hotel.logic.completed(),"physical hotel finale");}}
   finally{scene.close();}
  }
  System.out.println("HotelRender: entrance, room, source layers, opaque pickup hotspots and inventory painted");
 }
}
