package pilots;
import java.io.File;
import javax.imageio.ImageIO;
import javax.microedition.lcdui.Graphics;
/** Native crop, fixed camera, smaller original actors and a visible incoming crow. */
public final class FlightRenderTest {
 static void check(boolean b,String m){if(!b)throw new RuntimeException(m);}
 static Graphics paint(FlightScene h,int width,int height){int bar=(width*63+320)/640;Viewport v=new Viewport(width,height-bar,640,417);Graphics g=new Graphics(width,height);h.paint(g,v);h.presentation.panel(g,width,height,bar,h.flight);return g;}
 static void capture(FlightScene h,int width,int height,String file)throws Exception{ImageIO.write(paint(h,width,height).image,"png",new File(file));}
 public static void main(String[] args)throws Exception{
  int width=Integer.parseInt(args[0]),height=Integer.parseInt(args[1]),bar=(width*63+320)/640;String out=args[2];GameState s=new GameState();s.scene=16;Scene scene=new Scene(16);
  try{FlightScene h=new FlightScene(scene,s);HotelWorld w=h.flight.world;capture(h,width,height,out+"-entry.png");
   Viewport first=new Viewport(width,height-bar,640,417);first.cursorX=0;first.cursorY=0;first.zoom=true;first.update();Graphics a=new Graphics(width,height);h.paint(a,first);
   first.cursorX=639;first.cursorY=416;first.zoom=false;first.update();Graphics b=new Graphics(width,height);h.paint(b,first);
   for(int y=0;y<height-bar;y++)for(int x=0;x<width;x++)check(a.image.getRGB(x,y)==b.image.getRGB(x,y),"flight camera follows cursor/zoom");
   int n=100;while(!(w.g(0x1e1b58)==300&&w.g(0x1e1b5c)==5)&&n-->0)h.update(100);check(n>0,"initial crow timing");
   AnimatedActor bird=null,balloon=null;for(AnimatedActor actor:scene.actors)if(actor!=null){if(actor.id==61)bird=actor;if(actor.id==47)balloon=actor;}
   check(bird!=null&&balloon!=null,"source flight actors absent");Viewport v=h.presentation.stage(width,height-bar);
   int birdLeft=v.screenX(bird.x+bird.clip.offsetX),balloonRight=v.screenX(balloon.x+balloon.clip.offsetX+balloon.clip.width);
   check(birdLeft>balloonRight+width/4&&birdLeft<width,"crow is not visible ahead of balloon");
   check(v.screenX(balloon.clip.width)-v.screenX(0)<width*2/5,"balloon still fills screen");
   capture(h,width,height,out+"-warning.png");check(h.input(2),"upper band input");
   for(int i=0;i<20;i++)h.update(100);check(w.action()==43,"source upper encounter not reached");capture(h,width,height,out+"-encounter.png");
   n=100;while(!w.completed&&n-->0)h.update(100);check(w.completed,"flight encounter did not complete");
  }finally{scene.close();}
  System.out.println("FlightRender: static full-screen sky, camera independent of cursor/zoom, original balloon below 40% width, early visible crow, source encounter at "+width+"x"+height+" passed");
 }
}
