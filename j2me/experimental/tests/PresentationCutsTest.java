package pilots;
import java.lang.reflect.*;import java.util.Arrays;
import javax.microedition.lcdui.*;
public final class PresentationCutsTest {
 static void check(boolean b,String why)throws Exception{if(!b)throw new Exception(why);}
 static Object get(Object o,String name)throws Exception{Field f=o.getClass().getDeclaredField(name);f.setAccessible(true);return f.get(o);}
 static void set(Object o,String name,Object v)throws Exception{Field f=o.getClass().getDeclaredField(name);f.setAccessible(true);f.set(o,v);}
 public static void main(String[] args)throws Exception{
  AnimationClip wall=new AnimationClip("/scenes/02-73.anm"),rest=new AnimationClip("/scenes/02-74.anm");
  Field data=AnimationClip.class.getDeclaredField("pixels");data.setAccessible(true);
  check(wall.count==60,"Wall controller duration changed");
  for(int frame=51;frame<60;frame++){wall.seek(frame);check(wall.index==frame&&wall.offsetX==rest.offsetX&&wall.offsetY==rest.offsetY&&wall.width==rest.width&&wall.height==rest.height,"Frozen tail changed geometry/frame clock");check(Arrays.equals(Arrays.copyOf((byte[])data.get(wall),wall.width*wall.height),Arrays.copyOf((byte[])data.get(rest),rest.width*rest.height)),"Tail pose differs from original settled hippo");}
  wall.close();rest.close();
  for(int[] size:new int[][]{{128,160},{128,128},{240,320}}){
   System.setProperty("pilots.test.width",String.valueOf(size[0]));System.setProperty("pilots.test.height",String.valueOf(size[1]));
   PilotsMidlet app=new PilotsMidlet();PilotsMidlet.GameCanvas canvas=app.new GameCanvas();set(app,"canvas",canvas);set(app,"display",Display.getDisplay(app));
   try{canvas.startLocation(8);boolean loaded=false;
    for(int tick=0;tick<230;tick++){
     if(((GameState)get(canvas,"state")).scene!=8)break;
     Viewport v=(Viewport)get(canvas,"view");int left=v.left,top=v.top;check(v.fixedFocusX==260,"Hotel camera not fixed");v.cursorX=tick%2==0?0:639;v.zoom=true;v.update();check(v.left==left&&v.top==top&&!v.zoom,"Cursor/zoom moved hotel camera");
     Scene room=(Scene)get(canvas,"scene");for(AnimatedActor a:room.actors)if(a!=null&&a.clip.count>1){check(v.screenX(a.x+a.clip.offsetX)>=0&&v.screenX(a.x+a.clip.offsetX+a.clip.width)<=v.width,"Hotel crop cuts moving heroes at "+size[0]+"x"+size[1]);}
     if(tick==60){canvas.save();canvas.startLocation(2);canvas.load();loaded=true;}
     canvas.tick(100);
    }
    check(loaded&&((GameState)get(canvas,"state")).scene==9,"Hotel entry/save did not reach interior");check(((Viewport)get(canvas,"view")).fixedFocusX==-1,"Fixed camera leaked to hotel interior");
   }finally{canvas.shutdown();}
  }
  System.setProperty("pilots.test.width","128");System.setProperty("pilots.test.height","160");
  System.out.println("Hippo tail: original settled pose for frames 51..59, 60-tick clock intact; hotel camera fixed with both heroes visible at three resolutions, save/load and normal interior camera preserved");
 }
}
