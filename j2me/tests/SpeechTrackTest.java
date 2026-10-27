package pilots;
import java.io.*;import java.util.*;import javax.microedition.lcdui.Graphics;
public final class SpeechTrackTest {
 static void check(boolean b,String text){if(!b)throw new RuntimeException(text);}
 static final class Sink implements MusicSink{boolean mute;int audible,music;public void play(int cue){if(!mute)audible++;}public void music(int n){music=n;}}
 public static void main(String[] args)throws Exception{
  int bindings=0;for(int scene=2;scene<=17;scene++){
   Sink sink=new Sink();sink.mute=true;SpeechTrack captions=new SpeechTrack(sink,scene);
   DataInputStream in=new DataInputStream(SpeechTrackTest.class.getResourceAsStream("/scenes/"+(scene<10?"0":"")+scene+".speech"));check(in.readInt()==0x53504831,"speech magic");int count=in.readUnsignedShort();check(count>0,"missing scene speech "+scene);
   for(int i=0;i<count;i++){int cue=in.readUnsignedByte();in.readUnsignedShort();String expected=in.readUTF();captions.play(cue);check(expected.equals(captions.text()),"muted line missing "+scene+":"+cue);check(sink.audible==0,"muted sound escaped");bindings++;for(int tick=0;tick<250;tick++)captions.update(100);}
   in.close();captions.play(6);captions.play(7);captions.play(7);String current=captions.text();check(current.indexOf("Шеф")>=0&&current.indexOf("Коллега")>=0,"fast dialogue dropped previous phrase");captions.play(0);check(current.equals(captions.text()),"footstep erased speech");
   captions.music(2);check(sink.music==2,"music forwarding lost");for(int i=0;i<10;i++)captions.update(100);
   byte[] save={(byte)scene,1,2};captions.save(save);SpeechTrack loaded=new SpeechTrack(sink,scene);loaded.restore(save);check(current.equals(loaded.text())&&captions.age()==loaded.age(),"caption RMS continuation");
   SpeechTrack unrelated=new SpeechTrack(sink,scene);unrelated.restore(new byte[]{0});check(unrelated.text()==null,"caption from another game save");
   for(int i=0;i<250;i++)loaded.update(100);check(loaded.text()==null,"expired speech never cleared");
  }
  SpeechTrack oldScene=new SpeechTrack(null,5);oldScene.play(21);SpeechTrack nextScene=new SpeechTrack(null,6);nextScene.carry(oldScene);check(oldScene.text().equals(nextScene.text()),"transition discarded unread dialogue");
  String text="Или это ключ от номера Карбофоса... или одно из двух. Проверяем длинную фразу: ни одно слово не должно исчезнуть за нижним краем строки.";
  Graphics a=new Graphics(128,128),b=new Graphics(128,128);CaptionPanel.paint(a,128,128,text,0);CaptionPanel.paint(b,128,128,text,3500);
  String[] lines=(String[])LocationEntryTest.get(new CaptionPanel(),"lines");check(String.join(" ",lines).equals(text),"long caption truncated");boolean changed=false;for(int y=0;y<40;y++)for(int x=0;x<128;x++)if(a.image.getRGB(x,y)!=b.image.getRGB(x,y))changed=true;check(changed,"long caption did not change page");
  System.out.println("SpeechTrack: "+bindings+" speech bindings in all 16 scenes, mute independence, music forwarding, expiry, exact-save continuation and complete paged text passed");
 }
}
