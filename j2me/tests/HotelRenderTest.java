package pilots;
import java.io.File;
import javax.imageio.ImageIO;
import javax.microedition.lcdui.Graphics;
/** Actual hotel actor placement, input silhouettes and small-screen rendering. */
public final class HotelRenderTest {
 static void check(boolean b,String s){if(!b)throw new RuntimeException(s);}
 static void capture(HotelScene hotel,int width,int height,String path)throws Exception {
  int bar=(width*63+320)/640;Viewport view=new Viewport(width,height-bar,640,417);view.cursorX=320;view.cursorY=288;view.update();
  Graphics g=new Graphics(width,height);hotel.scene.background.paint(g,view);hotel.paint(g,view);hotel.scene.panel.paintPanel(g,width,height,bar);
  if(hotel.animals.world.scene==9){ZooPanel panel=new ZooPanel(9);try{panel.paint(g,hotel.logic,width,height,bar);}finally{panel.close();}}
  ImageIO.write(g.image,"png",new File(path));
 }
 static void noKeyHit(HotelScene hotel) {
  for(int i=0;i<300;i++){HotelWorld.Thing t=hotel.animals.world.cells[i];AnimatedActor a=hotel.scene.actors[i];if(t==null||t.id!=34||a==null)continue;
   for(int y=a.y+a.clip.offsetY;y<a.y+a.clip.offsetY+a.clip.height;y++)for(int x=a.x+a.clip.offsetX;x<a.x+a.clip.offsetX+a.clip.width;x++)check(hotel.targetAt(x,y)!=34,"key selected through occluder");
  }
 }
 static void act(HotelScene hotel,int id)throws Exception {
  if(id==101){for(int y=195;y<261;y++)for(int x=434;x<500;x++)if(hotel.targetAt(x,y)==101){check(hotel.request(x,y),"detail hotspot");return;}throw new RuntimeException("Missing refrigerator detail hotspot");}
  for(int i=0;i<302;i++) {
   HotelWorld.Thing t=i<300?hotel.animals.world.cells[i]:hotel.animals.world.heroes[i-300];AnimatedActor a=hotel.scene.actors[i];
   if(t==null||t.id!=id||a==null)continue;
   for(int y=Math.max(0,a.y+a.clip.offsetY);y<Math.min(417,a.y+a.clip.offsetY+a.clip.height);y++)for(int x=Math.max(0,a.x+a.clip.offsetX);x<Math.min(640,a.x+a.clip.offsetX+a.clip.width);x++)
    if(hotel.targetAt(x,y)==id){check(hotel.hoverText(x,y)!=null&&hotel.request(x,y),"hotel physical input "+id);return;}
  }
  throw new RuntimeException("Missing hotel hotspot "+id);
 }
 static void settle(HotelScene h)throws Exception {int n=1500;while(h.logic.busy()&&n-->0)h.update(100);check(n>0,"hotel render hangs");}
 static void occlusion()throws Exception {
  Scene scene=new Scene(9);GameState state=new GameState();state.scene=9;
  try { HotelScene h=new HotelScene(scene,state);settle(h);
   HotelWorld.Thing hero=h.animals.world.heroes[1];
   for(int offset=0;offset<=1;offset++)for(int[] point:new int[][]{{512,304},{576,320},{256,304},{384,272}}) {
    hero.x=hero.fromX=point[0];hero.y=hero.fromY=point[1];hero.layer=58-hero.y/8+offset;h.animals.world.g(0x44,hero.layer);hero.clip=16;hero.frame=0;hero.finished=false;
    h.update(100);check(hero.layer==58-hero.y/8+offset,"occlusion probe depth changed");AnimatedActor actor=scene.actors[301];int tested=0;
    for(int y=actor.y+actor.clip.offsetY;y<actor.y+actor.clip.offsetY+18;y++)for(int x=actor.x+actor.clip.offsetX;x<actor.x+actor.clip.offsetX+actor.clip.width;x++) {
     if(!actor.clip.contains(x-actor.x,y-actor.y))continue;
     for(int i=0;i<300;i++) { HotelWorld.Thing t=h.animals.world.cells[i];AnimatedActor mask=scene.actors[i];
      if(t!=null&&mask!=null&&(t.id==33||t.id==51||t.id==58||t.id==59||t.id==60)&&mask.clip.contains(x-mask.x,y-mask.y)) {
       check(h.targetAt(x,y)==12,"hero head hidden by room mask "+t.id+" at "+point[0]+","+point[1]);tested++;
      }
     }
    }
    check(tested>0,"occlusion regression did not overlap a mask");
   }
  } finally {scene.close();}
 }
 static int slot(HotelScene h,int id) {
  for(int i=0;i<300;i++)if(h.animals.world.cells[i]!=null&&h.animals.world.cells[i].id==id)return i;
  throw new RuntimeException("Missing object "+id);
 }
 static Graphics worldPaint(HotelScene h) {
  Graphics g=new Graphics(640,417);Viewport v=new Viewport(640,417,640,417);
  h.scene.background.paint(g,v);h.paint(g,v);return g;
 }
 static void entryAndLock(int width,int height,String path)throws Exception {
  Scene scene=new Scene(9);GameState state=new GameState();state.scene=9;
  try {HotelScene h=new HotelScene(scene,state);int overlaps=0,frames=0;
   while(h.animals.world.action()==83) {
    int doorSlot=-1;for(int i=0;i<300;i++)if(h.animals.world.cells[i]!=null&&h.animals.world.cells[i].id==51)doorSlot=i;
    // The source removes this closed-door mask when the entry opens.
    if(doorSlot>=0) {
    AnimatedActor door=scene.actors[doorSlot];
    HotelWorld.Thing doorThing=h.animals.world.cells[doorSlot];
    check(h.layer(doorSlot)==doorThing.layer,"entry must use original door plane");
    Graphics painted=worldPaint(h),expected=new Graphics(640,417);door.paint(expected,new Viewport(640,417,640,417));
    for(int hero=0;hero<2;hero++) {
     HotelWorld.Thing t=h.animals.world.heroes[hero];AnimatedActor a=scene.actors[300+hero];
     if(a==null||t.layer<=doorThing.layer)continue;
     for(int y=Math.max(0,a.y+a.clip.offsetY);y<Math.min(417,a.y+a.clip.offsetY+a.clip.height);y++)
      for(int x=Math.max(0,a.x+a.clip.offsetX);x<Math.min(640,a.x+a.clip.offsetX+a.clip.width);x++)
       if(a.clip.contains(x-a.x,y-a.y)&&door.clip.contains(x-door.x,y-door.y)) {
        check(painted.image.getRGB(x,y)==expected.image.getRGB(x,y),"entry hero painted on closed door at "+frames+":"+x+","+y);overlaps++;
       }
    }
    }
    if(frames==10){capture(h,width,height,path+"-room-entry.png");capture(h,640,480,path+"-room-entry-world.png");}
    check(++frames<500,"entry did not finish");h.update(100);
   }
   check(overlaps>0,"entry regression never overlapped door");settle(h);
   AnimatedActor lock=scene.actors[slot(h,33)],body=scene.actors[slot(h,61)];
   Graphics painted=worldPaint(h),expected=new Graphics(640,417);lock.paint(expected,new Viewport(640,417,640,417));int pixels=0;
   for(int y=lock.y+lock.clip.offsetY;y<lock.y+lock.clip.offsetY+lock.clip.height;y++)
    for(int x=lock.x+lock.clip.offsetX;x<lock.x+lock.clip.offsetX+lock.clip.width;x++)
     if(lock.clip.contains(x-lock.x,y-lock.y)&&body.clip.contains(x-body.x,y-body.y)) {
      check(painted.image.getRGB(x,y)==expected.image.getRGB(x,y),"refrigerator covers padlock at "+x+","+y);
      check(h.targetAt(x,y)==33,"visible padlock not selectable");pixels++;
     }
   check(pixels>0,"padlock regression never overlapped refrigerator");
   capture(h,640,480,path+"-lock-world.png");
  }finally{scene.close();}
 }
 public static void main(String[] a)throws Exception {
  occlusion();
  int width=Integer.parseInt(a[0]),height=Integer.parseInt(a[1]);
  entryAndLock(width,height,a[2]);
  for(int n=8;n<=9;n++){Scene scene=new Scene(n);GameState state=new GameState();state.scene=n;
   try{HotelScene hotel=new HotelScene(scene,state);if(n==8){for(int i=0;i<110;i++)hotel.update(100);capture(hotel,width,height,a[2]+"-entrance.png");settle(hotel);}
   else{settle(hotel);capture(hotel,width,height,a[2]+"-room.png");capture(hotel,640,480,a[2]+"-world.png");
    noKeyHit(hotel);
    act(hotel,26);settle(hotel);check(hotel.logic.hasItem(1),"cream pickup");hotel.logic.select(1);hotel.logic.selectItem(1);act(hotel,29);settle(hotel);
    noKeyHit(hotel);hotel.logic.select(0);act(hotel,27);settle(hotel);hotel.logic.select(1);hotel.logic.selectItem(0);act(hotel,34);settle(hotel);check(hotel.logic.hasItem(2),"key pickup");hotel.logic.select(1);act(hotel,31);settle(hotel);act(hotel,25);settle(hotel);check(hotel.logic.hasItem(3),"screwdriver pickup");capture(hotel,width,height,a[2]+"-inventory.png");
    hotel.logic.select(0);hotel.logic.selectItem(2);act(hotel,33);settle(hotel);hotel.logic.selectItem(0);act(hotel,101);settle(hotel);
    check(hotel.animals.world.fridgeRequested(),"detail request did not finish approach");
    System.setProperty("pilots.test.width",String.valueOf(width));System.setProperty("pilots.test.height",String.valueOf(height));
    final HotelWorld world=hotel.animals.world;
    final int[] returned={0};
    FridgeCanvas detail=new FridgeCanvas(world,new FridgeCanvas.Host(){public void back(){returned[0]++;}public void active(boolean value){}});
    Graphics detailGraphics=new Graphics(width,height);detail.paint(detailGraphics);ImageIO.write(detailGraphics.image,"png",new File(a[2]+"-detail.png"));
    int mask=world.fridgeMask(),solution=-1;for(int c=0;c<512;c++){int value=mask;for(int i=0;i<9;i++)if((c&(1<<i))!=0)value=RefrigeratorPuzzle.turn(value,i);if(value==0){solution=c;break;}}
    check(solution>=0,"render puzzle solution");int selected=0;
    for(int i=0;i<9;i++)if((solution&(1<<i))!=0){
     while(selected/3<i/3){detail.input(56);selected+=3;}
     while(selected%3<i%3){detail.input(54);selected++;}
     while(selected%3>i%3){detail.input(52);selected--;}
     detail.input(53);
    }
    check(world.fridgeMask()==0&&returned[0]==1&&!world.fridgeRequested(),"solved detail must return automatically");
    detail.input(53);detail.input(48);check(returned[0]==1,"closed detail received duplicate input");

    // Touch completion uses the same guarded return path as keyboard Fire.
    GameState touchState=new GameState();touchState.scene=9;HotelWorld touchWorld=new HotelWorld(touchState);
    touchState.variables[59]=2;touchState.variables[61]=RefrigeratorPuzzle.turn(0,4);
    final int[] touchReturn={0};
    FridgeCanvas touch=new FridgeCanvas(touchWorld,new FridgeCanvas.Host(){public void back(){touchReturn[0]++;}public void active(boolean value){}});
    int board=Math.max(3,Math.min(width-8,height-46)/3*3),top=17+Math.max(0,(height-46-board)/2);
    touch.pointerPressed(width/2,top+board/2);
    check(touchReturn[0]==1&&touchWorld.fridgeMask()==0&&!touchWorld.fridgeRequested(),"touch solution must return automatically");
    for(int i=0;i<200;i++)hotel.update(100);settle(hotel);
    capture(hotel,640,480,a[2]+"-solved-world.png");
    // Reach a real walkable cell beside the fridge before launching its movie.
    // This forces the partner's approach to cross the open-door silhouette.
    hotel.logic.select(1);hotel.logic.selectItem(0);check(world.move(576,320),"fridge approach staging");settle(hotel);
    hotel.logic.select(0);act(hotel,101);
    int budget=1000,doorPixels=0,exitFrames=0;boolean capturedDoor=false,capturedExit=false;
    while(!world.completed&&budget-->0) {
     int bodySlot=slot(hotel,61);AnimatedActor door=scene.actors[bodySlot],partner=scene.actors[301];HotelWorld.Thing t=world.heroes[1];
     if(world.action()==84&&partner!=null&&t.clip>=26&&t.clip<=33&&hotel.layer(bodySlot)==15&&hotel.layer(301)<hotel.layer(bodySlot)) {
      Graphics painted=worldPaint(hotel),expected=new Graphics(640,417);partner.paint(expected,new Viewport(640,417,640,417));
      for(int y=Math.max(0,partner.y+partner.clip.offsetY);y<Math.min(417,partner.y+partner.clip.offsetY+partner.clip.height);y++)
       for(int x=Math.max(448,partner.x+partner.clip.offsetX);x<Math.min(640,partner.x+partner.clip.offsetX+partner.clip.width);x++)
        if(partner.clip.contains(x-partner.x,y-partner.y)&&door.clip.contains(x-door.x,y-door.y)) {
         check(painted.image.getRGB(x,y)==expected.image.getRGB(x,y),"open fridge door cuts walking partner");doorPixels++;
        }
      if(doorPixels>0&&!capturedDoor){capture(hotel,640,480,a[2]+"-door-walk-world.png");capturedDoor=true;}
     }
     if(world.state.variables[63]==1&&t.active&&t.clip>=26&&t.clip<=33) {
      exitFrames|=1<<t.frame;
      if(!capturedExit){capture(hotel,640,480,a[2]+"-partner-exit-world.png");capturedExit=true;}
     }
     hotel.update(100);
    }
    check(budget>0&&world.completed&&doorPixels>0&&exitFrames==31,"movie/door/partner exit coverage");
    check(scene.actors[300]==null&&scene.actors[301]==null,"visible hero left after finale");
    for(HotelWorld.Thing t:world.cells)check(t==null||t.id!=97,"frozen final pose left in room");
    capture(hotel,width,height,a[2]+"-finale.png");}}
   finally{scene.close();}
  }
  System.out.println("HotelRender: entrance, room, source layers, opaque pickup hotspots and inventory painted");
 }
}
