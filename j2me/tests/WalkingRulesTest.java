package pilots;
import java.io.BufferedReader;
import java.io.FileReader;

/** Compare gait halves, facing and branch with executed original actor rules. */
public final class WalkingRulesTest {
    public static void main(String[] args) throws Exception {
        BufferedReader input=new BufferedReader(new FileReader(args[0]));String line;int count=0;
        try { while((line=input.readLine())!=null) {
            String[] parts=line.split(",");int[] v=new int[parts.length];
            for(int i=0;i<v.length;i++) v[i]=Integer.parseInt(parts[i]);
            GameState state=new GameState();state.scene=2;ZooLogic logic=new ZooLogic(state);logic.select(v[0]);
            logic.queueWalk(100,400,368);
            int dx=v[1]==1?-8:v[1]==2?8:0,dy=v[1]==3?-8:v[1]==4?8:0;
            for(int i=0;i<=v[2]*5;i++) logic.walkStep(dx,dy);
            if(logic.heroClip(v[0])!=20000+v[3]) throw new RuntimeException("Source gait mismatch "+line);
            if(state.variables[v[0]==0?51:46]!=v[5]) throw new RuntimeException("Source gait branch mismatch "+line);
            logic.finishWalk();int pose=v[4]==1?0:v[4]==3?1:v[4]==7?2:3;
            if(logic.heroClip(v[0])!=20000+(v[0]==0?12:16)+pose) throw new RuntimeException("Source idle facing mismatch "+line);
            count++;
        }} finally { input.close(); }
        if(count!=16) throw new RuntimeException("Missing walking source cases");
        System.out.println("WalkingRules: 16 source gait halves, idle poses and branch states passed");
    }
}
