package pilots;
import javax.microedition.lcdui.*;
/** Wrap every word and page long lines instead of silently truncating them. */
public final class CaptionPanel {
 private static String previous;
 private static String[] lines;
 private static int oldWidth;
 public static synchronized void paint(Graphics g,int width,int height,String text,int age){
  if(text==null||text.length()==0)return;Font font=Font.getFont(0,0,8);g.setFont(font);
  if(!text.equals(previous)||oldWidth!=width){String rest=text;String[] scratch=new String[text.length()+1];int count=0;while(rest.length()>0){int n=rest.length();while(n>1&&font.stringWidth(rest.substring(0,n))>width-4)n--;int space=rest.substring(0,n).lastIndexOf(' ');if(n<rest.length()&&space>0)n=space;scratch[count++]=rest.substring(0,n);rest=rest.substring(n).trim();}lines=new String[count];System.arraycopy(scratch,0,lines,0,count);previous=text;oldWidth=width;}
  int rows=Math.max(1,Math.min(4,(height/3-4)/font.getHeight()));int pages=(lines.length+rows-1)/rows;int page=Math.min(pages-1,Math.max(0,age)/3500),start=page*rows,count=Math.min(rows,lines.length-start);
  g.setColor(0);g.fillRect(0,0,width,count*font.getHeight()+4);g.setColor(0xffffff);for(int i=0;i<count;i++)g.drawString(lines[start+i],2,2+i*font.getHeight(),20);
 }
}
