package pilots;
import java.io.*;
import javax.microedition.rms.RecordStore;
/** Permanent unlocks are separate from the current game's checkpoint and save. */
public final class PlayerProgress {
 public static final int[] ORDER={2,3,4,5,6,7,8,9,10,11,12,13,15,14,16,17};
 public static final String[] NAMES={"У входа в зоопарк","Двор зоопарка","Звериный двор","Сад","Улица","Канализация","Гостиница: вступление","Гостиница: интерьер","Комната с балконом","Карниз гостиницы","Ванная комната","Электрощитовая","Подвал","Крыша","Полёт","Финал: лесная поляна"};
 public int unlocked=1<<2,lastScene=2;
 public boolean started,finished,sound=true;
 private static final String STORE="pilots-progress-v1";
 public PlayerProgress(){
  RecordStore rms=null;
  try{rms=RecordStore.openRecordStore(STORE,false);DataInputStream in=new DataInputStream(new ByteArrayInputStream(rms.getRecord(1)));if(in.readInt()!=0x50524731)throw new IOException("Invalid progress");unlocked=in.readInt()|1<<2;lastScene=in.readUnsignedByte();started=in.readBoolean();finished=in.readBoolean();sound=in.readBoolean();if(!valid(lastScene))throw new IOException("Invalid checkpoint");return;}catch(Exception ignored){unlocked=1<<2;lastScene=2;started=finished=false;sound=true;}finally{if(rms!=null)try{rms.closeRecordStore();}catch(Exception ignored){}}
  // Old builds had no unlock record. Import the saved campaign checkpoint.
  try{rms=RecordStore.openRecordStore("pilots-port-v1",false);GameState old=GameState.decode(rms.getRecord(1),18);if(valid(old.scene)){for(int i=0;i<ORDER.length;i++){unlocked|=1<<ORDER[i];if(ORDER[i]==old.scene)break;}lastScene=old.scene;started=true;persist();}}catch(Exception ignored){}finally{if(rms!=null)try{rms.closeRecordStore();}catch(Exception ignored){}}
 }
 public static boolean valid(int scene){return scene>=2&&scene<=17;}
 public boolean available(int scene){return valid(scene)&&(unlocked&(1<<scene))!=0;}
 public void open(int scene)throws IOException{if(!valid(scene))throw new IOException("Invalid progress scene");unlocked|=1<<scene;lastScene=scene;started=true;finished=false;persist();}
 public void newGame()throws IOException{lastScene=2;started=true;finished=false;unlocked|=1<<2;persist();}
 public void complete()throws IOException{if(!finished){finished=true;started=true;persist();}}
 public void persist()throws IOException{
  RecordStore rms=null;try{ByteArrayOutputStream b=new ByteArrayOutputStream();DataOutputStream out=new DataOutputStream(b);out.writeInt(0x50524731);out.writeInt(unlocked);out.writeByte(lastScene);out.writeBoolean(started);out.writeBoolean(finished);out.writeBoolean(sound);out.close();byte[] data=b.toByteArray();rms=RecordStore.openRecordStore(STORE,true);if(rms.getNumRecords()==0)rms.addRecord(data,0,data.length);else rms.setRecord(1,data,0,data.length);}catch(Exception error){throw new IOException("Progress: "+error.toString());}finally{if(rms!=null)try{rms.closeRecordStore();}catch(Exception ignored){}}
 }
}
