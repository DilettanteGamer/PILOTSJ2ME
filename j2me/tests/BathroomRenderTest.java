package pilots;
import java.lang.reflect.Method;
import javax.microedition.lcdui.Graphics;
/** Actual opaque object hits, timed HUD icons and exit rendering in section 14. */
public final class BathroomRenderTest {
 static void check(boolean b,String m){if(!b)throw new RuntimeException(m);}
 static void settle(HotelScene h)throws Exception{int n=3000;while(h.logic.busy()&&n-->0)h.update(100);check(n>0,"bathroom render lock");}
 static void act(HotelScene h,int hero,int item,int id)throws Exception{check(h.logic.select(hero)&&h.logic.selectItem(item),"bathroom render selection");HotelRenderTest.act(h,id);settle(h);}
 static int icon(HotelScene h)throws Exception{ZooPanel panel=new ZooPanel(12);try{Method m=ZooPanel.class.getDeclaredMethod("itemIcon",ZooLogic.class,int.class,boolean.class);m.setAccessible(true);return ((Integer)m.invoke(panel,h.logic,1,true)).intValue();}finally{panel.close();}}
 static void soapMovie(HotelScene h,int expected)throws Exception {
  check(h.logic.select(1)&&h.logic.selectItem(2),"soap movie selection");HotelRenderTest.act(h,28);
  int frames=0,hits=0,limit=3000;
  while(h.logic.busy()&&limit-->0) {
   if(h.animals.world.action()==expected) {
    frames++;AnimatedActor a=h.scene.actors[301];
    if(a!=null)for(int y=Math.max(0,a.y+a.clip.offsetY);y<Math.min(417,a.y+a.clip.offsetY+a.clip.height);y+=4)
     for(int x=Math.max(0,a.x+a.clip.offsetX);x<Math.min(640,a.x+a.clip.offsetX+a.clip.width);x+=4)
      if(h.targetAt(x,y)==11){check(h.hoverText(x,y)==null,"Colleague hover caption on soap movie");hits++;}
   }
   h.update(100);
  }
  check(limit>0&&frames>0&&hits>0,"soap movie regression did not reach visible Colleague "+expected);
 }
 static void sinkPassage(int width,int height,String path)throws Exception {
  Scene scene=new Scene(12);GameState s=new GameState();s.scene=12;
  try {HotelScene h=new HotelScene(scene,s);settle(h);int slot=HotelRenderTest.slot(h,31);
   check(h.layer(slot)==26,"sink must retain its source foreground plane");
   for(int hero=0;hero<2;hero++) {
    check(h.logic.select(hero),"sink walker selection");
    check(!h.animals.world.move(192,320)&&!h.animals.world.move(256,320),"free walk entered sink clearance");
    check(h.animals.world.move(192,336),"nearest safe sink approach");int limit=1000;
    while(h.logic.busy()&&limit-->0) {
     h.update(100);HotelWorld.Thing t=h.animals.world.heroes[hero];
     check(!(t.x>=192&&t.x<=256&&t.y<336),"route crossed pedestal clearance");
    }
    check(limit>0,"sink aisle route did not settle");
    AnimatedActor actor=scene.actors[300+hero],bowl=scene.actors[slot];
    check(h.layer(300+hero)<h.layer(slot),"safe approach hidden by sink");
    Graphics actual=HotelRenderTest.worldPaint(h),expected=new Graphics(640,417);Viewport v=new Viewport(640,417,640,417);
    bowl.paint(expected,v);actor.paint(expected,v);
    for(int py=bowl.y+bowl.clip.offsetY;py<bowl.y+bowl.clip.offsetY+bowl.clip.height;py++)
     for(int px=bowl.x+bowl.clip.offsetX;px<bowl.x+bowl.clip.offsetX+bowl.clip.width;px++)
      if(bowl.clip.contains(px-bowl.x,py-bowl.y)&&actor.clip.contains(px-actor.x,py-actor.y)) {
       check(actual.image.getRGB(px,py)==expected.image.getRGB(px,py),"safe sink foreground pixels");
      }
    // The safe pose can clear the bowl entirely; layer ordering still matters
    // for the authored wash pose, which deliberately reaches towards it.
    BalconyRenderTest.capture(h,640,480,path+"-sink-"+hero+"-world.png");
    BalconyRenderTest.capture(h,width,height,path+"-sink-"+hero+".png");
    check(h.animals.world.move(hero==0?512:448,368),"leave sink aisle");settle(h);
   }
   check(h.logic.select(1)&&h.logic.selectItem(0),"wash selection");HotelRenderTest.act(h,31);int frames=0,limit=3000;
   while(h.logic.busy()&&limit-->0) {
    if(h.animals.world.heroes[1].clip==95) {
     check(h.layer(301)<h.layer(slot),"authored wash hidden by bowl");
     if(frames++==20)BalconyRenderTest.capture(h,640,480,path+"-washing-world.png");
    }
    h.update(100);
   }
   check(limit>0&&frames>0&&h.animals.world.heroes[1].y>=336,"Colleague did not step back after washing");
   BalconyRenderTest.capture(h,640,480,path+"-washed-world.png");
  }finally{scene.close();}
 }
 public static void main(String[] args)throws Exception{
  int width=Integer.parseInt(args[0]),height=Integer.parseInt(args[1]);Scene scene=new Scene(12);GameState s=new GameState();s.scene=12;
  sinkPassage(width,height,args[2]);
  try{HotelScene h=new HotelScene(scene,s);settle(h);
   BalconyRenderTest.capture(h,640,480,args[2]+"-world.png");BalconyRenderTest.capture(h,width,height,args[2]+"-entry.png");
   BalconyRenderTest.label(h,21,"Вантуз");BalconyRenderTest.label(h,17,"Мыло");BalconyRenderTest.label(h,20,"Шнурок");BalconyRenderTest.label(h,18,"Кран");
   act(h,0,0,21);act(h,1,0,17);check(h.logic.hasItem(1)&&h.logic.hasItem(2)&&icon(h)==6,"bathroom dry inventory");
   BalconyRenderTest.capture(h,width,height,args[2]+"-inventory.png");
   soapMovie(h,96);check(h.logic.hasItem(2),"failed soap attempt consumed soap");
   act(h,0,0,18);soapMovie(h,95);act(h,0,1,26);act(h,1,0,20);act(h,0,0,25);
   check(icon(h)==13,"first timed plunger icon");BalconyRenderTest.capture(h,width,height,args[2]+"-plunger.png");
   byte[] prepared=s.encode();while(h.animals.world.state.variables[54]<200)h.update(100);check(icon(h)==15,"second timed plunger icon");
   while(h.animals.world.state.variables[54]!=0)h.update(100);check(icon(h)==6,"expired plunger icon");
   h=new HotelScene(scene,GameState.decode(prepared,13));act(h,0,1,15);check(h.logic.completed(),"bathroom physical exit");
   BalconyRenderTest.capture(h,640,480,args[2]+"-exit-world.png");
  }finally{scene.close();}
  System.out.println("BathroomRender: source hotspots/labels, soap/plunger HUD phases, cat/bath cooperation and both exits at "+width+"x"+height+" passed");
 }
}
