package pilots;
import java.io.IOException;
import javax.microedition.lcdui.*;

/** Front-view backdrop with original crate, servant and Chief artwork. */
public final class BasementStage {
    private final BasementGame game;
    private final IndexedBackground background;
    private final AnimationClip crates,servant;
    private final AnimationClip[] chief=new AnimationClip[5];
    private final Viewport view=new Viewport(128,160,128,160);
    public BasementStage(BasementGame game)throws IOException{
        this.game=game;background=new IndexedBackground(getClass().getResourceAsStream("/puzzles/basement.p8"));
        crates=new AnimationClip("/puzzles/basement-crates.sprite");servant=new AnimationClip("/puzzles/basement-servant.sprite");
        String[] names={"idle","left","right","jump","gun"};
        try{for(int i=0;i<names.length;i++)chief[i]=new AnimationClip("/puzzles/basement-chief-"+names[i]+".sprite");}
        catch(IOException error){close();throw error;}
    }
    public void close(){crates.close();servant.close();for(int i=0;i<chief.length;i++)if(chief[i]!=null)chief[i].close();}
    private void frame(AnimationClip clip,int index)throws IOException{
        index=Math.max(0,Math.min(clip.count-1,index));if(clip.index>index)clip.restart();while(clip.index<index)clip.advance();
    }
    static int x(int col){return 16+32*col;}
    static int feet(int lane,int height){return lane==0?154:132-18*height;}
    public void paint(Graphics g,int width,int height)throws IOException{
        int available=Math.max(1,height-42),w=Math.min(width,available*128/160),h=Math.max(1,w*160/128);
        view.width=width;view.height=height;view.drawWidth=w;view.drawHeight=h;view.left=(width-w)/2;view.top=15+(available-h)/2;view.paintClip(view.left,view.top,w,h);
        background.paint(g,view);
        // The contact line and sprite feet use the same logical floor coordinate.
        g.setColor(0x968b66);g.drawLine(view.screenX(0),view.screenY(132),view.screenX(128),view.screenY(132));
        int col=game.fallingColumn(),t=game.fallingTick();
        frame(servant,col<0?0:1+(t-1)*(servant.count-2)/20);servant.paint(g,view,86,48);
        frame(crates,0);
        for(int c=0;c<4;c++)for(int row=0;row<game.crates(c);row++){
            if(c==game.shotColumn() && row==game.shotLevel()-1 && game.shotTick()>0)continue;
            crates.paint(g,view,x(c),132-row*18);
        }
        if(col>=0){
            g.setColor(0xf7d24d);int sx=view.screenX(x(col));g.drawLine(sx,view.screenY(52),sx,view.screenY(132-game.crates(col)*18));
            if(t>=12){frame(crates,0);int end=132-game.crates(col)*18;int progress=t-12;crates.paint(g,view,86+(x(col)-86)*progress/10,48+(end-48)*progress*progress/100);}
        }
        if(game.shotTick()>0){frame(crates,1+(BasementGame.SHOT_TICKS-game.shotTick())*(crates.count-2)/(BasementGame.SHOT_TICKS-1));crates.paint(g,view,x(game.shotColumn()),132-(game.shotLevel()-1)*18);}
        int px=x(game.column()),py=feet(game.safe()?0:1,game.height()),motion=game.motion();
        if(motion>0){int progress=4-motion;px=x(game.data[BasementGame.FROM_COLUMN])+(px-x(game.data[BasementGame.FROM_COLUMN]))*progress/4;
            int from=feet(game.data[BasementGame.FROM_LANE],game.data[BasementGame.FROM_HEIGHT]);py=from+(py-from)*progress/4;
            if(!game.safe())py-=progress*(4-progress)*3;
        }
        int pose=game.shotTick()>0?4:motion>0?game.safe()?(game.data[BasementGame.FACING]<0?1:2):3:0;
        frame(chief[pose],motion>0?4-motion:game.shotTick()>0?BasementGame.SHOT_TICKS-game.shotTick():0);chief[pose].paint(g,view,px,py);
        g.setColor(0xffe09a);g.setFont(Font.getFont(Font.FACE_SYSTEM,Font.STYLE_PLAIN,Font.SIZE_SMALL));
        String message=game.message();g.drawString(message!=null?message:game.safe()?"Безопасная дорожка":"Ящики: прыжок авто",width/2,1,Graphics.TOP|Graphics.HCENTER);
        g.drawString("2/4/6/8 — Шеф",width/2,height-26,Graphics.TOP|Graphics.HCENTER);
        g.drawString(game.hasGun()?"5 автомат  0 назад":"0 назад за автоматом",width/2,height-13,Graphics.TOP|Graphics.HCENTER);
    }
}
