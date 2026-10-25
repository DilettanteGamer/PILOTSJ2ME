package pilots;
import java.io.File;
import javax.imageio.ImageIO;
import javax.microedition.lcdui.Graphics;
/** Native crop, fixed camera, smaller original actors and a visible incoming crow. */
public final class FlightRenderTest {
 static void check(boolean b,String m){if(!b)throw new RuntimeException(m);}
 static Graphics paint(FlightScene h,int width,int height){int bar=(width*63+320)/640;Viewport v=new Viewport(width,height-bar,640,417);Graphics g=new Graphics(width,height);h.paint(g,v);h.presentation.panel(g,width,height,bar,h.flight);return g;}
 static void capture(FlightScene h,int width,int height,String file)throws Exception{ImageIO.write(paint(h,width,height).image,"png",new File(file));}
 static void fallingBranch(int width,int height,int band,int initial,int key,int movie)throws Exception{
  GameState s=new GameState();s.scene=16;Scene scene=new Scene(16);
  try{
   FlightScene h=new FlightScene(scene,s);HotelWorld w=h.flight.world;
   if(initial==1){check(h.input(8),"initial lower command");while(w.busy())h.update(100);}
   int n=2000;
   while(n-->0&&!(w.g(0x1e1b58)==band*100&&w.g(0x1e1b5c)==5&&!w.busy())){
    if(w.g(0x1e1b58)<50&&!w.busy()&&w.g(0x1e1b54)!=initial)h.input(8);
    h.update(100);
   }
   check(n>0&&h.input(key),"fall branch input "+band);
   int play=height-(width*63+320)/640,last=-1,ticks=0;boolean crossed=false;
   Viewport v=h.presentation.stage(width,play);
   n=150;while(!w.completed&&n-->0){
    h.update(100);
    if(movie==64)for(AnimatedActor actor:scene.actors)if(actor!=null&&actor.id==64&&actor.clip.index==10){
     check(actor.clip.offsetY<-192,"upper fall balloon still clipped");
     capture(h,width,height,"/tmp/flight-repaired-top-"+width+"x"+height+".png");
    }
    for(AnimatedActor actor:scene.actors)if(actor!=null&&actor.id==movie&&actor.y>192){
     check(actor.clip.index==17,"clipped fall pose "+band);
     int top=v.screenY(actor.y+actor.clip.offsetY);
     check(top>=last,"fall reversed "+band);last=top;ticks++;
     if(top>=play)crossed=true;
    }
   }
   check(w.completed&&ticks>=15&&crossed,"fall did not leave screen "+band);
  }finally{scene.close();}
 }
 static void controlsAndDrops(int width,int height,String out)throws Exception{
  GameState s=new GameState();s.scene=16;Scene scene=new Scene(16);
  try{
   FlightScene h=new FlightScene(scene,s);HotelWorld w=h.flight.world;
   check(!h.input(5),"obsolete middle-height button active");
   check(h.input(8),"relative down");int n=100;
   while(w.busy()&&n-->0){w.g(0x1e1b58,-1000);h.update(100);}
   check(n>0&&h.flight.altitude()==1&&!h.input(8),"lower limit");
   check(h.input(2),"relative up from low");n=100;
   while(w.busy()&&n-->0){w.g(0x1e1b58,-1000);h.update(100);}
   check(n>0&&h.flight.altitude()==2&&h.flight.ballast()==3,"up skipped middle/used ballast early");
   // Ordinary ascent throws the refrigerator; the last bag throws the cat.
   w.g(0x94,2);check(h.input(2),"middle ascent");
   boolean entryHidden=false,entryEdge=false,entryWhole=false,entryReload=false;
   boolean[] bottom=new boolean[3];int[] last={-1,-1,-1};int play=height-(width*63+320)/640;
   Viewport v=h.presentation.stage(width,play);boolean returned=false,repeated=false;
   for(int tick=0;tick<650;tick++){
    w.g(0x1e1b58,-1000);h.update(100);
    if(!w.busy()&&!returned){check(h.flight.altitude()==3&&!h.input(2),"upper limit");check(h.input(8),"relative down from high");returned=true;}
    else if(returned&&!repeated&&!w.busy()){check(h.flight.altitude()==2,"down skipped middle");check(h.input(2),"last bag ascent");repeated=true;}
    for(AnimatedActor actor:scene.actors)if(actor!=null){
     if(actor.id==72&&actor.clip.index<=8){
      Graphics shown=paint(h,width,height);actor.visible=false;Graphics hidden=paint(h,width,height);actor.visible=true;
      int minY=play,maxY=-1;
      for(int y=0;y<play;y++)for(int x=0;x<width;x++)if(shown.image.getRGB(x,y)!=hidden.image.getRGB(x,y)){minY=Math.min(minY,y);maxY=y;}
      if(actor.clip.index==0){check(maxY<0,"cat starts inside screen");entryHidden=true;}
      if(actor.clip.index==1){
       check(minY>=play-Math.max(8,height/12)&&maxY==play-1,"cat does not enter through phone bottom");entryEdge=true;
       capture(h,width,height,out+"-cat-entry.png");
       w.syncSave();GameState restored=GameState.decode(s.encode(),17);Scene restoredScene=new Scene(16);
       try{FlightScene loaded=new FlightScene(restoredScene,restored);Graphics image=paint(loaded,width,height);
        for(int y=0;y<height;y++)for(int x=0;x<width;x++)check(image.image.getRGB(x,y)==shown.image.getRGB(x,y),"cat entry changed after load");
       }finally{restoredScene.close();}
       entryReload=true;
      }
      if(actor.clip.index==8){check(maxY>=minY&&maxY<play-1&&minY>=0,"cat entry never becomes whole");entryWhole=true;capture(h,width,height,out+"-cat-whole.png");}
     }
     int index=actor.id==57?0:actor.id==70?1:actor.id==72?2:-1;
     int base=index==0?3:index==1?38:10;
     if(index>=0&&actor.clip.index>base){
      int top=v.screenY(actor.y+actor.clip.offsetY);
      check(top>=last[index],"drop reversed "+actor.id);last[index]=top;
      if(top>=play)bottom[index]=true;
      if(index==0&&top<play&&v.screenY(actor.y+actor.clip.offsetY+actor.clip.height)>=play)capture(h,width,height,out+"-drop.png");
     }
    }
   }
   check(entryHidden&&entryEdge&&entryWhole&&entryReload,"bottom entry coverage");
   check(repeated&&bottom[0]&&bottom[1]&&bottom[2],"fridge/cat clipped before bottom "+bottom[0]+"/"+bottom[1]+"/"+bottom[2]+" tops "+last[0]+"/"+last[1]+"/"+last[2]);
  }finally{scene.close();}
 }
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
   int lastTop=-1,fallTicks=0;boolean crossedBottom=false;
   n=100;while(!w.completed&&n-->0){
    h.update(100);
    for(AnimatedActor actor:scene.actors)if(actor!=null&&actor.id==64&&actor.y>192){
     check(actor.clip.index==17,"fall uses a clipped source pose");
     int top=v.screenY(actor.y+actor.clip.offsetY);
     check(top>=lastTop,"fall moved upwards");lastTop=top;fallTicks++;
     if(top>=height-bar)crossedBottom=true;
    }
   }
   check(w.completed,"flight encounter did not complete");
   check(fallTicks>=15&&crossedBottom,"fall ended before leaving screen");
  }finally{scene.close();}
  controlsAndDrops(width,height,out);
  fallingBranch(width,height,1,2,8,62);fallingBranch(width,height,2,1,2,63);fallingBranch(width,height,3,2,2,64);
  System.out.println("FlightRender: static full-screen sky, camera independent of cursor/zoom, original balloon below 40% width, early visible crow, source encounter at "+width+"x"+height+" passed");
 }
}
