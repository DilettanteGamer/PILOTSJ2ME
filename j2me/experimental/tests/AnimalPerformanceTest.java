package pilots;
import java.security.MessageDigest;
import javax.microedition.lcdui.*;
/** Identical arrival ticks with four paints, cursor damage, camera movement and zoom. */
public final class AnimalPerformanceTest {
 private static String hex(byte[] b){String s="";for(byte n:b)s+=Integer.toHexString((n&255)|256).substring(1);return s;}
 public static void main(String[] args)throws Exception {
  int initialFrames=TileStore.frameCalls,initialBanks=TileStore.loads+TileStore.directLoads;long initialBytes=TileStore.rawDecoded;
  GameState state=new GameState();state.scene=4;Scene scene=new Scene(4);AnimalScene animals=new AnimalScene(scene,state);
  System.out.println("animal_initialization pixel_frames="+(TileStore.frameCalls-initialFrames)+" bank_reads="+(TileStore.loads+TileStore.directLoads-initialBanks)+" raw_bytes="+(TileStore.rawDecoded-initialBytes));
  Viewport v=new Viewport(128,148,640,417);Graphics g=new Graphics(128,160);
  MessageDigest pixels=MessageDigest.getInstance("SHA-256"),logic=MessageDigest.getInstance("SHA-256");long update=0,paint=0;
  long decoded=TileStore.rawDecoded;int banks=TileStore.loads,direct=TileStore.directLoads,opens=PackedResources.opens,alloc=Image.allocations;
  try {
   for(int tick=0;tick<400;tick++) {
    long began=System.nanoTime();animals.update(100);update+=System.nanoTime()-began;logic.update(state.encode());
    int base=tick%80<40?320:tick%160<80?150:500;
    for(int p=0;p<4;p++) {
     int oldX=v.screenX(v.cursorX),oldY=v.screenY(v.cursorY),oldLeft=v.left;
     v.cursorX=base+(p%2)*3;v.cursorY=240;v.zoom=tick>=320;v.update();int x=v.screenX(v.cursorX),y=v.screenY(v.cursorY);
     int cx=p==0||oldLeft!=v.left?0:Math.min(x,oldX)-6,cy=p==0||oldLeft!=v.left?0:Math.min(y,oldY)-6;
     int cw=p==0||oldLeft!=v.left?128:Math.abs(x-oldX)+13,ch=p==0||oldLeft!=v.left?148:Math.abs(y-oldY)+13;
     g.setClip(cx,cy,cw,ch);v.paintClip(cx,cy,cw,ch);began=System.nanoTime();
     scene.background.paint(g,v);animals.paint(g,v);g.setColor(0xffffff);g.drawLine(x-4,y,x+4,y);g.drawLine(x,y-4,x,y+4);animals.targetAt(v.cursorX,v.cursorY);animals.hoverText(v.cursorX,v.cursorY);paint+=System.nanoTime()-began;
     for(int yy=0;yy<148;yy++)for(int xx=0;xx<128;xx++){int rgb=g.image.getRGB(xx,yy);pixels.update((byte)(rgb>>16));pixels.update((byte)(rgb>>8));pixels.update((byte)rgb);}
    }
   }
   System.out.println("animal_arrival ticks=400 paints=1600 update_ms="+update/1000000+" paint_ms="+paint/1000000+" banks="+(TileStore.loads-banks)+" linear="+(TileStore.directLoads-direct)+" opens="+(PackedResources.opens-opens)+" raw_bytes="+(TileStore.rawDecoded-decoded)+" images="+(Image.allocations-alloc));
   String a=hex(logic.digest()),b=hex(pixels.digest());System.out.println("state_sha256="+a+" pixel_sha256="+b);
   if(!a.equals("95634f5c24b1da3977c93539cd6ebcd8d1bb649dc90f951413c9bad527a265a1") || !b.equals("fb84fa51b3254afe6c144a40a80bd8b63888c4f954f22ce2cd248c86703504e3"))throw new Exception("Animal arrival pixels or quest ticks differ from 0.27.13");
  } finally {scene.close();}
 }
}
