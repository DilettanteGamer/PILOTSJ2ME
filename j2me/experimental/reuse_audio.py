"""Reuse one recently completed footstep player inside the existing three slots."""
def patch(name, text):
    if name != 'GameAudio.java':
        return text
    def replace(old, new):
        nonlocal text
        assert text.count(old) == 1, old
        text = text.replace(old, new)
    replace('private final Player[] effects=new Player[3];', '''private final Player[] effects=new Player[3];
    private final int[] effectCues=new int[3];
    private final long[] idleSince=new long[3];
    public static volatile int effectCreates,effectReuses,effectReuseFailures;''')
    replace('int slot=-1;', '''int nativeStage=0;AudioDiagnostics.cue=cue;if(rank==1)AudioDiagnostics.stepCue=cue;
        // Only finished footsteps may be replayed; running effects keep their timing.
        if(rank==1)for(int i=0;i<effects.length;i++) {
            if(effects[i]!=null && priority[i]==1 && effectCues[i]==cue && !resume[i] && (effects[i].getState()==Player.PREFETCHED || effects[i].getState()==Player.REALIZED)) {
                try {
                    nativeStage=7;if(effects[i].getState()==Player.REALIZED){effects[i].prefetch();AudioDiagnostics.refetches++;}
                    nativeStage=8;if(effects[i].setMediaTime(0)!=0)throw new IllegalStateException();nativeStage=9;effects[i].start();age[i]=++serial;idleSince[i]=0;effectReuses++;AudioDiagnostics.reuses++;return;
                } catch(Exception unsupported) { AudioDiagnostics.failed(unsupported,nativeStage);effectReuseFailures++;AudioDiagnostics.failures++;release(i,6); }
                  catch(OutOfMemoryError low) { AudioDiagnostics.failed(low,nativeStage);release(i,7);return; }
            }
        }
        int slot=-1;''')
    replace('effects[slot]=Manager.createPlayer(streams[slot],"audio/mpeg");', '''effects[slot]=Manager.createPlayer(streams[slot],"audio/mpeg");
                effectCreates++;if(rank==1)AudioDiagnostics.creates++;effectCues[slot]=cue;idleSince[slot]=0;''')
    replace('streams[slot]=PackedResources.open(path);','nativeStage=1;streams[slot]=PackedResources.open(path);')
    replace('effects[slot]=Manager.createPlayer(streams[slot],"audio/mpeg");','nativeStage=2;effects[slot]=Manager.createPlayer(streams[slot],PackedResources.audioType(path));')
    replace('effects[slot].realize();effects[slot].prefetch();volume(effects[slot],location==3&&cue==23?35:85);effects[slot].start();return;', '''nativeStage=3;effects[slot].realize();nativeStage=4;effects[slot].prefetch();nativeStage=5;volume(effects[slot],location==3&&cue==23?35:85);nativeStage=6;effects[slot].start();if(rank==1)AudioDiagnostics.starts++;return;''')
    replace('catch(Exception ignored) { release(slot); }','catch(Exception ignored) { AudioDiagnostics.failed(ignored,nativeStage);if(rank==1)AudioDiagnostics.startFailures++;release(slot); }')
    replace('catch(OutOfMemoryError ignored) { release(slot);return; }', '''catch(OutOfMemoryError ignored) {
                AudioDiagnostics.failed(ignored,nativeStage);if(rank==1)AudioDiagnostics.startFailures++;release(slot);boolean freed=false;
                for(int i=0;i<effects.length;i++)if(effects[i]!=null && priority[i]==1 && !resume[i] && (effects[i].getState()==Player.PREFETCHED || effects[i].getState()==Player.REALIZED)){release(i,7);freed=true;}
                if(freed)continue;return;
            }''')
    replace('''private void reap() {
        for(int i=0;i<effects.length;i++)
            if(effects[i]!=null && !resume[i] && effects[i].getState()!=Player.STARTED) release(i);''', '''private void reap() {
        long now=System.currentTimeMillis();int keep=-1;boolean enoughHeap=Runtime.getRuntime().freeMemory()>=131072;
        // One idle player at most, for 1.5 seconds, only with 128 KiB free heap.
        if(enoughHeap)for(int i=0;i<effects.length;i++)
            if(effects[i]!=null && priority[i]==1 && !resume[i] && (effects[i].getState()==Player.PREFETCHED || effects[i].getState()==Player.REALIZED)) {
                if(idleSince[i]==0){idleSince[i]=now;AudioDiagnostics.endState=effects[i].getState();AudioDiagnostics.reason=0;}
                if(now-idleSince[i]<1500 && (keep<0 || age[i]>age[keep]))keep=i;
            }
        for(int i=0;i<effects.length;i++)
            if(i!=keep && effects[i]!=null && !resume[i] && effects[i].getState()!=Player.STARTED) {
                int why=effects[i].getState()!=Player.PREFETCHED && effects[i].getState()!=Player.REALIZED?3:!enoughHeap?2:idleSince[i]!=0&&now-idleSince[i]>=1500?1:8;
                release(i,why);
            }''')
    replace('private void release(int i) {', '''private void release(int i) { release(i,4); }
    private void release(int i,int why) {
        if(effects[i]!=null && priority[i]==1){AudioDiagnostics.endState=effects[i].getState();AudioDiagnostics.reason=why;}''')
    replace('for(int i=0;i<effects.length;i++) release(i);closeMusic();', 'for(int i=0;i<effects.length;i++) release(i,5);closeMusic();')
    replace('resume[i]=false;\n    }', 'resume[i]=false;idleSince[i]=0;effectCues[i]=-1;\n    }')
    return text
