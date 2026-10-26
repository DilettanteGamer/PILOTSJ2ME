package pilots;
import java.io.*;
/** Native ZIP and MMAPI profile: no Java audio or general-purpose decompressor. */
public final class PackedResources {
 private static short[] audioMap;private static int[] soundSizes;
 private static final int[] ids={-1,-1,-1,-1,-1,-1,-1,-1};
 private static final byte[][] data=new byte[8][];
 private static final long[] ages=new long[8];private static long clock;
 public static int opens,cacheHits;
 private static synchronized int sound(String name)throws IOException{
  if(audioMap==null){DataInputStream in=new DataInputStream(PackedResources.class.getResourceAsStream("/audio/native-map"));try{byte[] head=new byte[18*64*2+2];in.readFully(head);short[] map=new short[18*64];for(int i=0;i<map.length;i++)map[i]=(short)(((head[i*2]&255)<<8)|(head[i*2+1]&255));int count=((head[head.length-2]&255)<<8)|(head[head.length-1]&255);byte[] sizesRaw=new byte[count*4];in.readFully(sizesRaw);int[] sizes=new int[count];for(int i=0;i<count;i++){int p=i*4;sizes[i]=((sizesRaw[p]&255)<<24)|((sizesRaw[p+1]&255)<<16)|((sizesRaw[p+2]&255)<<8)|(sizesRaw[p+3]&255);}soundSizes=sizes;audioMap=map;}finally{in.close();}}

  int scene=(name.charAt(7)-'0')*10+name.charAt(8)-'0',cue=(name.charAt(10)-'0')*10+name.charAt(11)-'0';
  if(scene<0||scene>=18||cue<0||cue>=64)return -1;return audioMap[scene*64+cue]-1;
 }
 private static synchronized byte[] cached(int id){for(int i=0;i<8;i++)if(ids[i]==id){ages[i]=++clock;cacheHits++;return data[i];}return null;}
 private static synchronized void keep(int id,byte[] bytes){int slot=0,total=0;for(int i=0;i<8;i++){if(ages[i]<ages[slot])slot=i;if(data[i]!=null)total+=data[i].length;}if(data[slot]!=null)total-=data[slot].length;data[slot]=null;ids[slot]=-1;while(total+bytes.length>32768){int old=-1;for(int i=0;i<8;i++)if(data[i]!=null&&(old<0||ages[i]<ages[old]))old=i;if(old<0)break;total-=data[old].length;data[old]=null;ids[old]=-1;}data[slot]=bytes;ids[slot]=id;ages[slot]=++clock;}
 private static final String[] metaNames=new String[16];private static final byte[][] metas=new byte[16][];private static final long[] metaAges=new long[16];
 private static synchronized byte[] cachedMeta(String name){for(int i=0;i<16;i++)if(name.equals(metaNames[i])){metaAges[i]=++clock;cacheHits++;return metas[i];}return null;}
 private static synchronized void keepMeta(String name,byte[] bytes){int slot=0,total=0;for(int i=0;i<16;i++){if(metaAges[i]<metaAges[slot])slot=i;if(metas[i]!=null)total+=metas[i].length;}if(metas[slot]!=null)total-=metas[slot].length;metas[slot]=null;metaNames[slot]=null;while(total+bytes.length>32768){int old=-1;for(int i=0;i<16;i++)if(metas[i]!=null&&(old<0||metaAges[i]<metaAges[old]))old=i;if(old<0)break;total-=metas[old].length;metas[old]=null;metaNames[old]=null;}metas[slot]=bytes;metaNames[slot]=name;metaAges[slot]=++clock;}
 public static InputStream open(String name)throws IOException{long began=System.currentTimeMillis();try{return openMeasured(name);}finally{DebugHud.io(System.currentTimeMillis()-began);}}
 private static InputStream openMeasured(String name)throws IOException{
  if(name.startsWith("/audio/")&&name.endsWith(".wav")&&name.length()==16){int id=sound(name);if(id<0)return null;byte[] bytes=cached(id);if(bytes!=null)return new ByteArrayInputStream(bytes);InputStream in=PackedResources.class.getResourceAsStream("/sounds/"+id+".mp3");if(in==null)return null;opens++;
   if(soundSizes[id]>8192)return in;
   try{bytes=new byte[soundSizes[id]];new DataInputStream(in).readFully(bytes);keep(id,bytes);return new ByteArrayInputStream(bytes);}catch(OutOfMemoryError low){return PackedResources.class.getResourceAsStream("/sounds/"+id+".mp3");}finally{in.close();}
  }
  if(name.endsWith(".anm")){byte[] meta=cachedMeta(name);if(meta!=null)return new ByteArrayInputStream(meta);InputStream in=PackedResources.class.getResourceAsStream(name);if(in==null)return null;opens++;try{ByteArrayOutputStream out=new ByteArrayOutputStream();byte[] block=new byte[1024];int n;while((n=in.read(block))!=-1){if(out.size()+n>16384)throw new IOException("Animation descriptor too large");out.write(block,0,n);}meta=out.toByteArray();keepMeta(name,meta);return new ByteArrayInputStream(meta);}catch(OutOfMemoryError low){return PackedResources.class.getResourceAsStream(name);}finally{in.close();}}
  opens++;return PackedResources.class.getResourceAsStream(name);
 }
 static void unpack(DataInputStream in,byte[] data,int size)throws IOException{throw new IOException("Legacy tile bank in native profile");}
}
