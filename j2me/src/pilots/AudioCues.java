package pilots;

import java.io.DataInputStream;
import java.io.IOException;

/** Bindings compiled from the original predicates, including branch and counter. */
public final class AudioCues {
    private final byte[] events;
    public AudioCues(int scene) throws IOException {
        DataInputStream in=new DataInputStream(getClass().getResourceAsStream("/audio/"+(scene<10?"0":"")+scene+".events"));
        try {
            if(in.readInt()!=0x5a415531) throw new IOException("Invalid audio events");
            events=new byte[in.readUnsignedShort()*5];in.readFully(events);
            if(in.read()!=-1) throw new IOException("Trailing audio events");
        } finally { in.close(); }
    }
    public void at(SoundSink sink,int hero,int action,int branch,int step) {
        for(int i=0;i<events.length;i+=5)
            if(events[i]==hero && events[i+1]==action && events[i+2]==branch && events[i+3]==step)
                sink.play(events[i+4]&255);
    }
}
