package pilots;
import java.io.IOException;
import javax.microedition.lcdui.*;
/** A single fixed view of the pipe, both platform edges and the original rats. */
public final class SewerScene extends ZooScene {
 public final SewerLogic sewer;
 private long elapsed;
 private final Viewport backdrop=new Viewport(128,128,480,256);
 private final Viewport stage=new Viewport(128,128,480,256);
 public SewerScene(Scene scene,GameState state)throws IOException{super(scene,state,new SewerLogic(state),false);sewer=(SewerLogic)logic;scene.close();scene.actors=new AnimatedActor[9];
  int[] ids={70,46,47,48,72,75,77,81,20018};
  try{for(int i=0;i<ids.length;i++)scene.actors[i]=new AnimatedActor(ids[i],"/scenes/07-"+ids[i]+".anm",0,0);refresh();}catch(IOException error){scene.close();throw error;}
 }
 private void frame(AnimatedActor a,int frame)throws IOException{frame=Math.max(0,Math.min(a.clip.count-1,frame));if(a.clip.index>frame)a.clip.restart();while(a.clip.index<frame)a.clip.advance();a.playing=false;}
 private void refresh()throws IOException{
  for(int i=0;i<3;i++){AnimatedActor a=scene.actors[1+i];a.x=sewer.rats.x(i,sewer.clock%20)-104;a.y=16;frame(a,sewer.clock%20);}
  scene.actors[0].x=344;scene.actors[0].y=144;
  // Original ambient mice remain outside the walking plane.
  int[] ax={152,-104,472,24},ay={224,224,224,-128};
  for(int i=0;i<4;i++){AnimatedActor a=scene.actors[4+i];a.x=ax[i];a.y=ay[i];frame(a,(sewer.clock+i*17)%100<a.clip.count?(sewer.clock+i*17)%100:a.clip.count-1);}
  int progress=sewer.motionProgress();
  int id=sewer.fall>86?20032:sewer.fall>0?(sewer.fall<21?64:66):sewer.motion>0?(sewer.direction<0?20032:20028)+(progress/5&1):sewer.direction<0?20018:20019;
  AnimatedActor hero=scene.actors[8];if(hero.id!=id){hero.close();scene.actors[8]=hero=new AnimatedActor(id,"/scenes/07-"+id+".anm",0,0);}
  if(sewer.fall>86){hero.x=448+(89-sewer.fall)*16-104;hero.y=128;frame(hero,sewer.fall-87);}
  else if(sewer.fall>0&&sewer.fall<21){hero.x=sewer.fallX-104-85;hero.y=260;frame(hero,sewer.fall-1);}
  else if(sewer.fall>=21&&sewer.fall<=50){
   int f=16+(sewer.fall-21)*33/29;frame(hero,f);
   int center=sewer.fallX+32+(512-sewer.fallX-32)*(sewer.fall-21)/29;
   hero.x=center-104-hero.clip.offsetX-hero.clip.width/2;hero.y=224;
  }
  else if(sewer.fall>=51){
   // The original emergence is rooted at the ladder on the right platform.
   int f=sewer.fall<76?50+sewer.fall-51:75+(sewer.fall-76)*25/10;
   hero.x=176-104;hero.y=224;frame(hero,f);
  }
  else {hero.x=sewer.visibleX()-104;hero.y=128;frame(hero,sewer.motion>0?progress%5:0);}
 }
 public boolean input(int key)throws IOException{boolean changed=sewer.input(key);refresh();return changed;}
 public boolean update(long delta)throws IOException{if(delta<=0)return false;elapsed+=Math.min(delta,400);boolean changed=false;while(elapsed>=100){elapsed-=100;sewer.tick();changed=true;}if(changed)refresh();return changed;}
 public void paint(Graphics g,Viewport view){
  int w=view.width,h=view.height,drawH=w*256/480;
  g.setColor(0x171b13);g.fillRect(0,0,w,h);
  stage.width=w;stage.height=h;stage.drawWidth=w;stage.drawHeight=drawH;stage.left=0;stage.top=(h-drawH)/2;stage.paintClip(0,0,w,h);
  int cx=g.getClipX(),cy=g.getClipY(),cw=g.getClipWidth(),ch=g.getClipHeight();
  backdrop.width=w;backdrop.height=h;backdrop.drawWidth=w;backdrop.drawHeight=h;backdrop.left=backdrop.top=0;backdrop.paintClip(0,0,w,h);
  scene.background.paint(g,backdrop);
  for(int i=0;i<scene.actors.length;i++)scene.actors[i].paint(g,stage);
  g.setClip(cx,cy,cw,ch);
  g.setFont(Font.getFont(0,0,8));g.setColor(0xe2dab0);g.drawString("4/6  Меню: правая",w/2,h-2,Graphics.BOTTOM|Graphics.HCENTER);
 }
 public boolean request(int x,int y)throws IOException{return input(x<200?4:6);}
 public boolean activate(int x,int y)throws IOException{return request(x,y);}
 public int targetAt(int x,int y){return 0;}
 public String hoverText(int x,int y){return null;}
}
