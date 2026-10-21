package pilots;

import java.io.IOException;
import javax.microedition.lcdui.Graphics;

/** Fixed-step adapter for original zoo sequences; drawing never commits game state. */
public final class ZooScene {
    public final ZooLogic logic;
    private final Scene scene;
    private final GameState state;
    private long elapsed;
    private int[] path;
    private int pathIndex;

    public ZooScene(Scene scene, GameState state) throws IOException {
        this.scene = scene;
        this.state = state;
        logic = new ZooLogic(state);
        rebuild();
    }

    private AnimatedActor actor(int id, int x, int y, boolean playing, int frame) throws IOException {
        AnimatedActor result = new AnimatedActor(id, "/scenes/02-" + id + ".anm", x, y);
        try {
            if (frame < 0) throw new IOException("Invalid saved animation frame");
            int target = Math.min(frame, result.clip.count - 1);
            while (result.clip.index < target) result.clip.advance();
            result.playing = playing;
            return result;
        } catch (IOException failure) { result.close(); throw failure; }
    }

    private AnimatedActor hero(int hero) throws IOException {
        boolean active=logic.pendingClip()!=0 && logic.hero()==hero;
        int id=heroClip(hero);
        int frame=active?logic.step()-1:logic.walking() && logic.hero()==hero?logic.walkFrame(hero):0;
        if(hero==0 && logic.pendingAction()==30 && logic.step()>12) { id=20014;frame=0;active=false; }
        if(hero==0 && logic.guardPhase()==2) { id=102;frame=0;active=false; }
        return actor(id,logic.heroX(hero),logic.heroY(hero),active,frame);
    }

    private int heroClip(int hero) {
        if(hero==0 && logic.pendingAction()==30 && logic.step()>12) return 20014;
        if(hero==0 && logic.guardPhase()==2) return 102;
        return logic.pendingClip()!=0 && logic.hero()==hero?logic.pendingClip():logic.heroClip(hero);
    }
    private void syncHero(int hero) throws IOException {
        AnimatedActor current=scene.actors[2+hero];
        int id=heroClip(hero);
        boolean walking=logic.walking() && logic.hero()==hero;
        int frame=logic.walkFrame(hero);
        if(current.id!=id || walking && current.clip.index>frame) replace(2+hero,hero(hero));
        else if(walking) while(current.clip.index<frame) current.clip.advance();
    }

    private AnimatedActor crow() throws IOException {
        AnimatedActor result = actor(logic.crowState() == 1 ? 66 : 65, 320, 112,
            logic.crowState() == 1, logic.crowFrame());
        result.visible = logic.crowState() != 0;
        return result;
    }

