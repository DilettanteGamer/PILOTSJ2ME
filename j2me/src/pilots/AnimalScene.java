package pilots;
import java.io.IOException;
import javax.microedition.lcdui.Graphics;

/** Source object roots and layers, with the existing viewport and panel. */
public final class AnimalScene extends ZooScene {
    public final AnimalLogic animals;
    private final int[] order=new int[310];
    private long elapsed;
    public AnimalScene(Scene scene,GameState state) throws IOException {
        super(scene,state,new AnimalLogic(state),false);animals=(AnimalLogic)logic;
        scene.close();scene.actors=new AnimatedActor[310];refresh();
    }
    private AnimalWorld.Thing thing(int slot) { return slot<300?animals.world.cells[slot]:slot<302?animals.world.heroes[slot-300]:animals.world.effects[slot-302]; }
    private void refresh() throws IOException {
        for(int i=0;i<scene.actors.length;i++) {
            AnimalWorld.Thing t=thing(i);AnimatedActor actor=scene.actors[i];
            if(t==null || !t.active || t.clip<0 || t.y>=417) {
                if(actor!=null) { actor.close();scene.actors[i]=null; }continue;
            }
            int id=t.clip<46?20000+t.clip:t.clip;
            if(actor==null || actor.id!=id || actor.clip.index>t.frame) {
                AnimatedActor next=new AnimatedActor(id,"/scenes/04-"+id+".anm",t.x,t.y);
                if(actor!=null) actor.close();scene.actors[i]=actor=next;
            }
            while(actor.clip.index<Math.min(t.frame,actor.clip.count-1)) actor.clip.advance();
            actor.x=t.x;actor.y=t.y;actor.playing=false;actor.visible=true;
        }
    }
    public int layer(int slot) { AnimalWorld.Thing t=thing(slot);return t==null?0:t.layer; }
    public void paint(Graphics graphics,Viewport view) {
        int n=0;
        for(int i=0;i<scene.actors.length;i++) if(scene.actors[i]!=null) {
            int j=n;while(j>0 && layer(order[j-1])<layer(i)) { order[j]=order[j-1];j--; }order[j]=i;n++;
        }
        for(int i=0;i<n;i++) scene.actors[order[i]].paint(graphics,view);
    }
    private boolean interactive(int id) { return id==13 || id>=17&&id<=29; }
    private int target(int x,int y) {
        int hit=-1,best=999;
        for(int i=0;i<300;i++) {
            AnimalWorld.Thing t=thing(i);AnimatedActor actor=scene.actors[i];
            if(t!=null && interactive(t.id) && actor!=null && actor.clip.contains(x-actor.x,y-actor.y) && t.layer<=best) { hit=t.id;best=t.layer; }
        }
        return hit;
    }
    public String hoverText(int x,int y) {
        int id=target(x,y);
        return id==13?"Коллега":id==17?"Злой носорог":id==18||id==29?"Бананы":id==19||id==20?"Кенгуру":id==21?"Звериная тропа":id==22||id==28?"Пальма":id==23?"Фонтан":id==24?"Кран":id==25?"Секатор":id==26?"Грабли":id==27?"Лейка":null;
    }
    public boolean request(int x,int y) throws IOException {
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
