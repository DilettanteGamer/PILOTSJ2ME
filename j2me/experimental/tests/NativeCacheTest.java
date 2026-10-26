package pilots;
import java.lang.reflect.*;
/** Exercise real walking cycles; enforce total retained bytes, not per-slot claims. */
public final class NativeCacheTest {
 static int total(Class type,String field)throws Exception{Field f=type.getDeclaredField(field);f.setAccessible(true);int sum=0;for(byte[] b:(byte[][])f.get(null))if(b!=null)sum+=b.length;return sum;}
 static void bounds()throws Exception{if(total(TileStore.class,"frames")>196608||total(PackedResources.class,"data")>32768||total(PackedResources.class,"metas")>32768)throw new Exception("Cache budget exceeded");Field f=TileStore.class.getDeclaredField("banks");f.setAccessible(true);int sum=0,max=0;for(byte[] b:(byte[][])f.get(null))if(b!=null){sum+=b.length;max=Math.max(max,b.length);}if(sum>Math.max(131072,max))throw new Exception("Bank budget exceeded");}
 public static void main(String[] args)throws Exception{AnimationClip a=new AnimationClip("/scenes/02-20032.anm"),b=new AnimationClip("/scenes/02-20028.anm");for(int i=0;i<50;i++){if(!a.advance())a.restart();if(!b.advance())b.restart();bounds();}int loaded=TileStore.loads,direct=TileStore.directLoads;for(int i=0;i<100;i++){if(!a.advance())a.restart();if(!b.advance())b.restart();bounds();}if(TileStore.loads!=loaded||TileStore.directLoads!=direct)throw new Exception("Walking cycles still reload banks");a.close();b.close();System.out.println("Native caches: two warmed walking cycles cause zero bank reloads; all byte budgets enforced");}
}
