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
    replace('int slot=-1;', '''// Only finished footsteps may be replayed; running effects keep their timing.
        if(rank==1)for(int i=0;i<effects.length;i++) {
            if(effects[i]!=null && priority[i]==1 && effectCues[i]==cue && !resume[i] && effects[i].getState()==Player.PREFETCHED) {
                try {
                    if(effects[i].setMediaTime(0)!=0)throw new IllegalStateException();effects[i].start();age[i]=++serial;idleSince[i]=0;effectReuses++;return;
                } catch(Exception unsupported) { effectReuseFailures++;release(i); }
                  catch(OutOfMemoryError low) { release(i);return; }
            }
        }
        int slot=-1;''')
    replace('effects[slot]=Manager.createPlayer(streams[slot],"audio/mpeg");', '''effects[slot]=Manager.createPlayer(streams[slot],"audio/mpeg");
                effectCreates++;effectCues[slot]=cue;idleSince[slot]=0;''')
    replace('catch(OutOfMemoryError ignored) { release(slot);return; }', '''catch(OutOfMemoryError ignored) {
                release(slot);boolean freed=false;
                for(int i=0;i<effects.length;i++)if(effects[i]!=null && priority[i]==1 && !resume[i] && effects[i].getState()==Player.PREFETCHED){release(i);freed=true;}
                if(freed)continue;return;
            }''')
    replace('''private void reap() {
        for(int i=0;i<effects.length;i++)
            if(effects[i]!=null && !resume[i] && effects[i].getState()!=Player.STARTED) release(i);''', '''private void reap() {
        long now=System.currentTimeMillis();int keep=-1;
        // One idle player at most, for 1.5 seconds, only with 128 KiB free heap.
        if(Runtime.getRuntime().freeMemory()>=131072)for(int i=0;i<effects.length;i++)
            if(effects[i]!=null && priority[i]==1 && !resume[i] && effects[i].getState()==Player.PREFETCHED) {
                if(idleSince[i]==0)idleSince[i]=now;
                if(now-idleSince[i]<1500 && (keep<0 || age[i]>age[keep]))keep=i;
            }
        for(int i=0;i<effects.length;i++)
            if(i!=keep && effects[i]!=null && !resume[i] && effects[i].getState()!=Player.STARTED) release(i);''')
    replace('resume[i]=false;\n    }', 'resume[i]=false;idleSince[i]=0;effectCues[i]=-1;\n    }')
    return text
