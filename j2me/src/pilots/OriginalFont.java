package pilots;
import java.io.*;
import javax.microedition.lcdui.Graphics;
/** Both original bitmap fonts: 38 Latin, 39 Russian keyboard encoding. */
public final class OriginalFont {
 private static OriginalFont shared;
 private final byte[][] mask=new byte[194][];
 private final short[] width=new short[194],height=new short[194],ox=new short[194],oy=new short[194];
 private static final String LATIN="qwertyuiop[]asdfghjkl;'zxcvbnm,./QWERTYUIOP{}ASDFGHJKL:\"ZXCVBNM<>?";
 private static final String RUSSIAN="йцукенгшщзхъфывапролджэячсмитьбю.ЙЦУКЕНГШЩЗХЪФЫВАПРОЛДЖЭЯЧСМИТЬБЮ,";
 public static OriginalFont get()throws IOException{if(shared==null)shared=new OriginalFont();return shared;}
 private OriginalFont()throws IOException{
  DataInputStream in=new DataInputStream(getClass().getResourceAsStream("/ui/original.font"));
  try{if(in.readInt()!=0x464e5431||in.readUnsignedShort()!=97)throw new IOException("Invalid original font");
   for(int i=0;i<194;i++){width[i]=in.readShort();height[i]=in.readShort();ox[i]=in.readShort();oy[i]=in.readShort();int n=in.readUnsignedShort();if(width[i]<1||width[i]>64||height[i]<1||height[i]>64||n!=(width[i]*height[i]+7)/8)throw new IOException("Invalid glyph");mask[i]=new byte[n];in.readFully(mask[i]);}
  }finally{in.close();}
 }
 private int glyph(char ch){
  if(ch=='©')return 97+64-32;
  if(ch=='ё')ch='е';if(ch=='Ё')ch='Е';
  int r=RUSSIAN.indexOf(ch);if(r>=0&&ch!='.'&&ch!=',')return 97+LATIN.charAt(r)-32;
  if(ch=='—'||ch=='–')ch='-';if(ch=='«'||ch=='»'||ch=='“'||ch=='”')ch='"';
  return ch>=32&&ch<=128?ch-32:'?'-32;
 }
 private int nativeWidth(String s){int n=0;for(int i=0;i<s.length();i++){int g=glyph(s.charAt(i));n+=width[g]+ox[g];}return n;}
 public int width(String s,int size){return Math.max(1,(nativeWidth(s)*size+23)/24);}
 public int fit(String s,int size,int max){while(size>6&&width(s,size)>max)size--;return size;}
 public void draw(Graphics graphics,String text,int cx,int cy,int size,int color){
  if(text==null||text.length()==0)return;
  int nw=nativeWidth(text),top=100,bottom=-100;
  for(int i=0;i<text.length();i++){if(text.charAt(i)==' ')continue;int g=glyph(text.charAt(i));top=Math.min(top,oy[g]);bottom=Math.max(bottom,oy[g]+height[g]);}
  if(bottom<=top)return;
  int w=Math.max(1,(nw*size+23)/24),h=Math.max(1,((bottom-top)*size+23)/24);int[] pixels=new int[w*h];int pen=0;
  for(int i=0;i<text.length();i++){
   int g=glyph(text.charAt(i));
   for(int yy=0;yy<h;yy++){int sy=top+yy*24/size-oy[g];if(sy<0||sy>=height[g])continue;
    int left=Math.max(0,(pen+ox[g])*size/24),right=Math.min(w,(pen+ox[g]+width[g])*size/24+1);
    for(int xx=left;xx<right;xx++){int sx=xx*24/size-pen-ox[g];if(sx<0||sx>=width[g])continue;int p=sy*width[g]+sx;if((mask[g][p>>3]&(128>>(p&7)))!=0)pixels[yy*w+xx]=0xff000000|color;}
   }pen+=width[g]+ox[g];
  }
  graphics.drawRGB(pixels,0,w,cx-w/2,cy-h/2,w,h,true);
 }
}
