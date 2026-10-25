package pilots;

import java.io.*;
import java.lang.reflect.Field;
import java.security.*;
import java.util.Properties;

/** Compare every decoded Java frame byte to the source animation fingerprints. */
public final class AnimationDecodeTest {
    public static void main(String[] args) throws Exception {
        Properties expected=new Properties();FileInputStream in=new FileInputStream(args[0]);
        try { expected.load(in); } finally { in.close(); }
        Field pixels=AnimationClip.class.getDeclaredField("pixels");pixels.setAccessible(true);
        java.util.Enumeration names=expected.propertyNames();int clips=0,frames=0;
        while(names.hasMoreElements()) {
            String name=(String)names.nextElement();AnimationClip clip=new AnimationClip("/"+name);
            MessageDigest hash=MessageDigest.getInstance("SHA-256");
            DataOutputStream out=new DataOutputStream(new DigestOutputStream(new OutputStream() { public void write(int b) {} public void write(byte[] data,int start,int length) {} },hash));
            try {
                do {
                    out.writeShort(clip.offsetX);out.writeShort(clip.offsetY);out.writeShort(clip.width);out.writeShort(clip.height);
                    out.write((byte[])pixels.get(clip),0,clip.width*clip.height);frames++;
                } while(clip.advance());
            } finally { clip.close(); }
            String actual="";byte[] digest=hash.digest();
            for(byte b:digest) actual+=Integer.toHexString((b&255)|256).substring(1);
            if(!actual.equals(expected.getProperty(name))) throw new RuntimeException("Decoded pixels changed: "+name);
            clips++;
        }
        if(clips!=1513 || frames!=18678) throw new RuntimeException("Incomplete frame fingerprints");
        for(String name:new String[]{"overflow","literal","incomplete","size"}) {
            try { new AnimationClip("/bad-rle-"+name+".anm");throw new RuntimeException("Accepted malformed frame: "+name); }
            catch(IOException correct) {}
        }
        System.out.println("AnimationDecode: all "+clips+" clips / "+frames+" frames byte-identical to source; malformed RLE rejected");
    }
}
