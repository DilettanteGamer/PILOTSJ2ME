package pilots;

/** Local first-location walk graph. Exit cells are enabled only by the original gate event. */
public final class ZooWalk {
    private static final int WIDTH=40, ROWS=12, TOP=240;
    public static boolean allowed(int x,int y,boolean gateOpen) {
        if(x<16 || x>624 || y<TOP || y>416) return false;
        if(y<304) return gateOpen && x>=384 && x<=448;
        return !(x>=128 && x<192 && y<352);
    }
    public static int[] plan(int sx,int sy,int tx,int ty,boolean gateOpen) {
        int startX=Math.max(1,Math.min(39,(sx+8)/16)), startY=Math.max(0,Math.min(11,(sy-TOP+8)/16));
        int endX=(tx+8)/16,endY=(ty-TOP+8)/16;
        if(!allowed(endX*16,endY*16+TOP,gateOpen)) return new int[0];
        int start=startY*WIDTH+startX,end=endY*WIDTH+endX;
        int[] previous=new int[WIDTH*ROWS],queue=new int[WIDTH*ROWS];
        for(int i=0;i<previous.length;i++) previous[i]=-1;
        int head=0,tail=0;queue[tail++]=start;previous[start]=start;
        int[] dx={-1,1,0,0},dy={0,0,-1,1};
        while(head<tail && previous[end]<0) {
            int node=queue[head++],x=node%WIDTH,y=node/WIDTH;
            for(int i=0;i<4;i++) {
                int nx=x+dx[i],ny=y+dy[i];
                if(nx<0 || nx>=WIDTH || ny<0 || ny>=ROWS || !allowed(nx*16,ny*16+TOP,gateOpen)) continue;
                int next=ny*WIDTH+nx;
                if(previous[next]>=0) continue;
                previous[next]=node;queue[tail++]=next;
            }
        }
        if(previous[end]<0) return new int[0];
        int length=1;
        for(int n=end;n!=start;n=previous[n]) length++;
        int[] path=new int[length*2]; int n=end;
        for(int i=length-1;i>=0;i--) { path[i*2]=(n%WIDTH)*16; path[i*2+1]=(n/WIDTH)*16+TOP; n=previous[n]; }
        return path;
    }
}
