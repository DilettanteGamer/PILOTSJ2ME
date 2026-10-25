package pilots;
/** Play the final puzzle through ordinary character, inventory and scene actions. */
public final class FinalTestRoute {
 public interface Driver {HotelWorld world();void tick()throws Exception;void act(int id)throws Exception;void move(int x,int y)throws Exception;void milestone(String name)throws Exception;}
 static void check(boolean b,String s){if(!b)throw new RuntimeException(s);}
 static void settle(Driver d)throws Exception{int n=2200;while(d.world().busy()&&n-->0)d.tick();check(n>0,"final lock "+d.world().action()+"/"+d.world().step());}
 static void act(Driver d,int id)throws Exception{d.act(id);d.tick();settle(d);}
 static void hero(Driver d,int h)throws Exception{check(d.world().select(h),"final character "+h);}
 static void item(Driver d,int i)throws Exception{check(d.world().selectItem(i),"final item "+i);}
 public static void play(Driver d)throws Exception{
  settle(d);check(d.world().heroes[0]==null&&d.world().heroes[1]==null,"terrain mistaken for characters");d.milestone("entry");
  act(d,27);check(d.world().hasItem(1),"stool pickup");item(d,1);act(d,29);check(!d.world().hasItem(1),"stool not placed");item(d,0);act(d,28);
  check(d.world().flag(0x1e1b57)==1,"Chief stool climb");d.move(0,240);d.tick();settle(d);act(d,33);d.milestone("bird");
  hero(d,1);act(d,35);check(d.world().hasItem(2),"dropped flute pickup");act(d,28);act(d,30);item(d,2);act(d,32);
  check(d.world().flag(0x1e1b5e)==1&&d.world().flag(0x1e1b5d)==1,"flute did not free servant/open hollow");item(d,0);act(d,30);check(d.world().hasItem(4),"hollow flute");d.milestone("hollow");
  hero(d,0);act(d,33);check(d.world().flag(0x1e1b57)==3,"servant climb");act(d,36);check(d.world().hasItem(3),"tree flute");
  for(int i=2;i<=4;i++){item(d,i);act(d,31);check(!d.world().hasItem(i)&&d.world().flag(0x1e1b64)==i-1,"flute ladder rung "+i);}
  item(d,0);act(d,31);check(d.world().flag(0x1e1b57)==4,"ladder climb");act(d,37);check(d.world().hasItem(5),"last flute");d.milestone("last-flute");
  item(d,5);act(d,26);check(d.world().completed&&d.world().flag(0x1e1b5c)==12,"original final scene did not finish");d.milestone("completed");
 }
}
