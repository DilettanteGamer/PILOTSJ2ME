package pilots;
import java.io.IOException;
import javax.microedition.lcdui.*;
/** Original menu art and readable, paged keypad reference. */
public final class HelpCanvas extends Canvas implements CommandListener {
 private final MenuCanvas.Host host;
 private int selected;
 private static final String[][] PAGES={
  {"Основное управление","2/4/6/8 — курсор","5 — действие","1 — Шеф, 3 — Коллега","9 — сменить героя","7 — следующий предмет","# — инвентарь","* — масштаб"},
  {"Меню и сохранение","Правая клавиша — меню","Левая клавиша и 0","в игре не используются","Прогресс сохраняется","при выходе в меню","и смене локации","Продолжить — загрузка"},
  {"Отдельные головоломки","2/4/6/8 — выбор","5 — повернуть ручку","или участок провода","0 — вернуться в комнату","При верном решении","экран закроется сам"},
  {"Ящики","4/6 — движение","2 — к ящикам","8 — безопасная дорожка","Прыжок автоматический","5 — автомат с дорожки","если он есть в инвентаре","0 — выйти из мини-игры"},
  {"Канализация и полёт","Канализация: 4/6","Переходы между точками","После падения — старт","Полёт: 2 — вверх","8 — вниз","Удерживайте направление"}
 };
 public HelpCanvas(MenuCanvas.Host host){this.host=host;setFullScreenMode(true);addCommand(new Command("Назад",Command.BACK,1));setCommandListener(this);}
 protected void paint(Graphics g){int w=getWidth(),h=getHeight();try{MenuTheme theme=MenuTheme.get();theme.paint(g,w,h);OriginalFont font=theme.font;String[] lines=PAGES[selected];int top=12,step=Math.min(w<200?14:25,(h-36)/lines.length);g.setColor(0xcbc397);g.fillRect(3,top-6,w-6,step*lines.length+8);for(int i=0;i<lines.length;i++)font.draw(g,lines[i],w/2,top+i*step+step/2,font.fit(lines[i],w<200?12:17,w-12),0x101009);String footer=(selected+1)+"/"+PAGES.length+"  4/6  0 — назад";font.draw(g,footer,w/2,h-18,font.fit(footer,w<200?11:15,w-4),0xfffbf0);theme.copyright(g,w,h);}catch(IOException error){host.choose(0);}}
 protected void keyPressed(int key){int a=0;try{a=getGameAction(key);}catch(IllegalArgumentException ignored){}if(key==KEY_NUM0||key==-7){host.choose(0);return;}if(key==KEY_NUM4||key==KEY_NUM2||a==LEFT||a==UP)selected=(selected+PAGES.length-1)%PAGES.length;else if(key==KEY_NUM6||key==KEY_NUM8||key==KEY_NUM5||a==RIGHT||a==DOWN||a==FIRE)selected=(selected+1)%PAGES.length;repaint();}
 protected void pointerPressed(int x,int y){if(y>getHeight()-30)host.choose(0);else{selected=(selected+(x<getWidth()/2?PAGES.length-1:1))%PAGES.length;repaint();}}
 public void commandAction(Command c,Displayable d){host.choose(0);}
}
