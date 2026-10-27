package pilots;
/** Small cumulative MMAPI observations; no buffers, timers, or resource ownership. */
public final class AudioDiagnostics {
 public static volatile int cue=-1,stepCue=-1,creates,reuses,refetches,failures,endState=-1,reason;
 public static volatile int starts,startFailures,failedStage,errorType;
 public static volatile String errorText;
 public static volatile boolean workerAlive;
 public static volatile int musicState,musicEnds,effectStarts,effectFailures;
 public static void failed(Throwable failure,int stage) {
  failedStage=stage;
  errorType=failure instanceof javax.microedition.media.MediaException?1:
   failure instanceof IllegalArgumentException?2:failure instanceof SecurityException?3:
   failure instanceof IllegalStateException?4:failure instanceof java.io.IOException?5:
   failure instanceof OutOfMemoryError?6:7;
  try {
   String value=failure.getMessage();if(value==null)value=failure.getClass().getName();
   if(value.length()>40)value=value.substring(0,40);
   errorText=value.toUpperCase().replace('\n',' ').replace('\r',' ');
  } catch(OutOfMemoryError low) {errorText="ERROR DETAILS UNAVAILABLE";}
 }
 private AudioDiagnostics() { }
}
