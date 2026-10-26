package pilots;
import java.io.*;
/** Local banks bound the cold read to the frames of one small animation group. */
public final class TileStore {
 private static byte[] palettes,maps,olderMaps;
 private static int mapId=-1,olderMapId=-1;
 private static final byte[][] banks=new byte[2][];private static final int[] bankIds={-1,-1};private static final long[] ages=new long[2];private static long clock;
 private static final byte[][] frames=new byte[32][];private static final int[] frameIds=new int[32];private static final long[] frameAges=new long[32];private static boolean frameCache=true;
 private static void remember(int id,byte[] pixels,int size){
  if(!frameCache||size>32768)return;int slot=0,total=0;for(int i=0;i<32;i++){if(frameAges[i]<frameAges[slot])slot=i;if(frames[i]!=null)total+=frames[i].length;}
  if(frames[slot]!=null){total-=frames[slot].length;frames[slot]=null;frameIds[slot]=-1;}
  while(total+size>196608){int old=-1;for(int i=0;i<32;i++)if(frames[i]!=null&&(old<0||frameAges[i]<frameAges[old]))old=i;if(old<0)break;total-=frames[old].length;frames[old]=null;frameIds[old]=-1;}
  try{byte[] data=new byte[size];System.arraycopy(pixels,0,data,0,size);frames[slot]=data;frameIds[slot]=id;frameAges[slot]=++clock;}catch(OutOfMemoryError low){for(int i=0;i<32;i++){frames[i]=null;frameIds[i]=-1;}frameCache=false;}
 }
 static {for(int i=0;i<32;i++)frameIds[i]=-1;}
 public static int loads,frameCalls,frameCacheHits;public static long rawDecoded;
 public static void palette(int id,byte[] out)throws IOException{if(palettes==null){DataInputStream in=new DataInputStream(PackedResources.open("/tiles/palettes"));palettes=new byte[11520];try{in.readFully(palettes);}finally{in.close();}}if(id<0||id>=15)throw new IOException("Palette ID");System.arraycopy(palettes,id*768,out,0,768);}
 private static byte[] load(int id)throws IOException{
  int slot=0;for(int i=0;i<2;i++){if(bankIds[i]==id){ages[i]=++clock;return banks[i];}if(ages[i]<ages[slot])slot=i;}
  DataInputStream in=new DataInputStream(TileStore.class.getResourceAsStream("/tiles/b"+id));
  try{int declared=in.readInt();boolean nativeZip=declared<0;int size=nativeZip?-declared:declared;if(size<1||size>524288)throw new IOException("Tile bank length");
   banks[slot]=null;bankIds[slot]=-1;
   int retained=0;for(int i=0;i<2;i++)if(banks[i]!=null)retained+=banks[i].length;
   while(retained+size>131072){int old=-1;for(int i=0;i<2;i++)if(banks[i]!=null&&(old<0||ages[i]<ages[old]))old=i;if(old<0)break;retained-=banks[old].length;banks[old]=null;bankIds[old]=-1;}
   byte[] data=new byte[size];if(nativeZip)in.readFully(data);else PackedResources.unpack(in,data,size);banks[slot]=data;bankIds[slot]=id;ages[slot]=++clock;loads++;rawDecoded+=size;return data;
  }finally{in.close();}
 }
 public static synchronized void fill(int image,byte[] pixels,int w,int h)throws IOException{
  frameCalls++;for(int i=0;i<32;i++)if(frameIds[i]==image){if(frames[i].length!=w*h)throw new IOException("Cached dimensions");System.arraycopy(frames[i],0,pixels,0,w*h);frameAges[i]=++clock;frameCacheHits++;return;}int group=image/32;
  if(group!=mapId&&group==olderMapId){byte[] swap=maps;maps=olderMaps;olderMaps=swap;int old=mapId;mapId=olderMapId;olderMapId=old;}
  if(group!=mapId){InputStream in=PackedResources.open("/tiles/m"+group);ByteArrayOutputStream out=new ByteArrayOutputStream();byte[] temp=new byte[1024];try{int n;while((n=in.read(temp))!=-1)out.write(temp,0,n);}finally{in.close();}olderMaps=maps;olderMapId=mapId;maps=out.toByteArray();mapId=group;}
  DataInputStream in=new DataInputStream(new ByteArrayInputStream(maps));int count=in.readUnsignedByte(),offset=1+count*2,len=0,local=image%32;if(local>=count)throw new IOException("Image ID");for(int i=0;i<count;i++){int n=in.readUnsignedShort();if(i<local)offset+=n;if(i==local)len=n;}
  int cols=(w+15)/16,rows=(h+15)/16;if(len!=4+cols*rows*3)throw new IOException("Tile map length");
  int id=((maps[offset]&255)<<24)|((maps[offset+1]&255)<<16)|((maps[offset+2]&255)<<8)|(maps[offset+3]&255);byte[] bank=load(id);offset+=4;
  for(int i=0;i<cols*rows;i++){int p=offset+i*3,tile=((maps[p]&255)<<16)|((maps[p+1]&255)<<8)|(maps[p+2]&255),src=tile*256;if(src<0||src+256>bank.length)throw new IOException("Tile ID");int x=i%cols*16,y=i/cols*16;for(int yy=0;yy<16&&y+yy<h;yy++)System.arraycopy(bank,src+yy*16,pixels,(y+yy)*w+x,Math.min(16,w-x));}
  remember(image,pixels,w*h);
 }
}
