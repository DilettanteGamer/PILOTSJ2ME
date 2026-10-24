package pilots;
import java.io.*;import javax.imageio.ImageIO;import javax.microedition.lcdui.Graphics;
/** The actual indexed front-view art, keyboard poses and every destruction stage. */
public final class BasementRenderTest {
 static void check(boolean b,String m){if(!b)throw new RuntimeException(m);}
 static void steps(HotelWorld w,int n){for(int i=0;i<n;i++)w.basement.tick();}
 static void move(HotelWorld w,int key){check(w.basement.input(key),"detail movement");steps(w,22);}
 static void fire(HotelWorld w,int count){for(int i=0;i<count;i++){check(w.basement.input(5),"detail shot");steps(w,18);}}
 static void capture(BasementStage stage,int width,int height,String file)throws Exception{Graphics g=new Graphics(width,height);g.setColor(0x231d16);g.fillRect(0,0,width,height);stage.paint(g,width,height);ImageIO.write(g.image,"png",new File(file));}
 public static void main(String[] args)throws Exception{
  int width=Integer.parseInt(args[0]),height=Integer.parseInt(args[1]);GameState s=new GameState();s.scene=15;Scene scene=new Scene(15);
  try{HotelScene h=new HotelScene(scene,s);for(int i=0;i<80;i++)h.update(100);HotelWorld w=h.animals.world;
   check(h.logic.select(1),"Colleague selection");HotelRenderTest.act(h,14);for(int i=0;i<100;i++)h.update(100);check(w.hasItem(1),"physical gun pickup");
   check(h.logic.select(0)&&"Ящики: отдельный экран".equals(h.hoverText(400,350))&&h.request(400,350),"physical trigger zone");w.basement.open();
   BasementStage stage=new BasementStage(w.basement);try{
    capture(stage,width,height,args[2]+"-detail-entry.png");
    move(w,6);move(w,4);check(w.basement.crates(0)==2,"stage drops did not land");check(w.basement.input(5),"safe gun fire");
    for(int f=0;f<18;f++){if(f==0||f==7||f==13)capture(stage,width,height,args[2]+"-shot-"+f+".png");w.basement.tick();}
    check(w.basement.input(6),"movement after shot");steps(w,22);move(w,4);
    for(int n=0;n<4;n++){move(w,6);move(w,6);move(w,6);move(w,4);move(w,4);move(w,4);}
    move(w,6);fire(w,1);move(w,6);fire(w,2);move(w,6);fire(w,3);capture(stage,width,height,args[2]+"-stairs.png");
    check(w.basement.input(2),"enter box plane");steps(w,4);
    for(int n=0;n<3;n++){check(w.basement.input(4),"auto jump");steps(w,2);capture(stage,width,height,args[2]+"-jump-"+n+".png");steps(w,2);}
    check(w.basement.solved(),"front-view segment unsolved");
   }finally{stage.close();w.basement.close();}
   int limit=250;while(!w.completed&&limit-->0)h.update(100);check(w.completed,"room exit after detail");
   BalconyRenderTest.capture(h,640,480,args[2]+"-source-exit-world.png");
  }finally{scene.close();}
  System.out.println("BasementRender: generated front-view wall/crates/servant, original Chief poses, physical entry, staged destruction, movement recovery, automatic jumps and source exit at "+width+"x"+height+" passed");
 }
}
