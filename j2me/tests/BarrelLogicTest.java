package pilots;

import java.io.BufferedReader;
import java.io.FileReader;

public final class BarrelLogicTest {
    private static void check(boolean ok, String message) {
        if (!ok) throw new RuntimeException(message);
    }
    public static void main(String[] args) throws Exception {
        BufferedReader input = new BufferedReader(new FileReader(args[0]));
        int cases = 0;
        String line;
        try {
            while ((line = input.readLine()) != null) {
                String[] fields = line.split(","); int[] v = new int[fields.length];
                for (int i=0;i<v.length;i++) v[i]=Integer.parseInt(fields[i]);
                GameState state = new GameState(); state.scene=2;
                ZooLogic logic = new ZooLogic(state);
                state.inventory[0]=v[2]==1; state.variables[40]=v[3]; state.variables[61]=v[4];
                state.variables[51]=state.variables[46]=v[5];
                logic.select(v[1]); logic.selectKettle(v[2]==1);
                int expected=v[6] != 0 && v[6]<46 ? 20000+v[6] : v[6];
                check(logic.begin(v[0])==expected,"Original interaction mismatch: "+line);
                if (expected != 0) {
                    check(logic.endStep()==v[7],"Original completion mismatch");
                    check(!logic.selectKettle(false),"cannot change item during action");
                    for (int step=2;step<=v[7];step++) {
                        logic.advance();
                        if (expected==75 || expected==76)
                            check(logic.hasKettle()==(step >= (expected==75?8:4)),"pickup event timing");
                        if (expected==69) check(logic.catPresent()==(step>=25),"cat spawn timing");
                    }
                    check(!logic.busy(),"action completion");
                    if(v[2]==1) check(logic.hasKettle(),"kettle must not be consumed");
                } else check(!logic.busy(),"unmatched source rule cannot start an action");
                cases++;
            }
        } finally { input.close(); }
        check(cases==26,"missing source interaction cases");
        input=new BufferedReader(new FileReader(args[1])); cases=0;
        try {
            while ((line=input.readLine())!=null) {
                String[] fields=line.split(","); int[] v=new int[fields.length];
                for(int i=0;i<v.length;i++) v[i]=Integer.parseInt(fields[i]);
                GameState state=new GameState(); ZooLogic logic=new ZooLogic(state);
                state.variables[40]=v[0]==0?1:v[0]==1?2:3;
                state.variables[41]=v[1]-1; state.variables[44]=68;
                state.variables[62]=v[0]==1?2:0;
                logic.advanceCat();
                if(v[2]!=0) check(logic.catClip()==v[2],"source cat clip: "+line);
                if(v[3]==1) check(logic.crowState()==0,"source crow removal");
                if(v[4]==1) check(logic.butterflyState()==1,"source butterfly event");
                if(v[5]==1) check(!logic.catPresent(),"metadata cleanup allows retry");
                cases++;
            }
        } finally { input.close(); }
        check(cases==7,"missing source cat events");
        GameState state=new GameState(); ZooLogic logic=new ZooLogic(state);
        check(!logic.selectKettle(true),"unowned item is unavailable");
        state.variables[40]=1;
        for(int i=0;i<47;i++) logic.advanceCat();
        check(!logic.catPresent() && logic.butterflyState()==0,"wrong order cleans up without awarding butterfly");
        check(state.encode().length==299,"save compatibility");
        System.out.println("BarrelLogic: 26 source interaction cases, 7 source cat events and retry passed");
    }
}
