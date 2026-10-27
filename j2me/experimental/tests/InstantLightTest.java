package pilots;
/** Six original light-off controller stages stay in the save; none creates an image actor. */
public final class InstantLightTest {
 public static void main(String[] args)throws Exception{
  GameState state=new GameState();state.scene=13;Scene room=new Scene(13);HotelScene h=new HotelScene(room,state);boolean[] stages=new boolean[6];int hidden=0;
  try{
   for(int tick=0;tick<600;tick++){
    h.update(100);HotelWorld w=h.animals.world;
    for(int i=0;i<room.actors.length;i++){
     HotelWorld.Thing t=i<300?w.cells[i]:i<302?w.heroes[i-300]:w.effects[i-302];
     if(t!=null&&t.active&&t.clip>=205&&t.clip<=210){stages[t.clip-205]=true;hidden++;if(room.actors[i]!=null)throw new Exception("Light-off mask was loaded instead of exposing dark background");}
    }
   }
   for(boolean stage:stages)if(!stage)throw new Exception("Original light-off controller stage disappeared");
   if(hidden!=6)throw new Exception("Light-off controller timing changed: "+hidden);
   System.out.println("Instant electrical darkness: six original controller stages preserved; zero fade actors allocated; existing dark background exposed from first stage");
  }finally{room.close();}
 }
}
