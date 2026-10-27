"""Bound controller discovery to one grid scan; reuse paint/hit lists between updates."""
def patch(name,text):
 def replace(old,new):
  nonlocal text
  assert text.count(old)==1,(name,old)
  text=text.replace(old,new)
 if name=='AnimalWorld.java':
  replace('private Thing context;', '''private Thing context;
    private final Thing[] controllers=new Thing[302];
    private final int[] controllerSlots=new int[302];''')
  replace('''        int ceiling=serial,last=0;
        while(true) {
            Thing next=null;
            for(int i=0;i<cells.length;i++) { Thing t=cells[i];if(t!=null && t.serial>last && t.serial<=ceiling && (next==null||t.serial<next.serial)) next=t; }
            for(int h=0;h<2;h++) { Thing t=heroes[h];if(t!=null && t.serial>last && t.serial<=ceiling && (next==null||t.serial<next.serial)) next=t; }
            if(next==null) break;last=next.serial;
            if(walkHero>=0 && next==heroes[walkHero]) continue;
            if(rules(next)) break;
        }''', '''        int ceiling=serial,last=0,count=0;
        for(int slot=0;slot<302;slot++) {
            Thing t=slot<300?cells[slot]:heroes[slot-300];
            if(t==null || t.serial<=0 || t.serial>ceiling)continue;
            int at=count;while(at>0 && controllers[at-1].serial>t.serial){controllers[at]=controllers[at-1];controllerSlots[at]=controllerSlots[at-1];at--;}
            controllers[at]=t;controllerSlots[at]=slot;count++;
        }
        try {
            for(int i=0;i<count;i++) {
                Thing next=controllers[i];int slot=controllerSlots[i];
                // Earlier rules may delete/replace an owner; newly allocated owners wait a tick.
                if((slot<300?cells[slot]:heroes[slot-300])!=next || next.serial<=last)continue;
                last=next.serial;
                if(walkHero>=0 && next==heroes[walkHero])continue;
                if(rules(next))break;
            }
        } finally {for(int i=0;i<count;i++)controllers[i]=null;}''')
 if name=='AnimalScene.java':
  replace('private long elapsed;','''private long elapsed;
    private final int[] targets=new int[300];
    private int paintCount,targetCount;''')
  replace('''            actor.x=t.x;actor.y=t.y;actor.playing=false;actor.visible=true;
        }
    }''','''            actor.x=t.x;actor.y=t.y;actor.playing=false;actor.visible=true;
        }
        paintCount=0;targetCount=0;
        for(int i=0;i<scene.actors.length;i++)if(scene.actors[i]!=null){
            int at=paintCount;while(at>0 && layer(order[at-1])<layer(i)){order[at]=order[at-1];at--;}
            order[at]=i;paintCount++;
            if(i<300 && interactive(thing(i).id))targets[targetCount++]=i;
        }
    }''')
  replace('''        int n=0;
        for(int i=0;i<scene.actors.length;i++) if(scene.actors[i]!=null) {
            int j=n;while(j>0 && layer(order[j-1])<layer(i)) { order[j]=order[j-1];j--; }order[j]=i;n++;
        }
        for(int i=0;i<n;i++) scene.actors[order[i]].paint(graphics,view);''','''        for(int i=0;i<paintCount;i++)scene.actors[order[i]].paint(graphics,view);''')
  replace('''        for(int i=0;i<300;i++) {
            AnimalWorld.Thing t=thing(i);AnimatedActor actor=scene.actors[i];''','''        for(int at=0;at<targetCount;at++) {
            int i=targets[at];AnimalWorld.Thing t=thing(i);AnimatedActor actor=scene.actors[i];''')
 return text
