package pilots;
import java.io.File;import java.lang.reflect.*;import javax.imageio.ImageIO;import javax.microedition.lcdui.*;
/** Host-only actual 128x160 renderer capture; never included in MIDlet. */
public final class HalfsizePreview {
 static void set(Object o,String n,Object v)throws Exception{Field f=o.getClass().getDeclaredField(n);f.setAccessible(true);f.set(o,v);}
 public static void main(String[] args)throws Exception{
  while(DebugHud.mode()!=0)DebugHud.toggle();
  PilotsMidlet app=new PilotsMidlet();PilotsMidlet.GameCanvas canvas=app.new GameCanvas();set(app,"canvas",canvas);set(app,"display",Display.getDisplay(app));
  try{canvas.startLocation(Integer.parseInt(args[0]));for(int i=0;i<Integer.parseInt(args[1]);i++)canvas.tick(100);
   Graphics g=new Graphics(128,160);canvas.paint(g);ImageIO.write(g.image,"png",new File(args[2]));
  }finally{canvas.shutdown();}
 }
}
