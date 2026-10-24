package pilots;
/** Physical hotspots, source occlusion and HUD in the section-13 ledge scene. */
public final class FacadeRenderTest {
 static void check(boolean b,String m){if(!b)throw new RuntimeException(m);}
 static void settle(HotelScene h)throws Exception{int n=2000;while(h.logic.busy()&&n-->0)h.update(100);check(n>0,"ledge render hangs");}
 static void act(HotelScene h,int hero,int item,int target)throws Exception{check(h.logic.select(hero)&&h.logic.selectItem(item),"ledge select");HotelRenderTest.act(h,target);settle(h);}
 public static void main(String[] args)throws Exception{
  int width=Integer.parseInt(args[0]),height=Integer.parseInt(args[1]);Scene scene=new Scene(11);GameState s=new GameState();s.scene=11;
  try{HotelScene h=new HotelScene(scene,s);
   for(int i=0;i<30;i++)h.update(100);BalconyRenderTest.capture(h,width,height,args[2]+"-intro.png");
   for(int i=30;i<85;i++){
    h.update(100);
    if(i>=65)for(int hero=300;hero<302;hero++)check(scene.actors[hero]!=null&&scene.actors[hero].visible,"invisible hero after descent "+i);
    if(i==66)BalconyRenderTest.capture(h,640,480,args[2]+"-handoff-world.png");
   }
   settle(h);BalconyRenderTest.capture(h,640,480,args[2]+"-world.png");BalconyRenderTest.capture(h,width,height,args[2]+"-entry.png");
   act(h,0,0,15);for(int i=0;i<90;i++)h.update(100);
   check(scene.actors[5]!=null&&scene.actors[5].id==51&&scene.actors[6]!=null&&scene.actors[6].id==54,"lift system disappeared after fall");
   BalconyRenderTest.capture(h,640,480,args[2]+"-lift-rest-world.png");
   BalconyRenderTest.label(h,18,"Трос");BalconyRenderTest.label(h,16,"Окно");
   act(h,1,0,16);act(h,0,0,17);BalconyRenderTest.label(h,19,"Осколок");act(h,1,0,19);
   check(h.logic.hasItem(1),"glass HUD pickup");BalconyRenderTest.capture(h,width,height,args[2]+"-inventory.png");
   act(h,0,1,18);BalconyRenderTest.capture(h,640,480,args[2]+"-rope-world.png");
   check(h.logic.select(0)&&h.logic.selectItem(0),"left window selection");HotelRenderTest.act(h,15);
   int n=2000;boolean seen=false;
   while(h.logic.busy()&&n-->0){if(h.animals.world.action()==60&&h.animals.world.step()==25){BalconyRenderTest.capture(h,640,480,args[2]+"-exit-world.png");seen=true;}h.update(100);}
   check(n>0&&seen&&h.logic.completed(),"physical cooperative exit");
  }finally{scene.close();}
  System.out.println("FacadeRender: intro, source hotspots/labels, shard HUD, rope and cooperative window exit at "+width+"x"+height+" passed");
 }
}
