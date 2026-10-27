package pilots;
import javax.microedition.lcdui.*;
/** Diagnostic data only. No forced GC, image creation or polling thread. */
public final class DebugHud {
 private static int mode=2,scene,sceneLoad,visible,totalActors;
 private static long window=System.currentTimeMillis(),snapshot,pulse,lastPaint;
 private static int frames,ticks,changes,fps10,tickRate,changeRate;
 private static int update,updateMax,paint,paintMax,gap,load,loadMax,audio,audioMax,queue,effects,hud,hudMax;
 private static long minFree=Long.MAX_VALUE;
 private static int oldAudioReuses;
 private static int oldBanks,oldDirect,oldCalls,oldHits,oldRequests,oldRasterHits,oldOpens;
 private static String[] labels;private static int[] pixels;private static int pixelWidth,pixelHeight;
 private static int time(long value){return (int)Math.max(0L,Math.min(9999L,value));}
 private static int rate(int count,long duration){return (int)Math.min(999L,(long)count*1000L/Math.max(1L,duration));}
 private static int percent(int hits,int requests){return requests<=0?0:(int)Math.min(100L,(long)hits*100L/requests);}
 public static synchronized int mode(){return mode;}
 public static synchronized void toggle(){mode=(mode+2)%3;pixels=null;labels=null;resume();}
 public static synchronized void resume(){window=System.currentTimeMillis();snapshot=pulse=lastPaint=0;frames=ticks=changes=0;fps10=tickRate=changeRate=0;labels=null;}
 public static synchronized void scene(int value){scene=value;sceneLoad=0;update=updateMax=paint=paintMax=gap=load=loadMax=audio=audioMax=hud=hudMax=0;minFree=Long.MAX_VALUE;oldAudioReuses=GameAudio.effectReuses;oldBanks=TileStore.loads;oldDirect=TileStore.directLoads;oldCalls=TileStore.frameCalls;oldHits=TileStore.frameCacheHits;oldRequests=FrameRasters.requests;oldRasterHits=FrameRasters.hits;oldOpens=PackedResources.opens;resume();}
 public static synchronized void sceneCost(long duration){sceneLoad=time(duration);}
 public static synchronized void tick(long duration){ticks++;update=time(duration);updateMax=Math.max(updateMax,update);}
 public static synchronized void changed(){changes++;}
 public static synchronized void painted(long now,long duration){frames++;paint=time(duration);paintMax=Math.max(paintMax,paint);if(lastPaint!=0)gap=Math.max(gap,time(now-lastPaint));lastPaint=now;}
 public static synchronized void io(long duration){load=time(duration);loadMax=Math.max(loadMax,load);}
 public static synchronized void audio(long duration){audio=time(duration);audioMax=Math.max(audioMax,audio);}
 public static synchronized void audioState(int pending,int playing){queue=pending;effects=playing;}
 private static synchronized boolean due(long now){if(mode==0||now-pulse<1000)return false;pulse=now;return true;}
 public static void pulse(Canvas target,int height){if(due(System.currentTimeMillis())){int rows=mode()==2?7:2;target.repaint(0,Math.max(0,height-rows*8),target.getWidth(),Math.min(height,rows*8));}}
 private static void sample(long now){
  long elapsed=now-window;if(elapsed>=1000){fps10=(int)Math.min(9999L,((long)frames*10000L+elapsed/2)/elapsed);tickRate=rate(ticks,elapsed);changeRate=rate(changes,elapsed);frames=ticks=changes=0;window=now;}
  int banks=TileStore.loads,direct=TileStore.directLoads,calls=TileStore.frameCalls,hits=TileStore.frameCacheHits,requests=FrameRasters.requests,rasterHits=FrameRasters.hits,opens=PackedResources.opens;
  Runtime runtime=Runtime.getRuntime();long free=runtime.freeMemory(),size=runtime.totalMemory();minFree=Math.min(minFree,free);
  labels=new String[]{"F"+(fps10/10)+"."+(fps10%10)+" T"+tickRate+" A"+changeRate+" S"+scene+"/"+sceneLoad,
   "U"+update+"/"+updateMax+" P"+paint+"/"+paintMax,
   "G"+gap+" L"+load+"/"+loadMax,
   "B+"+Math.min(999,banks-oldBanks)+" I+"+Math.min(999,direct-oldDirect)+" C"+percent(hits-oldHits,calls-oldCalls)+"% R"+percent(rasterHits-oldRasterHits,requests-oldRequests)+"%",
   "M"+((size-free)/1024)+"/"+(size/1024)+"K N"+(minFree/1024)+"K",
   "V"+visible+"/"+totalActors+" D"+hud+"/"+hudMax+" O+"+Math.min(999,opens-oldOpens),
   "AU"+audio+"/"+audioMax+" Q"+queue+" E"+effects+" H+"+Math.min(99,GameAudio.effectReuses-oldAudioReuses)};
  oldAudioReuses=GameAudio.effectReuses;oldBanks=banks;oldDirect=direct;oldCalls=calls;oldHits=hits;oldRequests=requests;oldRasterHits=rasterHits;oldOpens=opens;snapshot=now;
 }
 private static synchronized int[] prepare(int width,int height,int actors,int shown,long now){
  if(mode==0||width<1||height<1)return null;totalActors=actors;visible=shown;
  int rows=mode==2?7:2,w=Math.min(width,128),h=Math.min(height,rows*8);
  try{boolean rebuild=labels==null||now-snapshot>=1000;if(rebuild)sample(now);if(pixels==null||pixelWidth!=w||pixelHeight!=h){pixels=new int[w*h];pixelWidth=w;pixelHeight=h;rebuild=true;}
   if(rebuild){for(int i=0;i<pixels.length;i++)pixels[i]=0;for(int line=0;line<rows;line++)text(labels[line],w,h,1,line*8);}return pixels;
  }catch(OutOfMemoryError low){mode=0;pixels=null;labels=null;return null;}
 }
 private static synchronized void hudCost(long duration){hud=time(duration);hudMax=Math.max(hudMax,hud);}
 public static void paint(Graphics g,int width,int height,int actors,int shown){
  long now=System.currentTimeMillis();int[] bitmap=prepare(width,height,actors,shown,now);if(bitmap==null)return;
  int w=Math.min(width,128),h=bitmap.length/w,y=Math.max(0,height-h),cx=g.getClipX(),cy=g.getClipY(),cw=g.getClipWidth(),ch=g.getClipHeight();
  try{g.clipRect(0,y,w,h);g.drawRGB(bitmap,0,w,0,y,w,h,false);}finally{g.setClip(cx,cy,cw,ch);hudCost(System.currentTimeMillis()-now);}
 }

