package pilots;
/** Resource work for the same complete first-location route, not a desktop FPS claim. */
public final class SceneRouteWorkloadTest {
 public static void main(String[] args)throws Exception{int calls=TileStore.frameCalls,banks=TileStore.loads,opens=PackedResources.opens;long start=System.nanoTime();FirstLocationSceneTest.main(new String[0]);System.out.println("first_location_route decoded_frames="+(TileStore.frameCalls-calls)+" bank_loads="+(TileStore.loads-banks)+" resource_opens="+(PackedResources.opens-opens)+" ms="+(System.nanoTime()-start)/1000000);}
}
