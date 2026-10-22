package pilots;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/** Two hot walking cycles; avoids repeated ZIP inflation when a gait loops. */
public final class WalkStreams {
    private static String recentName,olderName;
    private static byte[] recentData,olderData;
    static int loads,hits;
    public static void clear() { recentName=olderName=null;recentData=olderData=null; }
    public static InputStream open(String name) throws IOException {
        int dash=name.lastIndexOf('-');
        boolean walking=false;
        if(name.startsWith("/scenes/") && dash>=0 && name.endsWith(".anm")) {
            int id=Integer.parseInt(name.substring(dash+1,name.length()-4));
            walking=id>=20020 && id<=20033;
        }
        if(!walking) return WalkStreams.class.getResourceAsStream(name);
        if(name.equals(recentName)) { hits++;return new ByteArrayInputStream(recentData); }
        if(name.equals(olderName)) {
            String swapName=recentName;byte[] swapData=recentData;
            recentName=olderName;recentData=olderData;olderName=swapName;olderData=swapData;
            hits++;return new ByteArrayInputStream(recentData);
        }
        InputStream input=WalkStreams.class.getResourceAsStream(name);
        if(input==null) return null;
        try {
            ByteArrayOutputStream out=new ByteArrayOutputStream();byte[] block=new byte[1024];
            int count;
            while((count=input.read(block))!=-1) {
                if(out.size()+count>32768) { input.close();return WalkStreams.class.getResourceAsStream(name); }
                out.write(block,0,count);
            }
            byte[] data=out.toByteArray();
            olderName=recentName;olderData=recentData;recentName=name;recentData=data;loads++;
            return new ByteArrayInputStream(data);
        } catch(OutOfMemoryError exhausted) {
            clear();return WalkStreams.class.getResourceAsStream(name);
        } finally { input.close(); }
    }
}
