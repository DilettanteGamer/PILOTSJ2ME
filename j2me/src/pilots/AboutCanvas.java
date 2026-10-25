package pilots;
import java.io.IOException;
import javax.microedition.lcdui.*;
/** Seven original credit pages and the port author's own eighth page. */
public final class AboutCanvas extends Canvas implements CommandListener {
 private final MenuCanvas.Host host;
 private IndexedBackground page;
 private int selected,panX,panY;
 private boolean zoom;
 private final Viewport view=new Viewport(128,160,640,480);
 private final Command back=new Command("Назад",Command.BACK,1);
 public AboutCanvas(MenuCanvas.Host host){this.host=host;setFullScreenMode(true);addCommand(back);setCommandListener(this);}
 private void load()throws IOException{if(page==null)page=new IndexedBackground(getClass().getResourceAsStream(selected<7?"/ui/credits-"+selected+".p8":"/ui/leather.p8"));}
 protected void paint(Graphics g){
  int w=getWidth(),h=getHeight();g.setColor(0x302013);g.fillRect(0,0,w,h);
  try{load();OriginalFont font=OriginalFont.get();
   view.width=w;view.height=h;view.drawWidth=zoom?w*2:w;view.drawHeight=view.drawWidth*3/4;view.left=-panX;view.top=zoom?-panY:Math.max(0,(h-16-view.drawHeight)/2);view.paintClip(0,0,w,h-14);page.paint(g,view);
   if(selected==7){String[] lines={"Перенос на J2ME","Данила","\"DilettanteGamer\"","Григорьев"};for(int i=0;i<lines.length;i++)font.draw(g,lines[i],w/2,h/2+(i-2)*(w<200?19:27),font.fit(lines[i],w<200?16:24,w-8),0xfffbf0);}
   g.setColor(0x302013);g.fillRect(0,h-14,w,14);font.draw(g,(selected+1)+"/8  4/6  5 +/-  Назад",w/2,h-7,font.fit("8/8  4/6  5 +/-  Назад",w<200?12:16,w-4),0xfffbf0);
  }catch(IOException error){host.choose(0);}
 }
 private void change(int delta){selected=(selected+delta+8)%8;zoom=false;panX=panY=0;if(page!=null)page.clearCache();page=null;repaint();}
 protected void keyPressed(int key){input(key);}
 protected void keyRepeated(int key){int a=0;try{a=getGameAction(key);}catch(IllegalArgumentException ignored){}if(key!=KEY_NUM5&&a!=FIRE)input(key);}
 public void input(int key){
  int action=0;try{action=getGameAction(key);}catch(IllegalArgumentException ignored){}
  if(key==KEY_NUM0||key==-6)return;
  if(key==-7){close();host.choose(0);return;}
  if(action==UP)key=KEY_NUM2;else if(action==DOWN)key=KEY_NUM8;else if(action==LEFT)key=KEY_NUM4;else if(action==RIGHT)key=KEY_NUM6;else if(action==FIRE)key=KEY_NUM5;
  if(key==KEY_NUM5){zoom=!zoom;panX=panY=0;repaint();return;}
  if(zoom){if(key==KEY_NUM4)panX=Math.max(0,panX-getWidth()/4);if(key==KEY_NUM6)panX=Math.min(getWidth(),panX+getWidth()/4);if(key==KEY_NUM2)panY=Math.max(0,panY-getHeight()/4);if(key==KEY_NUM8)panY=Math.min(Math.max(0,getWidth()*3/2-getHeight()+14),panY+getHeight()/4);}
  else if(key==KEY_NUM4||key==KEY_NUM2)change(-1);else if(key==KEY_NUM6||key==KEY_NUM8)change(1);repaint();
 }
 protected void pointerPressed(int x,int y){if(y>getHeight()-16){close();host.choose(0);}else change(x<getWidth()/2?-1:1);}
 public void close(){if(page!=null)page.clearCache();page=null;}
 public void commandAction(Command c,Displayable d){close();host.choose(0);}
}
