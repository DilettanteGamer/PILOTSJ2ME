package javax.microedition.lcdui;
/** Desktop metrics for full-screen puzzle rendering. */
public final class Font {
 public static final int FACE_SYSTEM=0,STYLE_PLAIN=0,STYLE_BOLD=1,SIZE_SMALL=8,SIZE_MEDIUM=0,SIZE_LARGE=16;
 final java.awt.Font awt;
 private Font(int style,int size){awt=new java.awt.Font("Dialog",style==STYLE_BOLD?java.awt.Font.BOLD:java.awt.Font.PLAIN,size==SIZE_SMALL?10:size==SIZE_LARGE?16:12);}
 public static Font getFont(int face,int style,int size){return new Font(style,size);}
 public static Font getDefaultFont(){return new Font(0,0);}
 private java.awt.FontMetrics metrics(){return new java.awt.Canvas().getFontMetrics(awt);}
 public int getHeight(){return metrics().getHeight();}
 public int charWidth(char c){return metrics().charWidth(c);}
 public int stringWidth(String s){return metrics().stringWidth(s);}
}
