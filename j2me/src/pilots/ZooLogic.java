package pilots;

/** Original zoo rules; evidence is recorded in window/barrel-original-rules.json. */
public final class ZooLogic {
    public static final int CHIEF = 0, COLLEAGUE = 1;
    public static final int GLOBAL_CHIEF_IDLE = 20012, GLOBAL_COLLEAGUE_IDLE = 20016;
    public static final int GLOBAL_CHIEF_REFUSAL = 20036, GLOBAL_CHIEF_REPLY = 20034;
    private final GameState state;

    public ZooLogic(GameState state) {
        this.state = state;
        if (state.variables[60] != COLLEAGUE) state.variables[60] = CHIEF;
        state.variables[61] = state.variables[61] == 1 ? 1 : 0;
        if (state.variables[58] != 2) {
            // 0.4.0 only saved completed opening: its crow event has already happened.
            state.variables[62] = windowOpen() ? 2 : 0;
            state.variables[52] = state.variables[53] = state.variables[59] = 0;
            state.variables[54] = 448; state.variables[55] = 336;
            state.variables[56] = 512; state.variables[57] = 336;
            state.variables[58] = 2;
        }
    }

    public int hero() { return state.variables[60]; }
    public boolean windowOpen() { return state.variables[61] == 1; }
    public int pendingClip() { return state.variables[52]; }
    public int step() { return state.variables[53]; }
    public boolean busy() { return pendingClip() != 0 || walking() || guardPhase()==1 || guardPhase()==2; }
    public int crowState() { return state.variables[62]; }
    public int crowFrame() { return state.variables[59]; }
    public void crowFrame(int frame) { state.variables[59] = frame; }
    public void perchCrow() { state.variables[62] = 2; state.variables[59] = 0; }
    public int heroX(int hero) { return state.variables[54 + hero * 2]; }
    public int heroY(int hero) { return state.variables[55 + hero * 2]; }
    public void placeHero(int hero, int x, int y) {
        state.variables[54 + hero * 2] = x;
        state.variables[55 + hero * 2] = y;
    }
    public boolean select(int hero) {
        if (busy() || (hero != CHIEF && hero != COLLEAGUE)) return false;
        state.variables[60] = hero;
        return true;
    }
    public static boolean atWindow(int x, int y) {
        return x >= 150 && x < 218 && y >= 224 && y < 290;
    }
    public static final int WINDOW=1, KETTLE=2, BARREL=3, PICKAXE=4, COIN=5, BELL=6, GATE=7, WALL=8;
    public boolean hasKettle() { return state.inventory[0]; }
    public boolean hasItem(int item) { return item==0 || (item>=1 && item<=3 && state.inventory[item-1]); }
    public int selectedItem() { int item=state.variables[50]; return hasItem(item) ? item : 0; }
    public boolean kettleSelected() { return selectedItem()==1; }
    public boolean selectItem(int item) {
        if (busy() || !hasItem(item)) return false;
        state.variables[50]=item; return true;
    }
    public boolean selectKettle(boolean selected) { return selectItem(selected?1:0); }
    public int targetAt(int x, int y) {
        if (atWindow(x,y)) return WINDOW;
        if (!hasKettle() && x>=80 && x<118 && y>=284 && y<320) return KETTLE;
        if (!hasItem(2) && x>=80 && x<118 && y>=248 && y<284) return PICKAXE;
        if (x>=128 && x<192 && y>=282 && y<350) return BARREL;
        if (butterflyState()!=0 && x>=250 && x<320 && y>=265 && y<310) return COIN;
        if (x>=440 && x<480 && y>=205 && y<270) return BELL;
        if (x>=248 && x<435 && y>=155 && y<280) return GATE;
        if (x>=470 && x<640 && y>=180 && y<290) return WALL;
        return 0;
    }
    public int flags() { return (windowOpen()?1:0) | (catPresent()?2:0) | (guardPresent()?4:0) | (wallBroken()?8:0); }
    public int pendingAction() { return state.variables[10]; }
    public int pendingTarget() { return state.variables[63]==0?WINDOW:state.variables[63]; }
    public int beginWindow() { return begin(WINDOW); }
    public int begin(int target) {
        if (busy()) return 0;
        if(target==GATE && gateState()!=0) return 0;
        if (target==KETTLE && hasItem(1) || target==PICKAXE && hasItem(2) || target==COIN && butterflyState()==0) return 0;
        int action=OriginalZooRules.route(target,hero(),selectedItem(),flags());
        int variant=OriginalZooRules.variant(action,hero(),state.variables[hero()==0?51:46]);
        if (variant==0) return 0;
        state.variables[10]=action; state.variables[63]=target;
        state.variables[52]=variant>>>16; state.variables[53]=1;
        if (action==30) { state.variables[0]=3; state.variables[1]=1; state.variables[3]=97; state.variables[4]=0; }
        if (action==28) { state.variables[14]=0; state.variables[15]=0; }
        return pendingClip();
    }
    public int endStep() {
        if (pendingAction()!=0) {
            int variant=OriginalZooRules.variant(pendingAction(),hero(),state.variables[hero()==0?51:46]);
            if (variant!=0) return variant&65535;
        }
        int clip=pendingClip();
        return clip==64?88:clip==86?13:clip==87?14:clip==75?10:clip==76?6:clip==69?34:clip==83?45:clip==84?31:clip==20037?23:clip==20035?53:36;
    }
    public boolean shuttersVisible() { return pendingClip() != 64 || step() >= 13; }
    public void advance() {
        if (pendingClip()==0) return;
        state.variables[53]++;
        if (pendingClip() == 64) {
            if (step() >= 13) state.variables[61] = 1;
            if (step() >= 38 && crowState() == 0) state.variables[62] = 1;
        }
        if ((pendingClip()==75 && step()>=8) || (pendingClip()==76 && step()>=4))
            state.inventory[pendingTarget()==PICKAXE?1:0]=true;
        if ((pendingClip()==93 && step()==8) || (pendingClip()==92 && step()==5)) {
            state.inventory[2]=true; state.variables[42]=state.variables[43]=0;
        }
        if (pendingAction()==23 && step()==8) { state.variables[7]=1; state.variables[8]=0; }
        if (pendingAction()==28 && step()==9) { state.variables[14]=1; state.variables[15]=0; }
        if (pendingAction()==30) {
            state.variables[1]=step();
            if (step()==51) { state.variables[5]=1; state.variables[6]=0; }
        }
        if (pendingClip() == 69 && step() == 25) {
            state.variables[40] = 1; state.variables[41] = 0;
            state.variables[44] = 68; state.variables[45] = 0;
        }
        if (step()>=endStep()) {
            if (pendingAction()==20) { state.variables[0]=1; state.variables[1]=0; state.variables[3]=60; state.variables[4]=0; }
            if (pendingAction()==30) { state.variables[0]=6; state.variables[1]=0; state.variables[3]=59; state.variables[4]=0; }
            state.variables[52]=state.variables[53]=state.variables[10]=0;
        }
    }

