package pilots;

import java.io.IOException;
import javax.microedition.lcdui.Graphics;

/** Second-location adapter, with source roots, layers and action counters. */
public final class CourtyardScene extends ZooScene {
    public final CourtyardLogic courtyard;
    private long elapsed;
    private int[] path;
    private int pathIndex;
    private final int[] order=new int[15];
    private static final int[] IDS={0,0,46,47,48,49,50,51,52,53,54,66,74,71,75};
    private static final int[] X={0,0,256,0,576,576,0,576,576,128,256,512,0,0,192};
    private static final int[] Y={0,0,288,336,160,176,192,400,48,48,128,128,176,160,400};
    private static final int[] LAYERS={0,0,23,15,51,23,44,3,50,22,21,49,10,70,2};
    public CourtyardScene(Scene scene,GameState state) throws IOException {
        super(scene,state,new CourtyardLogic(state),false);
        courtyard=(CourtyardLogic)logic;
        scene.close();scene.actors=new AnimatedActor[15];
        try { refresh(); } catch(IOException failure) { scene.close();throw failure; }
    }
    private void sync(int slot,int id,int x,int y,boolean visible,int frame) throws IOException {
        AnimatedActor current=scene.actors[slot];
        if(current==null || current.id!=id || current.clip.index>frame) {
            AnimatedActor next=new AnimatedActor(id,"/scenes/03-"+id+".anm",x,y);
            if(current!=null) current.close();scene.actors[slot]=current=next;
        }
        int wanted=Math.min(frame,current.clip.count-1);
        while(current.clip.index<wanted) current.clip.advance();
        current.x=x;current.y=y;current.visible=visible;current.playing=false;
    }
    private void refresh() throws IOException {
        for(int h=0;h<2;h++) {
            int id=logic.heroClip(h),frame=logic.walking() && logic.hero()==h?logic.walkFrame(h):0;
            if(logic.pendingClip()!=0 && logic.hero()==h) { id=logic.pendingClip();frame=logic.step()-1; }
            if(h==1 && courtyard.companionClip()!=0) { id=courtyard.companionClip();frame=logic.step()-10; }
            sync(h,id,logic.heroX(h),logic.heroY(h),courtyard.heroesVisible(),frame);
        }
        for(int i=2;i<15;i++) {
            int id=IDS[i],frame=0;boolean visible=true;
            if(i==9) frame=state.variables[7];
            if(i==10) { id=courtyard.arrowState()==0?54:courtyard.arrowState()==1?56:55;frame=courtyard.arrowFrame(); }
            if(i==11) id=66+courtyard.sheet();
            if(i==12) {
                id=courtyard.animalPhase()==1?74:courtyard.animalPhase()==3?72:73;
                frame=courtyard.animalFrame();visible=courtyard.animalPhase()!=0;
            }
            if(i==13) visible=courtyard.arrowTurned();
            if(i==14) { frame=Math.max(0,courtyard.bubbleFrame());visible=courtyard.bubbleFrame()>=0; }
            sync(i,id,X[i],Y[i],visible,frame);
        }
    }
    public int layer(int slot) {
        if(slot>1) return LAYERS[slot];
        int value=27+(272-logic.heroY(slot))*3/16;
        return value+(logic.pendingClip()!=0 && logic.hero()==slot?1:0);
    }
    public void paint(Graphics graphics,Viewport view) {
        for(int i=0;i<15;i++) {
            int j=i;while(j>0 && layer(order[j-1])<layer(i)) { order[j]=order[j-1];j--; }order[j]=i;
        }
        for(int i=0;i<15;i++) scene.actors[order[i]].paint(graphics,view);
    }
    public String hoverText(int x,int y) {
        if(logic.targetAt(x,y)==CourtyardLogic.PIANO) return "Рояль в кустах";
        AnimatedActor animal=scene.actors[12];
        return animal.visible && animal.clip.contains(x-animal.x,y-animal.y)?"Кот":null;
    }
    private boolean walk(int target,int x,int y) {
        int[] route=CourtyardWalk.plan(logic.heroX(logic.hero()),logic.heroY(logic.hero()),x,y);
        if(route.length==0) return false;
        path=route;pathIndex=0;logic.queueWalk(target,x,y);return true;
    }
    public boolean request(int x,int y) throws IOException {
        if(logic.busy()) return false;
        int target=logic.targetAt(x,y);
        if(target==CourtyardLogic.PIANO) {
            int px=logic.hero()==0?512:448,other=1-logic.hero();
            if(logic.heroX(other)==px && logic.heroY(other)==176) {
                int aside=logic.heroX(logic.hero())==448 && logic.heroY(logic.hero())==192?512:448;
                state.variables[26]=logic.hero();state.variables[24]=px;
                logic.select(other);
                if(!walk(201,aside,192)) { logic.select(state.variables[26]);return false; }
            } else if(!walk(CourtyardLogic.PIANO,px,176)) return false;
        } else if(target==CourtyardLogic.FENCE || target==CourtyardLogic.EXIT) {
            state.variables[26]=logic.hero();
            if(!walk(target==CourtyardLogic.FENCE?202:204,target==CourtyardLogic.FENCE?576:0,target==CourtyardLogic.FENCE?384:288)) {
                logic.select(state.variables[26]);return false;
            }
        } else {
            int px=(x+8)/16*16,py=(y+8)/16*16;
            if(!walk(100,px,py)) return false;
        }
        refresh();return true;
    }
    public boolean activate(int x,int y) throws IOException { return request(x,y); }
    private void walkTick() throws IOException {
        if(!logic.walking()) return;
        if(path==null) { path=CourtyardWalk.plan(logic.heroX(logic.hero()),logic.heroY(logic.hero()),logic.walkX(),logic.walkY());pathIndex=0; }
        if(path.length==0) { logic.finishWalk();return; }
        if(pathIndex<path.length) {
            int h=logic.hero(),x=logic.heroX(h),y=logic.heroY(h);
            int dx=Math.max(-8,Math.min(8,path[pathIndex]-x)),dy=Math.max(-8,Math.min(8,path[pathIndex+1]-y));
            if(dx!=0 || dy!=0) logic.walkStep(dx,dy);
            logic.placeHero(h,x+dx,y+dy);
            if(x+dx==path[pathIndex] && y+dy==path[pathIndex+1]) pathIndex+=2;
            return;
        }
        int target=logic.walkTarget();logic.finishWalk();path=null;
        if(target==201) { logic.select(state.variables[26]);walk(CourtyardLogic.PIANO,state.variables[24],176); }
        else if(target==202 || target==204) { logic.select(1-state.variables[26]);walk(target+1,target==202?576:0,target==202?368:272); }
        else if(target==203 || target==205) { logic.select(state.variables[26]);logic.begin(target==203?CourtyardLogic.FENCE:CourtyardLogic.EXIT); }
        else if(target==CourtyardLogic.PIANO) logic.begin(target);
    }
    public boolean update(long delta) throws IOException {
        if(delta<=0) return false;
        elapsed+=Math.min(400L,delta);boolean changed=false;
        while(elapsed>=100L) {
            elapsed-=100L;changed=true;
            logic.tickSubtitle();int before=logic.pendingAction();walkTick();
            state.variables[7]=(state.variables[7]+1)%24;
            if(courtyard.arrowState()==1) {
                courtyard.arrowFrame(courtyard.arrowFrame()+1);
                if(courtyard.arrowFrame()>=10) courtyard.finishArrow();
            }
            if(courtyard.animalPhase()!=0) {
                int frame=courtyard.animalFrame()+1;
                if(courtyard.animalPhase()==2) frame%=30;
                courtyard.animalFrame(frame);
                if(courtyard.animalPhase()==1 && frame>=17 || courtyard.animalPhase()==3 && frame>=51) courtyard.finishAnimal();
            }
            if(courtyard.bubbleFrame()>=0) {
                courtyard.bubbleFrame(courtyard.bubbleFrame()+1);
                if(courtyard.bubbleFrame()>=39) courtyard.bubbleFrame(-1);
            }
            courtyard.ambientTick();
            if(!(before==0 && logic.pendingAction()!=0)) logic.advance();
            refresh();
        }
        return changed;
    }
}
