package pilots;

/** Crate segment: acquire a standing target, then keep it locked through the jump. */
public final class BasementGame {
    public static final int SHOT_TICKS=22;
    static final int COOKIE=0x42535031;
    static final int STATUS=0,COLUMN=1,LANE=2,HEIGHT=3,MOVED=4,FALL=5,FALL_TICK=6,
        SHOT=7,SHOT_TICK=8,MOTION=9,FROM_COLUMN=10,FROM_HEIGHT=11,FROM_LANE=12,
        FACING=13,HIT=14,MESSAGE=15,MESSAGE_TICK=16,SOLVED=17,THROWS=18,SHOT_LEVEL=19,HEIGHTS=20;
    final HotelWorld world;
    final int[] data;
    BasementGame(HotelWorld world,int[] data){this.world=world;this.data=data;}
    void initialize(){data[FALL]=-1;data[SHOT]=-1;data[FACING]=1;data[HEIGHTS]=1;}
    public int column(){return data[COLUMN];}
    public boolean safe(){return data[LANE]==0;}
    public int height(){return data[HEIGHT];}
    public int crates(int column){return data[HEIGHTS+column];}
    public int fallingColumn(){return data[FALL];}
    public int fallingTick(){return data[FALL_TICK];}
    public int shotColumn(){return data[SHOT];}
    public int shotTick(){return data[SHOT_TICK];}
    public int shotLevel(){return data[SHOT_LEVEL];}
    public int motion(){return data[MOTION];}
    public boolean solved(){return data[SOLVED]!=0;}
    public int throwsCount(){return data[THROWS];}
    public boolean hasGun(){return world.hasItem(1);}
    public boolean requested(){return data[STATUS]!=0;}
    public void request(){if(!solved())data[STATUS]=1;world.syncSave();}
    public void open(){if(!requested())throw new IllegalStateException("No crate approach");data[STATUS]=2;if(safe()&&data[FALL]>=0&&data[FALL_TICK]<12){data[FALL]=-1;data[FALL_TICK]=0;}world.syncSave();}
    public void close(){data[STATUS]=0;world.basementReturn();world.syncSave();}
    public String message(){
        if(data[MESSAGE_TICK]<=0)return null;
        switch(data[MESSAGE]){case 1:return "Нужен автомат";case 2:return "Стреляйте с дорожки";case 3:return "Слишком высоко";case 4:return "Осторожно, ящик!";default:return null;}
    }
    private void say(int id){data[MESSAGE]=id;data[MESSAGE_TICK]=25;}
    public boolean input(int key){
        if(data[STATUS]!=2 || solved())return false;
        if(key==5){boolean fired=fire();world.syncSave();return fired;}
        if(data[MOTION]>0 && key!=8 || data[SHOT_TICK]>0 && key==2)return false;
        int col=column(),lane=data[LANE],height=height(),previous=column();boolean wasSafe=safe();
        if(key==4)col--;else if(key==6)col++;else if(key==2)lane=1;else if(key==8)lane=0;else return false;
        if(col<0 || col>=4 || col==column() && lane==data[LANE])return false;
        int next=lane==0?0:crates(col);
        if(lane==1 && next>height+1){say(3);world.syncSave();return false;}
        data[FROM_COLUMN]=column();data[FROM_HEIGHT]=height;data[FROM_LANE]=data[LANE];
        if(col!=column())data[FACING]=col>column()?1:-1;
        data[COLUMN]=col;data[LANE]=lane;data[HEIGHT]=next;data[MOTION]=4;data[MOVED]=1;
        world.cue(3);
        // A jump cannot redirect the throw from the place Chief just occupied.
        if(lane==1 && data[FALL]<0 && data[SHOT_TICK]==0)aim(wasSafe?col:previous);
        if(lane==0 && data[FALL]>=0 && data[FALL_TICK]<12){data[FALL]=-1;data[FALL_TICK]=0;}
        world.syncSave();return true;
    }
    private void aim(int column){
        if(crates(column)>=4)return;
        data[FALL]=column;data[FALL_TICK]=1;data[THROWS]++;
    }
    private boolean fire(){
        if(!hasGun()){say(1);return false;}
        if(!safe()){say(2);return false;}
        if(data[SHOT_TICK]>0 || data[MOTION]>0 || crates(column())==0)return false;
        data[SHOT]=column();data[SHOT_LEVEL]=crates(column());data[SHOT_TICK]=SHOT_TICKS;world.cue(5);return true;
    }
    public void tick(){
        if(data[STATUS]!=2 || solved())return;
        if(data[MESSAGE_TICK]>0)data[MESSAGE_TICK]--;
        if(data[HIT]>0)data[HIT]--;
        if(data[MOTION]>0)data[MOTION]--;
        if(data[SHOT_TICK]>0 && --data[SHOT_TICK]==0){
            int col=data[SHOT];if(data[HEIGHTS+col]>0)data[HEIGHTS+col]--;
            data[SHOT]=-1;world.cue(29);
        }
        // Standing on the danger line also attracts the servant. The chosen
        // column remains fixed until that throw has landed or is cancelled.
        if(data[FALL]<0 && !safe() && data[MOTION]==0 && data[SHOT_TICK]==0)aim(column());
        if(data[FALL]>=0){
            int col=data[FALL],t=++data[FALL_TICK];
            if(t==12)world.cue(28);
            if(t>=22){
                if(crates(col)<4)data[HEIGHTS+col]++;
                if(!safe() && column()==col){
                    data[FROM_COLUMN]=column();data[FROM_LANE]=1;data[FROM_HEIGHT]=height();
                    data[LANE]=0;data[HEIGHT]=0;data[MOTION]=4;data[HIT]=12;say(4);world.cue(4);
                }
                data[FALL]=-1;data[FALL_TICK]=0;
            }
        }
        if(!safe() && data[MOTION]==0 && height()==4 && data[FALL]!=column()){
            data[SOLVED]=1;data[STATUS]=0;world.finishBasement();
        }
        world.syncSave();
    }
}