    private AnimatedActor kettle() throws IOException {
        AnimatedActor result = actor(70, 64, 288, false, 0);
        result.visible = !logic.hasKettle();
        return result;
    }
    private AnimatedActor cat() throws IOException {
        AnimatedActor result = actor(logic.catClip(), 192, 176, logic.catPresent(), logic.catFrame());
        result.visible = logic.catPresent();
        return result;
    }
    private AnimatedActor butterfly() throws IOException {
        AnimatedActor result = actor(logic.butterflyState() == 1 ? 90 : 91, 256, 272,
            logic.butterflyState() == 1, logic.butterflyFrame());
        result.visible = logic.butterflyState() != 0;
        return result;
    }
    private AnimatedActor guard() throws IOException {
        AnimatedActor result=actor(logic.guardClip(),256,128,logic.guardClip()!=59,logic.guardFrame());
        result.visible=logic.guardPhase()!=0;return result;
    }
    private AnimatedActor gate() throws IOException {
        return actor(logic.gateState()==0?51:logic.gateState()==1?49:50,320,160,logic.gateState()==1,logic.gateFrame());
    }
    private AnimatedActor wall() throws IOException {
        return actor(!logic.wallBroken()?72:state.variables[7]==1?73:74,576,160,state.variables[7]==1,logic.wallFrame());
    }
    private AnimatedActor pickaxe() throws IOException {
        AnimatedActor result=actor(71,64,272,false,0);result.visible=!logic.hasItem(2);return result;
    }
    private AnimatedActor bell() throws IOException {
        AnimatedActor result=actor(58,448,208,false,0);result.visible=logic.pendingAction()!=20;return result;
    }
    private AnimatedActor rock() throws IOException {
        AnimatedActor result=actor(104,512,240,logic.rockActive(),logic.rockFrame());result.visible=logic.rockActive();return result;
    }
    private void replace(int slot, AnimatedActor next) {
        scene.actors[slot].close(); scene.actors[slot] = next;
    }
    private void rebuild() throws IOException {
        AnimatedActor[] next = new AnimatedActor[16];
        try {
            next[0] = actor(logic.windowOpen() ? 63 : 62, 128, 224, false, 0);
            next[0].visible = logic.shuttersVisible();
            next[1] = guard();
            next[2] = hero(ZooLogic.CHIEF);
            next[3] = hero(ZooLogic.COLLEAGUE);
            next[4] = crow();
            next[5] = kettle();
            next[6] = actor(55, 128, 320, false, 0);
            next[7] = cat();
            next[8] = butterfly();
            // Original initial map 0x19: entity 11 (clip 52) and 37 (clip 99).
            // The left column (entity 38 / clip 100) is already baked into the background.
            next[14]=actor(52,576,48,false,0);
            next[15]=actor(99,384,96,false,0);
            next[9]=pickaxe();next[10]=bell();next[11]=gate();next[12]=wall();next[13]=rock();
        } catch (IOException failure) {
            for (int i = 0; i < next.length; i++) if (next[i] != null) next[i].close();
            throw failure;
        }
        scene.close();
        scene.actors = next;
    }

    /** A nearby free standing cell, rather than either hero's initial position. */
    private int[] aside(int hero) {
        int x=logic.heroX(hero),y=logic.heroY(hero),other=1-hero;
        int[] dx={64,-64,0,0},dy={0,0,32,-32};
        int[] best=null;int bestScore=2147483647;
        for(int i=0;i<dx.length;i++) {
            int tx=x+dx[i],ty=y+dy[i];
            if(!ZooWalk.allowed(tx,ty,logic.gateState()==2)) continue;
            int distance=Math.abs(tx-logic.heroX(other))+Math.abs(ty-logic.heroY(other));
            if(distance<48) continue;
            int[] route=ZooWalk.plan(x,y,tx,ty,logic.gateState()==2);
            if(route.length==0) continue;
            int score=route.length*16-distance;
            if(score<bestScore) { bestScore=score;best=new int[]{tx,ty}; }
        }
        return best;
    }

    // Original descriptor layers; FUN_004034fc paints larger layer numbers first.
    private static final int[] LAYERS={16,20,0,0,15,45,12,16,16,40,16,16,17,16,18,18};
    public int layer(int slot) {
        if(slot==7 && logic.catClip()==101) return 11;
        if(slot!=2 && slot!=3) return LAYERS[slot];
        int hero=slot-2;
        // Source v40/v44 start at 10 on row 21 and change by 2 per vertical cell.
        int layer=10+(336-logic.heroY(hero))/8;
        if(logic.pendingClip()!=0 && logic.hero()==hero) {
            int clip=logic.pendingClip();
            if(clip==69 || clip==83) return 11;
            if(clip==56) return 15;
            if(logic.pendingAction()!=30 || logic.step()<=12) layer++;
        }
        return layer;
    }
    private final int[] paintOrder=new int[16];
    public void paint(Graphics graphics,Viewport view) {
        for(int i=0;i<scene.actors.length;i++) {
            int j=i;
            // Stable insertion sort: keep actor slots unchanged for sequence updates.
            while(j>0 && layer(paintOrder[j-1])<layer(i)) {
                paintOrder[j]=paintOrder[j-1];j--;
            }
            paintOrder[j]=i;
        }
        for(int i=0;i<scene.actors.length;i++) scene.actors[paintOrder[i]].paint(graphics,view);
    }

