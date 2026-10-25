package pilots;
import java.io.IOException;
import java.lang.reflect.Field;
import javax.microedition.lcdui.Display;

/** Exercises the shipped MIDlet/Canvas entry routes, not just scene constructors. */
public final class LocationEntryTest {
    static void check(boolean ok,String message) { if(!ok) throw new RuntimeException(message); }
    static Object get(Object object,String name)throws Exception {
        Field f=object.getClass().getDeclaredField(name);f.setAccessible(true);return f.get(object);
    }
    static void set(Object object,String name,Object value)throws Exception {
        Field f=object.getClass().getDeclaredField(name);f.setAccessible(true);f.set(object,value);
    }
    static void garden(PilotsMidlet.GameCanvas canvas,String route)throws Exception {
        check(((GameState)get(canvas,"state")).scene==5 && get(canvas,"zoo") instanceof GardenScene,route);
    }
    static void street(PilotsMidlet.GameCanvas canvas,String route)throws Exception {
        check(((GameState)get(canvas,"state")).scene==6 && get(canvas,"zoo") instanceof StreetScene,route);
    }
    static void location(PilotsMidlet app,int scene)throws Exception {
        app.choose(2);
        MenuCanvas menu=(MenuCanvas)Display.getDisplay(app).getCurrent();
        PlayerProgress progress=(PlayerProgress)get(app,"progress");
        int count=1;for(int id:PlayerProgress.ORDER)if(progress.available(id))count++;check(((String[])get(menu,"items")).length==count,"locked locations exposed");
        int choice=1;for(int id:PlayerProgress.ORDER)if(progress.available(id)){if(id==scene){((MenuCanvas.Host)get(menu,"host")).choose(choice);return;}choice++;}
        throw new RuntimeException("missing menu scene "+scene);
    }
    static void boardSelect(SwitchboardCanvas canvas,int target)throws Exception {
        SwitchboardPuzzle puzzle=(SwitchboardPuzzle)get(canvas,"puzzle");int from=((Integer)get(canvas,"selected")).intValue();
        int[] previous=new int[27],keys=new int[27],queue=new int[27];for(int i=0;i<27;i++)previous[i]=-1;
        int head=0,tail=0;queue[tail++]=from;previous[from]=from;int[] dx={0,-1,1,0},dy={-1,0,0,1},code={50,52,54,56};
        while(head<tail){int a=queue[head++];for(int d=0;d<4;d++){int b=puzzle.select(a,dx[d],dy[d]);if(previous[b]<0){previous[b]=a;keys[b]=code[d];queue[tail++]=b;}}}
        check(previous[target]>=0,"detail arrow target unreachable");int length=0;for(int n=target;n!=from;n=previous[n])length++;
        int[] path=new int[length];for(int n=target,i=length-1;n!=from;n=previous[n])path[i--]=keys[n];for(int i=0;i<path.length;i++)canvas.input(path[i]);
        check(((Integer)get(canvas,"selected")).intValue()==target,"detail arrow selection");
    }
    static void crateKey(BasementCanvas detail,PilotsMidlet.GameCanvas canvas,int key,int ticks)throws Exception{
        detail.input(48+key);for(int i=0;i<ticks;i++)canvas.tick(100);
    }
    public static void main(String[] args)throws Exception {
        PilotsMidlet app=new PilotsMidlet();PilotsMidlet.GameCanvas canvas=app.new GameCanvas();
        try {
            set(app,"canvas",canvas);set(app,"display",Display.getDisplay(app));
            // Menu item 10 must actually enter scene 5 (errors normally become alerts).
            canvas.startLocation(5);
            location(app,5);garden(canvas,"fourth location menu entry");
            canvas.startLocation(4);
            ((AnimalScene)get(canvas,"zoo")).animals.world.completed=true;
            canvas.tick(100);garden(canvas,"third location completion transition");
            canvas.save();canvas.startLocation(2);canvas.load();garden(canvas,"RMS garden restore");
            // Enter the fifth location from its menu and from garden completion.
            canvas.startLocation(6);location(app,6);street(canvas,"fifth location menu entry");
            canvas.startLocation(5);((GardenScene)get(canvas,"zoo")).animals.world.completed=true;
            canvas.tick(100);street(canvas,"fourth-to-fifth transition");
            canvas.save();canvas.startLocation(2);canvas.load();street(canvas,"RMS street restore");
            ((StreetScene)get(canvas,"zoo")).animals.world.completed=true;
            canvas.tick(100);check(((GameState)get(canvas,"state")).scene==7&&get(canvas,"zoo") instanceof SewerScene,"street-to-sewer transition");
            location(app,7);SewerScene pipe=(SewerScene)get(canvas,"zoo");check(get(canvas,"zooPanel")==null&&((Scene)get(canvas,"scene")).panel==null,"sewer has inventory");
            canvas.keyPressed(52);canvas.tick(100);canvas.save();canvas.startLocation(2);canvas.load();pipe=(SewerScene)get(canvas,"zoo");check(pipe.sewer.motion>0,"sewer RMS motion");
            canvas.keyPressed(48);check(Display.getDisplay(app).getCurrent() instanceof MenuCanvas,"sewer menu key");
            canvas.startLocation(7);
            for(int key:SewerWorldTest.solution()){if(key!=0)canvas.keyPressed(48+key);canvas.tick(100);}
            check(((GameState)get(canvas,"state")).scene==8,"single crossing must enter hotel");
            canvas.save();canvas.startLocation(2);canvas.load();check(((GameState)get(canvas,"state")).scene==8,"cinematic save");
            for(int i=0;i<230;i++)canvas.tick(100);
            check(((GameState)get(canvas,"state")).scene==9,"entrance-to-interior transition");
            location(app,8);check(((GameState)get(canvas,"state")).scene==8,"cinematic menu");
            location(app,9);check(((GameState)get(canvas,"state")).scene==9,"hotel interior menu");
            canvas.save();canvas.startLocation(2);canvas.load();check(((GameState)get(canvas,"state")).scene==9,"interior save");
            HotelWorld hotel=((HotelScene)get(canvas,"zoo")).animals.world;
            for(int i=0;i<180;i++)canvas.tick(100);check(hotel.interact(101),"fridge approach");
            for(int i=0;i<180 && !(Display.getDisplay(app).getCurrent() instanceof FridgeCanvas);i++)canvas.tick(100);
            check(Display.getDisplay(app).getCurrent() instanceof FridgeCanvas,"detail canvas was not displayed");
            FridgeCanvas detail=(FridgeCanvas)Display.getDisplay(app).getCurrent();int before=hotel.fridgeMask();
            detail.input(54);detail.input(56);detail.input(53);check(hotel.fridgeMask()==RefrigeratorPuzzle.turn(before,4),"detail keyboard selection");
            int progress=hotel.fridgeMask();canvas.save();detail.input(48);check(Display.getDisplay(app).getCurrent()==canvas,"detail Back");
            canvas.startLocation(2);canvas.load();canvas.tick(100);check(Display.getDisplay(app).getCurrent() instanceof FridgeCanvas,"detail save did not reopen");
            check(((HotelScene)get(canvas,"zoo")).animals.world.fridgeMask()==progress,"detail save lost handles");
            detail=(FridgeCanvas)Display.getDisplay(app).getCurrent();
            int solution=-1;
            for(int moves=0;moves<512;moves++){int mask=progress;for(int i=0;i<9;i++)if((moves&(1<<i))!=0)mask=RefrigeratorPuzzle.turn(mask,i);if(mask==0){solution=moves;break;}}
            check(solution>0,"nontrivial detail solution");
            int selected=0;
            for(int i=0;i<9;i++)if((solution&(1<<i))!=0){
                while(selected/3<i/3){detail.input(56);selected+=3;}
                while(selected%3<i%3){detail.input(54);selected++;}
                while(selected%3>i%3){detail.input(52);selected--;}
                detail.input(53);
            }
            check(Display.getDisplay(app).getCurrent()==canvas,"solved puzzle did not return to room");
            check(!((HotelScene)get(canvas,"zoo")).animals.world.fridgeRequested(),"room stayed paused after solution");
            ((HotelScene)get(canvas,"zoo")).animals.world.completed=true;
            canvas.tick(100);check(((GameState)get(canvas,"state")).scene==10,"interior-to-balcony transition");
            location(app,10);check(((HotelScene)get(canvas,"zoo")).animals.world.scene==10,"balcony submenu route");
            canvas.save();canvas.startLocation(2);canvas.load();check(((GameState)get(canvas,"state")).scene==10,"balcony RMS restore");
            for(int i=0;i<180;i++)canvas.tick(100);
            check(!((HotelScene)get(canvas,"zoo")).logic.busy(),"balcony entry did not finish");
            byte[] menuBefore=((GameState)get(canvas,"state")).encode();
            app.choose(2);MenuCanvas locations=(MenuCanvas)Display.getDisplay(app).getCurrent();locations.keyPressed(48);
            MenuCanvas main=(MenuCanvas)Display.getDisplay(app).getCurrent();
            check(((String[])get(main,"items")).length==6,"main menu must have six game entries");
            check(java.util.Arrays.equals(menuBefore,((GameState)get(canvas,"state")).encode()),"submenu Back changed game");
            ((HotelScene)get(canvas,"zoo")).animals.world.completed=true;canvas.tick(100);
            check(((GameState)get(canvas,"state")).scene==11,"balcony-to-ledge transition");
            location(app,11);check(((HotelScene)get(canvas,"zoo")).animals.world.scene==11,"ledge submenu route");
            canvas.save();canvas.startLocation(2);canvas.load();check(((GameState)get(canvas,"state")).scene==11,"ledge intro RMS restore");
            for(int i=0;i<85;i++)canvas.tick(100);
            check(!((HotelScene)get(canvas,"zoo")).logic.busy(),"ledge entry did not finish");
            canvas.save();canvas.startLocation(2);canvas.load();check(((HotelScene)get(canvas,"zoo")).animals.world.heroes[0]!=null,"ledge room RMS restore");
            ((HotelScene)get(canvas,"zoo")).animals.world.completed=true;canvas.tick(100);
            check(((GameState)get(canvas,"state")).scene==12,"ledge-to-bathroom transition");
            location(app,12);check(((HotelScene)get(canvas,"zoo")).animals.world.scene==12,"bathroom submenu");
            canvas.save();canvas.startLocation(2);canvas.load();check(((GameState)get(canvas,"state")).scene==12,"bathroom RMS restore");
            check(!((HotelScene)get(canvas,"zoo")).logic.busy(),"bathroom not interactive");
            ((HotelScene)get(canvas,"zoo")).animals.world.completed=true;canvas.tick(100);
            check(((GameState)get(canvas,"state")).scene==13,"bathroom-to-switchboard transition");
            location(app,13);HotelWorld board=((HotelScene)get(canvas,"zoo")).animals.world;
            for(int i=0;i<500&&board.busy();i++)canvas.tick(100);check(!board.busy(),"electrical intro did not finish");
            check(board.select(1)&&board.interact(48),"open electrical shield");for(int i=0;i<500&&board.busy();i++)canvas.tick(100);
            check(board.select(0)&&board.interact(16),"approach enlarged electrical panel");
            for(int i=0;i<500&&!(Display.getDisplay(app).getCurrent() instanceof SwitchboardCanvas);i++)canvas.tick(100);
            check(Display.getDisplay(app).getCurrent() instanceof SwitchboardCanvas,"electrical detail canvas missing");
            SwitchboardCanvas electrical=(SwitchboardCanvas)Display.getDisplay(app).getCurrent();int orientation=board.boardOrientation(0);
            electrical.input(53);check(board.boardOrientation(0)==orientation%4+1,"electrical FIRE did not rotate source wire");
            orientation=board.boardOrientation(0);canvas.save();electrical.input(48);check(Display.getDisplay(app).getCurrent()==canvas,"electrical Back");
            canvas.startLocation(2);canvas.load();canvas.tick(100);check(Display.getDisplay(app).getCurrent() instanceof SwitchboardCanvas,"electrical detail RMS reopen");
            board=((HotelScene)get(canvas,"zoo")).animals.world;check(board.boardOrientation(0)==orientation,"electrical RMS lost orientation");
            electrical=(SwitchboardCanvas)Display.getDisplay(app).getCurrent();
            for(int i=0;i<27;i++)if(board.boardOrientation(i)!=1){boardSelect(electrical,i);int n=4;while(board.boardOrientation(i)!=1&&n-->0)electrical.input(53);}
            check(Display.getDisplay(app).getCurrent()==canvas&&!board.boardRequested()&&board.action()==97,"correct circuit did not automatically close detail");
            board.completed=true;canvas.tick(100);
            check(((GameState)get(canvas,"state")).scene==15,"electrical-to-basement transition");
            location(app,15);canvas.save();canvas.startLocation(2);canvas.load();
            check(((HotelScene)get(canvas,"zoo")).animals.world.scene==15,"basement intro RMS restore");
            for(int i=0;i<80;i++)canvas.tick(100);
            HotelWorld basement=((HotelScene)get(canvas,"zoo")).animals.world;
            check(!basement.busy()&&basement.select(1)&&basement.interact(14),"basement gun approach");
            for(int i=0;i<80;i++)canvas.tick(100);
            check(basement.hasItem(1),"basement inventory pickup");canvas.save();canvas.startLocation(2);canvas.load();
            check(((HotelScene)get(canvas,"zoo")).animals.world.hasItem(1),"basement gun RMS restore");
            basement=((HotelScene)get(canvas,"zoo")).animals.world;
            check(basement.select(0)&&((HotelScene)get(canvas,"zoo")).request(400,350),"crate zone approach");canvas.tick(100);
            check(Display.getDisplay(app).getCurrent() instanceof BasementCanvas,"crate Canvas missing");
            BasementCanvas crates=(BasementCanvas)Display.getDisplay(app).getCurrent();crates.showNotify();crateKey(crates,canvas,6,4);
            check(basement.basement.fallingColumn()<0,"safe Canvas movement armed throw");crateKey(crates,canvas,2,5);
            int fall=basement.basement.fallingTick();check(fall>1,"detail timer is not driven by Canvas");canvas.save();
            crates.hideNotify();canvas.tick(100);check(basement.basement.fallingTick()==fall,"hidden detail continued");
            crates.showNotify();crates.input(48);check(Display.getDisplay(app).getCurrent()==canvas,"crate Back");
            canvas.startLocation(2);canvas.load();canvas.tick(100);check(Display.getDisplay(app).getCurrent() instanceof BasementCanvas,"crate RMS did not reopen detail");
            crates=(BasementCanvas)Display.getDisplay(app).getCurrent();crates.showNotify();basement=((HotelScene)get(canvas,"zoo")).animals.world;
            check(basement.basement.fallingTick()==fall,"RMS lost falling crate");for(int i=0;i<22;i++)canvas.tick(100);crateKey(crates,canvas,4,22);
            final BasementCanvas crateScreen=crates;final PilotsMidlet.GameCanvas gameScreen=canvas;final HotelWorld crateWorld=basement;
            BasementTestRoute.stairs(new BasementTestRoute.Driver(){
                public HotelWorld world(){return crateWorld;}public void press(int k){crateScreen.input(48+k);}public void ticks(int n)throws Exception{for(int i=0;i<n;i++)gameScreen.tick(100);}
            });
            crateKey(crates,canvas,2,4);crateKey(crates,canvas,4,4);crateKey(crates,canvas,4,4);crateKey(crates,canvas,4,4);
            check(Display.getDisplay(app).getCurrent()==canvas&&get(app,"basement")==null&&basement.basement.solved(),"crate solution did not automatically return to source room");
            for(int i=0;i<250&&!basement.completed;i++)canvas.tick(100);check(basement.completed,"source ending after crate Canvas");
            canvas.tick(100);check(((GameState)get(canvas,"state")).scene==14,"basement-to-roof source transition");
            location(app,14);for(int i=0;i<100;i++)canvas.tick(100);
            HotelWorld roof=((HotelScene)get(canvas,"zoo")).animals.world;check(!roof.busy(),"roof menu entry");
            canvas.save();canvas.startLocation(2);canvas.load();check(((GameState)get(canvas,"state")).scene==14,"roof RMS restore");
            ((HotelScene)get(canvas,"zoo")).animals.world.completed=true;canvas.tick(100);check(((GameState)get(canvas,"state")).scene==16&&get(canvas,"zoo") instanceof FlightScene,"roof-to-flight transition");
            location(app,16);FlightScene flight=(FlightScene)get(canvas,"zoo");canvas.keyPressed(50);canvas.tick(100);check(flight.flight.ballast()==2&&flight.flight.world.action()==23,"flight keyboard upper band");
            int phase=flight.flight.world.step();canvas.save();canvas.startLocation(2);canvas.load();flight=(FlightScene)get(canvas,"zoo");check(flight.flight.world.step()==phase&&flight.flight.ballast()==2,"flight RMS phase/ballast");
            canvas.keyPressed(48);check(Display.getDisplay(app).getCurrent() instanceof MenuCanvas,"flight menu key");
            flight.flight.world.completed=true;canvas.tick(100);check(((GameState)get(canvas,"state")).scene==17,"flight-to-final transition");
            location(app,17);for(int i=0;i<100;i++)canvas.tick(100);HotelWorld forest=((HotelScene)get(canvas,"zoo")).animals.world;
            check(!forest.busy()&&forest.move(384,368),"final click route");canvas.tick(100);canvas.save();canvas.startLocation(2);canvas.load();
            check(((GameState)get(canvas,"state")).scene==17,"final RMS scene");for(int i=0;i<100;i++)canvas.tick(100);
            check(!((HotelScene)get(canvas,"zoo")).animals.world.busy(),"final RMS queued route");
            canvas.startLocation(6);
            // Re-enter, restore a street save, and verify its actual loader.
            GameState saved=GameState.decode(((GameState)get(canvas,"state")).encode(),7);
            canvas.startLocation(2);set(canvas,"state",saved);canvas.goToScene(saved.scene);
            street(canvas,"restored fifth location entry");
            // Garden previews must load from another scene and return to it.
            canvas.startLocation(2);set(app,"animationCatalog",new AnimationCatalog());
            canvas.preview(0);check(((GameState)get(canvas,"state")).scene==7,"preview scene");
            canvas.finishPreview();check(((GameState)get(canvas,"state")).scene==2,"preview return");
            Object original=get(canvas,"zoo");
            try { canvas.goToScene(18);throw new RuntimeException("unsupported scene accepted"); }
            catch(IOException expected) {}
            check(get(canvas,"zoo")==original,"invalid scene damaged active scene");
        } finally { canvas.shutdown(); }
        System.out.println("LocationEntry: MIDlet menus, third/fourth/fifth transitions, real Canvas RMS save/load, preview/return, fridge, switchboard and crate Canvas controls/timer/pause/RMS restore/automatic return and invalid catalog ID passed");
    }
}
