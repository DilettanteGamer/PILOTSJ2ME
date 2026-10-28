package pilots;
import java.io.*;
/** Original alpha masks are loaded only for hit tests, never for painting. */
public final class HalfPixels {
 private static byte[] map;
 private static int mapId=-1;
 private static final byte[][] masks=new byte[8][];
 private static final int[] ids={-1,-1,-1,-1,-1,-1,-1,-1};
 private static final long[] ages=new long[8];private static long clock;
 public static int reads;public static long decoded;
 public static int width(int w,int h){return w>64||h>64?(w+1)/2:w;}
 public static int height(int w,int h){return w>64||h>64?(h+1)/2:h;}
 private static byte[] read(String name,int limit)throws IOException{
  long began=System.currentTimeMillis();InputStream in=PackedResources.open(name);
  if(in==null)throw new IOException("Missing hit mask");
  try{DataInputStream source=new DataInputStream(in);int size=source.readInt();
   if(size<1||size>limit)throw new IOException("Hit mask length");byte[] data=new byte[size];source.readFully(data);
   if(source.read()!=-1)throw new IOException("Trailing hit mask bytes");reads++;decoded+=data.length;return data;
  }finally{try{in.close();}finally{DebugHud.io(System.currentTimeMillis()-began);}}
 }
 public static synchronized boolean contains(int id,int w,int h,int x,int y)throws IOException{
  if(x<0||y<0||x>=w||y>=h)return false;
  int slot=0;byte[] mask=null;
  for(int i=0;i<8;i++){if(ids[i]==id){mask=masks[i];ages[i]=++clock;break;}if(ages[i]<ages[slot])slot=i;}
  if(mask!=null)return (mask[(y*w+x)>>3]&(128>>((y*w+x)&7)))!=0;
  int group=id/32;if(mapId!=group){map=read("/half/m"+group,128);mapId=group;}
  int at=id%32*4;if(at>map.length-4)throw new IOException("Hit mask ID");
  int b=(map[at]&255)*256+(map[at+1]&255),offset=(map[at+2]&255)*256+(map[at+3]&255);
  int size=(w*h+7)/8,total=0;masks[slot]=null;ids[slot]=-1;
  for(int i=0;i<8;i++)if(masks[i]!=null)total+=masks[i].length;
  while(total+size>65536){int old=-1;for(int i=0;i<8;i++)if(masks[i]!=null&&(old<0||ages[i]<ages[old]))old=i;
   if(old<0)break;total-=masks[old].length;masks[old]=null;ids[old]=-1;
  }
  long began=System.currentTimeMillis();InputStream raw=PackedResources.open("/half/b"+b);
  if(raw==null)throw new IOException("Missing hit mask bank");DataInputStream in=new DataInputStream(raw);
  try{int declared=in.readInt();if(declared<1||declared>38400||offset>declared-size)throw new IOException("Hit mask range");
   int skip=offset;while(skip>0){int n=in.skipBytes(skip);if(n<=0){if(in.read()<0)throw new IOException("Truncated hit mask");n=1;}skip-=n;}
   mask=new byte[size];in.readFully(mask);masks[slot]=mask;ids[slot]=id;ages[slot]=++clock;reads++;decoded+=declared;
  }finally{try{in.close();}finally{DebugHud.io(System.currentTimeMillis()-began);}}
  int bit=y*w+x;return (mask[bit>>3]&(128>>(bit&7)))!=0;
 }
 public static synchronized void clear(){map=null;mapId=-1;for(int i=0;i<8;i++){masks[i]=null;ids[i]=-1;}}
 public static synchronized int retainedBytes(){int size=map==null?0:map.length;for(int i=0;i<8;i++)if(masks[i]!=null)size+=masks[i].length;return size;}
}
