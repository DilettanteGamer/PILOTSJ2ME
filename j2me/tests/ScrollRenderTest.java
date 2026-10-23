package pilots;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

/** Panning, exposed strips, clipped sprites and cursor-only compositing. */
public final class ScrollRenderTest {
    static void check(boolean ok,String message) { if(!ok) throw new RuntimeException(message); }
    static void same(Graphics a,Graphics b,String label) {
        for(int y=0;y<a.image.getHeight();y++) for(int x=0;x<a.image.getWidth();x++)
            check(a.image.getRGB(x,y)==b.image.getRGB(x,y),label+" at "+x+","+y);
    }
    static Viewport viewport(int width,int height) { return new Viewport(width,height-(width*63+320)/640,640,417); }
    static final int[][] POS={{320,208},{328,208},{336,208},{328,216},{320,224},{312,216},{304,208},{312,200},{320,208},{0,0},{639,416},{320,208}};
    static void background(int width,int height,int budget) throws Exception {
        Scene scene=new Scene(2);
        try {
            Viewport view=viewport(width,height);
            for(int zoom=0;zoom<2;zoom++) {
                view.zoom=zoom!=0;
                Graphics[] expected=new Graphics[POS.length];RasterCache.budget(0);
                for(int i=0;i<POS.length;i++) {
                    view.cursorX=POS[i][0];view.cursorY=POS[i][1];view.update();
                    expected[i]=new Graphics(width,view.height);scene.background.paint(expected[i],view);
                }
                RasterCache.budget(budget);long oldPixels=0,newPixels=0;
                int left=0,top=0;
                for(int i=0;i<POS.length;i++) {
                    view.cursorX=POS[i][0];view.cursorY=POS[i][1];view.update();
                    Graphics actual=new Graphics(width,view.height);
                    long pixels=Graphics.rgbPixels;int allocations=Image.allocations,calls=Graphics.rgbTotalCalls;
                    scene.background.paint(actual,view);same(expected[i],actual,"scroll/zoom "+zoom+"/"+i);
                    if(i>0) {
                        int dx=Math.abs(view.left-left),dy=Math.abs(view.top-top);
                        check(Image.allocations==allocations,"pan recreated background Image");
                        long updated=Graphics.rgbPixels-pixels;
                        if(dx<width && dy<view.height) {
                            check(updated<=(long)dx*view.height+(long)dy*width,"pan repainted interior pixels");
                            if(dx<=8 && dy<=8) check(Graphics.rgbTotalCalls-calls<=2,"pan submitted each band row separately");
                            if(dx!=0 || dy!=0) { oldPixels+=(long)width*view.height;newPixels+=updated; }
                        }
                    }
                    left=view.left;top=view.top;
                }
                check(newPixels<oldPixels/2,"strip update did not reduce scaling");
                System.out.println("ScrollRender "+width+"x"+height+" zoom="+zoom+": background scaled pixels "+oldPixels+" -> "+newPixels+", no Image recreation during pan");
            }
        } finally { scene.close(); }
        for(int mode=0;mode<2;mode++) {
            scene=new Scene(2);RasterCache.budget(budget);Viewport view=viewport(width,height);
            try {
                scene.background.paint(new Graphics(width,view.height),view);int allocations=Image.allocations;
                view.move(16,0);Graphics actual=new Graphics(width,view.height);
                Graphics.rejectCopyArea=mode==0;Graphics.failCopyArea=mode==1;
                try { scene.background.paint(actual,view); }
                finally { Graphics.rejectCopyArea=Graphics.failCopyArea=false; }
                check(Image.allocations==allocations,"copyArea fallback recreated Image");
                RasterCache.budget(0);Graphics plain=new Graphics(width,view.height);scene.background.paint(plain,view);
                same(plain,actual,"copyArea fallback "+mode);
            } finally { scene.close(); }
        }
    }
    static void sprite(int width,int height,int budget) throws Exception {
        AnimationClip clip=new AnimationClip("/scenes/02-20012.anm");Viewport view=viewport(width,height);
        Graphics[] expected=new Graphics[7];RasterCache.budget(0);
        try {
            for(int i=0;i<7;i++) {
                view.left=-1-i;expected[i]=new Graphics(width,height);
                clip.paint(expected[i],view,-clip.offsetX,208-clip.offsetY);
            }
            RasterCache.budget(budget);int allocations=0;
            for(int i=0;i<7;i++) {
                view.left=-1-i;Graphics actual=new Graphics(width,height);
                clip.paint(actual,view,-clip.offsetX,208-clip.offsetY);same(expected[i],actual,"sprite edge "+i);
                if(i==0) allocations=Image.allocations;
                else check(Image.allocations==allocations,"clipping a cached sprite recreated Image");
            }
            view.left=-7;Graphics fallback=new Graphics(width,height);Graphics.rejectDrawRegion=true;
            try { clip.paint(fallback,view,-clip.offsetX,208-clip.offsetY); }
            finally { Graphics.rejectDrawRegion=false; }
            same(expected[6],fallback,"drawRegion fallback");
        } finally { clip.close(); }
    }
    static void paint(Graphics g,Scene scene,ZooScene location,ZooPanel panel,Viewport view,int width,int height) {
        int cx=g.getClipX(),cy=g.getClipY(),cw=g.getClipWidth(),ch=g.getClipHeight();
        g.setColor(0);g.fillRect(0,0,width,height);g.clipRect(0,0,width,view.height);view.paintClip(cx,cy,cw,ch);
        scene.background.paint(g,view);location.paint(g,view);
        int x=view.screenX(view.cursorX),y=view.screenY(view.cursorY);
        g.setColor(0);g.drawLine(x-4,y+1,x+4,y+1);g.drawLine(x+1,y-4,x+1,y+4);
        g.setColor(location.logic.targetAt(view.cursorX,view.cursorY)!=0?0xffff00:0xffffff);
        g.drawLine(x-4,y,x+4,y);g.drawLine(x,y-4,x,y+4);
        g.setClip(cx,cy,cw,ch);
        if(cy+ch>view.height) { scene.panel.paintPanel(g,width,height,height-view.height);panel.paint(g,location.logic,width,height,height-view.height); }
    }
    static void cursor(int width,int height,int budget) throws Exception {
        Scene scene=new Scene(2);ZooPanel panel=new ZooPanel();GameState state=new GameState();state.scene=2;
        try {
            ZooScene location=new ZooScene(scene,state);Viewport view=viewport(width,height);
            int[] ys={120,128,112,280,288,272};Graphics[] expected=new Graphics[ys.length];RasterCache.budget(0);
            for(int i=0;i<ys.length;i++) { view.cursorY=ys[i];view.update();expected[i]=new Graphics(width,height);paint(expected[i],scene,location,panel,view,width,height); }
            RasterCache.budget(budget);Graphics actual=new Graphics(width,height);
            view.cursorY=ys[0];view.update();paint(actual,scene,location,panel,view,width,height);
            // Touch each cache once more so the call count describes an unchanged frame.
            paint(actual,scene,location,panel,view,width,height);int full=actual.imageCalls/2;
            int oldX=view.screenX(view.cursorX),oldY=view.screenY(view.cursorY);
            for(int i=1;i<ys.length;i++) {
                view.cursorY=ys[i];view.update();int x=view.screenX(view.cursorX),y=view.screenY(view.cursorY);
                int allocations=Image.allocations;actual.imageCalls=0;long pixels=Graphics.rgbPixels;
                actual.setClip(Math.min(oldX,x)-6,Math.min(oldY,y)-6,Math.abs(x-oldX)+13,Math.abs(y-oldY)+13);
                paint(actual,scene,location,panel,view,width,height);same(expected[i],actual,"cursor up/down "+i);
                check(actual.imageCalls<full,"cursor repainted every sprite");
                check(Image.allocations==allocations && Graphics.rgbPixels==pixels,"cursor recreated/rescaled rasters");
                System.out.println("ScrollRender "+width+"x"+height+": cursor bitmap calls "+full+" -> "+actual.imageCalls);
                oldX=x;oldY=y;
            }
            RasterCache.budget(0);actual.setClip(0,0,width,height);
            paint(actual,scene,location,panel,view,width,height);
            oldY=view.screenY(view.cursorY);view.cursorY=ys[0];view.update();int y=view.screenY(view.cursorY);
            actual.setClip(oldX-6,Math.min(oldY,y)-6,13,Math.abs(y-oldY)+13);
            paint(actual,scene,location,panel,view,width,height);same(expected[0],actual,"uncached cursor damage");
        } finally { panel.close();scene.close(); }
    }
    public static void main(String[] args) throws Exception {
        int width=Integer.parseInt(args[0]),height=Integer.parseInt(args[1]),budget=width<200?65536:131072;
        background(width,height,budget);sprite(width,height,budget);cursor(width,height,budget);
        RasterCache.budget(budget);
    }
}
