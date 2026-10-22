package pilots;
import java.io.*;
import java.util.*;
import javax.microedition.media.Manager;
import javax.microedition.media.Manager.FakePlayer;
import javax.microedition.media.Player;

public final class AudioTest {
    static void check(boolean ok,String message) { if(!ok) throw new RuntimeException(message); }
    static final class Cues implements SoundSink {
        final List<Integer> values=new ArrayList<Integer>();
        public void play(int cue) { values.add(cue); }
        void expect(int... expected) {
            List<Integer> want=new ArrayList<Integer>();for(int cue:expected) want.add(cue);
            check(values.equals(want),"Audio cues "+values+" expected "+Arrays.toString(expected));values.clear();
        }
    }
    static void steps(ZooLogic logic,int end) { while(logic.step()<end && logic.pendingAction()!=0) logic.advance(); }
    static void logic() throws Exception {
        GameState state=new GameState();state.scene=2;ZooLogic logic=new ZooLogic(state);Cues cues=new Cues();logic.setSound(cues);
        logic.beginWindow();steps(logic,11);cues.expect(22,22,22);steps(logic,logic.endStep());
        state.variables[51]=1;logic.begin(ZooLogic.WALL);cues.expect(6);steps(logic,logic.endStep());
        state.variables[0]=1;state.variables[1]=24;logic.advanceGuard();cues.expect(34);logic.continueTalk();cues.expect(35);logic.continueTalk();
        state.inventory[2]=true;logic.selectItem(3);logic.begin(ZooLogic.GATE);steps(logic,59);cues.expect(36,33);
        state.variables[10]=state.variables[52]=state.variables[53]=0;
        state.variables[40]=2;state.variables[41]=35;logic.advanceCat();cues.expect(17);
        state.variables[41]=41;logic.advanceCat();cues.expect(19);
        state.variables[40]=3;state.variables[41]=15;logic.advanceCat();cues.expect(30);
        state.variables[16]=-1;for(int i=0;i<10;i++) logic.walkStep(8,0);cues.expect(31,31);
        for(int sheet=0;sheet<3;sheet++) {
            state=new GameState();state.scene=3;CourtyardLogic courtyard=new CourtyardLogic(state);courtyard.setSound(cues);
            state.variables[0]=sheet;courtyard.begin(CourtyardLogic.PIANO);steps(courtyard,10);
            if(sheet==0) cues.expect(20,14);else if(sheet==1) cues.expect(22,13);else cues.expect(21);
            steps(courtyard,65);courtyard.select(1);courtyard.begin(CourtyardLogic.PIANO);cues.expect(12);
        }
        state=new GameState();state.scene=3;CourtyardLogic courtyard=new CourtyardLogic(state);courtyard.setSound(cues);
        courtyard.placeHero(1,320,288);state.variables[0]=1;courtyard.begin(CourtyardLogic.PIANO);steps(courtyard,13);cues.expect(22,13,19);
        courtyard.ambientTick();cues.expect(23);state.variables[22]=799;courtyard.ambientTick();cues.expect(23);state.variables[5]=99;courtyard.ambientTick();cues.expect(15);
        courtyard.finishAnimal();courtyard.placeHero(0,448,272); // Position is corrected below using the source map.
        boolean found=false;
        for(int y=0;y<416&&!found;y+=16) for(int x=0;x<640&&!found;x+=16)
            if(CourtyardWalk.animalSees(x,y)) { courtyard.placeHero(0,x,y);found=true; }
        courtyard.ambientTick();cues.expect(16);state.variables[6]=129;courtyard.ambientTick();cues.expect(17);
        // Loading a save attaches the sink without replaying past voices or actions.
        GameState saved=GameState.decode(state.encode(),4);new CourtyardLogic(saved).setSound(cues);cues.expect();
    }
    static void waitFor(java.util.function.BooleanSupplier condition) throws Exception {
        long end=System.currentTimeMillis()+3000;
        while(!condition.getAsBoolean() && System.currentTimeMillis()<end) Thread.sleep(10);
        check(condition.getAsBoolean(),"Audio worker timeout");
    }
    static FakePlayer latest() { return Manager.players.get(Manager.players.size()-1); }
    static void worker() throws Exception {
        GameAudio audio=new GameAudio();
        try {
            audio.scene(2);audio.active(true);waitFor(()->Manager.players.size()==1);FakePlayer music=latest();
            check(music.type.equals("audio/midi"),"Scene music missing");
            audio.play(34);waitFor(()->Manager.players.size()==2);FakePlayer speech=latest();
            audio.active(false);waitFor(()->speech.stops==1 && music.stops==1);
            audio.active(true);waitFor(()->speech.starts==2 && music.starts==2);
            check(Manager.players.size()==2,"Pause recreated players");
            speech.state=Player.PREFETCHED;waitFor(()->speech.state==Player.CLOSED);
            audio.scene(3);waitFor(()->music.state==Player.CLOSED && Manager.players.size()==3);
            FakePlayer nextMusic=latest();audio.play(22);waitFor(()->Manager.players.size()==4);FakePlayer piano=latest();
            audio.play(13);waitFor(()->Manager.players.size()==5);
            check(piano.state==Player.STARTED,"Reaction interrupted piano");
            audio.toggle();waitFor(()->nextMusic.state==Player.CLOSED && piano.state==Player.CLOSED);
            audio.play(12);Thread.sleep(150);check(Manager.players.size()==5,"Muted sound played");
            Manager.rejectMidi=true;audio.toggle();audio.play(12);waitFor(()->Manager.players.size()==6);
            check(latest().type.equals("audio/x-wav"),"WAV fallback failed");
            FakePlayer lastVoice=latest();Manager.maxPCM=1;audio.play(22);
            waitFor(()->Manager.players.size()==7 && lastVoice.state==Player.CLOSED);
            FakePlayer limitedPiano=latest();audio.play(0);Thread.sleep(150);
            check(limitedPiano.state==Player.STARTED,"Footsteps displaced piano on a one-player device");
            Manager.rejectAll=true;audio.play(0);Thread.sleep(150); // Unsupported MMAPI does not escape to gameplay.
        } finally { audio.close();Manager.rejectMidi=false;Manager.rejectAll=false;Manager.maxPCM=3; }
        waitFor(()->latest().state==Player.CLOSED);
        check(Manager.players.stream().allMatch(p->p.state==Player.CLOSED),"Leaked player");
    }
    static void resources(String[] hashes) throws Exception {
        for(int scene=2;scene<=3;scene++) {
            javax.sound.midi.Sequence music=javax.sound.midi.MidiSystem.getSequence(AudioTest.class.getResourceAsStream("/audio/0"+scene+".mid"));
            java.security.MessageDigest digest=java.security.MessageDigest.getInstance("SHA-256");
            javax.sound.midi.Track track=music.getTracks()[0];
            for(int i=0;i<track.size();i++) {
                javax.sound.midi.MidiEvent event=track.get(i);
                if(event.getMessage() instanceof javax.sound.midi.MetaMessage) continue;
                long tick=event.getTick();for(int shift=24;shift>=0;shift-=8) digest.update((byte)(tick>>shift));
                digest.update(event.getMessage().getMessage());
            }
            StringBuilder hex=new StringBuilder();for(byte b:digest.digest()) hex.append(String.format("%02x",b&255));
            check(hex.toString().equals(hashes[scene-2]),"MIDI notes/timestamps changed in conversion");
            check(music.getMicrosecondLength()==(scene==2?54850000L:314020000L),"MIDI timing changed");
            for(int cue=0;cue<(scene==2?39:24);cue++) {
                String path="/audio/0"+scene+"-"+(cue<10?"0":"")+cue+".wav";
                javax.sound.sampled.AudioInputStream wav=javax.sound.sampled.AudioSystem.getAudioInputStream(new BufferedInputStream(AudioTest.class.getResourceAsStream(path)));
                check(wav.getFormat().getChannels()==1 && wav.getFormat().getSampleSizeInBits()==8 && wav.getFormat().getSampleRate()==11025,"PCM format changed");wav.close();
            }
        }
    }
    public static void main(String[] args) throws Exception {
        resources(args);logic();worker();System.out.println("Audio: 63 WAVs, 2 MIDI tracks, source action cues, dialogue, ambient, mixing, pause/resume, mute, fallback and cleanup passed");
    }
}
