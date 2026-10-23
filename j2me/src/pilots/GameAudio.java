package pilots;

import java.io.InputStream;
import javax.microedition.media.Manager;
import javax.microedition.media.Player;
import javax.microedition.media.control.VolumeControl;

/** One MIDI and at most three PCM players; only the worker touches MMAPI. */
public final class GameAudio implements Runnable,MusicSink {
    private int scene=2,generation,track=1;
    private boolean active,enabled=true,closed,activated;
    private final int[] queue=new int[16];
    private int count;
    private final Thread worker;
    private final Player[] effects=new Player[3];
    private final InputStream[] streams=new InputStream[3];
    private final int[] priority=new int[3];
    private final long[] age=new long[3];
    private final boolean[] resume=new boolean[3];
    private Player music;
    private InputStream musicStream;
    private boolean resumeMusic,musicAttempted;
    private long serial;

    public GameAudio() { worker=new Thread(this);worker.start(); }
    public synchronized void scene(int value) { scene=value;track=1;generation++;count=0;activated=false;notifyAll(); }
    public synchronized void music(int value) { if(value>=1 && value<=2) { track=value;notifyAll(); } }
    public synchronized void active(boolean value) { active=value;if(value) activated=true;if(!value && activated) count=0;notifyAll(); }
    public synchronized boolean toggle() { enabled=!enabled;generation++;count=0;notifyAll();return enabled; }
    public synchronized void play(int cue) {
        if(closed || !enabled || !active&&activated) return;
        if(count<queue.length) queue[count++]=cue;
        notifyAll();
    }
    public synchronized void close() { closed=true;count=0;notifyAll(); }
    public void run() {
        int applied=-1,appliedTrack=-1;boolean wasActive=false;
        try {
            while(true) {
                int current,revision,currentTrack,cue=-1;boolean running;
                synchronized(this) {
                    if(closed) break;
                    current=scene;currentTrack=track;revision=generation;running=active&&enabled;
                    if(running && count>0) {
                        cue=queue[0];for(int i=1;i<count;i++) queue[i-1]=queue[i];count--;
                    }
                }
                if(applied!=revision) { releaseAll();applied=revision;wasActive=false;musicAttempted=false; }
                if(appliedTrack!=currentTrack) { closeMusic();musicAttempted=false;appliedTrack=currentTrack; }
                if(running!=wasActive) {
                    for(int i=0;i<effects.length;i++) resume[i]=pause(effects[i],running,resume[i]);
                    resumeMusic=pause(music,running,resumeMusic);wasActive=running;
                }
                reap();
                if(running && !musicAttempted) { musicAttempted=true;startMusic(current,currentTrack); }
                if(running && cue>=0) startEffect(current,cue);
                synchronized(this) {
                    if(!closed && (!running || count==0) && applied==generation && wasActive==(active&&enabled))
                        try { wait(100); } catch(InterruptedException ignored) { }
                }
            }
        } finally { releaseAll(); }
    }
    private boolean pause(Player player,boolean running,boolean shouldResume) {
        if(player==null) return false;
        try {
            if(!running && player.getState()==Player.STARTED) { player.stop();return true; }
            if(running && shouldResume) player.start();
        } catch(Exception ignored) { }
        return !running && shouldResume;
    }
    private void volume(Player player,int level) {
        VolumeControl control=(VolumeControl)player.getControl("VolumeControl");
        if(control!=null) control.setLevel(level);
    }
    private void startMusic(int location,int selected) {
        try {
            musicStream=getClass().getResourceAsStream("/audio/0"+location+(selected==1?"":"-02")+".mid");
            if(musicStream==null) return;
            music=Manager.createPlayer(musicStream,"audio/midi");music.realize();music.prefetch();
            volume(music,45);music.start();
        } catch(Exception ignored) { closeMusic(); }
        catch(OutOfMemoryError ignored) { closeMusic(); }
    }
    private void startEffect(int location,int cue) {
        int rank=location==3&&cue==23?0:cue==(location==3 || location==6?0:location==9?3:31)?1:
            cue==6||cue==7||location==2&&cue>=34&&cue<=36||location==3&&cue>=20&&cue<=22||location==4&&cue==58||location==6&&cue==31?3:2;
        int slot=-1;
        for(int i=0;i<effects.length;i++) {
            if(effects[i]==null) { slot=i;break; }
            if(priority[i]<=rank && (slot<0 || priority[i]<priority[slot] || priority[i]==priority[slot]&&age[i]<age[slot])) slot=i;
        }
        if(slot<0) return;
        release(slot);priority[slot]=rank;age[slot]=++serial;
        String path="/audio/0"+location+"-"+(cue<10?"0":"")+cue+".wav";
        // Some MIDP implementations cannot mix MIDI and WAV: give gameplay audio priority.
        for(int attempt=0;attempt<3;attempt++) {
            try {
                streams[slot]=getClass().getResourceAsStream(path);
                if(streams[slot]==null) return;
                effects[slot]=Manager.createPlayer(streams[slot],"audio/x-wav");
                effects[slot].realize();effects[slot].prefetch();volume(effects[slot],location==3&&cue==23?35:85);effects[slot].start();return;
            } catch(Exception ignored) { release(slot); }
            catch(OutOfMemoryError ignored) { release(slot);return; }
            if(music!=null) { closeMusic();continue; }
            boolean freed=false;
            for(int i=0;i<effects.length;i++) if(i!=slot && effects[i]!=null && priority[i]<=rank) { release(i);freed=true; }
            if(!freed) return;
        }
    }
    private void reap() {
        for(int i=0;i<effects.length;i++)
            if(effects[i]!=null && !resume[i] && effects[i].getState()!=Player.STARTED) release(i);
        if(music!=null && !resumeMusic && music.getState()!=Player.STARTED) closeMusic();
    }
    private void release(int i) {
        if(effects[i]!=null) { effects[i].close();effects[i]=null; }
        if(streams[i]!=null) { try { streams[i].close(); } catch(Exception ignored) { }streams[i]=null; }
        resume[i]=false;
    }
    private void closeMusic() {
        if(music!=null) { music.close();music=null; }
        if(musicStream!=null) { try { musicStream.close(); } catch(Exception ignored) { }musicStream=null; }
        resumeMusic=false;
    }
    private void releaseAll() { for(int i=0;i<effects.length;i++) release(i);closeMusic(); }
}
