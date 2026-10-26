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
            Manager.rejectAll=false;Manager.rejectMidi=false;Manager.maxPCM=3;
            audio.scene(4);waitFor(()->Manager.players.size()==8);audio.play(58);waitFor(()->Manager.players.size()==9);
            check(latest().type.equals("audio/x-wav"),"Third-location speech missing");
            audio.active(false);audio.scene(4);audio.play(41);Thread.sleep(150);
            check(Manager.players.size()==9,"Initial scene sound escaped the hidden canvas");
            audio.active(true);waitFor(()->Manager.players.size()==11);
            audio.scene(6);waitFor(()->Manager.players.size()==12);audio.play(31);waitFor(()->Manager.players.size()==13);
            FakePlayer streetFinal=latest();Manager.maxPCM=1;audio.play(0);Thread.sleep(150);
            check(streetFinal.state==Player.STARTED && Manager.players.size()==13,"Street footsteps interrupted final sound");
            Manager.maxPCM=3;audio.scene(9);waitFor(()->Manager.players.size()==14);FakePlayer roomMusic=latest();audio.music(2);waitFor(()->Manager.players.size()==15);
            check(roomMusic.state==Player.CLOSED && latest().type.equals("audio/midi"),"hotel ending music change failed");
            audio.scene(10);waitFor(()->Manager.players.size()==16);audio.play(41);waitFor(()->Manager.players.size()==17);
            check(latest().type.equals("audio/x-wav"),"balcony two-digit scene sound missing");
            audio.scene(11);waitFor(()->Manager.players.size()==18);audio.play(23);waitFor(()->Manager.players.size()==19);
            check(latest().type.equals("audio/x-wav"),"ledge speech missing");
            audio.scene(12);waitFor(()->Manager.players.size()==20);audio.play(45);waitFor(()->Manager.players.size()==21);
            check(latest().type.equals("audio/x-wav"),"bathroom cue missing");
            audio.scene(15);waitFor(()->Manager.players.size()==22);audio.play(29);waitFor(()->Manager.players.size()==23);
            check(latest().type.equals("audio/x-wav"),"basement cue missing");
        } finally { audio.close();Manager.rejectMidi=false;Manager.rejectAll=false;Manager.maxPCM=3; }
        waitFor(()->latest().state==Player.CLOSED);
        check(Manager.players.stream().allMatch(p->p.state==Player.CLOSED),"Leaked player");
        GameAudio menu=new GameAudio();menu.scene(9);menu.music(2);menu.menu(true);int prior=Manager.players.size();
        try{menu.active(true);waitFor(()->Manager.players.size()==prior+1&&latest().state==Player.STARTED);FakePlayer folder=latest();check(folder.loops==-1,"menu music not looped");check(Arrays.equals(folder.data,PackedResources.open("/audio/02.mid").readAllBytes()),"menu does not use entrance music");menu.active(false);waitFor(()->folder.stops==1);menu.active(true);waitFor(()->folder.starts==2);menu.menu(false);waitFor(()->folder.state==Player.CLOSED&&Manager.players.size()==prior+2);FakePlayer room=latest();check(room.loops==1&&Arrays.equals(room.data,PackedResources.open("/audio/09-02.mid").readAllBytes()),"menu lost gameplay track");menu.toggle();waitFor(()->room.state==Player.CLOSED);}finally{menu.close();}
    }
    static void resources(String[] hashes) throws Exception {
        int[] scenes={2,3,4,5,6,8,9,10,11,12,13,15,14,16,17};
        for(int si=0;si<scenes.length;si++) { int scene=scenes[si];
            javax.sound.midi.Sequence music=javax.sound.midi.MidiSystem.getSequence(PackedResources.open("/audio/"+(scene<10?"0":"")+scene+".mid"));
            java.security.MessageDigest digest=java.security.MessageDigest.getInstance("SHA-256");
            javax.sound.midi.Track track=music.getTracks()[0];
            for(int i=0;i<track.size();i++) {
                javax.sound.midi.MidiEvent event=track.get(i);
                if(event.getMessage() instanceof javax.sound.midi.MetaMessage) continue;
                long tick=event.getTick();for(int shift=24;shift>=0;shift-=8) digest.update((byte)(tick>>shift));
                digest.update(event.getMessage().getMessage());
            }
            StringBuilder hex=new StringBuilder();for(byte b:digest.digest()) hex.append(String.format("%02x",b&255));
            check(hex.toString().equals(hashes[si]),"MIDI notes/timestamps changed in conversion");
            check(music.getMicrosecondLength()==(scene==2?54850000L:scene==3?314020000L:scene==4?109580000L:scene==5?109670000L:scene==6?109710000L:scene==8?90300000L:scene==9?123390000L:scene==10?90350000L:scene==11?80160000L:scene==17?107290000L:scene==16?109700000L:scene==14?82280000L:scene==12||scene==15?109710000L:90300000L),"MIDI timing changed");
            for(int cue=0;cue<(scene==2?39:scene==3?24:scene==4?62:scene==5?25:scene==6?32:scene==8?16:scene==9?53:scene==10?42:scene==11?24:scene==12?46:scene==17?47:scene==16?28:scene==14?45:scene==15?30:31);cue++) {
                String path="/audio/"+(scene<10?"0":"")+scene+"-"+(cue<10?"0":"")+cue+".wav";
                javax.sound.sampled.AudioInputStream wav=javax.sound.sampled.AudioSystem.getAudioInputStream(new BufferedInputStream(PackedResources.open(path)));
                check(wav.getFormat().getChannels()==1 && wav.getFormat().getSampleSizeInBits()==16 && wav.getFormat().getSampleRate()==11025,"PCM format changed");wav.close();
            }
        }
    }
    public static void main(String[] args) throws Exception {
        resources(args);logic();worker();System.out.println("Audio: 471 WAVs, 15 MIDI tracks, source action cues, dialogue, ambient, mixing, pause/resume, mute, fallback and cleanup passed");
    }
}
