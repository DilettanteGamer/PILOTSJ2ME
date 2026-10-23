package pilots;
import java.io.BufferedReader;
import java.io.FileReader;
public final class OriginalRoutingTest {
    public static void main(String[] args) throws Exception {
        BufferedReader input=new BufferedReader(new FileReader(args[0]));String line;int count=0;
        try { while((line=input.readLine())!=null) {
            String[] parts=line.split(",");int[] v=new int[parts.length];
            for(int i=0;i<v.length;i++) v[i]=Integer.parseInt(parts[i]);
            if(OriginalZooRules.route(v[0],v[1],v[2],v[3])!=v[4]) throw new RuntimeException("Original route mismatch "+line);
            count++;
        }} finally { input.close(); }
        if(count!=1024) throw new RuntimeException("Missing full-location cases");
        if(ZooWalk.plan(64,320,192,304,false).length==0) throw new RuntimeException("route around barrel");
        if(ZooWalk.plan(384,304,448,240,false).length!=0) throw new RuntimeException("closed gate must block exit");
        if(ZooWalk.plan(384,304,448,240,true).length==0) throw new RuntimeException("open gate route");
        System.out.println("OriginalRouting: 1024 source object/item/hero/state combinations and walk boundaries passed");
    }
}
