package org.concentus;
/** Offline restricted-path experiment, not a production Opus decoder. */
public final class SilkNbOnlyProbe {
 final SilkDecoder decoder=new SilkDecoder();final DecControlState control=new DecControlState();
 public SilkNbOnlyProbe(){DecodeAPI.silk_InitDecoder(decoder);control.API_sampleRate=12000;control.nChannelsAPI=1;control.nChannelsInternal=1;control.payloadSize_ms=20;control.internalSampleRate=8000;}
 public int decode(byte[] packet,short[] pcm){
  if(packet.length<3||packet[0]!=8)throw new IllegalArgumentException("Only mono SILK-NB20ms single-frame packets");
  EntropyCoder ec=new EntropyCoder();ec.dec_init(packet,1,packet.length-1);int count=0;
  while(count<240){BoxedValueInt n=new BoxedValueInt(0);int error=DecodeAPI.silk_Decode(decoder,control,0,count==0?1:0,ec,pcm,count,n);if(error!=0||n.Val<=0)throw new IllegalArgumentException("SILK failed");count+=n.Val;}
  if(ec.tell()+17<=8*(packet.length-1))throw new IllegalArgumentException("Possible CELT redundancy");
  return count;
 }
}
