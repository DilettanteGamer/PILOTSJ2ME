"""Avoid decoding unseen restore frames and rebuilding unchanged first-location actors."""
def patch(name,text):
 if name=='AnimationClip.java':
  text=text.replace('    public boolean advance() throws IOException {','''    /** Seek presentation state only; game logic and sound events stay in their script ticks. */
    public void seek(int target) throws IOException {
        if(target<0 || target>=count)throw new IOException("Animation seek range");
        if(target==index)return;
        if(target<index || input==null)restart();
        if(tiled && target>index+1){int bytes=(target-index-1)*12;while(bytes>0){int n=input.skipBytes(bytes);if(n<=0)throw new IOException("Truncated animation seek");bytes-=n;}index=target-1;}
        while(index<target)if(!advance())throw new IOException("Incomplete animation seek");
    }

    public boolean advance() throws IOException {''')
 # These loops reconcile an already-advanced logical frame, never execute game events.
 replacements={
  'while (result.clip.index < target) result.clip.advance();':'result.clip.seek(target);',
  'else if(walking) while(current.clip.index<frame) current.clip.advance();':'else if(walking) current.clip.seek(Math.min(frame,current.clip.count-1));',
  'while(current.clip.index<wanted) current.clip.advance();':'current.clip.seek(Math.max(0,wanted));',
  'while(actor.clip.index<Math.min(t.frame,actor.clip.count-1)) actor.clip.advance();':'actor.clip.seek(Math.max(0,Math.min(t.frame,actor.clip.count-1)));',
  'while(actor.clip.index<Math.min(frame,actor.clip.count-1)) actor.clip.advance();':'actor.clip.seek(Math.max(0,Math.min(frame,actor.clip.count-1)));',
  'while(a.clip.index<Math.min(frame,a.clip.count-1))a.clip.advance();':'a.clip.seek(Math.max(0,Math.min(frame,a.clip.count-1)));',
  'if(clip.index>index)clip.restart();while(clip.index<index)clip.advance();':'clip.seek(index);',
  'if(a.clip.index>frame)a.clip.restart();while(a.clip.index<frame)a.clip.advance();':'a.clip.seek(frame);',
 }
 for before,after in replacements.items():text=text.replace(before,after)
 if name in ('AnimalScene.java','GardenScene.java','StreetScene.java','HotelScene.java','FlightScene.java'):
  text=text.replace(' || actor.clip.index>t.frame','').replace(' || actor.clip.index>frame','').replace('||a.clip.index>frame','')
 if name=='CourtyardScene.java':text=text.replace(' || current.clip.index>frame','')
 if name=='ZooScene.java':
  text=text.replace('if(current.id!=id || walking && current.clip.index>frame)','if(current.id!=id)')
  text=text.replace('    private AnimatedActor actor(int id,', '''    private AnimatedActor[] rebuildPool;
    private boolean[] rebuildUsed;
    private boolean retained(AnimatedActor actor){if(rebuildPool!=null)for(int i=0;i<rebuildPool.length;i++)if(rebuildUsed[i]&&rebuildPool[i]==actor)return true;return false;}
    private AnimatedActor actor(int id,''')
  text=text.replace('        AnimatedActor result = new AnimatedActor(id,', '''        if(frame<0)throw new IOException("Invalid saved animation frame");
        if(rebuildPool!=null && !playing)for(int i=0;i<rebuildPool.length;i++){
            AnimatedActor old=rebuildPool[i];if(!rebuildUsed[i] && old!=null && old.id==id && old.x==x && old.y==y && !old.playing && old.clip.index==Math.min(frame,old.clip.count-1)){rebuildUsed[i]=true;return old;}
        }
        AnimatedActor result = new AnimatedActor(id,''')
  text=text.replace('        AnimatedActor[] next = new AnimatedActor[16];','''        AnimatedActor[] next = new AnimatedActor[16];
        rebuildPool=scene.actors;rebuildUsed=new boolean[rebuildPool.length];boolean[] oldVisible=new boolean[rebuildPool.length];
        for(int i=0;i<rebuildPool.length;i++)oldVisible[i]=rebuildPool[i]!=null&&rebuildPool[i].visible;''')
  text=text.replace('if (next[i] != null) next[i].close();','if (next[i] != null && !retained(next[i])) next[i].close();')
  text=text.replace('''            throw failure;
        }
        scene.close();
        scene.actors = next;''','''            for(int i=0;i<rebuildPool.length;i++)if(rebuildPool[i]!=null)rebuildPool[i].visible=oldVisible[i];
            rebuildPool=null;rebuildUsed=null;throw failure;
        }
        for(int i=0;i<rebuildPool.length;i++)if(!rebuildUsed[i] && rebuildPool[i]!=null)rebuildPool[i].close();
        rebuildPool=null;rebuildUsed=null;
        scene.actors = next;''')
 return text
