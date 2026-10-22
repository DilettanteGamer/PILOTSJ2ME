package pilots;
import java.io.BufferedReader;
import java.io.FileReader;

/** Compare piano behavior with executed source predicates, not a copied port table. */
public final class CourtyardRulesTest {
    public static void main(String[] args) throws Exception {
        BufferedReader input=new BufferedReader(new FileReader(args[0]));String line;int count=0;
        try { while((line=input.readLine())!=null) {
            String[] fields=line.split(",");int[] v=new int[5];
            for(int i=0;i<5;i++) v[i]=Integer.parseInt(fields[i]);
            GameState state=new GameState();state.scene=3;CourtyardLogic logic=new CourtyardLogic(state);
            state.variables[0]=v[1];logic.select(v[0]);logic.begin(CourtyardLogic.PIANO);
            if(logic.pendingAction()!=v[2] || logic.pendingClip()!=v[3] || logic.endStep()!=v[4])
                throw new RuntimeException("Original courtyard action mismatch "+line);
            while(logic.step()<v[4]-1) logic.advance();
            if(!logic.busy()) throw new RuntimeException("Premature courtyard release "+line);
            logic.advance();if(logic.busy()) throw new RuntimeException("Missing courtyard release "+line);
            count++;
        }} finally { input.close(); }
        if(count!=6) throw new RuntimeException("Missing courtyard source cases");
        System.out.println("CourtyardRules: 6 source piano actions, clips and completion counters passed");
    }
}
