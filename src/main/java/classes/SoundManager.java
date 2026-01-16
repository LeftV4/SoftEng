package classes;

import javafx.scene.media.AudioClip;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class SoundManager {

    public enum Sound {
        CLICK("/sfx/button_sfx.wav"),
        CORRECT("/sfx/correct.wav"),   // Add this file to resources
        WRONG("/sfx/wrong.wav"),       // Add this file to resources
        GAMEOVER("/sfx/gameover.wav"); // Add this file to resources

        private final String path;

        Sound(String path) {
            this.path = path;
        }
    }

    // Cache a single AudioClip instance per sound.
    // AudioClip internally handles overlapping playback (mixing).
    private static final Map<Sound, AudioClip> soundCache = new HashMap<>();

    private static boolean isMuted = false;

    public static void toggleMute() {
        isMuted = !isMuted;
    }

    public static boolean isMuted() {
        return isMuted;
    }

    public static void preloadSounds() {
        for (Sound sound : Sound.values()) {
            getOrLoadClip(sound);
        }
    }

    public static void play(Sound sound) {
        if (isMuted) return;
        AudioClip clip = getOrLoadClip(sound);
        if (clip != null) {
            clip.play();
        }
    }

    private static AudioClip getOrLoadClip(Sound sound) {
        if (soundCache.containsKey(sound)) {
            return soundCache.get(sound);
        }
        try {
            URL resource = SoundManager.class.getResource(sound.path);
            if (resource != null) {
                AudioClip clip = new AudioClip(resource.toExternalForm());
                soundCache.put(sound, clip);
                return clip;
            } else {
                System.err.println("SoundManager: Warning - Sound file not found: " + sound.path);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}