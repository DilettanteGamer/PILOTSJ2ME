package pilots;
import java.io.IOException;
import javax.microedition.lcdui.Graphics;

/** Source object roots and layers, with the existing viewport and panel. */
public final class GardenScene extends ZooScene {
    public final GardenLogic animals;
    private final int[] order=new int[310];
    private long elapsed;
    public GardenScene(Scene scene,GameState state) throws IOException {
        super(scene,state,new GardenLogic(state),false);animals=(GardenLogic)logic;
        scene.close();scene.actors=new AnimatedActor[310];refresh();
    }
    private GardenWorld.Thing thing(int slot) { return slot<300?animals.world.cells[slot]:slot<302?animals.world.heroes[slot-300]:animals.world.effects[slot-302]; }
    private void refresh() throws IOException {
        for(int i=0;i<scene.actors.length;i++) {
            GardenWorld.Thing t=thing(i);AnimatedActor actor=scene.actors[i];
            if(t==null || !t.active || t.clip<0 || t.y>=417) {
                if(actor!=null) { actor.close();scene.actors[i]=null; }continue;
            }
            int id=t.clip<46?20000+t.clip:t.clip;
            if(actor==null || actor.id!=id || actor.clip.index>t.frame) {
                AnimatedActor next=new AnimatedActor(id,"/scenes/05-"+id+".anm",t.x,t.y);
                if(actor!=null) actor.close();scene.actors[i]=actor=next;
            }
            while(actor.clip.index<Math.min(t.frame,actor.clip.count-1)) actor.clip.advance();
            actor.x=t.x;actor.y=t.y;
            if(t.id==14 && t.clip>=47 && t.clip<=50 && !t.finished) {
                int count=animals.world.frames[t.clip],progress=t.frame+1;
                actor.x=t.fromX+(t.x-t.fromX)*progress/count;actor.y=t.fromY+(t.y-t.fromY)*progress/count;
            }
            actor.playing=false;actor.visible=true;
        }
    }
    public int layer(int slot) { GardenWorld.Thing t=thing(slot);return t==null?0:t.layer; }
    public void paint(Graphics graphics,Viewport view) {
        int n=0;
        for(int i=0;i<scene.actors.length;i++) if(scene.actors[i]!=null) {
            int j=n;while(j>0 && layer(order[j-1])<layer(i)) { order[j]=order[j-1];j--; }order[j]=i;n++;
        }
        for(int i=0;i<n;i++) scene.actors[order[i]].paint(graphics,view);
    }
    private boolean interactive(int id) { return id==11 || id==12 || id==14; }
    private int target(int x,int y) {
        int hit=-1,best=999;
        for(int i=0;i<302;i++) {
            GardenWorld.Thing t=thing(i);AnimatedActor actor=scene.actors[i];
            if(t!=null && interactive(t.id) && actor!=null && actor.clip.contains(x-actor.x,y-actor.y) && t.layer<=best) { hit=t.id;best=t.layer; }
        }
        return hit;
    }
    public int targetAt(int x,int y) { int id=target(x,y);return id<0?0:id; }
    public String hoverText(int x,int y) {
        int id=target(x,y);
        return id==11?"Шеф":id==12?"Коллега":id==14?"Мартышка с ключом":null;
    }
    public boolean request(int x,int y) throws IOException {
        if(animals.world.busy()) { boolean result=animals.world.interact(14);refresh();return result; }
        int id=target(x,y);boolean result=id>=0?animals.world.interact(id):animals.world.move(x,y);refresh();return result;
    }
    public boolean activate(int x,int y) throws IOException { return request(x,y); }
    public boolean update(long delta) throws IOException {
        if(delta<=0) return false;
        elapsed+=Math.min(delta,400L);boolean changed=false;
        while(elapsed>=100L) { elapsed-=100L;animals.world.tick();changed=true; }
        if(changed) refresh();return changed;
    }
}