    public static int approachX(int target) {
        return target==ZooLogic.KETTLE || target==ZooLogic.PICKAXE?64:target==ZooLogic.COIN?320:target>=ZooLogic.BELL?384:192;
    }
    public static int approachY(int target) { return target==ZooLogic.KETTLE || target==ZooLogic.PICKAXE || target==ZooLogic.BARREL?320:304; }
    private void startWalk(int target,int x,int y) throws IOException {
        logic.queueWalk(target,x,y);path=null;pathIndex=0;
        rebuild();
    }
    public boolean request(int x,int y) throws IOException {
        if(logic.continueTalk()) { rebuild();return true; }
        if(logic.busy()) return false;
        int target=logic.targetAt(x,y);
        if(target==ZooLogic.GATE && logic.gateState()==2 && logic.selectedItem()==0) {
            logic.select(0); startWalk(101,448,240);return true;
        }
        if(target==ZooLogic.GATE && logic.gateState()!=0) return false;
        if(target==0) {
            int px=(x+8)/16*16,py=(y+8)/16*16;
            if(!ZooWalk.allowed(px,py,logic.gateState()==2)) return false;
            startWalk(100,px,py);return true;
        }
        int action=OriginalZooRules.route(target,logic.hero(),logic.selectedItem(),logic.flags());
        if(action==0) return false;
        // Source refusal rules run before approach tests; preserve current position.
        if(action==13) return activate(x,y);
        int px=approachX(target),py=approachY(target),other=1-logic.hero();
        if(logic.heroX(other)==px && logic.heroY(other)==py) {
            state.variables[26]=logic.hero();state.variables[25]=target;
            state.variables[24]=px;state.variables[23]=py;
            int[] aside=aside(other);
            if(aside==null) return false;
            logic.select(other);startWalk(target+200,aside[0],aside[1]);
        } else startWalk(target,px,py);
        return true;
    }
    private boolean walkTick() throws IOException {
        if(!logic.walking()) return false;
        if(path==null) {
            path=ZooWalk.plan(logic.heroX(logic.hero()),logic.heroY(logic.hero()),logic.walkX(),logic.walkY(),logic.gateState()==2);
            pathIndex=0;
            if(path.length==0) { logic.finishWalk();return true; }
        }
        if(pathIndex<path.length) {
            int x=logic.heroX(logic.hero()),y=logic.heroY(logic.hero());
            int dx=Math.max(-8,Math.min(8,path[pathIndex]-x)),dy=Math.max(-8,Math.min(8,path[pathIndex+1]-y));
            if(dx!=0 || dy!=0) logic.walkStep(dx,dy);
            logic.placeHero(logic.hero(),x+dx,y+dy);
            syncHero(logic.hero());
            scene.actors[2+logic.hero()].x=x+dx;scene.actors[2+logic.hero()].y=y+dy;
            if(x+dx==path[pathIndex] && y+dy==path[pathIndex+1]) pathIndex+=2;
            return true;
        }
        int target=logic.walkTarget();logic.finishWalk();path=null;
        if(target>=200) {
            logic.select(state.variables[26]);startWalk(state.variables[25],state.variables[24],state.variables[23]);
        } else if(target==101) { logic.select(1);startWalk(102,448,256); }
        else if(target==102) { logic.completeLocation();rebuild(); }
        else if(target!=100) {
            // The world hit rectangle is irrelevant after arrival: execute the queued target.
            int[] previous=new int[state.variables.length];System.arraycopy(state.variables,0,previous,0,previous.length);
            try { logic.begin(target);rebuild(); }
            catch(IOException failure) { System.arraycopy(previous,0,state.variables,0,previous.length);throw failure; }
        }
        return true;
    }

    public boolean activate(int x, int y) throws IOException {
        int target = logic.targetAt(x, y);
        if (target == 0 || logic.busy()) return false;
        int[] previous = new int[state.variables.length];
        System.arraycopy(state.variables, 0, previous, 0, previous.length);
        try {
            int clip = logic.begin(target);
            if (clip == 0) return false;
            // Reactions with no approach retain the hero's actual current position.
            if (clip < 20000) {
                int px=approachX(target),py=approachY(target);
                int other = 1 - logic.hero();
                if (logic.heroX(other) == px && logic.heroY(other) == py)
                    {
                    int[] aside=aside(other);
                    if(aside==null) { System.arraycopy(previous,0,state.variables,0,previous.length);return false; }
                    logic.placeHero(other,aside[0],aside[1]);
                }
                logic.placeHero(logic.hero(), px, py);
            }
            rebuild();
        } catch (IOException failure) {
            System.arraycopy(previous, 0, state.variables, 0, previous.length);
            throw failure;
        }
        elapsed = 0;
        return true;
    }

