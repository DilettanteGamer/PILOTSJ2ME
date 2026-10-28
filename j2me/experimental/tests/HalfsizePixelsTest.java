package pilots;
import java.io.*;import java.lang.reflect.*;import java.security.*;
/** Compare all reduced pixels, original geometry, hit samples and reverse seeks to source fixtures. */
public final class HalfsizePixelsTest {
 static String hex(byte[] bytes){String s="";for(int i=0;i<bytes.length;i++)s+=Integer.toHexString((bytes[i]&255)|256).substring(1);return s;}
 static void frame(AnimationClip a,MessageDigest image,MessageDigest hits,Field pixels)throws Exception{
  ByteArrayOutputStream b=new ByteArrayOutputStream();DataOutputStream meta=new DataOutputStream(b);
  meta.writeShort(a.offsetX);meta.writeShort(a.offsetY);meta.writeShort(a.width);meta.writeShort(a.height);image.update(b.toByteArray());
  int w=a.width,h=a.height;image.update((byte[])pixels.get(a),0,HalfPixels.width(w,h)*HalfPixels.height(w,h));
  if(HalfPixels.retainedBytes()>65664)throw new Exception("Hit mask cache budget exceeded");
  int[][] points={{0,0},{w-1,h-1},{w/2,h/2},{-1,0},{w,0},{0,h}};
  for(int i=0;i<points.length+16;i++){
   int x=i<points.length?points[i][0]:(i-points.length)*37%w;
   int y=i<points.length?points[i][1]:(i-points.length)*53%h;
   hits.update((byte)(a.contains(a.offsetX+x,a.offsetY+y)?1:0));
  }
 }
 public static void main(String[] args)throws Exception{
  Field pixels=AnimationClip.class.getDeclaredField("pixels");pixels.setAccessible(true);
  BufferedReader in=new BufferedReader(new FileReader(args[0]));String line;int clips=0,frames=0;
  while((line=in.readLine())!=null){String[] row=line.split(",");AnimationClip a=new AnimationClip("/"+row[0]);
   try{
    if(a.count!=Integer.parseInt(row[1])||a.duration!=Integer.parseInt(row[2]))throw new Exception("Timing changed "+row[0]);
    MessageDigest data=MessageDigest.getInstance("SHA-256"),hits=MessageDigest.getInstance("SHA-256");
    do{frame(a,data,hits,pixels);frames++;}while(a.advance());
    if(!hex(data.digest()).equals(row[3]))throw new Exception("Reduced pixels/geometry "+row[0]);
    if(!hex(hits.digest()).equals(row[4]))throw new Exception("Original hit mask changed "+row[0]);
    // Backwards seek also resets storage dimensions when successive frames differ.
    for(int target:new int[]{a.count-1,0,a.count/2,0}){a.seek(target);if(a.index!=target)throw new Exception("Seek "+row[0]);}
   }finally{a.close();}clips++;
  }
  if(clips!=1667||frames!=20916)throw new Exception("Incomplete corpus");
  in.close();in=new BufferedReader(new FileReader(args[1]));int samples=0;
  while((line=in.readLine())!=null){String[] row=line.split(",");byte[] raw=TileStore.sampled(Integer.parseInt(row[0]),Integer.parseInt(row[1]),Integer.parseInt(row[2]),null);
   if(raw==null||!hex(MessageDigest.getInstance("SHA-256").digest(raw)).equals(row[3]))throw new Exception("Retained exact screen sample "+line);samples++;
  }in.close();if(samples!=152)throw new Exception("Missing exact screen samples");
  HalfPixels.clear();if(HalfPixels.retainedBytes()!=0)throw new Exception("Hit masks retained after scene clear");
  System.out.println("Halfsize: 1667 clips / 20916 frames match independent reduced pixels; original pivots, geometry, clocks and 460152 hit samples preserved; reverse seeks passed");
 }
}
