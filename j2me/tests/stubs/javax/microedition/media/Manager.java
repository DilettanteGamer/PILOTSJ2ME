package javax.microedition.media;
import java.io.*;
import java.util.*;
import javax.microedition.media.control.VolumeControl;

/** Desktop media spy: parses real packaged streams, records device lifecycle. */
public final class Manager {
    public static final List<FakePlayer> players=Collections.synchronizedList(new ArrayList<FakePlayer>());
    public static volatile boolean rejectMidi,rejectAll;
    public static volatile int maxPCM=3;
    public static Player createPlayer(InputStream input,String type) throws IOException,MediaException {
        if(rejectAll || rejectMidi && type.equals("audio/midi")) throw new MediaException("Unsupported");
        if(type.equals("audio/x-wav")) {
            int opened=0;
            synchronized(players) { for(FakePlayer player:players) if(player.type.equals(type) && player.state!=Player.CLOSED) opened++; }
            if(opened>=maxPCM) throw new MediaException("Device PCM player limit");
        }
        ByteArrayOutputStream bytes=new ByteArrayOutputStream();byte[] block=new byte[4096];int n;
        while((n=input.read(block))!=-1) bytes.write(block,0,n);
        byte[] data=bytes.toByteArray();
        try {
            if(type.equals("audio/midi")) javax.sound.midi.MidiSystem.getSequence(new ByteArrayInputStream(data));
            else javax.sound.sampled.AudioSystem.getAudioInputStream(new ByteArrayInputStream(data)).close();
        } catch(Exception bad) { throw new IOException(bad); }
        FakePlayer player=new FakePlayer(type,data);players.add(player);return player;
    }
    public static final class FakePlayer implements Player,VolumeControl {
        public final String type;public final byte[] data;
        public volatile int state=UNREALIZED,starts,stops,level,loops=1;
        public FakePlayer(String type,byte[] data) { this.type=type;this.data=data; }
        public void realize() { state=REALIZED; }
        public void prefetch() { state=PREFETCHED; }
        public void start() { if(state==CLOSED) throw new IllegalStateException();state=STARTED;starts++; }
        public void stop() { state=PREFETCHED;stops++; }
        public void deallocate() { state=REALIZED; }
        public void close() { state=CLOSED; }
        public long setMediaTime(long value) { return value; }
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
