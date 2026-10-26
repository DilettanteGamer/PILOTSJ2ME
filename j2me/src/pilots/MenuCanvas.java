package pilots;
import javax.microedition.lcdui.*;
/** Folder menus, original bitmap text, keypad and touch hit boxes. */
public final class MenuCanvas extends Canvas {
 private static final String[] DEFAULT_ITEMS={"Новая игра","Продолжить","Локации","Звук вкл.","Об игре","Помощь","Выход"};
 private final Host host;
 private final MenuLayout layout;
 private final String[] items;
 private final String title;
 private final boolean mainMenu;
 private static String[] defaults(){String[] result=new String[DEFAULT_ITEMS.length];System.arraycopy(DEFAULT_ITEMS,0,result,0,result.length);return result;}
 public MenuCanvas(Host host){this(host,"",defaults(),true);}
 public MenuCanvas(Host host,String title,String[] items){this(host,title,items,false);}
 private MenuCanvas(Host host,String title,String[] items,boolean main){this.host=host;this.title=title;this.items=items;mainMenu=main;layout=new MenuLayout(items.length);setFullScreenMode(true);resizeMenu(getWidth(),getHeight());}
 public void sound(boolean enabled){if(mainMenu){items[3]=enabled?"Звук вкл.":"Звук выкл.";repaint();}}
 private void resizeMenu(int w,int h){
  layout.width=Math.max(1,w);layout.height=Math.max(1,h);
  if(mainMenu){layout.header=h*22/100;layout.rowHeight=Math.max(13,h*10/100);layout.footer=Math.max(7,h/25);layout.visible=7;layout.first=0;}
  else {layout.header=w<200?18:30;layout.footer=w<200?12:18;layout.rowHeight=w<200?18:32;layout.visible=Math.min(items.length,Math.max(1,(h-layout.header-layout.footer)/layout.rowHeight));}
  layout.select(layout.selected);
 }
 protected synchronized void sizeChanged(int w,int h){if(layout!=null)resizeMenu(w,h);repaint();}
 protected synchronized void showNotify(){resizeMenu(getWidth(),getHeight());repaint();}
 private int left(){return mainMenu?getWidth()*17/100:getWidth()/12;}
 private int buttonWidth(){return getWidth()-left()*2;}
 private int buttonHeight(){return mainMenu?Math.max(11,layout.rowHeight*2/3):layout.rowHeight-4;}
 protected synchronized void paint(Graphics g){
  int w=getWidth(),h=getHeight();if(layout.width!=w||layout.height!=h)resizeMenu(w,h);
  try{
   MenuTheme theme=MenuTheme.get();theme.paint(g,w,h);OriginalFont font=theme.font;
   if(!mainMenu)font.draw(g,title,w/2,layout.header/2,font.fit(title,w<200?14:20,w-10),0xfffbf0);
   for(int row=0;row<layout.visible;row++){
    int index=layout.first+row,y=layout.header+row*layout.rowHeight,bh=buttonHeight();
    g.setColor(index==layout.selected?0xdfdfcf:0xcbc397);g.fillRect(left(),y,buttonWidth(),bh);
    if(index==layout.selected){g.setColor(0x5d4a2c);g.drawRect(left(),y,buttonWidth()-1,bh-1);}
    int size=font.fit(items[index],w<200?12:16,buttonWidth()-6);font.draw(g,items[index],w/2,y+bh/2,size,0x101009);
   }
   if(layout.visible<layout.count){int track=layout.visible*layout.rowHeight,thumb=Math.max(4,track*layout.visible/layout.count);g.setColor(0x5d4a2c);g.fillRect(w-5,layout.header,2,track);g.setColor(0xdfdfcf);g.fillRect(w-5,layout.header+(track-thumb)*layout.first/(layout.count-layout.visible),2,thumb);}
   theme.copyright(g,w,h);
  }catch(java.io.IOException error){g.setColor(0);g.fillRect(0,0,w,h);g.setColor(0xffffff);g.drawString("Menu: "+error.toString(),0,0,20);}
 }
 protected void keyPressed(int key){input(key,false);}
 protected void keyRepeated(int key){input(key,true);}
 private void input(int key,boolean repeated){int action=0;try{action=getGameAction(key);}catch(IllegalArgumentException ignored){}
  if(key==KEY_NUM0||key==-6)return;
  if(!repeated&&key==-7){host.choose(mainMenu?-1:0);return;}
  if(key==KEY_NUM2||action==UP)move(-1);else if(key==KEY_NUM8||action==DOWN)move(1);else if(!repeated&&(key==KEY_NUM5||action==FIRE))host.choose(layout.selected);
 }
 public synchronized void select(int n){layout.select(n);repaint();}
 private synchronized void move(int n){layout.move(n);repaint();}
 protected void pointerPressed(int x,int y){int index; synchronized(this){if(x>=getWidth()-10&&layout.visible<layout.count){move(y<layout.header+layout.visible*layout.rowHeight/2?-layout.visible:layout.visible);return;}if(x<left()||x>=left()+buttonWidth())return;index=layout.itemAt(y);if(index<0||(y-layout.header)%layout.rowHeight>=buttonHeight())return;layout.select(index);}host.choose(index);}
 public static interface Host{void choose(int item);}
}
