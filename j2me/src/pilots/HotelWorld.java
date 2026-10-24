package pilots;

import java.io.*;

/** Sections 10/11/12/13/14 VM. Original object rules and map predicates are packaged unchanged. */
public final class HotelWorld {
    public static final class Definition {
        int type,layer;byte[] init,exit;byte[][][] rules;
    }
    public static final class Thing {
        public int id,x,y,clip=-1,frame,layer,under=254,serial,fallback=-1,fromX,fromY;
        boolean active=true;
        boolean finished;
        int[] local=new int[8];
        Thing(int id,int x,int y,int serial) { this.id=id;this.x=x;this.y=y;this.serial=serial; }
    }
    final GameState state;
    final int scene;private int random=1;
    final byte[][] choices=new byte[16][];private String[] texts;
    final Definition[] definitions=new Definition[128];
    final byte[][] maps=new byte[180][];
    final byte[][] groups=new byte[16][];
    final int[] frames=new int[256],flags=new int[256];
    public final Thing[] cells=new Thing[300];
    public final Thing[] heroes=new Thing[2];
    public final Thing[] effects=new Thing[8];
    private Thing context;
    private SoundSink sound;
    private final int[] initialCues=new int[16];private int initialCueCount;
    private final int[] boardGlobals=new int[32];
    private int serial,clicked=-1,continuing,captionTicks;
    private String caption;
    public int walkHero=-1,walkX,walkY,walkEntity=-1,walkRestore=-1;
    private int[] path;private int pathIndex,walkPhase;
    public boolean completed;
    private byte[] initial;
    private int routeDirection=-1;
    private static final int[] STEP_X={0,1,0,-1},STEP_Y={-1,0,1,0};
    private final int[] routeQueue=new int[300],routeFirst=new int[300];
    private boolean acknowledge;
    public HotelWorld(GameState state) throws IOException {
        this.state=state;this.scene=state.scene;
        DataInputStream in=new DataInputStream(getClass().getResourceAsStream("/scenes/"+(scene<10?"0":"")+scene+".vm"));
        try {
            if(in.readInt()!=0x41564d31) throw new IOException("Invalid hotel rules");
            int n=in.readUnsignedShort();
            for(int i=0;i<n;i++) {
                int id=in.readUnsignedShort();Definition d=new Definition();definitions[id]=d;
                d.type=in.readUnsignedByte();in.readUnsignedByte();d.layer=in.readUnsignedByte();
                d.init=blob(in);d.exit=blob(in);d.rules=new byte[in.readUnsignedShort()][3][];
                for(int j=0;j<d.rules.length;j++) for(int k=0;k<3;k++) d.rules[j][k]=blob(in);
            }
            n=in.readUnsignedShort();for(int i=0;i<n;i++) maps[in.readUnsignedShort()]=blob(in);
            initial=blob(in);n=in.readUnsignedShort();
            for(int i=0;i<n;i++) { int id=in.readUnsignedShort();frames[id]=in.readUnsignedByte();flags[id]=in.readUnsignedByte(); }
            n=in.readUnsignedShort();for(int i=0;i<n;i++) groups[in.readUnsignedShort()]=blob(in);
            n=in.readUnsignedShort();for(int i=0;i<n;i++) choices[in.readUnsignedShort()]=blob(in);
            texts=new String[in.readUnsignedShort()];for(int i=0;i<texts.length;i++) { byte[] b=new byte[in.readUnsignedShort()];in.readFully(b);texts[i]=new String(b,"UTF-8"); }
        } finally { in.close(); }
        if(state.location!=null) restore(state.location);
        else {
            for(int i=0;i<state.variables.length;i++) state.variables[i]=0;
            state.variables[58]=scene;state.variables[0x1c/4]=2;state.variables[0xac/4]=1;
            apply(initial,null,true,false);
            // Controller 14 initializes the room; the entrance starts at source frame zero.
            if(scene>=9)tick();
        }
        // 0.14.0 treated a returned static sprite as an ended animation. The
        // fly's default end rule consequently replaced its TV pose with 111.
        if(scene==10 && state.location!=null) {
            Thing fly=find(38);
            if(fly!=null && g(0x1e1b60)==2 && fly.clip==111) {
                fly.clip=fly.fallback=action()==74?108:109;fly.frame=0;fly.finished=false;
                syncSave();
            }
        }
        if(scene==11 && state.location!=null) {
            // Older saves lost the loop underneath the lift/rope reaction.
            for(int i=0;i<cells.length;i++) {
                Thing t=cells[i];if(t==null || t.id!=18 && t.id!=21)continue;
                int base=t.id==18?54:51;
                if(t.clip<0) { t.clip=base;t.frame=0;t.finished=false;t.fallback=-1; }
                else if(t.clip==(t.id==18?56:53) && t.fallback<0)t.fallback=base;
            }
            if(action()==90)for(int h=0;h<2;h++)if(heroes[h]!=null && heroes[h].active && heroes[h].clip<0) {
                idle(heroes[h]);heroes[h].layer=g(h==0?0x40:0x44);
            }
            syncSave();
        }
        initFridge();
        // Reconcile snapshots written before map replacement retired hero 12.
        if(scene==9 && !completed && state.variables[63]==0 && (action()==84 || action()==87) && find(97)!=null) {
            heroes[1].active=false;heroes[1].clip=-1;
        }
        // Older hotel builds stopped after the first exit, leaving pose 97.
        if(scene==9 && completed && state.variables[63]==0 && heroes[1].active)beginPartnerExit();
        clearSinkPassage();
        syncSave();
    }
    public boolean boardRequested() { return scene==13 && (state.variables[59]==2 || state.variables[59]==3); }
    public void beginBoard() { if(!boardRequested())throw new IllegalStateException("No switchboard approach");state.variables[59]=3;syncSave(); }
    public void endBoard() { state.variables[59]=0;syncSave(); }
    public Thing boardThing(int index) { return index<0 || index>=27?null:find(16+index); }
    public int boardOrientation(int index) { return g(0x1e1b54+index*4); }
    public boolean turnBoard(int index) {
        if(!boardRequested() || index<0 || index>=27 || boardThing(index)==null)return false;
        state.variables[59]=4;
        boolean accepted=interact(16+index);int limit=300;
        while(accepted && action()!=0 && action()!=97 && limit-->0)tick();
        if(limit<=0)throw new IllegalStateException("Switchboard turn did not settle");
        state.variables[59]=action()==97?0:3;syncSave();return accepted;
    }
    public boolean fridgePanelVisible() { return scene==9 && find(35)!=null; }
    public int fridgeMask() { return state.variables[61]; }
    public boolean fridgeRequested() { return scene==9 && (state.variables[59]==2 || state.variables[59]==3); }
    public void beginFridge() { if(!fridgeRequested())throw new IllegalStateException("No refrigerator approach");state.variables[59]=3;syncSave(); }
    public void endFridge() { state.variables[59]=0;syncSave(); }
    public boolean turnFridge(int index) {
        if(scene!=9 || state.variables[59]!=3 || completed || hero()!=0 || item()!=0)return false;
        state.variables[61]=RefrigeratorPuzzle.turn(fridgeMask(),index);mirrorFridge();cue(13);syncSave();return true;
    }
    private void initFridge() {
        if(scene!=9)return;
        if(state.variables[62]!=0x46523331) {
            if(action()!=83)for(int h=0;h<2;h++)if(heroes[h]!=null && heroes[h].active) {
                Thing t=heroes[h];int offset=t.layer-g(h==0?0x40:0x44),depth=58-t.y/8;
                g(h==0?0x40:0x44,depth);t.layer=depth+offset;
            }
            int seed=0;for(int i=0;i<16;i++)if(at(2+i%4*2,3+i/4*2)>=80)seed|=1<<i;
            state.variables[61]=seed==0?0:RefrigeratorPuzzle.initial(seed);state.variables[62]=0x46523331;mirrorFridge();
        }
    }
    private void mirrorFridge() {
        for(int i=0;i<16;i++) {
            int x=2+i%4*2,y=3+i/4*2,id=64+i+(i<9 && (fridgeMask()&(1<<i))!=0?16:0);
            Thing t=cells[y*10+x];if(t==null || t.id!=id)place(id,x,y);
        }
        for(int i=0;i<4;i++) { int id=(fridgeMask()==0?46:42)+i;Thing t=cells[20+2+i*2];if(t==null || t.id!=id)place(id,2+i*2,2); }
    }
    public boolean keyAvailable() {
        return scene==9 && find(57)==null && find(27)==null && g(0x9c)<100 && apply(maps[95],null,true,true);
    }
    private byte[] blob(DataInputStream in) throws IOException {
        int length=in.readInt();if(length<0 || length>65536) throw new IOException("Invalid VM block");
        byte[] out=new byte[length];in.readFully(out);return out;
    }
    static int word(byte[] b,int p) { return (b[p]&255)|((b[p+1]&255)<<8)|((b[p+2]&255)<<16)|(b[p+3]<<24); }
    private int variableIndex(int offset) {
        if((scene==10 || scene==12) && offset>=0x1e1b54 && offset<=0x1e1b64)return 54+(offset-0x1e1b54)/4;
        return offset==0x1e1b54?60:offset/4;
    }
    int g(int offset) { return scene==13 && offset>=0x1e1b54 && offset<=0x1e1bd0?boardGlobals[(offset-0x1e1b54)/4]:state.variables[variableIndex(offset)]; }
    void g(int offset,int value) { if(scene==13 && offset>=0x1e1b54 && offset<=0x1e1bd0)boardGlobals[(offset-0x1e1b54)/4]=value;else state.variables[variableIndex(offset)]=value; }
    private int heroIndex(int id) { return scene>=10?(id==12?0:id==11?1:-1):scene==9 && (id==12 || id==13)?(id==13?0:1):-1; }
    public int hero() { return g(0x18)==1?1:0; }
    public int action() { return g(0x20); }
    public int step() { return g(0x30); }
    private boolean nativeWalking() { for(int h=0;h<2;h++) { Thing t=heroes[h];if(t!=null && t.active && t.clip>=20 && t.clip<=33 && !t.finished)return true; }return false; }
    public boolean busy() { return !completed && (scene==8 || scene==13 && g(0x88)!=0 && (find(6)!=null || find(7)!=null) || scene==12 && g(0x1e1b60)==1 || action()!=0 || walkHero>=0 || nativeWalking()); }
    public void sound(SoundSink value) { sound=value;if(value instanceof MusicSink && scene==9 && (completed || action()==87 || action()==84 && step()>=10))((MusicSink)value).music(2);if(value!=null) { for(int i=0;i<initialCueCount;i++) value.play(initialCues[i]);initialCueCount=0; } }
    public void cue(int cue) {
        if(sound!=null) sound.play(cue);else if(initialCueCount<initialCues.length) initialCues[initialCueCount++]=cue;
        if(cue==6 || cue==7) { caption=cue==6?"Шеф: Ничего не понимаю.":"Коллега: Ничего не понимаю.";captionTicks=35; }
    }
    public String caption() { return captionTicks>0?caption:completed?(scene==13?"Электрощитовая пройдена":scene==12?"Ванная комната пройдена":scene==11?"Карниз гостиницы пройден":scene==10?"Комната с балконом пройдена":"Гостиница пройдена"):null; }
    private Thing find(int id) {
        if(heroIndex(id)>=0) { int h=heroIndex(id);return heroes[h]!=null && heroes[h].active?heroes[h]:null; }
        for(int i=0;i<cells.length;i++) if(cells[i]!=null && cells[i].id==id) return cells[i];
        return null;
    }
    int at(int x,int y) {
        if(x<0 || x>=10 || y<0 || y>=30) return 254;
        for(int h=0;h<2;h++) if(heroes[h]!=null && heroes[h].active && heroes[h].x/64==x && heroes[h].y/16==y) return heroes[h].id;
        Thing t=cells[y*10+x];return t==null?254:t.id;
    }
    private void remove(int x,int y) {
        if(x<0 || x>=10 || y<0 || y>=30) return;
        for(int h=0;h<2;h++) if(heroes[h]!=null && heroes[h].active && heroes[h].x==x*64 && heroes[h].y==y*16) {
            // Deleting an actor also executes its original exit script.
            // Any resulting sprite continues independently of the removed actor.
            Thing hero=heroes[h];Definition d=definitions[hero.id];if(d!=null) exit(d,hero);
            hero.active=false;hero.clip=-1;return;
        }
        removeCell(x,y);
    }
    private void enter(Thing hero) {
        int x=hero.x/64,y=hero.y/16;Thing target=cells[y*10+x];
        if(target!=null && (target.id==6 && hero==heroes[hero()] || target.id==7 && hero!=heroes[hero()])) removeCell(x,y);
    }
    private void removeCell(int x,int y) {
        Thing t=cells[y*10+x];if(t==null) return;
        cells[y*10+x]=null;
        Definition d=definitions[t.id];if(d!=null) exit(d,t);
        if(t.under!=254) place(t.under,x,y);
    }
    /** Original exit scripts may leave an unbound sprite after deleting their map object. */
    private void exit(Definition definition,Thing owner) {
        Thing sprite=new Thing(owner.id,owner.x,owner.y,0);sprite.layer=owner.layer;sprite.under=owner.under;
        for(int i=0;i<owner.local.length;i++) sprite.local[i]=owner.local[i];
        execute(definition.exit,sprite);
        if(sprite.clip>=0) {
            for(int i=0;i<effects.length;i++) if(effects[i]==null) { effects[i]=sprite;return; }
            throw new IllegalStateException("Too many hotel exit sprites");
        }
    }
    private void place(int id,int x,int y) {
        if(x<0 || x>=10 || y<0 || y>=30) throw new IllegalStateException("Map cell outside scene");
        if(heroIndex(id)>=0) {
            int h=heroIndex(id);Thing t=heroes[h];if(t==null) { t=new Thing(id,x*64,y*16,++serial);heroes[h]=t;t.layer=definitions[id].layer;execute(definitions[id].init,t);
                if((scene==11 && action()==90 || scene==13 && action()==98) && t.clip<0) { idle(t);t.layer=g(h==0?0x40:0x44); }
            }
            else { t.x=x*64;t.y=y*16;t.active=true;if(t.clip<0 || t.clip>=20 && t.clip<=33) idle(t);t.fromX=t.x;t.fromY=t.y; }
            enter(t);return;
        }
        if(id==254) { remove(x,y);return; }
        Thing old=cells[y*10+x];int under=old==null?254:definitions[old.id]!=null&&definitions[old.id].type!=0?old.under:old.id;
        if(old!=null && definitions[old.id]!=null) exit(definitions[old.id],old);
        Thing t=new Thing(id,x*64,y*16,++serial);t.under=under;cells[y*10+x]=t;
        Definition d=definitions[id];
        if(d!=null) { t.layer=d.layer;execute(d.init,t); }
        // The bath controller creates the crocodile on step 1; its first rules
        // must run before that controller advances to step 2 on the next tick.
        if(id==27)rules(t);
        // Source map replacement 12 -> 97 retires the movie controller. Keep
        // its root for the final J2ME exit, but display only the authored hold.
        if(scene==9 && id==97 && heroes[1]!=null && heroes[1].x==t.x && heroes[1].y==t.y) {
            heroes[1].active=false;heroes[1].clip=-1;
        }
    }
    boolean inGroup(int group,int id) {
        byte[] bits=group>=0 && group<groups.length?groups[group]:null;
        return id!=254 && bits!=null && id>=0 && id/8<bits.length && (bits[id/8]&(1<<(id%8)))!=0;
    }
    /** Original map group predicates, including 04 mandatory and 08 alternatives. */
    boolean apply(byte[] data,Thing owner,boolean absolute,boolean query) {
        int mask=word(data,0),p=4;
        if((mask&1)!=0) p+=4;
        if((mask&2)!=0) {
            boolean accepted=false;
            while(true) {
                int header=word(data,p);p+=4;int count=header&65535;boolean group=false;
                for(int i=0;i<count;i++) {
                    int id=data[p]&255,bits=data[p+1]&255,x=data[p+2],y=data[p+3];p+=4;
                    if(!absolute && owner!=null) { x+=owner.x/64;y+=owner.y/16; }
                    else { x&=255;y&=255; }
                    int actual=at(x,y);
                    boolean match=(bits&1)!=0?inGroup(id,actual):id==actual;
                    if((bits&2)!=0) match=!match;
                    if(match) group=true;
                    if(!match && (bits&12)==4) return false;
                    if(!match && (bits&12)==8) accepted=true;
                }
                if(header>>>24==4 && !group) return false;
                if((header&65536)!=0) break;
                if(accepted) {
                    while((header&65536)==0) { header=word(data,p);p+=4+(header&65535)*4; }
                    break;
                }
            }
        }
        if(query) return true;
        if((mask&4)!=0 && owner!=null) {
            int dx=(byte)(mask>>16),dy=(byte)(mask>>24);
            int old=owner.y/16*10+owner.x/64;
            owner.fromX=owner.x;owner.fromY=owner.y;owner.x+=dx*64;owner.y+=dy*16;
            if(owner==heroes[0] || owner==heroes[1]) enter(owner);
            if(owner!=heroes[0] && owner!=heroes[1] && old>=0 && old<300 && cells[old]==owner) {
                cells[old]=null;if(owner.under!=254) place(owner.under,old%10,old/10);
                int next=owner.y/16*10+owner.x/64;Thing ground=cells[next];owner.under=ground==null?254:ground.id;cells[next]=owner;
            }
        }
        if((mask&8)!=0) p+=4;
        if((mask&16)!=0) {
            while(true) {
                int header=word(data,p);p+=4;
                if(header>>>24!=0) throw new IllegalStateException("Unsupported placement group");
                for(int i=0;i<(header&65535);i++) {
                    int id=data[p]&255,bits=data[p+1]&255,x=data[p+2],y=data[p+3];p+=4;
                    if((bits&1)!=0) { byte[] candidates=choices[id];random=random*0x41c64e6d+0x3039;id=candidates[1+((random>>>16)*(candidates[0]&255)>>>16)]&255; }
                    if(!absolute && owner!=null) { x+=owner.x/64;y+=owner.y/16; }else { x&=255;y&=255; }
                    place(id,x,y);
                }
                if((header&65536)!=0) break;
            }
        }
        return true;
    }
    private int execute(byte[] code,Thing owner) {
        Thing saved=context;context=owner;
        int a=0,d=0,p=0,top=0;int[] stack=new int[32];
        try {
            while(p<code.length) {
                int op=code[p++]&255,v;
                switch(op) {
                case 0:return a;
                case 0x23:case 0x1d:a=word(code,p);p+=4;break;
                case 0x31:a=g(word(code,p));p+=4;break;
                case 0x32:a=owner.local[word(code,p)/4];p+=4;break;
                case 0x10:g(word(code,p),a);p+=4;break;
                case 0x11:owner.local[word(code,p)/4]=a;p+=4;break;
                case 0x1e:a++;break;
                case 0x1f:a--;break;
                case 0x21:stack[top++]=a;break;
                case 0x22:d=stack[--top];break;
                case 0x14:d=a;break;
                case 0x20:v=a;a=d;d=v;break;
                case 0x2e:v=d;d=v%a;a=v/a;break;
                case 0x15:a+=d;break;
                case 0x16:a*=d;break;
                case 0x17:a|=d;break;
                case 0x18:a^=d;break;
                case 0x19:a&=d;break;
                case 0x1a:a=-a;break;
                case 0x2d:a=d-a;break;
                case 0x01:a=d==a?1:0;break;
                case 0x02:a=d!=a?1:0;break;
                case 0x03:a=d<a?1:0;break;
                case 0x04:a=d<=a?1:0;break;
                case 0x05:a=d>a?1:0;break;
                case 0x06:a=d>=a?1:0;break;
                case 0x36:
                    v=word(code,p);p+=4;
                    int arg=0;
                    if(!(v==0||v==1||v==3||v==4||v==0x1a||v==0x1b||v==0x1e)) { if(top==0) throw new IllegalStateException("Missing native argument "+v+" entity "+owner.id);arg=stack[--top]; }
                    if(v==0x24) {
                        if(top==0) throw new IllegalStateException("Missing path target");
                        int target=stack[--top];
                        routeDirection=(context==heroes[0] || context==heroes[1]) && arg==2?passageDirection(context,target):-1;
                        a=routeDirection>=0?1:0;
                    } else if(v==0x18) { if(top==0) throw new IllegalStateException("Missing caption position");top--;caption=texts[arg];captionTicks=10000;a=1; }
                    else a=nativeCall(v,arg);break;
                default:throw new IllegalStateException("Unsupported original opcode "+op+" in entity "+owner.id);
                }
            }
            throw new IllegalStateException("Unterminated original rule");
        } catch(ArrayIndexOutOfBoundsException e) { throw new IllegalStateException("Hotel object "+owner.id+" opcode position "+p+": "+e.toString()); } finally { context=saved; }
    }
    private int nativeCall(int api,int arg) {
        switch(api) {
        case 0:continuing=1;if(context.clip>=0 && frames[context.clip]==1) context.fallback=context.clip;return 1;
        case 1:return context.clip<0 || context.finished?1:0;
        case 3:return 0; // Hover captions are rendered independently, without executing string bytecode.
        case 4:return clicked==context.id?1:0;
        case 5:return acknowledge?1:0;
        case 9:return apply(maps[arg],context,true,true)?1:0;
        case 0x0e:
            if(scene==9 && arg==10)beginPartnerExit();
            else if(scene==8 && arg==9 || scene==10 && arg==11 || scene==11 && arg==12 || scene==12 && arg==13 || scene==13 && arg==15) { completed=true;g(0x20,0);g(0x30,0);g(0x88,0); }
            return 1;
        case 0x10:if(sound instanceof MusicSink)((MusicSink)sound).music(arg);return 1; // MIDI lifecycle belongs to GameAudio.
        case 0x11:cue(arg);return 1;
        case 0x13:
            if(context.clip==arg && !context.finished)return 1;
            // FUN_00406d80 keeps the base sprite when flag 1 marks a temporary
            // animation. A loop replaces the base; a one-shot returns to it.
            if((flags[arg]&1)!=0 && context.clip>=0 && (flags[context.clip]&1)==0)context.fallback=context.clip;
            if((flags[arg]&1)==0)context.fallback=-1;
            context.clip=arg;context.frame=0;context.finished=false;return 1;
        case 0x14:apply(maps[arg],null,true,false);return 1;
        case 0x19:context.layer=arg;return 1;
        case 0x1a:captionTicks=0;return 1;
        case 0x1e:context.clip=-1;context.fallback=-1;context.finished=true;return 1;
        case 0x1b:context.under=254;return 1;
        case 0x1c:context.under=arg;return 1;
        case 0x36:random=random*0x41c64e6d+0x3039;return (random>>>16)*arg>>>16;
        case 0x1f:return 1;
        case 0x23:return 0;
        case 0x2a:
            if(scene!=11 || arg!=0)throw new IllegalStateException("Unexpected walk cancellation");
            walkHero=-1;path=null;pathIndex=0;walkEntity=walkRestore=-1;return 1;
        case 0x24:return routeDirection>=0?1:0;
        case 0x26:return routeDirection==arg?1:0;
        default:throw new IllegalStateException("Unsupported original native "+api+" in entity "+context.id);
        }
    }
    private boolean ledgeReaction() { return scene==11 && (action()==53 || action()==54); }
    private boolean ledgeRecoil() { return ledgeReaction() && g(4)>=5; }
    private boolean rules(Thing t) {
        routeDirection=-1;
        if(scene==9 && t.id==34 && action()==6 && !keyAvailable()) { g(0x20,13);g(0x30,0); }
        if((t==heroes[0] || t==heroes[1]) && t.clip>=20 && t.clip<=33 && !t.finished && !ledgeRecoil()) return false;
        Definition def=definitions[t.id];if(!t.active || def==null || def.type==0) return false;
        for(int i=0;i<def.rules.length;i++) {
            byte[][] rule=def.rules[i];int mask=word(rule[0],0);
            if(execute(rule[1],t)==0 || !apply(rule[0],t,false,true)) continue;
            continuing=0;execute(rule[2],t);
            apply(rule[0],t,false,false);
            if((mask&128)!=0) break;
            if(continuing==0) break;
        }
        return false;
    }
    private void animate(Thing t) {
        if(t==null || !t.active || t.clip<0 || t.finished) return;
        if(++t.frame>=frames[t.clip]) {
            if((flags[t.clip]&1)!=0) { t.finished=true;t.frame=frames[t.clip]-1;
                // Returning to a looping rest pose starts that animation. Its
                // completion predicate must not report the preceding flight.
                if(t.fallback>=0) { t.clip=t.fallback;t.frame=0;t.finished=false; }
                else if(t!=heroes[0] && t!=heroes[1]) t.clip=-1; }
            else t.frame=0;
        }
    }
    /** The PC switches scenes at native 0e(10). Finish both visible exits here. */
    private void beginPartnerExit() {
        if(state.variables[63]!=0)return;
        completed=false;state.variables[63]=1;
        for(int i=0;i<cells.length;i++)if(cells[i]!=null && cells[i].id==97)removeCell(i%10,i/10);
        heroes[0].active=false;heroes[0].clip=-1;
        heroes[1].active=true;idle(heroes[1]);
        g(0x18,0);g(0x20,87);g(0x30,0);g(0x88,1);
        // Source controller 12 follows marker 7 when controller 13 is selected.
        // Reuse the same doorway after the first character has cleared it.
        place(7,0,18);
    }
    private boolean finishPartnerExit() {
        Thing t=heroes[1];
        if(scene!=9 || state.variables[63]!=1 || t.x!=0 || t.y!=288 || nativeWalking())return false;
        t.active=false;t.clip=-1;completed=true;state.variables[63]=2;
        g(0x20,0);g(0x30,0);g(0x88,0);return true;
    }
    public void tick() {
        if(completed || fridgeRequested() || boardRequested()) return;
        int previousAction=action();
        if(captionTicks>0) captionTicks--;
        g(4,g(4)+1);
        for(int i=0;i<cells.length;i++) animate(cells[i]);
        for(int h=0;h<2;h++) animate(heroes[h]);
        for(int i=0;i<effects.length;i++) if(effects[i]!=null) { animate(effects[i]);if(effects[i].finished) effects[i]=null; }
        walkTick();
        if(finishPartnerExit()) { syncSave();return; }
        // Native controller order follows allocation, not entity number or paint layer.
        int ceiling=serial,last=0;
        while(true) {
            Thing next=null;
            for(int i=0;i<cells.length;i++) { Thing t=cells[i];if(t!=null && t.serial>last && t.serial<=ceiling && (next==null||t.serial<next.serial)) next=t; }
            for(int h=0;h<2;h++) { Thing t=heroes[h];if(t!=null && t.serial>last && t.serial<=ceiling && (next==null||t.serial<next.serial)) next=t; }
            if(next==null) break;last=next.serial;
            if(walkHero>=0 && next==heroes[walkHero] && !ledgeRecoil()) continue;
            if(rules(next)) break;
        }
        if(scene==9 && previousAction!=0 && action()==0 && !completed && walkHero<0) {
            for(int h=0;h<2;h++)if(heroes[h]!=null && heroes[h].active) {
                Thing t=heroes[h];
                if(previousAction==83) { int depth=58-t.y/8;g(h==0?0x40:0x44,depth);t.layer=depth; }
                if(!(t.clip>=20 && t.clip<=33 && !t.finished))idle(t);
            }
        }
        if(scene==9 && state.variables[59]==1 && action()==0) state.variables[59]=previousAction==78?4:0;
        if(scene==9 && state.variables[59]==4 && !nativeWalking() && walkHero<0)state.variables[59]=2;
        if(scene==13 && state.variables[59]==1 && action()>=51 && action()<=85 && step()==0) {
            g(0x20,0);g(0x30,0);g(0x88,0);idle(heroes[0]);state.variables[59]=2;
        }
        clearSinkPassage();
        clicked=-1;acknowledge=false;
        if(action()==0 && walkHero<0 && !(scene==13 && (find(6)!=null || find(7)!=null)) && !(scene==12 && g(0x1e1b60)==1)) g(0x88,0);
        syncSave();
    }
    // Both characters are selectable in the room; the entrance is a cinematic.
    public boolean select(int hero) {
        if(fridgeRequested() || boardRequested() || busy() || completed || scene<9 || hero<0 || hero>1 || scene>=10 && (heroes[hero]==null || !heroes[hero].active)) return false;g(0x18,hero);syncSave();return true;
    }
    public boolean hasItem(int item) { if(scene==13)return item==0 || item==1 && find(15)!=null;if(scene==12)return item==0 || item==1 && (find(22)!=null || find(23)!=null) || item==2 && find(24)!=null;if(scene==11)return item==0 || item==1 && find(20)!=null;if(scene==10)return item==0 || item==1 && find(18)!=null;return item==0 || item>=1 && item<=3 && find(item==1?56:item==2?54:55)!=null; }
    public int item() { return g(0x1c)-2; }
    public boolean selectItem(int item) {
        if(fridgeRequested() || boardRequested() || busy() || completed || !hasItem(item)) return false;g(0x1c,item+2);syncSave();return true;
    }
    public boolean interact(int entity) {
        if(completed || fridgeRequested() || boardRequested() || busy()) return false;
        if(scene==13 && entity==15)return selectItem(1);
        if(scene==12 && entity>=22 && entity<=24)return selectItem(entity==24?2:1);
        if(scene==11 && entity==20)return selectItem(1);
        if(scene==10 && entity==18)return selectItem(1);
        if(scene==9 && entity>=54 && entity<=56)return selectItem(entity==56?1:entity==54?2:3);
        if(heroIndex(entity)>=0) return select(heroIndex(entity));
        if(scene==9 && entity==101)entity=35;
        if(scene==9 && (entity==34 && !keyAvailable() || entity>=64 && entity<=95))return false;
        Thing t=find(entity);if(t==null || !interactive(entity)) return false;
        if(scene==13 && entity>=16 && entity<=42 && hero()==0 && item()==0 && state.variables[59]!=4)state.variables[59]=1;
        if(scene==9 && entity==35 && hero()==0 && item()==0)state.variables[59]=1;
        g(0x88,0);clicked=entity;rules(t);clicked=-1;g(0x88,1);syncSave();return action()!=0;
    }
    public boolean interactive(int id) { return scene==13?(id>=15 && id<=42 || id==48 || id==51 || id==52):scene==12?(id>=15 && id<=26 || id==28 || id==31):scene==11?(id>=15 && id<=20):scene==10?(id==15 || id==16 || id==17 || id==18 || id==22 || id==23 || id==24 || id>=26 && id<=34 || id==38 || id>=40 && id<=42):scene==9 && (id>=20 && id<=37); }
    /** Native 24(target,2), shared by the autonomous Chief and pending approaches. */
    private int passageDirection(Thing actor,int target) {
        if(actor.clip>=20 && actor.clip<=33 && !actor.finished) return -1;
        int start=actor.y/16*10+actor.x/64;
        for(int i=0;i<300;i++) routeFirst[i]=-1;
        int head=0,tail=0;routeQueue[tail++]=start;routeFirst[start]=8;
        while(head<tail) {
            int cell=routeQueue[head++],x=cell%10,y=cell/10;
            for(int d=0;d<4;d++) {
                int nx=x+STEP_X[d],ny=y+STEP_Y[d];if(nx<0||nx>=10||ny<0||ny>=26) continue;
                int next=ny*10+nx;if(routeFirst[next]>=0) continue;
                int id=at(nx,ny);if(id!=254 && id!=target) continue;
                // Only the final authored approach may enter the wash position.
                // Ordinary routes go around the pedestal through the front aisle.
                if(sinkObstacle(nx*64,ny*16) && !(action()==23 && target==6 && id==6 && nx==4 && ny==20))continue;
                routeFirst[next]=cell==start?d*2:routeFirst[cell];
                if(id==target) return routeFirst[next];
                routeQueue[tail++]=next;
            }
        }
        return -1;
    }
    private boolean walk(int h,int x,int y,int entity,int restore) {
        int other=1-h;
        if(heroes[other]!=null && heroes[other].active && heroes[other].x==x && heroes[other].y==y && entity>=0 && restore<0) {
            int[] ax={x-64,x+64,x,x},ay={y,y,y+16,y-16};
            for(int i=0;i<ax.length;i++) {
                if(heroes[h].x==ax[i] && heroes[h].y==ay[i] || !allowed(ax[i],ay[i],heroes[other].x,heroes[other].y)) continue;
                if(walk(other,ax[i],ay[i],entity,h)) return true;
            }
            return false;
        }
        int[] route=plan(heroes[h].x,heroes[h].y,x,y);
        if(route.length==0) return false;
        g(0x24,1);walkHero=h;walkX=x;walkY=y;walkEntity=entity;walkRestore=restore;path=route;pathIndex=2;walkPhase=-1;syncSave();return true;
    }
    public boolean move(int x,int y) { return !completed && !fridgeRequested() && !boardRequested() && !busy() && walk(hero(),Math.max(0,Math.min(576,(x+32)/64*64)),Math.max(16,Math.min(400,(y+8)/16*16)),-1,-1); }
    private void idle(Thing hero) {
        int pose=g(heroIndex(hero.id)==0?0x10:0x14);
        hero.clip=(heroIndex(hero.id)==0?12:16)+(pose==3?1:pose==7?2:pose==9?3:0);
        hero.frame=0;hero.finished=false;hero.fallback=-1;
    }
    private void walkTick() {
        // The lift interrupts travel before native 2a(0) cancels it at tick 5.
        if(walkHero<0 || ledgeReaction()) return;
        Thing h=heroes[walkHero];
        if(path==null) { path=plan(h.x,h.y,walkX,walkY);pathIndex=2;walkPhase=-1; }
        if(path.length==0) { walkHero=-1;idle(h);return; }
        if(pathIndex<path.length) {
            walkPhase=(walkPhase+1)%10;int half=walkPhase/5;
            if(walkPhase%5==0) {
                h.fromX=h.x;h.fromY=h.y;h.x=path[pathIndex];h.y=path[pathIndex+1];
                enter(h);
                int dx=h.x-h.fromX,dy=h.y-h.fromY;
                h.clip=walkHero==0?(dx<0?20:dx>0?21:dy<0?24+half:22+half):(dx<0?32+half:dx>0?28+half:dy<0?30+half:26+half);
                int depth=scene==13?30-h.y/8:scene==12?78-h.y*3/16:scene==11?46-h.y/8:scene==10?69-h.y*3/16:58-h.y/8;g(walkHero==0?0x40:0x44,depth);h.layer=depth;
                int pose=dx<0?(half==0?7:1):dx>0?(half==0?9:3):dy<0?(half==0?7:9):(half==0?1:3);
                g(walkHero==0?0x10:0x14,pose);g(walkHero==0?8:12,1-half);cue(0);
            }
            h.frame=walkPhase%5;h.finished=false;h.fallback=-1;
            if(walkPhase%5==4) pathIndex+=2;
            return;
        }
        int entity=walkEntity,restore=walkRestore;g(0x24,0);walkHero=-1;path=null;walkEntity=walkRestore=-1;
        if(restore>=0) { interactAfterAside(restore,entity);return; }
        idle(h);
        if(entity>=0) {
            Thing target=find(entity);if(target!=null) {
                g(0x88,0);clicked=entity;rules(target);clicked=-1;g(0x88,1);
                // Source 7 marks an interaction point. The J2ME path has already reached it.
                g(0x90,0);
            }
        }
    }
    private void interactAfterAside(int h,int entity) { g(0x18,h);interact(entity); }
    private boolean sinkObstacle(int x,int y) {
        return scene==12 && x>=192 && x<=256 && y<336;
    }
    /** Keep the wash pose authored at its original root; then visibly step back. */
    private void clearSinkPassage() {
        if(scene!=12 || completed || action()!=0 || walkHero>=0 || nativeWalking())return;
        for(int h=0;h<2;h++) {
            Thing t=heroes[h];if(t==null || !t.active || !sinkObstacle(t.x,t.y))continue;
            int[] xs={t.x,t.x,128,320},ys={336,352,336,336};
            for(int i=0;i<xs.length;i++)if(walk(h,xs[i],ys[i],-1,-1))return;
        }
    }
    private boolean allowed(int x,int y,int sx,int sy) {
        if(x<0 || x>576 || y<16 || y>400) return false;
        if(x==sx && y==sy) return true;
        if(sinkObstacle(x,y))return false;
        for(int h=0;h<2;h++) if(heroes[h]!=null && heroes[h].active && heroes[h].x/64==x/64 && heroes[h].y/16==y/16) return false;
        Thing t=cells[y/16*10+x/64];
        // Native 23(8) walks through empty cells. Approach markers belong to
        // the scripted actors, and objects/road cells cannot be crossed freely.
        return t==null;
    }
    int[] plan(int sx,int sy,int tx,int ty) {
        if(!allowed(tx,ty,sx,sy)) return new int[0];
        int width=10,size=10*26,start=((sy+8)/16)*width+(sx+32)/64,end=ty/16*width+tx/64;
        if(start<0||start>=size||end<0||end>=size) return new int[0];
        int[] previous=new int[size],queue=new int[size];for(int i=0;i<size;i++) previous[i]=-1;
        int head=0,tail=0;queue[tail++]=start;previous[start]=start;int[] dx={-1,1,0,0},dy={0,0,-1,1};
        while(head<tail && previous[end]<0) {
            int n=queue[head++],x=n%width,y=n/width;
            for(int i=0;i<4;i++) { int nx=x+dx[i],ny=y+dy[i];if(!allowed(nx*64,ny*16,sx,sy)) continue;
                int next=ny*width+nx;if(previous[next]<0) { previous[next]=n;queue[tail++]=next; }
            }
        }
        if(previous[end]<0) return new int[0];int length=1;
        for(int p=end;p!=start;p=previous[p]) length++;
        int[] result=new int[length*2];int p=end;
        for(int i=length-1;i>=0;i--) { result[i*2]=p%width*64;result[i*2+1]=p/width*16;p=previous[p]; }
        return result;
    }
    public void syncSave() {
        try {
            ByteArrayOutputStream bytes=new ByteArrayOutputStream();DataOutputStream out=new DataOutputStream(bytes);
            out.writeInt(0x48575331);out.writeInt(random);out.writeInt(serial);out.writeBoolean(completed);
            out.writeInt(walkHero);out.writeInt(walkX);out.writeInt(walkY);out.writeInt(walkEntity);out.writeInt(walkRestore);out.writeInt(walkPhase);
            out.writeInt(captionTicks);out.writeUTF(caption==null?"":caption);
            for(int i=0;i<cells.length+2+effects.length;i++) {
                Thing t=i<300?cells[i]:i<302?heroes[i-300]:effects[i-302];out.writeBoolean(t!=null);if(t==null) continue;
                out.writeByte(t.id);out.writeBoolean(t.active);out.writeBoolean(t.finished);out.writeShort(t.x);out.writeShort(t.y);out.writeShort(t.clip);out.writeShort(t.frame);out.writeShort(t.fallback);out.writeShort(t.fromX);out.writeShort(t.fromY);out.writeShort(t.layer);out.writeByte(t.under);out.writeInt(t.serial);
                for(int j=0;j<t.local.length;j++) out.writeInt(t.local[j]);
            }
            out.writeInt(path==null?0:path.length);
            if(path!=null) for(int i=0;i<path.length;i++) out.writeInt(path[i]);
            out.writeInt(path==null?0:pathIndex);
            if(scene==13)for(int i=0;i<boardGlobals.length;i++)out.writeInt(boardGlobals[i]);
            out.close();state.location=bytes.toByteArray();
        } catch(IOException e) { throw new IllegalStateException(e.toString()); }
    }
    private void restore(byte[] bytes) throws IOException {
        DataInputStream in=new DataInputStream(new ByteArrayInputStream(bytes));
        int version=in.readInt();boolean extended=version==0x48575331;
        if(!extended) throw new IOException("Invalid hotel save");
        random=in.readInt();serial=in.readInt();completed=in.readBoolean();
        walkHero=in.readInt();walkX=in.readInt();walkY=in.readInt();walkEntity=in.readInt();walkRestore=in.readInt();walkPhase=in.readInt();captionTicks=in.readInt();caption=in.readUTF();
        if(walkHero < -1 || walkHero>1) throw new IOException("Invalid walking hero");
        for(int i=0;i<(302+effects.length);i++) if(in.readBoolean()) {
            int id=in.readUnsignedByte();boolean active=in.readBoolean(),finished=extended&&in.readBoolean();int x=in.readShort(),y=in.readShort();if(id>=128 || x<0 || x>=640 || y<0 || y>=480) throw new IOException("Invalid saved hotel object");
            Thing t=new Thing(id,x,y,0);t.active=active;t.finished=finished;t.clip=in.readShort();t.frame=in.readShort();t.fallback=in.readShort();t.fromX=in.readShort();t.fromY=in.readShort();t.layer=in.readShort();t.under=in.readUnsignedByte();t.serial=in.readInt();
            for(int j=0;j<t.local.length;j++) t.local[j]=in.readInt();
            if(i<300) cells[i]=t;else if(i<302) heroes[i-300]=t;else effects[i-302]=t;
        }
        int length=in.readInt();if(length<0 || length>2080 || length%2!=0) throw new IOException("Invalid saved path");
        if(length>0) { path=new int[length];for(int i=0;i<length;i++) path[i]=in.readInt(); }
        pathIndex=in.readInt();if(pathIndex<0 || pathIndex>length || pathIndex%2!=0) throw new IOException("Invalid path progress");
        if(scene==13)for(int i=0;i<boardGlobals.length;i++)boardGlobals[i]=in.readInt();
        if(in.read()!=-1 || (scene==9 || scene==10 || scene==11 && action()!=90 || scene==12) && (heroes[0]==null || heroes[1]==null)) throw new IOException("Incomplete hotel save");
    }
}