    public int guardPhase() { return state.variables[0]; }
    public boolean guardPresent() { int p=guardPhase(); return p!=0 && p!=5; }
    public int guardClip() { return state.variables[3]==0?59:state.variables[3]; }
    public int guardFrame() { return state.variables[4]; }
    public void guardFrame(int f) { state.variables[4]=f; }
    public void finishGuardClip() {
        if (guardPhase()==5) { state.variables[0]=0; state.variables[3]=59; }
        else if (guardClip()!=59 && guardPhase()!=3) state.variables[3]=59;
        state.variables[4]=0;
    }
    public String message() {
        if (completed()) return "Первая локация пройдена";
        if (guardPhase()==2) return state.variables[1]==0 ? "Шеф: Откройте!  [5]" : "Сторож: Доступ ограничен.  [5]";
        return null;
    }
    public boolean continueTalk() {
        if (guardPhase()!=2) return false;
        if (state.variables[1]==0) { state.variables[1]=1; state.variables[3]=103; state.variables[4]=0; }
        else { state.variables[0]=4; state.variables[2]=100; state.variables[3]=59; state.variables[4]=0; }
        return true;
    }
    public void advanceGuard() {
        if (guardPhase()==1 && ++state.variables[1]>=25) { state.variables[0]=2; state.variables[1]=0; }
        if (guardPhase()==4 && ++state.variables[2]>=250) {
            state.variables[0]=5; state.variables[3]=61; state.variables[4]=0;
        }
    }
    public int gateState() { return state.variables[5]; }
    public int gateFrame() { return state.variables[6]; }
    public void gateFrame(int f) { state.variables[6]=f; }
    public void finishGate() { state.variables[5]=2; state.variables[6]=0; }
    public boolean wallBroken() { return state.variables[7]!=0; }
    public int wallFrame() { return state.variables[8]; }
    public void wallFrame(int f) { state.variables[8]=f; }
    public void finishWall() { state.variables[7]=2; state.variables[8]=0; }
    public int rockFrame() { return state.variables[15]; }
    public void rockFrame(int f) { state.variables[15]=f; }
    public boolean rockActive() { return state.variables[14]!=0; }
    public void finishRock() { state.variables[14]=state.variables[15]=0; }
    public boolean completed() { return state.variables[11]==1; }
    public void completeLocation() { state.variables[11]=1; state.scene=2; }
    public boolean walking() { return state.variables[30]!=0; }
    public int walkTarget() { return state.variables[31]; }
    public int walkX() { return state.variables[28]; }
    public int walkY() { return state.variables[29]; }
    public void queueWalk(int target,int x,int y) {
        state.variables[30]=1; state.variables[31]=target; state.variables[28]=x; state.variables[29]=y;
    }
    public void finishWalk() { state.variables[30]=state.variables[31]=0; }

