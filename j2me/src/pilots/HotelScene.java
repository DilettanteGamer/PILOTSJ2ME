package pilots;
import java.io.IOException;
import javax.microedition.lcdui.Graphics;

/** Source object roots and layers, with the existing viewport and panel. */
public final class HotelScene extends ZooScene {
    public final HotelLogic animals;
    private final int[] order=new int[310];
    private long elapsed;
    public HotelScene(Scene scene,GameState state) throws IOException {
        super(scene,state,new HotelLogic(state),false);animals=(HotelLogic)logic;
        scene.close();scene.actors=new AnimatedActor[310];refresh();
    }
    private HotelWorld.Thing thing(int slot) { return slot<300?animals.world.cells[slot]:slot<302?animals.world.heroes[slot-300]:animals.world.effects[slot-302]; }
    private void refresh() throws IOException {
        for(int i=0;i<scene.actors.length;i++) {
            HotelWorld.Thing t=thing(i);AnimatedActor actor=scene.actors[i];
            if(t==null || !t.active || t.clip<0 || t.y>=417) {
                if(actor!=null) { actor.close();scene.actors[i]=null; }continue;
            }
            int id=t.clip<46?20000+t.clip:t.clip;
            if(actor==null || actor.id!=id || actor.clip.index>t.frame) {
                AnimatedActor next=new AnimatedActor(id,"/scenes/0"+animals.world.scene+"-"+id+".anm",t.x,t.y);
                if(actor!=null) actor.close();scene.actors[i]=actor=next;
            }
            while(actor.clip.index<Math.min(t.frame,actor.clip.count-1)) actor.clip.advance();
            actor.x=t.x;actor.y=t.y;
            if(i>=300 && i<302 && t.clip>=20 && t.clip<=33 && !t.finished) {
                int progress=t.frame+1;
                actor.x=t.fromX+(t.x-t.fromX)*progress/5;actor.y=t.fromY+(t.y-t.fromY)*progress/5;
            }
            actor.playing=false;actor.visible=true;
        }
    }
    public int layer(int slot) { HotelWorld.Thing t=thing(slot);return t==null?0:t.layer; }
    public void paint(Graphics graphics,Viewport view) {
        int n=0;
        for(int i=0;i<scene.actors.length;i++) if(scene.actors[i]!=null) {
            int j=n;while(j>0 && layer(order[j-1])<layer(i)) { order[j]=order[j-1];j--; }order[j]=i;n++;
        }
        for(int i=0;i<n;i++) scene.actors[order[i]].paint(graphics,view);
    }
    private boolean interactive(int id) { return id==12 || id==13 || animals.world.interactive(id); }
    private int target(int x,int y) {
        int hit=-1,best=999;
        for(int i=0;i<302;i++) {
            HotelWorld.Thing t=thing(i);AnimatedActor actor=scene.actors[i];
            if(t!=null && interactive(t.id) && actor!=null && actor.clip.contains(x-actor.x,y-actor.y) && t.layer<=best) { hit=t.id;best=t.layer; }
        }
        return hit;
    }
    public int targetAt(int x,int y) { int id=target(x,y);return id<0?0:id; }
    public String hoverText(int x,int y) {
        int id=target(x,y);
        return id==13?"Шеф":id==12?"Коллега":id==20||id==21?"Выход":id==22?"Штора":id==23||id==24?"Куча":id==25?"Отвёртка":id==26?"Баллончик":id==27?"Злобное чудище":id==28||id==29?"Ванная":id==30?"Чайник":id==31||id==32?"Сахарница":id==33?"Замок":id==34?"Ключ":id==35?"Ручка":id==36||id==37?"Включатель":id>=64&&id<=95?"Переключатель":null;
    }
    public boolean request(int x,int y) throws IOException {
        if(animals.world.busy()) return false;
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
