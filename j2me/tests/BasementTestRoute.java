package pilots;
/** Real controls only, including waiting for release before retreating to safety. */
public final class BasementTestRoute {
 public interface Driver{HotelWorld world();void press(int key)throws Exception;void ticks(int n)throws Exception;}
 static void check(boolean b,String m){if(!b)throw new RuntimeException(m);}
 public static void act(Driver d,int key,int ticks)throws Exception{d.press(key);d.ticks(ticks);}
 public static void go(Driver d,int column)throws Exception{
  if(!d.world().basement.safe())act(d,8,4);
  while(d.world().basement.column()<column)act(d,6,4);
  while(d.world().basement.column()>column)act(d,4,4);
 }
 public static void expose(Driver d)throws Exception{act(d,2,12);check(!d.world().basement.safe(),"unsafe approach failed");act(d,8,10);}
 public static void fire(Driver d)throws Exception{check(d.world().basement.hasGun(),"route needs gun");act(d,5,BasementGame.SHOT_TICKS);}
 private static void entry(Driver d)throws Exception{go(d,3);while(d.world().basement.crates(3)>1)fire(d);}
 private static void grow(Driver d,int column)throws Exception{
  entry(d);act(d,2,4);for(int c=2;c>column;c--)act(d,4,4);
  d.ticks(22-4*(3-column));check(d.world().basement.fallingColumn()<0,"previous crate still airborne");
  act(d,4,12);act(d,8,10);
 }
 public static void stairs(Driver d)throws Exception{
  for(int c=0;c<3;c++){go(d,c);while(d.world().basement.crates(c)<2)expose(d);while(d.world().basement.crates(c)>2)fire(d);}
  go(d,3);while(d.world().basement.crates(3)<1)expose(d);while(d.world().basement.crates(3)>1)fire(d);
  grow(d,1);grow(d,0);grow(d,0);entry(d);
  for(int c=0;c<4;c++)check(d.world().basement.crates(c)==4-c,"stairs could not be built at "+c);
 }
 public static void finish(Driver d)throws Exception{act(d,2,4);for(int i=0;i<3;i++)act(d,4,4);check(d.world().basement.solved(),"route did not finish");}
}