    // Cat object 16: v94 branches on the presence of crow entity 15; v98 is its counter.
    public boolean catPresent() { return state.variables[40] != 0; }
    public int catMode() { return state.variables[40]; }
    public int catStep() { return state.variables[41]; }
    public int catClip() { return state.variables[44] == 0 ? 68 : state.variables[44]; }
    public int catFrame() { return state.variables[45]; }
    public void catFrame(int frame) { state.variables[45] = frame; }
    public int butterflyState() { return state.variables[42]; }
    public int butterflyFrame() { return state.variables[43]; }
    public void butterflyFrame(int frame) { state.variables[43] = frame; }
    public void settleButterfly() { state.variables[42] = 2; state.variables[43] = 0; }
    private void catClip(int id) { state.variables[44] = id; state.variables[45] = 0; }
    public void advanceCat() {
        if (!catPresent()) return;
        if (catStep() == 0 && crowState() != 0) state.variables[40] = 2;
        state.variables[41]++;
        if (catMode() == 3) {
            // Rule 16.13 has map effects in metadata 0x199 as well as its sound call.
            if (catStep() >= 16) {
                state.variables[40] = state.variables[41] = state.variables[45] = 0;
            }
            return;
        }
        if (catStep() == 15) catClip(67);
        if (catStep() == 32 && catMode() == 2) {
            state.variables[62] = state.variables[59] = 0; // map 10 removes the crow
            catClip(89);
        }
        if (catStep() == 45 && catMode() == 2) {
            state.variables[42] = 1; state.variables[43] = 0; // map 43 creates butterfly entity 30
        }
        if ((catMode() == 1 && catStep() == 32) || (catMode() == 2 && catStep() == 58)) {
            state.variables[40] = 3; state.variables[41] = 1;
            catClip(101);
        }
    }
}
