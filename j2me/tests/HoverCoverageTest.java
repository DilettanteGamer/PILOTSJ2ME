package pilots;
import java.util.*;import java.io.*;
public final class HoverCoverageTest{
 static ZooScene create(Scene s,GameState g)throws Exception{return g.scene==2?new ZooScene(s,g):g.scene==3?new CourtyardScene(s,g):g.scene==4?new AnimalScene(s,g):g.scene==5?new GardenScene(s,g):g.scene==6?new StreetScene(s,g):new HotelScene(s,g);}
 public static void main(String[] args)throws Exception{
  Map<Integer,Map<Integer,String>> report=new LinkedHashMap<Integer,Map<Integer,String>>();
  for(int n:new int[]{2,3,4,5,6,9,10,11,12,13,15,14,17}){
   Scene scene=new Scene(n);try{GameState state=new GameState();state.scene=n;ZooScene world=create(scene,state);int ticks=1000;while(world.logic.busy()&&ticks-->0)world.update(100);
    Map<Integer,String> labels=new TreeMap<Integer,String>();
    for(int y=0;y<417;y+=4)for(int x=0;x<640;x+=4){int id=world.targetAt(x,y);if(id==0)continue;String name=world.hoverText(x,y);if(name==null||name.length()==0)throw new RuntimeException("Missing hover label in scene "+n+" target "+id+" at "+x+","+y);labels.put(id,name);}
    if(labels.isEmpty())throw new RuntimeException("No hover targets exercised in scene "+n);report.put(n,labels);
   }finally{scene.close();}
  }
  System.out.println("HoverCoverage: every visible cursor target named in all 13 interactive locations: "+report);
 }
}
