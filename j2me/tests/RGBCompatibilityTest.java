package pilots;

import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import javax.microedition.lcdui.Graphics;

/** All packaged frames through the production renderer and full-stride driver spy. */
public final class RGBCompatibilityTest {
    public static void main(String[] args) throws Exception {
        // Exercise the device-safe fallback even when image caches are available.
        RasterCache.budget(0);
        int width=Integer.parseInt(args[1]),height=Integer.parseInt(args[2]);
        Graphics surface=new Graphics(width,height);
        Viewport view=new Viewport(width,height,640,417);
        int clips=0,frames=0;
        ZipFile archive=new ZipFile(args[0]);
        try {
            Enumeration entries=archive.entries();
            while(entries.hasMoreElements()) {
                String name=((ZipEntry)entries.nextElement()).getName();
                if(!name.endsWith(".anm")) continue;
                AnimationClip clip=new AnimationClip("/"+name);
                try {
                    do {
                        // Centre every source frame, including frames whose native
                        // offsets put them off-screen at a generic actor root.
                        int x=320-clip.offsetX-clip.width/2;
                        int y=208-clip.offsetY-clip.height/2;
                        view.cursorX=320;view.cursorY=208;
                        for(int zoom=0;zoom<2;zoom++) {
                            view.zoom=zoom!=0;view.update();
                            clip.paint(surface,view,x,y);
                            // Clip the same frame against the top and left edges.
                            clip.paint(surface,view,x-320,y-208);
                        }
                        frames++;
                    } while(clip.advance());
                } finally { clip.close(); }
                clips++;
            }
        } finally { archive.close(); }
        if(clips!=856 || frames<=clips) throw new RuntimeException("Incomplete RGB coverage: "+clips+" / "+frames);
        System.out.println("RGBCompatibility: "+clips+" clips / "+frames+" frames, normal/zoomed/clipped, full-stride bounds at "+width+"x"+height);
    }
}
