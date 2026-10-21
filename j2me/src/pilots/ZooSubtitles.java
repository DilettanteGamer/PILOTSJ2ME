package pilots;

import java.io.DataInputStream;
import java.io.InputStream;
import java.io.IOException;

/** Speech captions and event bindings compiled against the original native 11 calls. */
public final class ZooSubtitles {
    private static String[] captions;
    private static byte[] events;
    private static synchronized void load() {
        if(captions!=null) return;
        InputStream stream=ZooSubtitles.class.getResourceAsStream("/scenes/02.subtitles");
        if(stream==null) throw new IllegalStateException("Missing zoo subtitles");
        DataInputStream input=new DataInputStream(stream);
        try {
            if(input.readInt()!=0x5a535431) throw new IOException("Invalid subtitle header");
            int count=input.readUnsignedShort();
            if(count>64) throw new IOException("Invalid subtitle count");
            String[] text=new String[256];
            for(int i=0;i<count;i++) text[input.readUnsignedByte()]=input.readUTF();
            count=input.readUnsignedShort();
            if(count>128) throw new IOException("Invalid subtitle event count");
            byte[] bindings=new byte[count*5];input.readFully(bindings);
            if(input.read()!=-1) throw new IOException("Trailing subtitle data");
            events=bindings;captions=text;
        } catch(IOException failure) { throw new IllegalStateException(failure.toString()); }
        finally { try { input.close(); } catch(IOException ignored) {} }
    }
    public static String text(int cue) {
        load();return cue>=0 && cue<captions.length?captions[cue]:null;
    }
    public static int at(int action,int hero,int branch,int step) {
        load();
        for(int i=0;i<events.length;i+=5)
            if((events[i]&255)==action && (events[i+1]&255)==hero &&
               (events[i+2]&255)==(action==13?branch:0) && (events[i+3]&255)==step) return events[i+4]&255;
        return 0;
    }
}
