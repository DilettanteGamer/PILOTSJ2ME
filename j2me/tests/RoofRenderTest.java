package pilots;
/** Actual source silhouettes, layers, panel and complete roof route. */
public final class RoofRenderTest {
 static void check(boolean b,String m){if(!b)throw new RuntimeException(m);}
 static void settle(HotelScene h)throws Exception{int n=2500;while(h.logic.busy()&&n-->0)h.update(100);check(n>0,"roof render lock");h.update(100);}
 static void act(HotelScene h,int hero,int item,int id)throws Exception{check(h.logic.select(hero)&&h.logic.selectItem(item),"roof render selection");HotelRenderTest.act(h,id);settle(h);}
 public static void main(String[] args)throws Exception{
  int width=Integer.parseInt(args[0]),height=Integer.parseInt(args[1]);String out=args[2];GameState s=new GameState();s.scene=14;Scene scene=new Scene(14);
  try{HotelScene h=new HotelScene(scene,s);for(int i=0;i<20;i++)h.update(100);BalconyRenderTest.capture(h,width,height,out+"-intro.png");settle(h);
   BalconyRenderTest.capture(h,width,height,out+"-entry.png");BalconyRenderTest.capture(h,640,480,out+"-world.png");
   BalconyRenderTest.label(h,22,"Килоперьев");BalconyRenderTest.label(h,18,"Воздушный шар");BalconyRenderTest.label(h,28,"Ножницы");BalconyRenderTest.label(h,35,"Верёвка");
   act(h,0,0,15);act(h,1,0,16);act(h,0,0,23);act(h,0,0,28);
   check(h.logic.hasItem(1)&&h.logic.hasItem(2)&&h.logic.hasItem(3)&&h.logic.hasItem(4),"four roof inventory cells");check(h.logic.selectItem(4),"ladder selection");
   BalconyRenderTest.capture(h,width,height,out+"-inventory.png");
   check(h.logic.select(0)&&h.request(384,384),"Chief placement through floor hotspot");settle(h);
   check(h.animals.world.g(0x1e1b64)==1,"Chief placed outside original hose trigger");act(h,1,0,27);act(h,0,4,18);
   for(int f=0;f<4;f++){check(h.targetAt(152,320)==39&&"Шланг".equals(h.hoverText(152,320)),"hose target moves with animation");h.update(100);}
   check(h.logic.select(0)&&h.logic.selectItem(0)&&h.request(152,320),"fixed hose input rejected");settle(h);
   BalconyRenderTest.capture(h,width,height,out+"-inflated.png");act(h,0,0,17);for(int i=0;i<600;i++)h.update(100);check(!h.logic.busy(),"waiting Chief blocked paint");
   check(h.logic.select(1)&&h.logic.selectItem(1),"late green paint selection");HotelRenderTest.act(h,22);
   int paintFrames=0,limit=2000;while(h.logic.busy()&&limit-->0){check(h.hoverText(366,180)==null,"paint movie labels Colleague");if(h.animals.world.action()==51)paintFrames++;h.update(100);}
   check(limit>0&&paintFrames>0,"green paint movie did not finish");h.update(100);act(h,1,0,17);for(int i=0;i<600;i++)h.update(100);check(!h.logic.busy(),"idle basket blocks rope");
   check(h.scene.actors[33]!=null&&h.scene.actors[33].id==107,"Chief basket sprite missing");check(h.logic.select(0)&&h.logic.heroX(0)==192&&h.logic.select(1)&&h.logic.heroX(1)==256,"camera follows stale ground actor");
   BalconyRenderTest.capture(h,width,height,out+"-basket.png");BalconyRenderTest.capture(h,640,480,out+"-basket-world.png");
   check(h.logic.selectItem(3),"cutting tool");HotelRenderTest.act(h,35);for(int i=0;i<20;i++){h.update(100);check(h.hoverText(366,180)==null,"rope movie labels Colleague");}BalconyRenderTest.capture(h,width,height,out+"-flight.png");
   settle(h);check(h.logic.completed(),"roof did not finish source flight");
   for(HotelWorld.Thing t:h.animals.world.cells)if(t!=null&&t.id==46)check(t.clip<0,"floating basket head after flight");
   BalconyRenderTest.capture(h,width,height,out+"-finished.png");
  }finally{scene.close();}
  System.out.println("RoofRender: original intro/hotspots/labels/layers, four-item HUD, full source quest, basket selection and flight at "+width+"x"+height+" passed");
 }
}
