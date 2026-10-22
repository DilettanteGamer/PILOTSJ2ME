package pilots;

/** Second location: original objects 15 (piano), 28 (fence), 31 (exit). */
public final class CourtyardLogic extends ZooLogic {
    public static final int PIANO=1,FENCE=2,EXIT=3;
    public CourtyardLogic(GameState state) {
        super(state);
        if(state.variables[58]!=3) {
            for(int i=0;i<state.variables.length;i++) state.variables[i]=0;
            for(int i=0;i<state.inventory.length;i++) state.inventory[i]=false;
            state.variables[54]=128;state.variables[55]=272;state.variables[56]=64;state.variables[57]=272;
            state.variables[58]=3;state.variables[9]=-1;
        }
    }
    public boolean busy() { return walking() || pendingAction()!=0; }
    public boolean hasItem(int item) { return item==0; }
    public boolean hasKettle() { return false; }
    public int selectedItem() { return 0; }
    public boolean selectItem(int item) { return item==0 && !busy(); }
    public boolean continueTalk() { return false; }
    public int targetAt(int x,int y) {
        if(x>=490 && x<585 && y>=55 && y<185) return PIANO;
        if(!arrowTurned() && x>=278 && x<640 && y>=338 && y<416) return FENCE;
        if(arrowTurned() && x>=0 && x<128 && y>=160 && y<258) return EXIT;
        return 0;
    }
    public int sheet() { return state.variables[0]; }
    public boolean arrowTurned() { return state.variables[1]!=0; }
    public int arrowState() { return state.variables[1]; }
    public int arrowFrame() { return state.variables[2]; }
    public void arrowFrame(int frame) { state.variables[2]=frame; }
    public void finishArrow() { state.variables[1]=2;state.variables[2]=0; }
    public int animalPhase() { return state.variables[3]; }
    public int animalFrame() { return state.variables[4]; }
    public void animalFrame(int frame) { state.variables[4]=frame; }
    public int bubbleFrame() { return state.variables[9]; }
    public void bubbleFrame(int frame) { state.variables[9]=frame; }
    public int begin(int target) {
        if(busy()) return 0;
        if(target==PIANO) {
            state.variables[10]=(hero()==0?51:56)+sheet();state.variables[52]=(hero()==0?63:60)+sheet();
            state.variables[53]=1;
            if(hero()==1) { state.variables[18]=12;state.variables[19]=35; }
        } else if(target==FENCE && !arrowTurned()) { state.variables[10]=54;state.variables[53]=2; }
        else if(target==EXIT && arrowTurned()) completeLocation();
        return pendingClip();
    }
    public int endStep() { return pendingAction()==54?50:hero()==0?65:25; }
    public int companionClip() {
        return pendingAction()>=51 && pendingAction()<=53 && step()>=10?57+pendingAction()-51:0;
    }
    public boolean heroesVisible() { return pendingAction()!=54; }
    public void advance() {
        if(pendingAction()==0) return;
        state.variables[53]++;
        int action=pendingAction();
        if(action>=56 && action<=58 && step()==9) state.variables[0]=(sheet()+1)%3;
        if(action>=51 && action<=53 && step()==10) {
            state.variables[13]=action==51?2:1;state.variables[17]=5;
        }
        // Original map query 22: Colleague on (5,18) or (5,19), arrow entity 27 still present.
        if(action==52 && step()==13 && !arrowTurned() && heroX(1)==320 && (heroY(1)==288 || heroY(1)==304)) {
            state.variables[1]=1;state.variables[2]=0;
        }
        if(step()>=endStep()) {
            if(action==54) { placeHero(0,576,96);placeHero(1,576,80); }
            else if(hero()==0) { state.variables[12]=2;state.variables[16]=0; }
            else { state.variables[13]=2;state.variables[17]=0; }
            state.variables[10]=state.variables[52]=state.variables[53]=0;
        }
    }
    public void ambientTick() {
        if(++state.variables[6]>=130) { state.variables[6]=0;state.variables[9]=0; }
        if(animalPhase()==0) {
            if(++state.variables[5]>=100) { state.variables[3]=1;state.variables[4]=0; }
        } else if(animalPhase()==2 && heroApproachesAnimal()) {
            state.variables[3]=3;state.variables[4]=0;
        }
    }
    private boolean heroApproachesAnimal() {
        // Source map query 29: 16 exact cells, not a rectangular distance approximation.
        for(int h=0;h<2;h++) if(CourtyardWalk.animalSees(heroX(h),heroY(h))) return true;
        return false;
    }
    public void finishAnimal() {
        if(animalPhase()==1) { state.variables[3]=2;state.variables[4]=0;state.variables[5]=150; }
        else if(animalPhase()==3) state.variables[3]=state.variables[4]=state.variables[5]=0;
    }
    public String message() {
        if(completed()) return "Вторая локация пройдена";
        return state.variables[19]>0 && state.variables[18]==12?"Коллега: Маэстро!":null;
    }
    public boolean completed() { return state.variables[8]==1; }
    public void completeLocation() { state.variables[8]=1;state.scene=3; }
}
