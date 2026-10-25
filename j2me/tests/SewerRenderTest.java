package pilots;
import java.io.File;import javax.imageio.ImageIO;import javax.microedition.lcdui.Graphics;
public final class SewerRenderTest{
 static void paint(SewerScene sewer,Scene scene,int w,int h,String name)throws Exception{Graphics g=new Graphics(w,h);sewer.paint(g,new Viewport(w,h,432,256));ImageIO.write(g.image,"png",new File(name+".png"));}
 static void visibleHits(int w,int h)throws Exception{
  int hits=0;boolean middleHit=false;
  GameState pixelState=new GameState();pixelState.scene=7;Scene pixelScene=new Scene(7);SewerScene pixels=new SewerScene(pixelScene,pixelState);
  try{for(int f=0;f<20;f++){
   for(int r=0;r<3;r++){AnimatedActor a=pixelScene.actors[r+1];for(int x=128;x<=480;x++){
    int count=0;for(int y=235;y<=272;y++)if(a.clip.contains(x-104-a.x,y-144-a.y))count++;boolean visible=count>=4;
    SewerWorldTest.check(visible==pixels.sewer.rats.hits(r,f,x),"rat collision does not follow painted frame "+r+":"+f+":"+x);
   }}pixels.update(100);
  }}finally{pixelScene.close();}
  for(int delay=0;delay<20;delay++){
   GameState g=new GameState();g.scene=7;Scene s=new Scene(7);SewerScene view=new SewerScene(s,g);
   try{for(int n=0;n<delay;n++)view.update(100);
    for(int n=0;n<60&&!view.sewer.completed();n++){
     view.input(4);view.update(100);
     if(view.sewer.fall==1){
      int point=view.sewer.fallX+32;boolean visible=false;
      for(int r=0;r<3;r++)if(view.sewer.dangerous(r)){
       AnimatedActor a=s.actors[r+1];int count=0;
       for(int y=235;y<=272;y++)if(a.clip.contains(point-104-a.x,y-144-a.y))count++;boolean contact=count>=4;
       if(contact){visible=true;if(r==1)middleHit=true;}
      }
      SewerWorldTest.check(visible,"damage without a visible rat at frame "+view.sewer.clock%20+" x="+point);hits++;break;
     }
    }
   }finally{s.close();}
  }
  SewerWorldTest.check(hits>0&&middleHit,"visible middle rat strike not exercised");
 }
 public static void main(String[] args)throws Exception{
  int w=Integer.parseInt(args[0]),h=Integer.parseInt(args[1]);visibleHits(w,h);GameState state=new GameState();state.scene=7;Scene scene=new Scene(7);SewerScene sewer=new SewerScene(scene,state);
  try{
   SewerWorldTest.check(scene.panel==null,"sewer inventory still visible");paint(sewer,scene,w,h,args[2]+"-entry");
   byte[] before=state.encode();sewer.update(0);SewerWorldTest.check(java.util.Arrays.equals(before,state.encode()),"paused sewer moved");
   for(int i=0;i<100&&sewer.sewer.fall==0;i++){sewer.input(4);sewer.update(100);}
   SewerWorldTest.check(sewer.sewer.fall>0,"physical rat fall");paint(sewer,scene,w,h,args[2]+"-fall");
   while(sewer.sewer.fall>0){sewer.update(100);if(sewer.sewer.fall>=21&&sewer.sewer.fall<=50){AnimatedActor a=scene.actors[8];SewerWorldTest.check(a.y+a.clip.offsetY>=144,"hero emerges before reaching starting platform");}if(sewer.sewer.fall>=76){AnimatedActor a=scene.actors[8];SewerWorldTest.check(a.y+a.clip.offsetY+a.clip.height<=145,"speech before landing on platform");}if(sewer.sewer.fall>=51){SewerWorldTest.check(scene.actors[8].x==24&&scene.actors[8].y==224,"climb or speech dragged away from starting platform");}if(sewer.sewer.fall==41){SewerWorldTest.check(scene.actors[8].id==66,"swimming clip missing");paint(sewer,scene,w,h,args[2]+"-swim");}if(sewer.sewer.fall==79){SewerWorldTest.check(scene.actors[8].id==66&&scene.actors[8].clip.index>=75,"return speech missing");paint(sewer,scene,w,h,args[2]+"-return");}}paint(sewer,scene,w,h,args[2]+"-retry");
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
