package pilots;
import java.io.*;
/** Section-9 timed rat crossing, adapted to one hero and direct left/right steps. */
public final class SewerLogic extends ZooLogic {
 private static final int[] STOPS={128,328,448};
 public int clock,x=448,from=448,motion,direction=-1,fall,fallX,falls;
 private boolean done;
 final SewerRats rats;
 private SoundSink sink;
 public SewerLogic(GameState state)throws IOException{super(state);rats=SewerRats.get();if(state.location!=null)load(state.location);sync();}
 public int hero(){return COLLEAGUE;}
 public boolean completed(){return done;}
 public boolean busy(){return motion>0||fall>0;}
 public boolean walking(){return motion>0;}
 public boolean hasItem(int item){return false;}
 public boolean selectItem(int item){return false;}
 public boolean select(int hero){return false;}
 public String message(){return null;}
 public void setSound(SoundSink sink){this.sink=sink;}
 private void cue(int n){if(sink!=null)sink.play(n);}
 public int motionDuration(){return (Math.abs(x-from)+15)/16;}
 public int motionProgress(){return motion>0?motionDuration()-motion:0;}
 public int visibleX(){return motion>0?from+(x-from)*motionProgress()/motionDuration():x;}
 private static boolean safe(int point){return point==128||point==328||point==448;}
 public boolean input(int key){
  if(done||busy()||(key!=4&&key!=6))return false;
  int index=0;while(index<STOPS.length&&STOPS[index]!=x)index++;
  int next=index+(key==4?-1:1);if(index==STOPS.length||next<0||next>=STOPS.length)return false;
  from=x;x=STOPS[next];direction=key==4?-1:1;motion=motionDuration();cue((clock&1)==0?3:4);sync();return true;
 }
 public int phase(){return clock%20+1;}
 public boolean dangerous(int index){int p=phase();return index==0?p>=14:index==1?p>=6&&p<=11:p>=9&&p<=16;}
 public void tick(){
  if(done)return;clock=(clock+1)%1000;
  if(fall>0){fall++;
   if(fall==5)cue(21);if(fall==30||fall==42)cue(15);if(fall==51)cue(16);
   if(fall==76){x=from=448;motion=0;direction=-1;cue(17);}
   if(fall>89){fall=0;x=from=448;motion=0;direction=-1;}sync();return;}
  if(motion>0)motion--;
  int feet=visibleX()+32;
  for(int i=0;i<3;i++)if(dangerous(i)&&rats.hits(i,clock%20,feet)){fallX=visibleX();fall=1;motion=0;falls++;cue(14);sync();return;}
  if(motion==0&&x==128)done=true;
  sync();
 }
 public void sync(){try{ByteArrayOutputStream b=new ByteArrayOutputStream();DataOutputStream o=new DataOutputStream(b);o.writeInt(0x53575235);o.writeShort(clock);o.writeShort(x);o.writeShort(from);o.writeByte(motion);o.writeByte(direction);o.writeByte(fall);o.writeShort(fallX);o.writeInt(falls);o.writeBoolean(done);o.close();state.location=b.toByteArray();}catch(IOException impossible){throw new RuntimeException(impossible.toString());}}
 private void load(byte[] b)throws IOException{
  DataInputStream in=new DataInputStream(new ByteArrayInputStream(b));int cookie=in.readInt();boolean legacy=cookie==0x53575231,oldRoute=cookie==0x53575232,oldRecovery=cookie==0x53575233,oldAligned=cookie==0x53575234;if(!legacy&&!oldRoute&&!oldRecovery&&!oldAligned&&cookie!=0x53575235)throw new IOException("Invalid sewer save");clock=in.readUnsignedShort();x=in.readUnsignedShort();from=in.readUnsignedShort();motion=in.readUnsignedByte();direction=in.readByte();fall=in.readUnsignedByte();fallX=in.readUnsignedShort();falls=in.readInt();done=in.readBoolean();
  if(in.read()!=-1||clock>=1000||x<128||x>448||from<128||from>448||direction!=-1&&direction!=1||fall>(legacy||oldRoute?24:oldRecovery?94:oldAligned?86:89)||fallX>512||falls<0||done&&(x!=128||motion!=0||fall!=0))throw new IOException("Invalid sewer state");
  if(legacy){
   if(x%64!=0||from%64!=0||Math.abs(x-from)>64||motion>4)throw new IOException("Invalid old sewer state");
   // Older builds allowed stopping in a rat's path. Resume from a safe point;
   // keep the running rat phases, attempts and any fall already in progress.
   if(!done&&(motion>0||x!=128&&x!=320&&x!=448))x=448;
   if(x==320)x=328;
   from=x;motion=0;
  }else if(oldRoute){
   if((x!=128&&x!=320&&x!=448)||(from!=128&&from!=320&&from!=448)||x!=from&&x!=320&&from!=320||motion>(Math.abs(x-from)/16)||motion>0&&(fall>0||direction!=(x<from?-1:1)))throw new IOException("Invalid old sewer route");
   if(motion>0){x=from=448;motion=0;direction=-1;}else{x=x==320?328:x;from=x;}
  }else if(!safe(x)||!safe(from)||x!=from&&x!=328&&from!=328||motion>motionDuration()||motion>0&&(fall>0||direction!=(x<from?-1:1)))throw new IOException("Invalid sewer route");
  if(oldRecovery&&fall>=21){if(fall<72)fall=21+(fall-21)*29/50;else fall=76+(fall-72)*10/22;}

 }
}
