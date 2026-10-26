import java.io.*;import java.util.*;import java.security.*;import org.concentus.*;
/** Desktop-only corpus harness; it is deliberately separate from the game runtime. */
public class TestSilkNbCorpus{
 public static void main(String[] args)throws Exception{
  File[] files=new File(args[0]).listFiles();Arrays.sort(files);int clips=0,packets=0;long samples=0;MessageDigest digest=MessageDigest.getInstance("SHA-256");long start=System.nanoTime();
  for(File file:files)if(file.getName().endsWith(".packets")){
   DataInputStream in=new DataInputStream(new FileInputStream(file));if(in.readInt()!=0x4f504b31)throw new Exception("OPK1 magic");int count=in.readInt();OpusDecoder reference=new OpusDecoder(12000,1);SilkNbOnlyProbe restricted=new SilkNbOnlyProbe();
   for(int i=0;i<count;i++){byte[] packet=new byte[in.readUnsignedShort()];in.readFully(packet);short[] expected=new short[240],actual=new short[240];int n=reference.decode(packet,0,packet.length,expected,0,240,false);int m=restricted.decode(packet,actual);if(n!=m||!Arrays.equals(expected,actual))throw new Exception("PCM mismatch "+file+" packet "+i);for(int j=0;j<m;j++){digest.update((byte)actual[j]);digest.update((byte)(actual[j]>>8));}packets++;samples+=m;}
   if(in.read()!=-1)throw new Exception("Trailing packet fixture data");in.close();clips++;
  }
  String hash="";for(byte b:digest.digest())hash+=Integer.toHexString((b&255)|256).substring(1);
  System.out.println("SILK-NB corpus exact vs full Concentus: clips="+clips+" packets="+packets+" samples="+samples+" pcm_le_sha256="+hash+" desktop_ms="+(System.nanoTime()-start)/1000000);
 }
}
