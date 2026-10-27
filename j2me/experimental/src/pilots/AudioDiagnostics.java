package pilots;
/** Small cumulative MMAPI observations; no buffers, timers, or resource ownership. */
public final class AudioDiagnostics {
 public static volatile int cue=-1,stepCue=-1,creates,reuses,failures,endState=-1,reason;
 private AudioDiagnostics() { }
}
