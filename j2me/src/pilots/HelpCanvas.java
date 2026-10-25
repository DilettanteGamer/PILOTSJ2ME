package pilots;
import java.io.IOException;
import javax.microedition.lcdui.*;
/** Original menu art and readable keypad reference. */
public final class HelpCanvas extends Canvas implements CommandListener {
 private final MenuCanvas.Host host;
 private static final String[] LINES={"Основное управление","2/4/6/8 — курсор","5 — действие","1 — Шеф, 3 — Коллега","9 — сменить героя","7 — следующий предмет","# — инвентарь","* — масштаб"};
 public HelpCanvas(MenuCanvas.Host host){this.host=host;setFullScreenMode(true);addCommand(new Command("Назад",Command.BACK,1));setCommandListener(this);}
 protected void paint(Graphics g){int w=getWidth(),h=getHeight();try{MenuTheme theme=MenuTheme.get();theme.paint(g,w,h);OriginalFont font=theme.font;String[] lines=LINES;int top=12,step=Math.min(w<200?14:25,(h-36)/lines.length);g.setColor(0xcbc397);g.fillRect(3,top-6,w-6,step*lines.length+8);for(int i=0;i<lines.length;i++)font.draw(g,lines[i],w/2,top+i*step+step/2,font.fit(lines[i],w<200?12:17,w-12),0x101009);String footer="Правая клавиша — назад";font.draw(g,footer,w/2,h-18,font.fit(footer,w<200?11:15,w-4),0xfffbf0);theme.copyright(g,w,h);}catch(IOException error){host.choose(0);}}
 protected void keyPressed(int key){if(key==-7)host.choose(0);}
 protected void pointerPressed(int x,int y){if(y>getHeight()-30)host.choose(0);}
 public void commandAction(Command c,Displayable d){host.choose(0);}
}
