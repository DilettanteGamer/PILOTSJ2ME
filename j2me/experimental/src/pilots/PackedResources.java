package pilots;
import java.io.*;
public final class PackedResources {
 private static lzma.sdk.lzma.Decoder decoder;
 public static long decodeMillis;public static int decodes;
 static synchronized void unpack(DataInputStream in,final byte[] data,int size)throws IOException{
  byte[] props=new byte[5];in.readFully(props);if(decoder==null)decoder=new lzma.sdk.lzma.Decoder();
  if(!decoder.setDecoderProperties(props))throw new IOException("LZMA properties");
  final int limit=size;OutputStream out=new OutputStream(){int position;public void write(int b)throws IOException{if(position>=limit)throw new IOException("LZMA overflow");data[position++]=(byte)b;}public void write(byte[] b,int off,int len)throws IOException{if(len>limit-position)throw new IOException("LZMA overflow");System.arraycopy(b,off,data,position,len);position+=len;}};
  decoder.resetBlock();long start=System.currentTimeMillis();if(!decoder.code(new BufferedSource(in),out,size))throw new IOException("LZMA stream");decodeMillis+=System.currentTimeMillis()-start;decodes++;
 }
 private static final class BufferedSource extends InputStream {
  private final InputStream source;private final byte[] buffer=new byte[8192];private int position,limit;
  BufferedSource(InputStream source){this.source=source;}
  public int read()throws IOException{if(position==limit){limit=source.read(buffer);position=0;if(limit<0)return -1;if(limit==0)throw new IOException("Empty resource read");}return buffer[position++]&255;}
  public int read(byte[] b,int off,int len)throws IOException{if(len==0)return 0;if(position==limit)return source.read(b,off,len);int n=Math.min(len,limit-position);System.arraycopy(buffer,position,b,off,n);position+=n;return n;}
 }
 private static final class PrefixSource extends InputStream {
  private final byte[] first;private final InputStream source;private int position;
  PrefixSource(byte[] first,InputStream source){this.first=first;this.source=source;}
  public int read()throws IOException{return position<4?first[position++]&255:source.read();}
  public int read(byte[] b,int off,int len)throws IOException{if(len==0)return 0;if(position<4){int n=Math.min(len,4-position);System.arraycopy(first,position,b,off,n);position+=n;return n;}return source.read(b,off,len);}
  public int available()throws IOException{return 4-position+source.available();}
  public void close()throws IOException{source.close();}
 }
 private static final String[] waveNames=new String[8],aliasNames=new String[32],aliasTargets=new String[32];
 private static final byte[][] waves=new byte[8][];private static final long[] waveAges=new long[8];private static long waveClock;private static int aliasNext;
 public static int waveCacheHits,opens,cacheHits;
 private static synchronized byte[] cachedWave(String name){for(int i=0;i<8;i++)if(name.equals(waveNames[i])){waveAges[i]=++waveClock;waveCacheHits++;return waves[i];}return null;}
 private static synchronized void keepWave(String name,byte[] data){int slot=0,total=0;for(int i=0;i<8;i++){if(waveAges[i]<waveAges[slot])slot=i;if(waves[i]!=null)total+=waves[i].length;}if(waves[slot]!=null)total-=waves[slot].length;waves[slot]=null;waveNames[slot]=null;while(total+data.length>65536){int old=-1;for(int i=0;i<8;i++)if(waves[i]!=null&&(old<0||waveAges[i]<waveAges[old]))old=i;if(old<0)break;total-=waves[old].length;waves[old]=null;waveNames[old]=null;}waves[slot]=data;waveNames[slot]=name;waveAges[slot]=++waveClock;}
 private static synchronized String cachedAlias(String name){for(int i=0;i<32;i++)if(name.equals(aliasNames[i]))return aliasTargets[i];return null;}
 private static synchronized void keepAlias(String name,String path){aliasTargets[aliasNext]=path;aliasNames[aliasNext]=name;aliasNext=(aliasNext+1)%32;}
 public static InputStream open(String name)throws IOException{
  if(name.startsWith("/sounds/")){byte[] cached=cachedWave(name);if(cached!=null)return new ByteArrayInputStream(cached);}
  if(name.endsWith(".wav")){String target=cachedAlias(name);if(target!=null)return open(target);}
  InputStream raw=PackedResources.class.getResourceAsStream(name);if(raw==null)return null;
  DataInputStream in=new DataInputStream(raw);byte[] magic=new byte[4];try{in.readFully(magic);}catch(EOFException shortFile){in.close();return PackedResources.class.getResourceAsStream(name);}
  int tag=((magic[0]&255)<<24)|((magic[1]&255)<<16)|((magic[2]&255)<<8)|(magic[3]&255);
  if(tag==0x52454631){String path=in.readUTF();in.close();if(name.endsWith(".wav"))keepAlias(name,path);return open(path);}
  if(tag==0x4c5a4d31){int size=in.readInt();if(size<0||size>1048576){in.close();throw new IOException("Resource length");}byte[] data=new byte[size];try{unpack(in,data,size);}finally{in.close();}return new ByteArrayInputStream(data);}
  if(tag==0x4f505531){OpusWave wave=new OpusWave(new DataInputStream(new BufferedSource(in)));int size=wave.available();if(size<=32768){byte[] data;try{data=new byte[size];}catch(OutOfMemoryError low){return wave;}try{new DataInputStream(wave).readFully(data);}finally{wave.close();}keepWave(name,data);return new ByteArrayInputStream(data);}return wave;}
  return new PrefixSource(magic,in);
 }
}
