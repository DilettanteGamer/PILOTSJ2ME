package pilots;
import java.util.Arrays;import java.io.File;import javax.imageio.ImageIO;import javax.microedition.lcdui.*;
/** Real menu controls, bitmap text centering, permanent unlocks and cross-launch Continue. */
public final class GraphicalMenuTest {
 static void check(boolean b,String s){if(!b)throw new RuntimeException(s);}
 static Object get(Object o,String f)throws Exception{return LocationEntryTest.get(o,f);}
 static void set(Object o,String f,Object v)throws Exception{LocationEntryTest.set(o,f,v);}
 static void choose(MenuCanvas m,int n)throws Exception{((MenuCanvas.Host)get(m,"host")).choose(n);}
 static PilotsMidlet app()throws Exception{PilotsMidlet a=new PilotsMidlet();set(a,"display",Display.getDisplay(a));set(a,"canvas",a.new GameCanvas());return a;}
 static MenuCanvas current(){return (MenuCanvas)Display.getDisplay(null).getCurrent();}
 static void paint(MenuCanvas m,int w,int h,String path)throws Exception{
  Graphics g=new Graphics(w,h);m.paint(g);ImageIO.write(g.image,"png",new File(path));
  MenuLayout layout=(MenuLayout)get(m,"layout");
  int left=w*17/100,bh=Math.max(11,layout.rowHeight*2/3);
  for(int row=0;row<7;row++){
   int top=layout.header+row*layout.rowHeight,minX=w,maxX=-1,minY=h,maxY=-1;
   check((g.image.getRGB(left+2,top+2)&0xffffff)==(row==layout.selected?0xdfdfcf:0xcbc397),"original button color");
   for(int y=top+1;y<top+bh-1;y++)for(int x=left+1;x<w-left-1;x++)if((g.image.getRGB(x,y)&0xffffff)==0x101009){minX=Math.min(minX,x);maxX=Math.max(maxX,x);minY=Math.min(minY,y);maxY=Math.max(maxY,y);}
   check(maxX>=minX&&maxY>=minY,"missing original text row "+row);
   check(Math.abs(minX+maxX-w)<=3&&Math.abs(minY+maxY-(2*top+bh))<=3,"uncentered original text row "+row+" "+minX+","+maxX+","+minY+","+maxY);
  }
 }
 public static void main(String[] args)throws Exception{
  int w=Integer.parseInt(args[0]),h=Integer.parseInt(args[1]);System.setProperty("pilots.test.width",args[0]);System.setProperty("pilots.test.height",args[1]);String out=args[2];
  PilotsMidlet first=app(),second=null,third=null;PilotsMidlet.GameCanvas c=(PilotsMidlet.GameCanvas)get(first,"canvas");
  try{
   PlayerProgress p=(PlayerProgress)get(first,"progress");check(!p.started&&p.available(2)&&!p.available(3),"fresh progress");
   first.choose(2);check(((String[])get(current(),"items")).length==2,"fresh locations expose future levels");choose(current(),0);
   MenuCanvas main=current();check(((String[])get(main,"items")).length==7,"main item count");paint(main,w,h,out+"-main.png");
   main.keyPressed(-2);check(((MenuLayout)get(main,"layout")).selected==1,"D-pad menu navigation");
   first.choose(0);check(Display.getDisplay(null).getCurrent()==c&&p.started,"first New Game asks unnecessarily");
   c.keyPressed(48);c.keyPressed(-6);check(Display.getDisplay(null).getCurrent()==c,"disabled main-game menu keys");c.keyPressed(-7);check(Display.getDisplay(null).getCurrent() instanceof MenuCanvas,"right game menu key");
   GameAudio menuAudio=(GameAudio)get(c,"audio");check(((Integer)get(menuAudio,"scene")).intValue()==2&&((Boolean)get(menuAudio,"menuMode")).booleanValue()&&((Boolean)get(menuAudio,"active")).booleanValue(),"first location loop in menu");
   c.hideNotify();check(((Boolean)get(menuAudio,"active")).booleanValue(),"delayed game hide stopped menu music");first.pauseApp();check(!((Boolean)get(menuAudio,"active")).booleanValue(),"pause left menu playing");first.startApp();check(((Boolean)get(menuAudio,"active")).booleanValue(),"resume lost menu music");
   set(c,"stopped",Boolean.TRUE);c.showNotify();check(!menuAudio.inMenu(),"game retained menu track");set(c,"stopped",Boolean.FALSE);c.hideNotify();c.keyPressed(-7);
   first.choose(5);HelpCanvas help=(HelpCanvas)Display.getDisplay(null).getCurrent();Graphics hg=new Graphics(w,h);help.paint(hg);for(int page=1;page<5;page++){help.keyPressed(54);help.paint(hg);}check(((Integer)get(help,"selected")).intValue()==4,"help pages");ImageIO.write(hg.image,"png",new File(out+"-help.png"));help.keyPressed(48);check(Display.getDisplay(null).getCurrent() instanceof MenuCanvas,"help back");
   byte[] before=((GameState)get(c,"state")).encode();first.choose(0);MenuCanvas confirm=current();check(((String[])get(confirm,"items")).length==2,"missing New Game confirmation");choose(confirm,0);check(Arrays.equals(before,((GameState)get(c,"state")).encode()),"Cancel reset game");
   c.startLocation(3);first.choose(2);check(((String[])get(current(),"items")).length==3,"visited location not unlocked");choose(current(),0);
   first.choose(0);choose(current(),1);check(((GameState)get(c,"state")).scene==2&&p.available(3),"New Game erases permanent unlocks");
   for(int scene:PlayerProgress.ORDER)p.open(scene);first.choose(2);MenuCanvas locations=current();check(((String[])get(locations,"items")).length==17,"all unlocked locations missing");
   locations.select(16);Graphics list=new Graphics(w,h);locations.paint(list);ImageIO.write(list.image,"png",new File(out+"-locations.png"));choose(locations,0);
   c.startLocation(7);c.tick(100);c.save();first.choose(3);check(!p.sound&&((String[])get(current(),"items"))[3].equals("Звук выкл."),"sound toggle label");
   second=app();PilotsMidlet.GameCanvas c2=(PilotsMidlet.GameCanvas)get(second,"canvas");PlayerProgress p2=(PlayerProgress)get(second,"progress");check(p2.available(3)&&!p2.sound,"cross-launch prefs/unlocks");
   second.choose(1);check(((GameState)get(c2,"state")).scene==7,"Continue overwrote save with startup scene");
   // Without a readable save, Continue uses the recorded last-opened checkpoint.
   javax.microedition.rms.RecordStore rms=javax.microedition.rms.RecordStore.openRecordStore("pilots-port-v1",true);rms.setRecord(1,new byte[]{0},0,1);rms.closeRecordStore();
   third=app();PilotsMidlet.GameCanvas c3=(PilotsMidlet.GameCanvas)get(third,"canvas");third.choose(1);check(((GameState)get(c3,"state")).scene==7,"Continue checkpoint fallback");
   c3.startLocation(17);((HotelScene)get(c3,"zoo")).animals.world.completed=true;c3.tick(100);check(((PlayerProgress)get(third,"progress")).finished,"completion not persisted");
   third.choose(1);check(Display.getDisplay(null).getCurrent()==c3&&((GameState)get(c3,"state")).scene==2,"completed Continue did not start New Game");
   third.choose(4);AboutCanvas about=(AboutCanvas)Display.getDisplay(null).getCurrent();Graphics g=new Graphics(w,h);about.paint(g);ImageIO.write(g.image,"png",new File(out+"-credits.png"));
   for(int i=0;i<7;i++)about.input(54);about.paint(g);check(((Integer)get(about,"selected")).intValue()==7,"port credit page missing");ImageIO.write(g.image,"png",new File(out+"-port-author.png"));
   about.input(53);about.paint(g);about.input(48);check(Display.getDisplay(null).getCurrent() instanceof MenuCanvas,"About back");
   third.choose(6);check(third.destroyed,"Exit did not close MIDlet");
   System.out.println("GraphicalMenu: seven entries, original centered fonts, confirmation/cancel, saved and checkpoint Continue, permanent unlocks, sound persistence, complete/restart, original credits and port author, Exit at "+w+"x"+h+" passed");
  }finally{first.destroyApp(true);if(second!=null)second.destroyApp(true);if(third!=null)third.destroyApp(true);}
 }
}
