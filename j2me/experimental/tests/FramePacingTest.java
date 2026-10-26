package pilots;
import java.lang.reflect.*;import javax.microedition.lcdui.*;
/** A stalled iteration advances one script step, then releases the Canvas for display service. */
public final class FramePacingTest {
 static Object get(Object target,String name)throws Exception{Field f=target.getClass().getDeclaredField(name);f.setAccessible(true);return f.get(target);}
 public static void main(String[] args)throws Exception{PilotsMidlet app=new PilotsMidlet();app.startApp();final PilotsMidlet.GameCanvas canvas=(PilotsMidlet.GameCanvas)get(app,"canvas");
  try{canvas.startLocation(3);GameState state=(GameState)get(canvas,"state");int before=state.variables[7];canvas.tick(2000);if(state.variables[7]!=(before+1)%24)throw new Exception("Stall caused catch-up burst");canvas.tick(40);canvas.tick(40);if(state.variables[7]!=(before+1)%24)throw new Exception("Normal fixed-step interval changed");canvas.tick(20);if(state.variables[7]!=(before+2)%24)throw new Exception("Lost normal 100ms step");
   final int[] serviced={0};final boolean[] locked={false};Canvas.serviceHook=new Runnable(){public void run(){if(Thread.holdsLock(canvas))locked[0]=true;serviced[0]++;}};canvas.showNotify();long until=System.currentTimeMillis()+3000;while(serviced[0]<3&&System.currentTimeMillis()<until)Thread.sleep(10);canvas.hideNotify();canvas.pauseAnimation(true);if(serviced[0]<3||locked[0])throw new Exception("Display service absent or under game lock");System.out.println("Frame pacing: long stall capped to one step; normal 100ms steps preserved; display service outside Canvas lock");
  }finally{Canvas.serviceHook=null;app.destroyApp(true);}
 }
}
