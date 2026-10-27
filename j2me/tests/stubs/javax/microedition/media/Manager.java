package javax.microedition.media;
import java.io.*;
import java.util.*;
import javax.microedition.media.control.VolumeControl;

/** Desktop media spy: parses real packaged streams, records device lifecycle. */
public final class Manager {
    public static final List<FakePlayer> players=Collections.synchronizedList(new ArrayList<FakePlayer>());
    public static volatile boolean rejectMidi,rejectAll,rejectSeek,rejectReplay;
    public static volatile boolean oomNextEffect;
    public static volatile boolean rejectEffectPrefetch;
    public static volatile int maxPCM=3;
    public static Player createPlayer(InputStream input,String type) throws IOException,MediaException {
        if(rejectAll || rejectMidi && type.equals("audio/midi")) throw new MediaException("Unsupported");
        if(type.equals("audio/x-wav")||type.equals("audio/mpeg")) {
            if(oomNextEffect){oomNextEffect=false;throw new OutOfMemoryError("Simulated native player allocation failure");}
            int opened=0;
            synchronized(players) { for(FakePlayer player:players) if(player.type.equals(type) && player.state!=Player.CLOSED) opened++; }
            if(opened>=maxPCM) throw new MediaException("Device PCM player limit");
        }
        ByteArrayOutputStream bytes=new ByteArrayOutputStream();byte[] block=new byte[4096];int n;
        while((n=input.read(block))!=-1) bytes.write(block,0,n);
        byte[] data=bytes.toByteArray();
        try {
            if(type.equals("audio/midi")) javax.sound.midi.MidiSystem.getSequence(new ByteArrayInputStream(data));
            else if(type.equals("audio/mpeg")) { if(data.length<4||(data[0]&255)!=255||(data[1]&224)!=224)throw new IOException("Invalid MP3 frame"); }
            else javax.sound.sampled.AudioSystem.getAudioInputStream(new ByteArrayInputStream(data)).close();
        } catch(Exception bad) { throw new IOException(bad); }
        FakePlayer player=new FakePlayer(type,data);players.add(player);return player;
    }
    public static final class FakePlayer implements Player,VolumeControl {
        public final String type;public final byte[] data;
        public volatile int state=UNREALIZED,starts,stops,level,loops=1,seeks;
        public volatile long lastSeek=-1;
        public volatile boolean failPrefetch;
        public FakePlayer(String type,byte[] data) { this.type=type;this.data=data; }
        public void realize() { state=REALIZED; }
        public void prefetch() throws MediaException { if(rejectEffectPrefetch && !type.equals("audio/midi"))throw new MediaException("TEST MP3 PREFETCH REJECTED");if(failPrefetch){failPrefetch=false;throw new IllegalStateException("Simulated prefetch failure");}state=PREFETCHED; }
        public void start() { if(state==CLOSED || rejectReplay && starts>0) throw new IllegalStateException();state=STARTED;starts++; }
        public void stop() { state=PREFETCHED;stops++; }
        public void deallocate() { state=REALIZED; }
        public void close() { state=CLOSED; }
        public long setMediaTime(long value) { if(rejectSeek)throw new IllegalStateException();lastSeek=value;seeks++;return value; }
        public long getMediaTime() { return 123456; }
        public long getDuration() { return 1000000; }
        public int getState() { return state; }
        public String getContentType() { return type; }
        public void setLoopCount(int count) { loops=count; }
        public void addPlayerListener(PlayerListener listener) { }
        public void removePlayerListener(PlayerListener listener) { }
        public Control getControl(String name) { return this; }
        public Control[] getControls() { return new Control[]{this}; }
        public void setMute(boolean mute) { }
        public boolean isMuted() { return false; }
        public int setLevel(int value) { return level=value; }
        public int getLevel() { return level; }
    }
}
