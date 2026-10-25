package pilots;
import java.io.IOException;
import javax.microedition.lcdui.Graphics;
/** The original folder and game font, shared by all graphical menus. */
public final class MenuTheme {
 private static MenuTheme shared;
 public final OriginalFont font;
 private final IndexedBackground folder;
 private final Viewport view=new Viewport(128,160,128,160);
 public static MenuTheme get()throws IOException{if(shared==null)shared=new MenuTheme();return shared;}
 private MenuTheme()throws IOException{font=OriginalFont.get();folder=new IndexedBackground(getClass().getResourceAsStream("/ui/folder.p8"));}
 public void paint(Graphics g,int w,int h){view.width=w;view.height=h;view.drawWidth=w;view.drawHeight=h;view.left=view.top=0;view.paintClip(0,0,w,h);folder.paint(g,view);}
 public void copyright(Graphics g,int w,int h){String text="© GAMOS, 1997  © 1C, 1997";int size=font.fit(text,w<200?9:12,w-4);font.draw(g,text,w/2+1,h-(w<200?5:6)+1,size,0);font.draw(g,text,w/2,h-(w<200?5:6),size,0xfffbf0);}
}
