package pilots;
import java.io.DataInputStream;
import java.io.IOException;

/** Original initial map supplies occupied cells; BFS adapts it to a 16-pixel grid. */
public final class CourtyardWalk {
    private static byte[] occupied,watched;
    private static void load() {
        if(occupied!=null) return;
        try {
            DataInputStream input=new DataInputStream(CourtyardWalk.class.getResourceAsStream("/scenes/03.walk"));
            try {
                if(input.readInt()!=0x5a574d31) throw new IOException("Invalid courtyard walk map");
                byte[] cells=new byte[270],trigger=new byte[270];input.readFully(cells);input.readFully(trigger);occupied=cells;watched=trigger;
            } finally { input.close(); }
        } catch(IOException e) { throw new IllegalStateException(e.toString()); }
    }
    public static boolean allowed(int x,int y) {
        load();return x>=0 && x<=624 && y>=64 && y<=400 && occupied[y/16*10+x/64]==0;
    }
    public static boolean animalSees(int x,int y) {
        load();return x>=0 && x<640 && y>=0 && y<432 && watched[y/16*10+x/64]!=0;
    }
    public static int[] plan(int sx,int sy,int tx,int ty) {
        if(!allowed(tx,ty)) return new int[0];
        int width=40,size=40*26;
        int start=(sy+8)/16*width+(sx+8)/16,end=ty/16*width+tx/16;
        int[] previous=new int[size],queue=new int[size];
        for(int i=0;i<size;i++) previous[i]=-1;
        if(start<0 || start>=size) return new int[0];
        int head=0,tail=0;queue[tail++]=start;previous[start]=start;
        int[] dx={-1,1,0,0},dy={0,0,-1,1};
        while(head<tail && previous[end]<0) {
            int node=queue[head++],x=node%width,y=node/width;
            for(int i=0;i<4;i++) {
                int nx=x+dx[i],ny=y+dy[i];
                if(nx<0 || nx>=40 || !allowed(nx*16,ny*16)) continue;
                int next=ny*width+nx;
                if(previous[next]<0) { previous[next]=node;queue[tail++]=next; }
            }
        }
        if(previous[end]<0) return new int[0];
        int length=1;for(int p=end;p!=start;p=previous[p]) length++;
        int[] path=new int[length*2];int p=end;
        for(int i=length-1;i>=0;i--) { path[i*2]=(p%width)*16;path[i*2+1]=(p/width)*16;p=previous[p]; }
        return path;
    }
}
