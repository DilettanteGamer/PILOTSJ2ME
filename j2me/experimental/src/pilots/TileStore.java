package pilots;
import java.io.*;
/** Local banks bound the cold read to the frames of one small animation group. */
public final class TileStore {
 private static byte[] palettes,maps,olderMaps;
 private static int mapId=-1,olderMapId=-1;
 private static final byte[][] banks=new byte[4][];private static final int[] bankIds={-1,-1,-1,-1};private static final long[] ages=new long[4];private static long clock;
 private static final byte[][] frames=new byte[64][];private static final int[] frameIds=new int[64];private static final long[] frameAges=new long[64];private static boolean frameCache=true;
 private static void remember(int id,byte[] pixels,int size){
  if(!frameCache||size>32768)return;int slot=0,total=0;for(int i=0;i<64;i++){if(frameAges[i]<frameAges[slot])slot=i;if(frames[i]!=null)total+=frames[i].length;}
  byte[] recycled=frames[slot];if(recycled!=null&&recycled.length!=size)recycled=null;
  if(frames[slot]!=null){total-=frames[slot].length;frames[slot]=null;frameIds[slot]=-1;}
  while(total+size>196608){int old=-1;for(int i=0;i<64;i++)if(frames[i]!=null&&(old<0||frameAges[i]<frameAges[old]))old=i;if(old<0)break;total-=frames[old].length;frames[old]=null;frameIds[old]=-1;}
  try{byte[] data=recycled==null?new byte[size]:recycled;System.arraycopy(pixels,0,data,0,size);frames[slot]=data;frameIds[slot]=id;frameAges[slot]=++clock;}catch(OutOfMemoryError low){for(int i=0;i<64;i++){frames[i]=null;frameIds[i]=-1;}frameCache=false;}
 }
 static {for(int i=0;i<64;i++)frameIds[i]=-1;}
 public static int loads,directLoads,frameCalls,frameCacheHits;public static long rawDecoded;
 public static void palette(int id,byte[] out)throws IOException{if(palettes==null){DataInputStream in=new DataInputStream(PackedResources.open("/tiles/palettes"));palettes=new byte[11520];try{in.readFully(palettes);}finally{in.close();}}if(id<0||id>=15)throw new IOException("Palette ID");System.arraycopy(palettes,id*768,out,0,768);}
 private static byte[] load(int id,boolean linear)throws IOException{
  int slot=0;for(int i=0;i<4;i++){if(bankIds[i]==id){ages[i]=++clock;return banks[i];}if(ages[i]<ages[slot])slot=i;}
  long began=System.currentTimeMillis();InputStream source=TileStore.class.getResourceAsStream("/tiles/b"+id);if(source==null)throw new IOException("Missing frame bank");DataInputStream in=new DataInputStream(source);
  try{int declared=in.readInt();boolean nativeZip=declared<0;int size=nativeZip?-declared:declared;if(size<1||size>524288)throw new IOException("Tile bank length");
   banks[slot]=null;bankIds[slot]=-1;
   int retained=0;for(int i=0;i<4;i++)if(banks[i]!=null)retained+=banks[i].length;
   while(retained+size>131072){int old=-1;for(int i=0;i<4;i++)if(banks[i]!=null&&(old<0||ages[i]<ages[old]))old=i;if(old<0)break;retained-=banks[old].length;banks[old]=null;bankIds[old]=-1;}
   byte[] data=new byte[size];if(nativeZip)in.readFully(data);else PackedResources.unpack(in,data,size);banks[slot]=data;bankIds[slot]=id;ages[slot]=++clock;if(linear)directLoads++;else loads++;rawDecoded+=size;return data;
  }finally{try{in.close();}finally{DebugHud.io(System.currentTimeMillis()-began);}}
 }
 private static short[] sampledMap;
 public static int sampleReads,sampleHits;
 public static synchronized byte[] sampled(int image,int w,int h,byte[] reuse)throws IOException{
  if(sampledMap==null){DataInputStream in=new DataInputStream(PackedResources.open("/tiles/sampled"));try{int count=in.readUnsignedShort();if(count>2048)throw new IOException("Sample count");byte[] raw=new byte[count*10];in.readFully(raw);short[] map=new short[count*5];for(int i=0;i<map.length;i++)map[i]=(short)(((raw[i*2]&255)<<8)|(raw[i*2+1]&255));sampledMap=map;}finally{in.close();}}
  int lo=0,hi=sampledMap.length/5-1;
  while(lo<=hi){int mid=(lo+hi)/2,id=sampledMap[mid*5]&65535;if(id<image)lo=mid+1;else hi=mid-1;}
  for(int at=lo*5;at<sampledMap.length && (sampledMap[at]&65535)==image;at+=5){
   if((sampledMap[at+1]&65535)!=w || (sampledMap[at+2]&65535)!=h)continue;
   int size=w*h,key=-2-at/5;byte[] out=reuse!=null&&reuse.length>=size?reuse:new byte[size];frameCalls++;
   for(int i=0;i<64;i++)if(frameIds[i]==key){if(frames[i].length!=size)throw new IOException("Sample dimensions");System.arraycopy(frames[i],0,out,0,size);frameAges[i]=++clock;frameCacheHits++;sampleHits++;return out;}
   byte[] bank=load(sampledMap[at+3]&65535,true);int offset=sampledMap[at+4]&65535;if(offset>bank.length-size)throw new IOException("Sample range");
   System.arraycopy(bank,offset,out,0,size);remember(key,out,size);sampleReads++;return out;
  }
  return null;
 }
 public static synchronized void fill(int image,byte[] pixels,int w,int h)throws IOException{
  frameCalls++;for(int i=0;i<64;i++)if(frameIds[i]==image){if(frames[i].length!=w*h)throw new IOException("Cached dimensions");System.arraycopy(frames[i],0,pixels,0,w*h);frameAges[i]=++clock;frameCacheHits++;return;}int group=image/32;
  if(group!=mapId&&group==olderMapId){byte[] swap=maps;maps=olderMaps;olderMaps=swap;int old=mapId;mapId=olderMapId;olderMapId=old;}
  if(group!=mapId){InputStream in=PackedResources.open("/tiles/m"+group);ByteArrayOutputStream out=new ByteArrayOutputStream();byte[] temp=new byte[1024];try{int n;while((n=in.read(temp))!=-1)out.write(temp,0,n);}finally{in.close();}olderMaps=maps;olderMapId=mapId;maps=out.toByteArray();mapId=group;}
  DataInputStream in=new DataInputStream(new ByteArrayInputStream(maps));int count=in.readUnsignedByte(),offset=1+count*2,len=0,local=image%32;if(local>=count)throw new IOException("Image ID");for(int i=0;i<count;i++){int n=in.readUnsignedShort();if(i<local)offset+=n;if(i==local)len=n;}
  int cols=(w+15)/16,rows=(h+15)/16;if(len<4)throw new IOException("Tile map length");
  int id=((maps[offset]&255)<<24)|((maps[offset+1]&255)<<16)|((maps[offset+2]&255)<<8)|(maps[offset+3]&255);
  if(id<0){if(len!=8)throw new IOException("Direct frame map");int p=offset+4,start=((maps[p]&255)<<24)|((maps[p+1]&255)<<16)|((maps[p+2]&255)<<8)|(maps[p+3]&255);byte[] bank=load(-id-1,true);if(start<0||start>bank.length-w*h)throw new IOException("Direct frame range");System.arraycopy(bank,start,pixels,0,w*h);remember(image,pixels,w*h);return;}
  if(len!=4+cols*rows*3)throw new IOException("Tile map length");byte[] bank=load(id,false);offset+=4;
  for(int i=0;i<cols*rows;i++){int p=offset+i*3,tile=((maps[p]&255)<<16)|((maps[p+1]&255)<<8)|(maps[p+2]&255),src=tile*256;if(src<0||src+256>bank.length)throw new IOException("Tile ID");int x=i%cols*16,y=i/cols*16;for(int yy=0;yy<16&&y+yy<h;yy++)System.arraycopy(bank,src+yy*16,pixels,(y+yy)*w+x,Math.min(16,w-x));}
  remember(image,pixels,w*h);
 }
}
