package pilots;
import java.io.*;import java.util.*;
/** Interleave two live characters while loading and advancing new action clips. */
public final class ActionLatencyTest {
 static int decodes(){try{return Class.forName("pilots.PackedResources").getField("decodes").getInt(null);}catch(Throwable missing){return 0;}}
 static double ms(long ns){return Math.round(ns/1000.0)/1000.0;}
 public static void main(String[] args)throws Exception{
  AnimationClip a=new AnimationClip("/scenes/02-20032.anm"),b=new AnimationClip("/scenes/02-20028.anm");for(int i=0;i<30;i++){if(!a.advance())a.restart();if(!b.advance())b.restart();}
  BufferedReader input=new BufferedReader(new FileReader(args[0]));List<Double> first=new ArrayList<Double>(),frames=new ArrayList<Double>();String path;long start=System.nanoTime();int actions=0;int initial=decodes();
  while((path=input.readLine())!=null){long begin=System.nanoTime();AnimationClip action=new AnimationClip("/"+path);first.add(ms(System.nanoTime()-begin));for(int j=0;j<7;j++){long t=System.nanoTime();if(!a.advance())a.restart();if(!b.advance())b.restart();if(!action.advance())break;frames.add(ms(System.nanoTime()-t));}action.close();actions++;}
  Collections.sort(first);Collections.sort(frames);System.out.println("{\"actions\":"+actions+",\"total_ms\":"+ms(System.nanoTime()-start)+",\"first_frame_p50_ms\":"+first.get(first.size()/2)+",\"first_frame_p95_ms\":"+first.get(first.size()*95/100)+",\"first_frame_max_ms\":"+first.get(first.size()-1)+",\"interleaved_frame_p95_ms\":"+frames.get(frames.size()*95/100)+",\"lzma_blocks\":"+(decodes()-initial)+"}");a.close();b.close();
 }
}
