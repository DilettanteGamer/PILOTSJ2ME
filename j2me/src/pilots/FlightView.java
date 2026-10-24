package pilots;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Font;
/** Fixed sky crop and the entire original horizontal flight path on every screen. */
public final class FlightView {
 private Viewport sky,stage;
 private final int[] order=new int[310];
 public Viewport stage(int width,int height){
  if(stage==null||stage.width!=width||stage.height!=height){stage=new Viewport(width,height,640,417);}
  stage.drawWidth=width;stage.drawHeight=Math.max(1,width*417/640);stage.left=0;stage.top=(height-stage.drawHeight)/2;stage.cameraX=stage.cameraY=0;stage.zoom=false;stage.paintClip(0,0,width,height);return stage;
 }
 public void paint(Graphics g,Scene scene,Viewport output,HotelWorld world){
  if(sky==null||sky.width!=output.width||sky.height!=output.height)sky=new Viewport(output.width,output.height,640,417);
  sky.drawWidth=output.width;sky.drawHeight=output.height;sky.left=sky.top=sky.cameraX=sky.cameraY=0;sky.paintClip(0,0,output.width,output.height);
  scene.background.paint(g,sky);Viewport v=stage(output.width,output.height);int n=0;
  for(int i=0;i<scene.actors.length;i++)if(scene.actors[i]!=null){int j=n;while(j>0&&layer(world,order[j-1])<layer(world,i)){order[j]=order[j-1];j--;}order[j]=i;n++;}
  for(int i=0;i<n;i++)scene.actors[order[i]].paint(g,v);
 }
 private int layer(HotelWorld world,int i){return world==null?0:i<300?(world.cells[i]==null?0:world.cells[i].layer):i<302?0:world.effects[i-302].layer;}
 public void panel(Graphics g,int width,int height,int bar,FlightLogic logic){
  Font font=Font.getFont(0,0,8);g.setFont(font);int top=height-bar;
  String[] labels={"2","5","8",logic==null?"0":"Б:"+logic.ballast()};
  for(int i=0;i<4;i++){int x=i*width/4,w=(i+1)*width/4-x;g.setColor(logic!=null&&logic.altitude()==3-i?0xeee4a5:0xaaa57f);g.fillRect(x+1,top+1,w-2,Math.max(1,bar-2));g.setColor(0x24251c);g.drawString(labels[i],x+(w-font.stringWidth(labels[i]))/2,top+Math.max(0,(bar-font.getHeight())/2),20);}
 }
}
