package classes;

import javafx.scene.media.AudioClip;
import java.net.URL;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;


public class SoundManager {

    // Sounds go here
    public enum Sound {
        CLICK("/sfx/button_sfx.wav");
        /* Future examples:
        CORRECT("/sfx/correct.mp3");
        WRONG("/sfx/wrong.mp3");
        or sum shi */

        private final String path;

        Sound(String path) {
            this.path = path;
        }
    }

    // Prevents lag from loading sounds
    private static final Map<Sound, AudioClip> soundCache = new ConcurrentHashMap<>();


    public static void preloadSounds() {
        Thread loader = new Thread( () -> {
           for (Sound sound : Sound.values()) {
               getClip(sound);
           }
        });
        loader.setDaemon(true);
        loader.start();
    }

    private static AudioClip getClip(Sound sound){
        return soundCache.computeIfAbsent(sound, s -> {
            try {
                URL resource = SoundManager.class.getResource(s.path);
                if (resource != null) {
                    return new AudioClip(resource.toExternalForm());
                } else {
                    System.err.println("Sound file missing: " + s.path);
                    return null; // This will cause computeIfAbsent to throw NPE if not handled, but see below
                }
            } catch (Exception e) {
                System.err.println("Error loading sound " + s.name() + ": " + e.getMessage());
            }
            return null;

        });
    }

    //Plays the specified sound
    public static void play(Sound sound) {
       // Check if clip exists before playing to avoid NPEs from getClip failures
       try {
           AudioClip clip = getClip(sound);
           if (clip != null){
               clip.play(1.0);
           }
       } catch (Exception e) {
           e.printStackTrace();
           // Handle potential NPE from ConcurrentHashMap if getClip returned null
       }
    }
}