package pilots;
import java.io.IOException;
import javax.microedition.lcdui.Graphics;
/** Section 18 VM controls the balloon, crow, clouds and authored transition movies. */
public final class FlightScene extends ZooScene {
 public final FlightLogic flight;
 public final FlightView presentation=new FlightView();
 private long elapsed;
 public FlightScene(Scene scene,GameState state)throws IOException{super(scene,state,new FlightLogic(state),false);flight=(FlightLogic)logic;scene.close();scene.actors=new AnimatedActor[310];refresh();}
 private HotelWorld.Thing thing(int i){return i<300?flight.world.cells[i]:i<302?flight.world.heroes[i-300]:flight.world.effects[i-302];}
 private void refresh()throws IOException{
  for(int i=0;i<scene.actors.length;i++){HotelWorld.Thing t=thing(i);AnimatedActor a=scene.actors[i];
   if(t==null||!t.active||t.clip<0||t.y>=417){if(a!=null){a.close();scene.actors[i]=null;}continue;}
   int id=t.clip<46?20000+t.clip:t.clip;
   if(a==null||a.id!=id||a.clip.index>t.frame){AnimatedActor next=new AnimatedActor(id,"/scenes/16-"+id+".anm",t.x,t.y);if(a!=null)a.close();scene.actors[i]=a=next;}
   while(a.clip.index<Math.min(t.frame,a.clip.count-1))a.clip.advance();a.x=t.x;a.y=t.y;a.playing=false;a.visible=true;
  }
 }
 public boolean input(int key)throws IOException{boolean result=flight.command(key);refresh();return result;}
 public boolean update(long delta)throws IOException{if(delta<=0)return false;elapsed+=Math.min(delta,400L);boolean changed=false;while(elapsed>=100){elapsed-=100;flight.world.tick();changed=true;}if(changed)refresh();return changed;}
 public void paint(Graphics g,Viewport v){presentation.paint(g,scene,v,flight.world);}
 public boolean request(int x,int y)throws IOException{return input(y<144?2:y<272?5:8);}
 public boolean activate(int x,int y)throws IOException{return request(x,y);}
 public int targetAt(int x,int y){return 0;}
 public String hoverText(int x,int y){return null;}
}
