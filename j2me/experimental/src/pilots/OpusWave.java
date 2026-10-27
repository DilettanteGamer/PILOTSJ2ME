package pilots;
import java.io.*;
import org.concentus.SilkNbOnlyProbe;
/** Restricted mono NB20ms packets, bounded PCM buffers, exact source duration. */
public final class OpusWave extends InputStream {
 private final DataInputStream in;private final SilkNbOnlyProbe decoder=new SilkNbOnlyProbe();private final short[] pcm=new short[240];private final byte[] header=new byte[44];private int packets,at=240,head,remaining,position,index,a,b,value,part;private boolean initialized;
 OpusWave(DataInputStream input)throws IOException{in=input;remaining=in.readInt();packets=in.readInt();if(remaining<0||remaining>2000000||packets<1||packets>65535)throw new IOException("Opus lengths");int skip=in.readUnsignedShort();if(skip!=78)throw new IOException("Opus pre-skip");String names="RIFF    WAVEfmt                 data    ";byte[] tags={82,73,70,70};System.arraycopy(tags,0,header,0,4);put(4,36+remaining*2);header[8]=87;header[9]=65;header[10]=86;header[11]=69;header[12]=102;header[13]=109;header[14]=116;header[15]=32;put(16,16);header[20]=1;header[22]=1;put(24,11025);put(28,22050);header[32]=2;header[34]=16;header[36]=100;header[37]=97;header[38]=116;header[39]=97;put(40,remaining*2);for(int i=0;i<skip;i++)next();}
 private void put(int p,int n){for(int i=0;i<4;i++)header[p+i]=(byte)(n>>>(8*i));}
 private int next()throws IOException{if(at==240){if(packets--<=0)return 0;int n=in.readUnsignedShort();if(n<1||n>1500)throw new IOException("Opus packet");byte[] data=new byte[n];in.readFully(data);try{if(decoder.decode(data,pcm)!=240)throw new IOException("Opus frame");}catch(RuntimeException e){throw new IOException("Opus: "+e.toString());}at=0;}return pcm[at++];}
 public int read()throws IOException{if(head<44)return header[head++]&255;if(part==1){part=0;return(value>>>8)&255;}if(remaining==0)return -1;if(!initialized){a=next();b=next();initialized=true;}long phase=(long)position*12000;int target=(int)(phase/11025);while(index<target){a=b;b=next();index++;}value=a+(int)((long)(b-a)*(phase%11025)/11025);position++;remaining--;part=1;return value&255;}
 public int read(byte[] b,int off,int len)throws IOException{if(len==0)return 0;int n=0;while(n<len){int v=read();if(v<0)break;b[off+n++]=(byte)v;}return n==0?-1:n;}
 public int available(){return 44-head+remaining*2+(part==1?1:0);}
 public void close()throws IOException{in.close();}
}
