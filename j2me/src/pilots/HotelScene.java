package pilots;
import java.io.IOException;
import javax.microedition.lcdui.Graphics;

/** Source object roots and layers, with the existing viewport and panel. */
public final class HotelScene extends ZooScene {
    public final HotelLogic animals;
    private final int[] order=new int[310];
    private long elapsed;
    private int hitSlot=-1;
    private final RefrigeratorPuzzle handles;
    public HotelScene(Scene scene,GameState state) throws IOException {
        super(scene,state,new HotelLogic(state),false);animals=(HotelLogic)logic;
        handles=state.scene==9?new RefrigeratorPuzzle():null;
        scene.close();scene.actors=new AnimatedActor[310];refresh();
    }
    private HotelWorld.Thing thing(int slot) { return slot<300?animals.world.cells[slot]:slot<302?animals.world.heroes[slot-300]:animals.world.effects[slot-302]; }
    private void refresh() throws IOException {
        for(int i=0;i<scene.actors.length;i++) {
            HotelWorld.Thing t=thing(i);AnimatedActor actor=scene.actors[i];
            if(t==null || !t.active || t.clip<0 || t.y>=417 || hiddenGrid(t.id)) {
                if(actor!=null) { actor.close();scene.actors[i]=null; }continue;
            }
            int frame=animals.world.scene==15 && t.clip==80?(t.direction==1?0:t.direction==5?2:t.direction==7?3:1):t.frame;
            int id=t.clip<46?20000+t.clip:t.clip;
            if(actor==null || actor.id!=id || actor.clip.index>frame) {
                AnimatedActor next=new AnimatedActor(id,"/scenes/"+(animals.world.scene<10?"0":"")+animals.world.scene+"-"+id+".anm",t.x,t.y);
                if(actor!=null) actor.close();scene.actors[i]=actor=next;
            }
            while(actor.clip.index<Math.min(frame,actor.clip.count-1)) actor.clip.advance();
            actor.x=t.x;actor.y=t.y;
            if((i>=300 && i<302 || animals.world.scene==15 && t.waitTicks>0) && t.clip>=20 && t.clip<=33 && !t.finished) {
                int progress=t.frame+1;
                actor.x=t.fromX+(t.x-t.fromX)*progress/5;actor.y=t.fromY+(t.y-t.fromY)*progress/5;
            }
            actor.playing=false;actor.visible=true;
        }
    }
    public void refreshState() throws IOException { refresh(); }
    private boolean hiddenGrid(int id) { return animals.world.scene==9 && (id>=42 && id<=49 || id>=64 && id<=95); }
    private boolean authoredFinale() { return animals.world.action()==84 || animals.world.action()==87; }
    public int layer(int slot) {
        HotelWorld.Thing t=thing(slot);if(t==null)return 0;
        if(animals.world.scene==9) {
            // Normal room movement was normalized to 58-y/8. The final movie
            // uses absolute layers authored for the original 54-y/8 baseline.
            if(slot>=300 && slot<302 && authoredFinale())return t.layer-4;
            // These are wall/door cutouts, not foreground furniture. Their floor
            // plane is the bottom of the opaque mask; hero roots sit 16px above feet.
            if(animals.world.action()!=83 && !authoredFinale() && (t.id==51 || t.id==58 || t.id==59 || t.id==60)) {
                AnimatedActor a=scene.actors[slot];
                if(a!=null)return 58-(a.y+a.clip.offsetY+a.clip.height-16)/8;
            }
            // The lock and handle belong to the closed refrigerator's surface.
            if(t.id==33 || t.id==35)return 25;
            if(animals.world.action()!=83 && !authoredFinale() && (t.id==36 || t.id==37))return 22; // Switch mounted on wall mask 58.
        }
        if(animals.world.scene==12) {
            // Walking roots already hold the next grid cell. Sort the visible
            // intermediate pose on its current plane, preserving script offsets.
            AnimatedActor a=scene.actors[slot];
            if(slot>=300 && slot<302 && a!=null && t.clip>=20 && t.clip<=33 && !t.finished)
                return t.layer+t.y*3/16-a.y*3/16;
        }
        return t.layer;
    }
    private int paintDepth(int slot) {
        // Keep attachments in front of their supporting surface at equal depth,
        // without moving that surface across a neighbouring character plane.
        return layer(slot)*2+(animals.world.scene==9 && thing(slot).id==61?1:0);
    }
    public void paint(Graphics graphics,Viewport view) {
        int n=0;
        for(int i=0;i<scene.actors.length;i++) if(scene.actors[i]!=null) {
            int j=n;while(j>0 && paintDepth(order[j-1])<paintDepth(i)) { order[j]=order[j-1];j--; }order[j]=i;n++;
        }
        boolean grid=false;
        for(int i=0;i<n;i++) {
            if(!grid && (layer(order[i])<25 || layer(order[i])==25 && order[i]>=300)) { paintGrid(graphics,view);grid=true; }
            scene.actors[order[i]].paint(graphics,view);
            // Paint the panel directly on its refrigerator, before equal-depth
            // character reactions. A separate layer would cover those poses.
            if(!grid && thing(order[i]).id==61) { paintGrid(graphics,view);grid=true; }
        }
        if(!grid)paintGrid(graphics,view);
    }
    private void paintGrid(Graphics graphics,Viewport view) {
        if(!animals.world.fridgePanelVisible())return;
        handles.paint(graphics,view.screenX(434),view.screenY(195),Math.max(3,view.screenX(500)-view.screenX(434)),animals.world.fridgeMask(),-1);
    }
    private boolean interactive(int id) { return id==(animals.world.scene>=10?11:13) || id==12 || animals.world.interactive(id); }
    private int target(int x,int y) {
        int hit=-1,best=999;hitSlot=-1;
        for(int i=0;i<302;i++) {
            HotelWorld.Thing t=thing(i);AnimatedActor actor=scene.actors[i];
            if(t!=null && actor!=null && actor.clip.contains(x-actor.x,y-actor.y) && paintDepth(i)<=best) {
                hitSlot=i;hit=interactive(t.id) && (animals.world.scene!=9 || t.id!=34 || animals.world.keyAvailable())?t.id:-1;best=paintDepth(i);
            }
        }
        // Fixed union of all four hose poses, enlarged for keypad cursor steps.
        if(animals.world.roofHoseAvailable() && x>=144 && x<216 && y>=316 && y<390 && (hit<0 || hit==39))hit=39;
        if(animals.world.fridgePanelVisible() && x>=434 && x<500 && y>=195 && y<261 && (50<best || 50==best && hit!=12 && hit!=13))hit=101;
        return hit;
    }
    public int targetAt(int x,int y) { int id=target(x,y);return id<0?0:id; }
    public String hoverText(int x,int y) {
        // These authored clips include Colleague far above his walking root.
        // A cursor left on the cistern must not label the movie as a hero.
        if(animals.world.scene==14 && animals.world.busy())return null;
        if(animals.world.scene==12 && (animals.world.action()==95 || animals.world.action()==96))return null;
        int id=target(x,y);
        if(animals.world.scene==15 && animals.world.hero()==0 && x>=336 && y>=160 && y<417)return "Ящики: отдельный экран";
        if(animals.world.scene==14)return id==12||id==47?"Шеф":id==11||id==46?"Коллега":id==15?"Зелёная краска":id==16?"Красная краска":id==17||id==18?"Воздушный шар":id==19?"Зелёная краска":id==20?"Красная краска":id==21||id==23?"Лестница":id==22?"Килоперьев":id==25?"Фишка":id==31||id==28?"Ножницы":id==26?"Вытяжка":id==27?"Кран":id==35?"Верёвка":id==39?"Шланг":null;
        if(animals.world.scene==15)return id==14?(animals.world.hasItem(1)?"Ящик без автомата":"Ящик с автоматом"):id==24?"Ящик":id==31?"Электричество":id==33?"Автомат":id==15||id==12||id==13?"Коллега":id>=16&&id<=20?"Шеф":null;
        if(animals.world.scene==13)return id==12?"Шеф":id==11?"Коллега":id==15||id==51?"Огнетушитель":id==48?"Щит":id>=16&&id<=42?"Провода":id==52?"Лифт":null;
        if(animals.world.scene==12)return id==12?"Шеф":id==11?"Коллега":id==15?"Ручка":id==16?"Выход":id==17||id==24?"Мыло":id==18?"Кран":id==19?"Рулон":id==20?"Шнурок":id==21||id==22||id==23||id==25?"Вантуз":id==26?"Унитаз":id==28?"Кот":id==31?"Умывальник":null;
        if(animals.world.scene==11)return id==12?"Шеф":id==11?"Коллега":id==15||id==16||id==17?"Окно":id==18?"Трос":id==19||id==20?"Осколок":null;
        if(animals.world.scene==10)return id==12?"Шеф":id==11?"Коллега":id==15?"Диван":id==16?"Балкон":id==17||id==18?"Мухобойка":id>=22&&id<=24?"Ящик":id==26?"Бумага":id==27||id==28?"Дверца":id==29||id==30?"Шнур":id==31||id==32||id>=40&&id<=42?"Телевизор":id==33?"Кнопка":id==34?"Тумблер":id==38?"Гнусный Мух":null;
        return id==101?"Холодильник":id==13?"Шеф":id==12?"Коллега":id==20||id==21?"Выход":id==22?"Штора":id==23||id==24?"Куча":id==25?"Отвёртка":id==26?"Баллончик":id==27?"Злобное чудище":id==28||id==29?"Ванная":id==30?"Чайник":id==31||id==32?"Сахарница":id==33?"Замок":id==34?"Ключ":id==35?"Ручка":id==36||id==37?"Включатель":id>=64&&id<=95?"Переключатель":null;
    }
    public boolean request(int x,int y) throws IOException {
        if(animals.world.busy()) return false;
        int id=target(x,y);if(animals.world.scene==15)return animals.world.requestBasement(id>=0&&hitSlot>=0?thing(hitSlot):null,x,y);boolean result=id>=0?animals.world.interact(id):animals.world.move(x,y);refresh();return result;
    }
    public boolean activate(int x,int y) throws IOException { return request(x,y); }
    public boolean update(long delta) throws IOException {
        if(delta<=0) return false;
        elapsed+=Math.min(delta,400L);boolean changed=false;
        while(elapsed>=100L) { elapsed-=100L;animals.world.tick();changed=true; }
        if(changed) refresh();return changed;
    }
}
