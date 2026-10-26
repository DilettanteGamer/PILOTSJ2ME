package pilots;
import java.io.*;
public final class TileStore {
 private static byte[] palettes,block=new byte[262144],maps;
 private static int blockId=-1,mapId=-1,olderMapId=-1;private static byte[] olderMaps;
 private static final byte[] hot=new byte[262144];private static final int[] keys=new int[1024];
 static {for(int i=0;i<keys.length;i++)keys[i]=-1;}
 private static void paintTile(byte[] data,int src,byte[] pixels,int i,int cols,int w,int h){int x=i%cols*16,y=i/cols*16;for(int yy=0;yy<16&&y+yy<h;yy++)System.arraycopy(data,src+yy*16,pixels,(y+yy)*w+x,Math.min(16,w-x));}
 public static void palette(int id,byte[] out)throws IOException{if(palettes==null){DataInputStream in=new DataInputStream(PackedResources.open("/tiles/palettes"));palettes=new byte[11520];try{in.readFully(palettes);}finally{in.close();}}if(id<0||id>=15)throw new IOException("Palette ID");System.arraycopy(palettes,id*768,out,0,768);}
 private static void load(int id)throws IOException{if(id==blockId)return;DataInputStream in=new DataInputStream(TileStore.class.getResourceAsStream("/tiles/b"+id));try{int size=in.readInt();if(size<1||size>block.length)throw new IOException("Tile block");PackedResources.unpack(in,block,size);blockId=id;}finally{in.close();}}
 public static synchronized void fill(int image,byte[] pixels,int w,int h)throws IOException{
  int group=image/32;if(group!=mapId&&group==olderMapId){byte[] swap=maps;maps=olderMaps;olderMaps=swap;int old=mapId;mapId=olderMapId;olderMapId=old;}if(group!=mapId){olderMaps=maps;olderMapId=mapId;InputStream in=PackedResources.open("/tiles/m"+group);ByteArrayOutputStream out=new ByteArrayOutputStream();byte[] temp=new byte[1024];try{int n;while((n=in.read(temp))!=-1)out.write(temp,0,n);}finally{in.close();}maps=out.toByteArray();mapId=group;}
  DataInputStream in=new DataInputStream(new ByteArrayInputStream(maps));int count=in.readUnsignedByte(),offset=1+count*2,len=0;int local=image%32;if(local>=count)throw new IOException("Image ID");for(int i=0;i<count;i++){int n=in.readUnsignedShort();if(i<local)offset+=n;if(i==local)len=n;}
  int cols=(w+15)/16,rows=(h+15)/16;if(len!=cols*rows*3)throw new IOException("Tile map length");int[] ids=new int[cols*rows];int max=-1;for(int i=0;i<ids.length;i++){int p=offset+i*3;ids[i]=((maps[p]&255)<<16)|((maps[p+1]&255)<<8)|(maps[p+2]&255);max=Math.max(max,ids[i]/1024);}
  for(int i=0;i<ids.length;i++){int id=ids[i],slot=id%1024;if(keys[slot]==id){paintTile(hot,slot*256,pixels,i,cols,w,h);ids[i]=~id;}}
  // Group reads by block so one frame never repeatedly inflates the same block.
  for(int b=0;b<=max;b++){boolean needed=false;for(int i=0;i<ids.length;i++)if(ids[i]>=0&&ids[i]/1024==b){needed=true;break;}if(!needed)continue;load(b);
   for(int i=0;i<ids.length;i++)if(ids[i]>=0&&ids[i]/1024==b){int id=ids[i],slot=id%1024,src=id%1024*256;paintTile(block,src,pixels,i,cols,w,h);System.arraycopy(block,src,hot,slot*256,256);keys[slot]=id;}
  }
 }
}
