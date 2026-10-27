package pilots;
import java.lang.reflect.*;import java.util.Arrays;import javax.microedition.lcdui.*;
/** Real overlays, stable buffers, 0.5fps arithmetic, input preservation and low-overhead idle refresh. */
public final class DebugHudTest {
 static Field field(String n)throws Exception{Field f=DebugHud.class.getDeclaredField(n);f.setAccessible(true);return f;}
 static void check(boolean ok,String why){if(!ok)throw new RuntimeException(why);}
 static final class Spy extends Canvas{int paints;protected void paint(Graphics g){}public void repaint(int x,int y,int w,int h){paints++;}}
 public static void main(String[] args)throws Exception{
  DebugHud.scene(2);check(DebugHud.mode()==3,"audio diagnostic default");long now=System.currentTimeMillis();field("window").setLong(null,now-2000);field("frames").setInt(null,1);field("snapshot").setLong(null,0);DebugHud.tick(1500);DebugHud.io(700);DebugHud.audio(600);DebugHud.audioState(2,1);
  Graphics g=new Graphics(128,160);g.setColor(0x123456);g.fillRect(0,0,128,160);DebugHud.paint(g,128,148,16,4);String[] labels=(String[])field("labels").get(null);
  check(labels[0].startsWith("F0.5"),"low fps arithmetic: "+labels[0]);check(labels[1].contains("U1500/1500"),"update peak missing");check(labels[2].contains("L700/700"),"I/O peak missing");check(labels[6].contains("AU600/600 S-1 Z0")&&labels[5].contains("K-1 W-1 C0 H0 X0"),"audio lifecycle metrics missing");
  check(g.image.getRGB(0,147)==0xff000000&&g.image.getRGB(0,148)==0xff123456,"overlay overwrites inventory");Object buffer=field("pixels").get(null);int allocations=Image.allocations;for(int i=0;i<20;i++)DebugHud.paint(g,128,148,16,4);check(field("pixels").get(null)==buffer&&Image.allocations==allocations,"HUD allocates images or buffers per paint");
  javax.imageio.ImageIO.write(g.image,"png",new java.io.File("/tmp/pilots-debug-hud.png"));
  Spy spy=new Spy();field("pulse").setLong(null,0);DebugHud.pulse(spy,148);DebugHud.pulse(spy,148);check(spy.paints==1,"idle refresh exceeds 1Hz");
  DebugHud.toggle();check(DebugHud.mode()==2,"full metrics toggle");field("snapshot").setLong(null,0);DebugHud.paint(g,128,148,16,4);check(((String[])field("labels").get(null))[6].contains("Q2 E1"),"original audio metrics lost");DebugHud.toggle();check(DebugHud.mode()==1,"compact toggle");DebugHud.paint(g,128,148,16,4);check(((int[])field("pixels").get(null)).length==128*16,"compact size");DebugHud.toggle();check(DebugHud.mode()==0&&field("pixels").get(null)==null,"off does not release buffer");int[] before=g.image.getRGB(0,0,128,160,null,0,128);DebugHud.paint(g,128,148,16,4);DebugHud.pulse(spy,148);check(Arrays.equals(before,g.image.getRGB(0,0,128,160,null,0,128))&&spy.paints==1,"hidden HUD draws or requests repaint");
  DebugHud.toggle();Graphics small=new Graphics(80,64);small.setClip(4,6,20,12);DebugHud.paint(small,80,40,2,1);check(small.getClipX()==4&&small.getClipY()==6&&small.getClipWidth()==20&&small.getClipHeight()==12,"clip not restored");
  PilotsMidlet app=new PilotsMidlet();app.startApp();try{Field f=app.getClass().getDeclaredField("canvas");f.setAccessible(true);PilotsMidlet.GameCanvas canvas=(PilotsMidlet.GameCanvas)f.get(app);canvas.keyPressed(-6);check(DebugHud.mode()==2,"left softkey");canvas.keyRepeated(-6);check(DebugHud.mode()==2,"repeat cycles HUD");canvas.keyPressed(-6);check(DebugHud.mode()==1,"second left softkey");canvas.keyPressed(-6);check(DebugHud.mode()==0,"third left softkey");canvas.keyPressed(-7);check(Display.getDisplay(app).getCurrent() instanceof MenuCanvas,"right softkey menu changed");}finally{app.destroyApp(true);}
  System.out.println("Debug HUD: correct low FPS/peaks; fixed buffers; no Image allocations; clipped/inventory-safe drawing; 1Hz idle; disabled no repaint; left toggle/right menu preserved");
 }
}
