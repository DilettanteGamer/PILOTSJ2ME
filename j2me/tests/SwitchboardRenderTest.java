package pilots;
import java.io.File;
import javax.imageio.ImageIO;
import javax.microedition.lcdui.Graphics;
/** Real room silhouettes, original wire artwork, directional reachability and full source exit. */
public final class SwitchboardRenderTest {
 static void check(boolean b,String m){if(!b)throw new RuntimeException(m);}
 static void settle(HotelScene h)throws Exception{int n=3000;while(h.logic.busy()&&!h.animals.world.boardRequested()&&n-->0)h.update(100);check(n>0,"switchboard render lock");}
 static void act(HotelScene h,int hero,int item,int id)throws Exception{check(h.logic.select(hero)&&h.logic.selectItem(item),"switchboard render selection");HotelRenderTest.act(h,id);settle(h);}
 static void detail(SwitchboardPuzzle p,int width,int height,int selected,String path)throws Exception {
  Graphics g=new Graphics(width,height);g.setColor(0x514b35);g.fillRect(0,0,width,height);p.paint(g,width,height,selected);ImageIO.write(g.image,"png",new File(path));
 }
 static int[] route(SwitchboardPuzzle p,int from,int to) {
  int[] previous=new int[27],key=new int[27],queue=new int[27];for(int i=0;i<27;i++)previous[i]=-1;
  int head=0,tail=0;queue[tail++]=from;previous[from]=from;
  int[] dx={0,-1,1,0},dy={-1,0,0,1},keys={50,52,54,56};
  while(head<tail){int a=queue[head++];for(int d=0;d<4;d++){int b=p.select(a,dx[d],dy[d]);if(previous[b]<0){previous[b]=a;key[b]=keys[d];queue[tail++]=b;}}}
  check(previous[to]>=0,"wire unreachable with arrows: "+from+"->"+to);
  int length=0;for(int n=to;n!=from;n=previous[n])length++;int[] path=new int[length];
  for(int n=to,i=length-1;n!=from;n=previous[n])path[i--]=key[n];return path;
 }
 public static void main(String[] args)throws Exception {
  int width=Integer.parseInt(args[0]),height=Integer.parseInt(args[1]);Scene scene=new Scene(13);GameState s=new GameState();s.scene=13;
  try {HotelScene h=new HotelScene(scene,s);settle(h);HotelWorld w=h.animals.world;
   BalconyRenderTest.capture(h,640,480,args[2]+"-world.png");BalconyRenderTest.capture(h,width,height,args[2]+"-entry.png");
   BalconyRenderTest.label(h,48,"Щит");BalconyRenderTest.label(h,51,"Огнетушитель");BalconyRenderTest.label(h,52,"Лифт");
   act(h,1,0,48);BalconyRenderTest.label(h,16,"Провода");act(h,0,0,16);check(w.boardRequested(),"physical switchboard approach");w.beginBoard();
   SwitchboardPuzzle p=new SwitchboardPuzzle(w);
   try {detail(p,width,height,0,args[2]+"-detail.png");
    for(int a=0;a<27;a++)for(int b=0;b<27;b++)route(p,a,b);
    check(p.hit(-1,-1)==-1,"outside detail pointer accepted");
    for(int i=0;i<27;i++){int n=4;while(w.boardOrientation(i)!=1&&n-->0){check(w.turnBoard(i),"detail turn");if(w.boardRequested()){detail(p,width,height,i,args[2]+"-progress.png");for(int b=0;b<27;b++)route(p,i,b);}}}
    check(!w.boardRequested()&&w.action()==97,"detail solution did not return to electrical cinematic");
   }finally{p.close();}
   h.refreshState();settle(h);BalconyRenderTest.capture(h,640,480,args[2]+"-powered-world.png");
   act(h,0,0,51);check(w.hasItem(1),"physical extinguisher pickup");BalconyRenderTest.capture(h,width,height,args[2]+"-inventory.png");
   act(h,0,1,52);act(h,0,0,52);check(w.completed,"physical elevator exit");
   BalconyRenderTest.capture(h,640,480,args[2]+"-exit-world.png");
  }finally{scene.close();}
  System.out.println("SwitchboardRender: source hotspots/layers, original enlarged panel, all arrow routes, original solution, inventory and both exits at "+width+"x"+height+" passed");
 }
}
