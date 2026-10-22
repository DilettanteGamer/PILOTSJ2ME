package pilots;

/** Optional event output: gameplay remains independent of device media support. */
public interface SoundSink {
    void play(int cue);
}
