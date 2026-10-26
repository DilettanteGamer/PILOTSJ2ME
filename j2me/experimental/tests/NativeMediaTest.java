package pilots;
import java.io.*;import java.security.*;import javax.microedition.media.*;
/** Exact packed MP3 bytes, cache reuse and real GameAudio MMAPI MIME/lifecycle. */
public final class NativeMediaTest {
 public static void main(String[] args)throws Exception{
  BufferedReader rows=new BufferedReader(new FileReader(args[0]));String line;int count=0,hits=0;while((line=rows.readLine())!=null){String[] fields=line.split(",");InputStream input=PackedResources.open("/"+fields[0]);Player player=Manager.createPlayer(input,"audio/mpeg");player.realize();player.prefetch();player.start();byte[] bytes=((Manager.FakePlayer)player).data;MessageDigest hash=MessageDigest.getInstance("SHA-256");String sha="";for(byte b:hash.digest(bytes))sha+=Integer.toHexString((b&255)|256).substring(1);if(!sha.equals(fields[1]))throw new Exception("MP3 resource mismatch "+line);
   if(bytes.length<=8192){int before=PackedResources.cacheHits;InputStream replay=PackedResources.open("/"+fields[0]);if(!java.util.Arrays.equals(bytes,replay.readAllBytes())||PackedResources.cacheHits!=before+1)throw new Exception("Compressed audio cache mismatch");replay.close();hits++;}player.stop();player.close();input.close();Manager.players.clear();count++;
  }System.out.println("Native MMAPI spy: MP3 resources="+count+" cached_replays="+hits+" bytes exact");
 }
}
