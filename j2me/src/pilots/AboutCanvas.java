package pilots;
import java.io.IOException;
import java.util.Vector;
import javax.microedition.lcdui.*;
/** Readable, screen-sized credit pages on the original leather background. */
public final class AboutCanvas extends Canvas {
 private static final String[] TEXT={
  "Перенос на J2ME:","{Данила \"DilettanteGamer\" Григорьев}"
 };
 private final MenuCanvas.Host host;
 private IndexedBackground background;
 private String[] lines,marks;
 private int[] sizes;
 private int selected,layoutWidth,layoutHeight,fontSize,step,rows,top,pages;
 private final Viewport view=new Viewport(128,160,640,480);
 public AboutCanvas(MenuCanvas.Host host){this.host=host;setFullScreenMode(true);}
 private void layout()throws IOException{
  int w=getWidth(),h=getHeight();if(lines!=null&&layoutWidth==w&&layoutHeight==h)return;
  OriginalFont font=OriginalFont.get();layoutWidth=w;layoutHeight=h;fontSize=w<200?16:24;step=w<200?17:29;top=w<200?22:34;
  rows=Math.max(1,(h-top-(w<200?18:26))/step);
  Vector textRows=new Vector(),colorRows=new Vector(),sizeRows=new Vector();
  for(int p=0;p<TEXT.length;p++){
   // Keep the port credit on a dedicated page, including the complete name.
   if(p==TEXT.length-2)while(textRows.size()%rows!=0){textRows.addElement("");colorRows.addElement("");sizeRows.addElement(new Integer(fontSize));}
   StringBuffer plain=new StringBuffer(),color=new StringBuffer();boolean yellow=false;
   for(int i=0;i<TEXT[p].length();i++){char c=TEXT[p].charAt(i);if(c=='{'){yellow=true;continue;}if(c=='}'){yellow=false;continue;}plain.append(c);color.append(yellow?'y':'w');}
   String text=plain.toString(),mask=color.toString();int start=0,size=fontSize;
   // A long unbroken name uses a slightly smaller original font, never a tiny page image.
   for(int word=0;word<text.length();){int end=text.indexOf(' ',word);if(end<0)end=text.length();while(size>12&&font.width(text.substring(word,end),size)>w-12)size--;word=end+1;}
   while(start<text.length()){
    int end=start+1;while(end<=text.length()&&font.width(text.substring(start,end),size)<=w-12)end++;
    end=Math.max(start+1,end-1);
    if(end<text.length()&&text.charAt(end)!=' '){int space=text.lastIndexOf(' ',end);if(space>start)end=space;}
    textRows.addElement(text.substring(start,end));colorRows.addElement(mask.substring(start,end));sizeRows.addElement(new Integer(size));start=end;while(start<text.length()&&text.charAt(start)==' ')start++;
   }
  }
  lines=new String[textRows.size()];marks=new String[lines.length];textRows.copyInto(lines);colorRows.copyInto(marks);sizes=new int[lines.length];for(int i=0;i<sizes.length;i++)sizes[i]=((Integer)sizeRows.elementAt(i)).intValue();pages=(lines.length+rows-1)/rows;selected=Math.min(selected,pages-1);
 }
 protected void paint(Graphics g){
  int w=getWidth(),h=getHeight();g.setColor(0x302013);g.fillRect(0,0,w,h);
  try{
   layout();if(background==null)background=new IndexedBackground(getClass().getResourceAsStream("/ui/leather.p8"));
   view.width=w;view.height=h;view.drawWidth=w;view.drawHeight=h;view.left=view.top=0;view.paintClip(0,0,w,h);background.paint(g,view);
   OriginalFont font=OriginalFont.get();font.draw(g,"Об игре",w/2,w<200?10:16,w<200?14:22,0xfffbf0);
   for(int row=0;row<rows;row++){int n=selected*rows+row;if(n>=lines.length)break;font.draw(g,lines[n],w/2,top+row*step+step/2,sizes[n],0xfffbf0,0xffe35a,marks[n]);}
   String footer="4/6  "+(selected+1)+"/"+pages+"  Назад: правая";font.draw(g,footer,w/2,h-(w<200?8:12),font.fit(footer,w<200?11:16,w-6),0xfffbf0);
  }catch(IOException error){host.choose(0);}
 }
 protected void sizeChanged(int w,int h){layoutWidth=0;repaint();}
 protected void keyPressed(int key){input(key);}
 protected void keyRepeated(int key){if(key!=-7)input(key);}
 public void input(int key){
  if(key==-7){close();host.choose(0);return;}if(key==KEY_NUM0||key==-6)return;
  try{layout();}catch(IOException error){host.choose(0);return;}
  int a=0;try{a=getGameAction(key);}catch(IllegalArgumentException ignored){}
  if(key==KEY_NUM4||key==KEY_NUM2||a==LEFT||a==UP)selected=Math.max(0,selected-1);
  else if(key==KEY_NUM6||key==KEY_NUM8||a==RIGHT||a==DOWN)selected=Math.min(pages-1,selected+1);repaint();
 }
 protected void pointerPressed(int x,int y){if(y>getHeight()-18){close();host.choose(0);}else input(x<getWidth()/2?KEY_NUM4:KEY_NUM6);}
 public void close(){if(background!=null)background.clearCache();background=null;}
}
