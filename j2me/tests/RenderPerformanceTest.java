package pilots;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

/** Driver call counts, pixel identity, invalidation and low-memory fallback. */
public final class RenderPerformanceTest {
    static void check(boolean ok,String message) { if(!ok) throw new RuntimeException(message); }
    static Graphics paint(Scene scene,ZooScene location,ZooPanel panel,Viewport view,int width,int height) {
        Graphics g=new Graphics(width,height);
        scene.background.paint(g,view);location.paint(g,view);
        int bar=height-view.height;scene.panel.paintPanel(g,width,height,bar);
        panel.paint(g,location.logic,width,height,bar);return g;
    }
    static void same(Graphics a,Graphics b,String label) {
        for(int y=0;y<a.image.getHeight();y++) for(int x=0;x<a.image.getWidth();x++)
            check(a.image.getRGB(x,y)==b.image.getRGB(x,y),label+" pixel "+x+","+y);
    }
    public static void main(String[] args) throws Exception {
        int width=Integer.parseInt(args[0]),height=Integer.parseInt(args[1]);
        int budget=width<200?65536:131072;
        for(int id=2;id<=4;id++) {
            Scene scene=new Scene(id);ZooPanel panel=new ZooPanel(id==4);
            try {
                GameState state=new GameState();state.scene=id;
                ZooScene location=id==4?new AnimalScene(scene,state):id==3?new CourtyardScene(scene,state):new ZooScene(scene,state);
                Viewport view=new Viewport(width,height-(width*63+320)/640,640,417);
                RasterCache.budget(0);Graphics original=paint(scene,location,panel,view,width,height);
                RasterCache.budget(budget);Graphics initial=paint(scene,location,panel,view,width,height);
                same(original,initial,"initial cached scene "+id);
                Graphics warm=paint(scene,location,panel,view,width,height);same(original,warm,"warm scene "+id);
                int before=original.rgbCalls,after=warm.rgbCalls+warm.imageCalls;
                check(after<before/2,"native calls did not fall: "+id+": "+before+" -> "+after);
                check(RasterCache.pixels()<=budget,"raster budget exceeded");
                System.out.println("RenderPerformance "+width+"x"+height+" scene "+id+": native calls "+before+" -> "+after+", cached pixels "+RasterCache.pixels()+" / "+budget);
                for(int variant=0;variant<3;variant++) {
                    view.move(64,32);view.zoom=variant==1;view.update();location.update(100);
                    Graphics cached=paint(scene,location,panel,view,width,height);
                    RasterCache.budget(0);Graphics plain=paint(scene,location,panel,view,width,height);
                    same(plain,cached,"camera/frame/zoom invalidation "+id);RasterCache.budget(budget);
                }
                RasterCache.budget(0);Graphics plain=paint(scene,location,panel,view,width,height);
                RasterCache.budget(budget);Image.failAllocations=true;
                try { same(plain,paint(scene,location,panel,view,width,height),"low-memory fallback "+id); }
                finally { Image.failAllocations=false; }
                check(RasterCache.pixels()==0,"failed image allocations retained cache");
            } finally { panel.close();scene.close(); }
        }
        RasterCache.budget(budget);
        WalkStreams.clear();int loads=WalkStreams.loads,hits=WalkStreams.hits;
        for(int i=0;i<4;i++) {
            AnimationClip clip=new AnimationClip("/scenes/02-20020.anm");
            try { while(clip.advance()) {} } finally { clip.close(); }
        }
        check(WalkStreams.loads-loads==1 && WalkStreams.hits-hits==3,"walking cycles repeatedly inflated ZIP data");
        WalkStreams.clear();
    }
}
