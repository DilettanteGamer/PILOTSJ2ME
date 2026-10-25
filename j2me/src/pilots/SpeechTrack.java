package pilots;
import java.io.*;
import javax.microedition.rms.RecordStore;
/** Captions follow original sound events before the audio mute gate. */
public final class SpeechTrack implements MusicSink {
 private final SoundSink sound;
 private final int scene;
 private final String[] lines=new String[256];
 private final int[] lengths=new int[256];
 private int remaining,elapsed;
 private String current;
 public SpeechTrack(SoundSink sound,int scene)throws IOException{
  this.sound=sound;this.scene=scene;InputStream raw=getClass().getResourceAsStream("/scenes/"+(scene<10?"0":"")+scene+".speech");
  if(raw==null)throw new IOException("Missing speech captions");DataInputStream in=new DataInputStream(raw);
  try{if(in.readInt()!=0x53504831)throw new IOException("Invalid speech captions");int n=in.readUnsignedShort();if(n>256)throw new IOException("Invalid speech count");for(int i=0;i<n;i++){int id=in.readUnsignedByte();lengths[id]=in.readUnsignedShort();lines[id]=in.readUTF();}if(in.read()!=-1)throw new IOException("Trailing speech captions");}finally{in.close();}
 }
 public synchronized void play(int id){if(id>=0&&id<lines.length&&lines[id]!=null){String next=lines[id];if(current!=null&&remaining>0&&elapsed<Math.min(5000,current.length()*65+500)){if(current.endsWith(next))next=current;else if(current.length()+next.length()<300)next=current+" "+next;}current=next;remaining=Math.min(20000,Math.max(3500,Math.max(lengths[id]+500,current.length()*90+1500)));elapsed=0;}if(sound!=null)sound.play(id);}
 public void carry(SpeechTrack previous){
  if(previous==null||previous==this)return;String text;int left,age;synchronized(previous){text=previous.text();left=previous.remaining;age=previous.elapsed;}if(text==null)return;
  synchronized(this){if(current==null||remaining<=0){current=text;remaining=left;elapsed=age;}else if(!current.equals(text)&&current.length()+text.length()<300){current=text+" "+current;remaining=Math.min(20000,Math.max(remaining,current.length()*90+1500));elapsed=0;}}
 }
 public void music(int track){if(sound instanceof MusicSink)((MusicSink)sound).music(track);}
 public synchronized String text(){return remaining>0?current:null;}
 public synchronized int age(){return elapsed;}
 public synchronized boolean update(long delta){if(remaining<=0||delta<=0)return false;int before=elapsed/3500;int step=(int)Math.min(delta,400);remaining=Math.max(0,remaining-step);elapsed+=step;return remaining==0||before!=elapsed/3500;}
 private static int hash(byte[] bytes){int h=1;for(int i=0;i<bytes.length;i++)h=31*h+(bytes[i]&255);return h;}
 public synchronized void save(byte[] game){RecordStore rms=null;try{ByteArrayOutputStream b=new ByteArrayOutputStream();DataOutputStream out=new DataOutputStream(b);out.writeInt(0x53504332);out.writeInt(hash(game));out.writeByte(scene);out.writeUTF(current==null?"":current);out.writeInt(remaining);out.writeInt(elapsed);out.close();byte[] data=b.toByteArray();rms=RecordStore.openRecordStore("pilots-caption-v1",true);if(rms.getNumRecords()==0)rms.addRecord(data,0,data.length);else rms.setRecord(1,data,0,data.length);}catch(Exception ignored){}finally{if(rms!=null)try{rms.closeRecordStore();}catch(Exception ignored){}}}
 public synchronized void restore(byte[] game){RecordStore rms=null;try{rms=RecordStore.openRecordStore("pilots-caption-v1",false);DataInputStream in=new DataInputStream(new ByteArrayInputStream(rms.getRecord(1)));if(in.readInt()!=0x53504332||in.readInt()!=hash(game)||in.readUnsignedByte()!=scene)return;String text=in.readUTF();int left=in.readInt(),age=in.readInt();if(text.length()>0&&text.length()<=512&&left>0&&left<=20000&&age>=0&&age<=20000){current=text;remaining=left;elapsed=age;}}catch(Exception ignored){}finally{if(rms!=null)try{rms.closeRecordStore();}catch(Exception ignored){}}}
}
