package pilots;
import java.io.File;import javax.imageio.ImageIO;import javax.microedition.lcdui.Graphics;
public final class SewerRenderTest{
 static void paint(SewerScene sewer,Scene scene,int w,int h,String name)throws Exception{Graphics g=new Graphics(w,h);sewer.paint(g,new Viewport(w,h,432,208));ImageIO.write(g.image,"png",new File(name+".png"));}
 public static void main(String[] args)throws Exception{
  int w=Integer.parseInt(args[0]),h=Integer.parseInt(args[1]);GameState state=new GameState();state.scene=7;Scene scene=new Scene(7);SewerScene sewer=new SewerScene(scene,state);
  try{
   SewerWorldTest.check(scene.panel==null,"sewer inventory still visible");paint(sewer,scene,w,h,args[2]+"-entry");
   byte[] before=state.encode();sewer.update(0);SewerWorldTest.check(java.util.Arrays.equals(before,state.encode()),"paused sewer moved");
   for(int i=0;i<100&&sewer.sewer.fall==0;i++){sewer.input(4);sewer.update(100);}
   SewerWorldTest.check(sewer.sewer.fall>0,"physical rat fall");paint(sewer,scene,w,h,args[2]+"-fall");
   while(sewer.sewer.fall>0)sewer.update(100);paint(sewer,scene,w,h,args[2]+"-retry");
   scene.close();state=new GameState();state.scene=7;scene=new Scene(7);sewer=new SewerScene(scene,state);
   for(int key:SewerWorldTest.solution()){
    sewer.input(key);if(sewer.sewer.motion>0){AnimatedActor actor=scene.actors[8];SewerWorldTest.check(actor.x==sewer.sewer.visibleX()-104&&actor.id>=20028&&actor.id<=20033,"walking sprite jumped to next safe stop");}
    sewer.update(100);
   }
   SewerWorldTest.check(sewer.sewer.completed(),"rendered timed crossing blocked");paint(sewer,scene,w,h,args[2]+"-finish");
   System.out.println("SewerRender: cropped bridge/platforms, three original attacking rats and ambient mice, no panel, original walking/fall and single crossing at "+w+"x"+h+" passed");
  }finally{scene.close();}
 }
}
