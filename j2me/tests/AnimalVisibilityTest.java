package pilots;

import java.io.*;

/** Physical pickup hit testing, visible transitions and original banana cooperation. */
public final class AnimalVisibilityTest {
    static GameState state;
    static Scene scene;
    static AnimalScene location;
    static void check(boolean ok,String text) { if(!ok) throw new RuntimeException(text); }
    static AnimalWorld world() { return location.animals.world; }
    static void visible() {
        for(int h=0;h<2;h++) if(world().heroes[h].active && world().heroes[h].clip>=0)
            check(scene.actors[300+h]!=null && scene.actors[300+h].visible,"active character lost its scene actor");
    }
    static void settle() throws Exception {
        int budget=2500;
        while(location.logic.busy() && budget-->0) {
            int action=world().action();location.update(100);visible();
            if(action==51 || action==52 || action==55 || action==59 || action==62)
                for(int h=0;h<2;h++) check(world().heroes[h].active && world().heroes[h].clip>=0 && scene.actors[300+h]!=null,"pickup-to-idle transition hid hero "+h);
        }
        check(budget>0,"UI animal sequence hangs");
        for(int h=0;h<2;h++) if(world().heroes[h].active)
            check(world().heroes[h].clip>=0 && scene.actors[300+h]!=null,"idle hero vanished");
    }
    static int hit(int x,int y) {
        try {
            java.lang.reflect.Method method=AnimalScene.class.getDeclaredMethod("target",new Class[]{Integer.TYPE,Integer.TYPE});
            method.setAccessible(true);return ((Integer)method.invoke(location,new Object[]{Integer.valueOf(x),Integer.valueOf(y)})).intValue();
        } catch(Exception e) { throw new RuntimeException(e); }
    }
    static int[] point(int id) {
        for(int slot=0;slot<302;slot++) {
            AnimalWorld.Thing t=slot<300?world().cells[slot]:world().heroes[slot-300];
            AnimatedActor actor=scene.actors[slot];
            if(t==null || t.id!=id || actor==null) continue;
            AnimationClip clip=actor.clip;
            for(int y=Math.max(0,actor.y+clip.offsetY);y<Math.min(417,actor.y+clip.offsetY+clip.height);y++)
                for(int x=Math.max(0,actor.x+clip.offsetX);x<Math.min(640,actor.x+clip.offsetX+clip.width);x++)
                    if(clip.contains(x-actor.x,y-actor.y) && hit(x,y)==id) return new int[]{x,y};
        }
        throw new RuntimeException("No visible clickable pixel for entity "+id);
    }
    static void click(int id) throws Exception {
        int[] p=point(id);check(location.request(p[0],p[1]),"scene rejected target "+id);settle();
    }
    static void act(int hero,int item,int entity) throws Exception {
        check(location.logic.select(hero),"hero selection failed");check(location.logic.selectItem(item),"item selection failed");click(entity);
    }
    static void save() throws Exception {
        world().syncSave();state=GameState.decode(state.encode(),20);location=new AnimalScene(scene,state);visible();
    }
    static void prepare() throws Exception {
        state=new GameState();state.scene=4;scene=new Scene(4);location=new AnimalScene(scene,state);settle();
        act(0,0,27);act(1,0,26);act(0,0,25);
        for(int i=0;i<5;i++) { location.update(100);visible(); }
        act(0,1,22);
        act(1,0,18);check(!world().busy(),"first banana attempt did not finish");
        act(0,2,21);
        check(world().select(0) && world().move(448,208),"Chief returns to original cooperation mark");settle();
        act(1,0,18);
        check(world().g(0x18)==3 && !world().heroes[1].active,"original banana interaction did not create tree form");
        check(point(13)!=null && point(29)!=null,"tree form or bananas cannot be selected");save();
    }
    static void legacy(String name,boolean kettle) throws Exception {
        FileInputStream in=new FileInputStream(name);ByteArrayOutputStream out=new ByteArrayOutputStream();
        try { byte[] block=new byte[1024];int n;while((n=in.read(block))!=-1) out.write(block,0,n); } finally { in.close(); }
        state=GameState.decode(out.toByteArray(),20);scene=new Scene(4);
        try {
            location=new AnimalScene(scene,state);visible();
            check(world().hasItem(1)==kettle,"legacy inventory changed");
            for(int h=0;h<2;h++) check(world().heroes[h].active && world().heroes[h].clip>=0 && scene.actors[300+h]!=null,"legacy idle hero was not restored");
            save();check(world().hasItem(1)==kettle,"legacy-to-current save round trip");
        } finally { scene.close(); }
    }
    public static void main(String[] args) throws Exception {
        prepare();scene.close();
        legacy(args[0],false);legacy(args[1],true);
        System.out.println("AnimalVisibility: physical pickups, visible transitions, original banana cooperation, tree form and original AWS1 saves passed");
    }
}