 private static void text(String value,int w,int h,int left,int top){int step=value.length()*6<=w-left?6:5;for(int i=0;i<value.length()&&left+5<=w;i++,left+=step){int glyph=CHARS.indexOf(value.charAt(i));if(glyph<0)continue;long bits=GLYPHS[glyph];for(int yy=0;yy<7&&top+yy<h;yy++)for(int xx=0;xx<5;xx++)if((bits&(1L<<(yy*5+xx)))!=0)pixels[(top+yy)*w+left+xx]=0x00ff80;}}
 // Small fixed bitmap avoids driver-dependent fonts and per-character native calls.
 private static final String CHARS="0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ./%+-";
 private static final long[] GLYPHS={0x3a33ae62eL,0x3884210c4L,0x7c444422eL,0x3e107420fL,0x211f4a988L,0x3e107843fL,0x3a317842eL,0x8422221fL,0x3a317462eL,0x3a10f462eL,0x4631fc62eL,0x3e317c62fL,0x78210843eL,0x3e318c62fL,0x7c217843fL,0x4217843fL,0x7a31e843eL,0x4631fc631L,0x38842108eL,0x19294211cL,0x452519531L,0x7c2108421L,0x4631ad771L,0x4639ace31L,0x3a318c62eL,0x4217c62fL,0x59358c62eL,0x45257c62fL,0x3e107043eL,0x10842109fL,0x3a318c631L,0x11518c631L,0x4775ac631L,0x462a22a31L,0x108422a31L,0x7c222221fL,0x108000000L,0x44222110L,0x674222173L,0x84f9080L,0xf8000L};
}
