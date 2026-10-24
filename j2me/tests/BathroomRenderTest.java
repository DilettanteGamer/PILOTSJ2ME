package pilots;
import java.lang.reflect.Method;
/** Actual opaque object hits, timed HUD icons and exit rendering in section 14. */
public final class BathroomRenderTest {
 static void check(boolean b,String m){if(!b)throw new RuntimeException(m);}
 static void settle(HotelScene h)throws Exception{int n=3000;while(h.logic.busy()&&n-->0)h.update(100);check(n>0,"bathroom render lock");}
 static void act(HotelScene h,int hero,int item,int id)throws Exception{check(h.logic.select(hero)&&h.logic.selectItem(item),"bathroom render selection");HotelRenderTest.act(h,id);settle(h);}
 static int icon(HotelScene h)throws Exception{ZooPanel panel=new ZooPanel(12);try{Method m=ZooPanel.class.getDeclaredMethod("itemIcon",ZooLogic.class,int.class,boolean.class);m.setAccessible(true);return ((Integer)m.invoke(panel,h.logic,1,true)).intValue();}finally{panel.close();}}
 public static void main(String[] args)throws Exception{
  int width=Integer.parseInt(args[0]),height=Integer.parseInt(args[1]);Scene scene=new Scene(12);GameState s=new GameState();s.scene=12;
  try{HotelScene h=new HotelScene(scene,s);settle(h);
   BalconyRenderTest.capture(h,640,480,args[2]+"-world.png");BalconyRenderTest.capture(h,width,height,args[2]+"-entry.png");
   BalconyRenderTest.label(h,21,"Вантуз");BalconyRenderTest.label(h,17,"Мыло");BalconyRenderTest.label(h,20,"Шнурок");BalconyRenderTest.label(h,18,"Кран");
   act(h,0,0,21);act(h,1,0,17);check(h.logic.hasItem(1)&&h.logic.hasItem(2)&&icon(h)==6,"bathroom dry inventory");
   BalconyRenderTest.capture(h,width,height,args[2]+"-inventory.png");
   act(h,0,0,18);act(h,1,2,28);act(h,0,1,26);act(h,1,0,20);act(h,0,0,25);
   check(icon(h)==13,"first timed plunger icon");BalconyRenderTest.capture(h,width,height,args[2]+"-plunger.png");
   byte[] prepared=s.encode();while(h.animals.world.state.variables[54]<200)h.update(100);check(icon(h)==15,"second timed plunger icon");
   while(h.animals.world.state.variables[54]!=0)h.update(100);check(icon(h)==6,"expired plunger icon");
   h=new HotelScene(scene,GameState.decode(prepared,13));act(h,0,1,15);check(h.logic.completed(),"bathroom physical exit");
   BalconyRenderTest.capture(h,640,480,args[2]+"-exit-world.png");
  }finally{scene.close();}
  System.out.println("BathroomRender: source hotspots/labels, soap/plunger HUD phases, cat/bath cooperation and both exits at "+width+"x"+height+" passed");
 }
}
