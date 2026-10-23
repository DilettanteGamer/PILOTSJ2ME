package javax.microedition.rms;
import java.util.HashMap;
/** In-memory RMS for actual Canvas save/load integration tests; never shipped. */
public class RecordStore {
    private static final HashMap<String,RecordStore> stores=new HashMap<String,RecordStore>();
    private byte[] record;
    public static RecordStore openRecordStore(String name,boolean create) {
        RecordStore store=stores.get(name);
        if(store==null){if(!create)throw new IllegalStateException("No saved game");store=new RecordStore();stores.put(name,store);}return store;
    }
    public int getNumRecords(){return record==null?0:1;}
    public byte[] getRecord(int id){return (byte[])record.clone();}
    public int addRecord(byte[] data,int offset,int length){setRecord(1,data,offset,length);return 1;}
    public void setRecord(int id,byte[] data,int offset,int length){record=new byte[length];System.arraycopy(data,offset,record,0,length);}
    public void closeRecordStore(){}
}
