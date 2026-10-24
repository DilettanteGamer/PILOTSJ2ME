package pilots;
import java.io.*;import javax.imageio.ImageIO;import javax.microedition.lcdui.Graphics;
/** The actual indexed front-view art, keyboard poses and every destruction stage. */
public final class BasementRenderTest {
 static void check(boolean b,String m){if(!b)throw new RuntimeException(m);}
 static void steps(HotelWorld w,int n){for(int i=0;i<n;i++)w.basement.tick();}
 static void move(HotelWorld w,int key){check(w.basement.input(key),"detail movement");steps(w,22);}
 static void fire(HotelWorld w,int count){for(int i=0;i<count;i++){check(w.basement.input(5),"detail shot");steps(w,BasementGame.SHOT_TICKS);}}
 static void capture(BasementStage stage,int width,int height,String file)throws Exception{Graphics g=new Graphics(width,height);g.setColor(0x231d16);g.fillRect(0,0,width,height);stage.paint(g,width,height);ImageIO.write(g.image,"png",new File(file));}
 static BasementTestRoute.Driver driver(final HotelWorld w){return new BasementTestRoute.Driver(){
  public HotelWorld world(){return w;}public void press(int k){check(w.basement.input(k),"detail key "+k);}public void ticks(int n){steps(w,n);}
 };}
 static void contacts(BasementStage stage)throws Exception{
  java.lang.reflect.Field f=BasementStage.class.getDeclaredField("crates");f.setAccessible(true);AnimationClip box=(AnimationClip)f.get(stage);box.restart();
  check(BasementStage.x(0)+box.offsetX==4 && BasementStage.x(3)+box.offsetX+box.width==124,"unused horizontal play area");
  for(int height=1;height<=4;height++)for(int y=BasementStage.feet(1,height);y<132;y++){
   boolean opaque=false;for(int row=0;row<height;row++)opaque|=box.contains(-4,y-(132-row*18));
   check(opaque,"gap between boxes or floor at "+height+"/"+y);
  }
 }
 static void crateSeams(HotelScene h)throws Exception{
  java.lang.reflect.Field pf=AnimationClip.class.getDeclaredField("pixels"),cf=AnimationClip.class.getDeclaredField("palette");pf.setAccessible(true);cf.setAccessible(true);
  Graphics g=new Graphics(640,417);Viewport v=new Viewport(640,417,640,417);h.scene.background.paint(g,v);h.paint(g,v);int checked=0;
  HotelWorld w=h.animals.world;
  for(int i=0;i<300;i++){HotelWorld.Thing upper=w.cells[i];AnimatedActor a=h.scene.actors[i];if(upper==null||upper.id!=24||a==null)continue;
   for(int j=0;j<300;j++){HotelWorld.Thing lower=w.cells[j];AnimatedActor b=h.scene.actors[j];if(lower==null||lower.id!=24||b==null||lower.x!=upper.x||lower.y-upper.y!=48)continue;
    check(h.layer(i)<h.layer(j),"lower crate covers upper crate seam");byte[] pixels=(byte[])pf.get(a.clip);int[] palette=(int[])cf.get(a.clip);
    for(int y=Math.max(a.y+a.clip.offsetY,b.y+b.clip.offsetY);y<Math.min(a.y+a.clip.offsetY+a.clip.height,b.y+b.clip.offsetY+b.clip.height);y++)for(int x=a.x+a.clip.offsetX;x<a.x+a.clip.offsetX+a.clip.width;x++){
     if(!a.clip.contains(x-a.x,y-a.y)||!b.clip.contains(x-b.x,y-b.y))continue;boolean covered=false;
     for(int k=0;k<h.scene.actors.length;k++){AnimatedActor c=h.scene.actors[k];if(k!=i&&c!=null&&h.layer(k)<=h.layer(i)&&c.clip.contains(x-c.x,y-c.y)){covered=true;break;}}
     if(covered)continue;int index=pixels[(y-a.y-a.clip.offsetY)*a.clip.width+x-a.x-a.clip.offsetX]&255;
     check((g.image.getRGB(x,y)&0xffffff)==(palette[index]&0xffffff),"upper crate pixels lost on return");checked++;
    }
   }
  }
  check(checked>100,"crate seam regression did not exercise overlap");
 }
 public static void main(String[] args)throws Exception{
  int width=Integer.parseInt(args[0]),height=Integer.parseInt(args[1]);GameState s=new GameState();s.scene=15;Scene scene=new Scene(15);
  try{HotelScene h=new HotelScene(scene,s);for(int i=0;i<80;i++)h.update(100);HotelWorld w=h.animals.world;
   check(h.logic.select(0)&&h.request(400,350),"first detail entry");w.basement.open();
   BasementTestRoute.Driver setup=driver(w);BasementTestRoute.expose(setup);BasementTestRoute.go(setup,1);BasementTestRoute.expose(setup);BasementTestRoute.expose(setup);w.basement.close();h.refreshState();crateSeams(h);
   BalconyRenderTest.capture(h,640,480,args[2]+"-return-world.png");
   byte[] beforePickup=w.state.encode();
   HotelRenderTest.act(h,14);boolean chiefPickup=false;for(int i=0;i<100;i++){h.update(100);for(HotelWorld.Thing t:w.cells)if(t!=null&&t.id==18&&t.clip==94)chiefPickup=true;}
   check(chiefPickup&&w.hasItem(1)&&!w.busy()&&h.logic.heroX(0)==256&&h.logic.heroY(0)==336,"Chief gun pickup/position");
   BalconyRenderTest.capture(h,640,480,args[2]+"-chief-gun-world.png");
   h=new HotelScene(scene,GameState.decode(beforePickup,16));w=h.animals.world;
   check(h.logic.select(1),"Colleague selection");HotelRenderTest.act(h,14);for(int i=0;i<100;i++)h.update(100);check(w.hasItem(1),"physical gun pickup");
   check(h.logic.select(0)&&"Ящики: отдельный экран".equals(h.hoverText(400,350))&&h.request(400,350),"physical trigger zone");w.basement.open();
   BasementStage stage=new BasementStage(w.basement);try{
    capture(stage,width,height,args[2]+"-detail-entry.png");
    contacts(stage);
    int attempts=w.basement.throwsCount();move(w,6);move(w,4);check(w.basement.throwsCount()==attempts&&w.basement.fallingColumn()<0,"safe walking threw a crate");check(w.basement.input(5),"safe gun fire");
    for(int f=0;f<BasementGame.SHOT_TICKS;f++){if(f==0||f==7||f==13)capture(stage,width,height,args[2]+"-shot-"+f+".png");w.basement.tick();}
    BasementTestRoute.stairs(driver(w));capture(stage,width,height,args[2]+"-stairs.png");
    check(w.basement.input(2),"enter box plane");steps(w,4);
    for(int n=0;n<3;n++){check(w.basement.input(4),"auto jump");steps(w,2);capture(stage,width,height,args[2]+"-jump-"+n+".png");steps(w,2);}
    check(w.basement.solved(),"front-view segment unsolved");
   }finally{stage.close();w.basement.close();}
   int limit=250;while(!w.completed&&limit-->0)h.update(100);check(w.completed,"room exit after detail");
   BalconyRenderTest.capture(h,640,480,args[2]+"-source-exit-world.png");
  }finally{scene.close();}
  System.out.println("BasementRender: original crate/balloon/servant/Chief frames, returned crate seams and free room walking, physical entry, staged destruction, movement recovery, automatic jumps and source exit at "+width+"x"+height+" passed");
 }
}