    public boolean update(long delta) throws IOException {
        if (delta <= 0) return false;
        elapsed += Math.min(delta, 400L);
        boolean changed = false;
        while (elapsed >= 100L) {
            elapsed -= 100L;
            changed=logic.tickSubtitle() || changed;
            changed=walkTick() || changed;
            for (int i = 0; i < scene.actors.length; i++) changed = scene.actors[i].update(100L) || changed;
            if (logic.crowState() == 1) {
                logic.crowFrame(scene.actors[4].clip.index);
                if (scene.actors[4].clip.index == scene.actors[4].clip.count - 1) logic.perchCrow();
            }
            if (logic.catPresent()) logic.catFrame(scene.actors[7].clip.index);
            if (logic.butterflyState() == 1) {
                logic.butterflyFrame(scene.actors[8].clip.index);
                if (scene.actors[8].clip.index == scene.actors[8].clip.count - 1) logic.settleButterfly();
            }
            if(logic.guardClip()!=59 && logic.guardPhase()!=0) {
                logic.guardFrame(scene.actors[1].clip.index);
                if(scene.actors[1].clip.index==scene.actors[1].clip.count-1) logic.finishGuardClip();
            }
            if(logic.gateState()==1) { logic.gateFrame(scene.actors[11].clip.index);if(scene.actors[11].clip.index==scene.actors[11].clip.count-1) logic.finishGate(); }
            if(state.variables[7]==1) { logic.wallFrame(scene.actors[12].clip.index);if(scene.actors[12].clip.index==scene.actors[12].clip.count-1) logic.finishWall(); }
            if(logic.rockActive()) { logic.rockFrame(scene.actors[13].clip.index);if(scene.actors[13].clip.index==scene.actors[13].clip.count-1) logic.finishRock(); }
            int oldGuardPhase=logic.guardPhase();
            int oldClip = logic.pendingClip();
            boolean catActive = logic.catPresent();
            logic.advanceCat();
            logic.advanceGuard();
            logic.advance();
            if (oldClip != 0 || catActive) changed = true;
            if (oldClip != logic.pendingClip() || oldGuardPhase!=logic.guardPhase()) { rebuild();changed=true; }
            else {
                if (scene.actors[0].id != (logic.windowOpen() ? 63 : 62))
                    replace(0, actor(logic.windowOpen() ? 63 : 62, 128, 224, false, 0));
                scene.actors[0].visible = logic.shuttersVisible();
                if(scene.actors[1].id!=logic.guardClip() || scene.actors[1].visible!=(logic.guardPhase()!=0)) replace(1,guard());
                for(int h=0;h<2;h++) syncHero(h);
                scene.actors[9].visible=!logic.hasItem(2);
                scene.actors[10].visible=logic.pendingAction()!=20;
                if(scene.actors[11].id!=(logic.gateState()==0?51:logic.gateState()==1?49:50)) replace(11,gate());
                if(scene.actors[12].id!=(!logic.wallBroken()?72:state.variables[7]==1?73:74)) replace(12,wall());
                if(scene.actors[13].visible!=logic.rockActive()) replace(13,rock());
                if (scene.actors[4].id != (logic.crowState() == 1 ? 66 : 65)
                    || scene.actors[4].visible != (logic.crowState() != 0)) replace(4, crow());
                scene.actors[5].visible = !logic.hasKettle();
                if (scene.actors[7].id != logic.catClip() || scene.actors[7].visible != logic.catPresent()) replace(7, cat());
                if (scene.actors[8].id != (logic.butterflyState() == 1 ? 90 : 91)
                    || scene.actors[8].visible != (logic.butterflyState() != 0)) replace(8, butterfly());
            }
        }
        return changed;
    }
}
