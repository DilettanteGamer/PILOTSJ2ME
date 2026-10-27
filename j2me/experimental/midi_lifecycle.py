"""Separate natural MIDI completion from disposal; isolate MMAPI lifecycle failures."""
import re
def patch(name,text):
 if name!='GameAudio.java':return text
 def replace(old,new):
  nonlocal text
  assert text.count(old)==1,(name,old)
  text=text.replace(old,new)
 # All state/close operations stay on the existing worker.
 text=re.sub(r'(effects\[[^\]]+\]|music|player)\.getState\(\)',r'playerState(\1)',text)
 replace('private boolean resumeMusic,musicAttempted;', 'private boolean resumeMusic,musicAttempted,musicFinished;')
 replace('int applied=-1,appliedTrack=-1;boolean wasActive=false;', 'AudioDiagnostics.workerAlive=true;int applied=-1,appliedTrack=-1;boolean wasActive=false;')
 replace('while(true) {\n                int current,revision', 'while(true) {\n                AudioDiagnostics.workerAlive=true;int current,revision')
 replace('} finally { releaseAll(); }', '} finally {try{releaseAll();}finally{AudioDiagnostics.workerAlive=false;}}')
 replace('music=Manager.createPlayer(musicStream,"audio/midi");music.realize();music.prefetch();','musicFinished=false;music=Manager.createPlayer(musicStream,"audio/midi");music.realize();music.prefetch();')
 replace('volume(music,45);if(loop)music.setLoopCount(-1);music.start();','volume(music,45);if(loop)music.setLoopCount(-1);music.start();AudioDiagnostics.musicState=playerState(music);')
 replace('if(music!=null && !resumeMusic && playerState(music)!=Player.STARTED) closeMusic();','''if(music!=null && !resumeMusic && !musicFinished) {
            int state=playerState(music);AudioDiagnostics.musicState=state;
            if(state!=Player.STARTED) {
                if(state==Player.CLOSED || menuMode)closeMusic();
                else {musicFinished=true;AudioDiagnostics.musicEnds++;}
            }
        }''')
 # Detach first, so a throwing driver cannot leave the same player in the next reap pass.
 replace('if(effects[i]!=null) { effects[i].close();effects[i]=null; }', 'Player disposed=effects[i];effects[i]=null;dispose(disposed);')
 replace('if(streams[i]!=null) { try { streams[i].close(); } catch(Exception ignored) { }streams[i]=null; }', 'InputStream input=streams[i];streams[i]=null;closeStream(input);')
 replace('''        if(music!=null) { music.close();music=null; }
        if(musicStream!=null) { try { musicStream.close(); } catch(Exception ignored) { }musicStream=null; }
        resumeMusic=false;''','''        Player disposed=music;music=null;InputStream input=musicStream;musicStream=null;
        resumeMusic=false;musicFinished=false;AudioDiagnostics.musicState=Player.CLOSED;
        dispose(disposed);closeStream(input);''')
 replace('    private void releaseAll() {','''    private int playerState(Player player) {
        try{return player.getState();}
        catch(RuntimeException failure){AudioDiagnostics.failed(failure,10);}
        catch(OutOfMemoryError low){AudioDiagnostics.failed(low,10);}
        return Player.CLOSED;
    }
    private void dispose(Player player) {
        if(player==null)return;
        try{player.close();}
        catch(RuntimeException failure){AudioDiagnostics.failed(failure,11);}
        catch(OutOfMemoryError low){AudioDiagnostics.failed(low,11);}
    }
    private void closeStream(InputStream input) {
        if(input==null)return;
        try{input.close();}
        catch(Exception failure){AudioDiagnostics.failed(failure,11);}
        catch(OutOfMemoryError low){AudioDiagnostics.failed(low,11);}
    }
    private void releaseAll() {''')
 # Counters apply to all FX, rather than just the footstep subset.
 replace('if(rank==1)AudioDiagnostics.starts++;return;', 'if(rank==1)AudioDiagnostics.starts++;AudioDiagnostics.effectStarts++;return;')
 replace('effectReuses++;AudioDiagnostics.reuses++;return;', 'effectReuses++;AudioDiagnostics.reuses++;AudioDiagnostics.effectStarts++;return;')
 old='AudioDiagnostics.failed(ignored,nativeStage);if(rank==1)AudioDiagnostics.startFailures++;release(slot);'
 assert text.count(old)==2
 text=text.replace(old,'AudioDiagnostics.failed(ignored,nativeStage);AudioDiagnostics.effectFailures++;if(rank==1)AudioDiagnostics.startFailures++;release(slot);')
 return text
