package pilots;
import java.io.*;
/** Original visible rat columns and the exact roots used to paint each frame. */
public final class SewerRats {
 private static SewerRats shared;
 private final int[] root=new int[3];
 private final int[][] shift=new int[3][20],left=new int[3][20],width=new int[3][20];
 private final byte[][][] columns=new byte[3][20][];
 public static synchronized SewerRats get()throws IOException{if(shared==null)shared=new SewerRats();return shared;}
 private SewerRats()throws IOException{
  InputStream raw=getClass().getResourceAsStream("/scenes/07.contact");if(raw==null)throw new IOException("Missing rat contact data");DataInputStream in=new DataInputStream(raw);
  try{if(in.readInt()!=0x52435431)throw new IOException("Invalid rat contact data");for(int r=0;r<3;r++){root[r]=in.readShort();for(int f=0;f<20;f++){shift[r][f]=in.readShort();left[r][f]=in.readShort();int n=in.readUnsignedShort();if(n>640)throw new IOException("Invalid rat contact width");width[r][f]=n;columns[r][f]=new byte[(n+7)/8];in.readFully(columns[r][f]);}}if(in.read()!=-1)throw new IOException("Trailing rat contact data");}finally{in.close();}
 }
 public int x(int rat,int frame){return root[rat]+shift[rat][frame];}
 public boolean hits(int rat,int frame,int point){int p=point-left[rat][frame];return p>=0&&p<width[rat][frame]&&(columns[rat][frame][p/8]&(1<<(p%8)))!=0;}
}
