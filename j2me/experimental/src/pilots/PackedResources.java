package pilots;
import java.io.*;
public final class PackedResources {
 private static lzma.sdk.lzma.Decoder decoder;
 public static long decodeMillis;public static int decodes;
 static synchronized void unpack(DataInputStream in,final byte[] data,int size)throws IOException{
  byte[] props=new byte[5];in.readFully(props);decoder=new lzma.sdk.lzma.Decoder();
  if(!decoder.setDecoderProperties(props))throw new IOException("LZMA properties");
  final int limit=size;OutputStream out=new OutputStream(){int position;public void write(int b)throws IOException{if(position>=limit)throw new IOException("LZMA overflow");data[position++]=(byte)b;}public void write(byte[] b,int off,int len)throws IOException{if(len>limit-position)throw new IOException("LZMA overflow");System.arraycopy(b,off,data,position,len);position+=len;}};
  long start=System.currentTimeMillis();if(!decoder.code(in,out,size))throw new IOException("LZMA stream");decodeMillis+=System.currentTimeMillis()-start;decodes++;
 }
 public static InputStream open(String name)throws IOException{
  InputStream raw=PackedResources.class.getResourceAsStream(name);if(raw==null)return null;
  DataInputStream in=new DataInputStream(raw);byte[] magic=new byte[4];try{in.readFully(magic);}catch(EOFException shortFile){in.close();return PackedResources.class.getResourceAsStream(name);}
  int tag=((magic[0]&255)<<24)|((magic[1]&255)<<16)|((magic[2]&255)<<8)|(magic[3]&255);
  if(tag==0x52454631){String path=in.readUTF();in.close();return open(path);}
  if(tag==0x4c5a4d31){int size=in.readInt();if(size<0||size>1048576){in.close();throw new IOException("Resource length");}byte[] data=new byte[size];try{unpack(in,data,size);}finally{in.close();}return new ByteArrayInputStream(data);}
  if(tag==0x4f505531)return new OpusWave(in);
  in.close();return PackedResources.class.getResourceAsStream(name);
 }
}
