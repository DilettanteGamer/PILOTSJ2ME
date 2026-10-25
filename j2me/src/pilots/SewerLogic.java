package pilots;
import java.io.*;
/** Section-9 timed rat crossing, adapted to one hero and direct left/right steps. */
public final class SewerLogic extends ZooLogic {
 public int clock,x=448,from=448,motion,direction=-1,fall,fallX,falls;
 private boolean done;
 private SoundSink sink;
 public SewerLogic(GameState state)throws IOException{super(state);if(state.location!=null)load(state.location);sync();}
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
 public int visibleX(){return motion>0?from+(x-from)*(4-motion)/4:x;}
 public boolean input(int key){
  if(done||busy()||(key!=4&&key!=6))return false;
  int dx=key==4?-64:64,next=x+dx;if(next<128||next>448)return false;
  from=x;x=next;direction=dx<0?-1:1;motion=4;cue((clock&1)==0?3:4);sync();return true;
 }
 public int phase(){return clock%20+1;}
 public boolean dangerous(int index){int p=phase();return index==0?p>=14:index==1?p>=6&&p<=11:p>=9&&p<=16;}
 public void tick(){
  if(done)return;clock=(clock+1)%1000;
  if(fall>0){fall++;if(fall==5)cue(21);if(fall==17)cue(20);if(fall>24){fall=0;x=from=448;motion=0;}sync();return;}
  boolean moving=motion>0;if(motion>0)motion--;
  int feet=visibleX()+32;
  int[] rats={224,288,416};
  for(int i=0;i<rats.length;i++)if((i!=1||moving)&&dangerous(i)&&Math.abs(feet-rats[i])<=20){fallX=visibleX();fall=1;motion=0;falls++;cue(14);sync();return;}
  if(motion==0&&x==128)done=true;
  sync();
 }
 public void sync(){try{ByteArrayOutputStream b=new ByteArrayOutputStream();DataOutputStream o=new DataOutputStream(b);o.writeInt(0x53575231);o.writeShort(clock);o.writeShort(x);o.writeShort(from);o.writeByte(motion);o.writeByte(direction);o.writeByte(fall);o.writeShort(fallX);o.writeInt(falls);o.writeBoolean(done);o.close();state.location=b.toByteArray();}catch(IOException impossible){throw new RuntimeException(impossible.toString());}}
 private void load(byte[] b)throws IOException{
  DataInputStream in=new DataInputStream(new ByteArrayInputStream(b));if(in.readInt()!=0x53575231)throw new IOException("Invalid sewer save");clock=in.readUnsignedShort();x=in.readUnsignedShort();from=in.readUnsignedShort();motion=in.readUnsignedByte();direction=in.readByte();fall=in.readUnsignedByte();fallX=in.readUnsignedShort();falls=in.readInt();done=in.readBoolean();
  if(in.read()!=-1||clock>=1000||x<128||x>448||x%64!=0||from<128||from>448||from%64!=0||Math.abs(x-from)>64||motion>4||direction!=-1&&direction!=1||fall>24||fallX>512||falls<0||done&&(x!=128||motion!=0||fall!=0))throw new IOException("Invalid sewer state");
 }
}
