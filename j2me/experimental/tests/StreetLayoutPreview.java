package pilots;
import javax.microedition.lcdui.*;import java.io.File;import javax.imageio.ImageIO;
public class StreetLayoutPreview{
 static void shot(String file,boolean whole)throws Exception{
  Scene s=StreetRenderTest.scene;StreetScene st=StreetRenderTest.street;int width=128,height=160,bar=(width*s.panel.worldHeight+s.panel.worldWidth/2)/s.panel.worldWidth;
  Viewport v=new Viewport(width,height-bar,s.background.worldWidth,s.background.worldHeight);v.cursorX=st.logic.heroX(st.logic.hero());v.cursorY=st.logic.heroY(st.logic.hero());v.update();
  if(whole){v.drawWidth=width;v.drawHeight=width*v.worldHeight/v.worldWidth;v.cameraX=v.cameraY=0;v.left=0;v.top=(v.height-v.drawHeight)/2;}
  byte[] before=StreetRenderTest.state.encode();Graphics g=new Graphics(width,height);s.background.paint(g,v);st.paint(g,v);s.panel.paintPanel(g,width,height,bar);ZooPanel panel=new ZooPanel(6);panel.paint(g,st.logic,width,height,bar);panel.close();ImageIO.write(g.image,"png",new File(file));if(!java.util.Arrays.equals(before,StreetRenderTest.state.encode()))throw new Exception("Preview paint changed game state");
 }
 public static void main(String[] a)throws Exception{
  StreetRenderTest.scene=new Scene(6);StreetRenderTest.state=new GameState();StreetRenderTest.state.scene=6;StreetRenderTest.street=new StreetScene(StreetRenderTest.scene,StreetRenderTest.state);
  try{StreetRenderTest.act(19);StreetRenderTest.settle();StreetRenderTest.act(18);StreetRenderTest.settle();StreetRenderTest.act(17);StreetRenderTest.settle();int limit=2000;while(StreetRenderTest.street.animals.world.at(9,18)!=25&&limit-->0)StreetRenderTest.street.update(100);if(limit<=0)throw new Exception("No crossing opportunity");
   shot(a[0]+"-current.png",false);shot(a[0]+"-overview.png",true);
   for(StreetWorld.Thing t:StreetRenderTest.street.animals.world.cells)if(t!=null&&(t.id==25||t.id==16||t.id==17||t.id==20||t.id==19))System.out.println(t.id+"@"+t.x+","+t.y+" clip="+t.clip);
  }finally{StreetRenderTest.scene.close();}
 }
}
