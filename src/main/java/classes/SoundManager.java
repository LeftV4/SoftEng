package classes;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class SoundManager {

    public enum Sound {
        CLICK("/sfx/button_sfx.wav"),
        CORRECT("/sfx/correct.wav"),   // Add this file to resources
        WRONG("/sfx/wrong.wav"),       // Add this file to resources
        GAMEOVER("/sfx/q2gameover.wav"), // Add this file to resources
        BASIC("/sfx/q2dif1.wav"), // Add this file to resources
        INTERMEDIATE("/sfx/q2dif2.wav"), // Add this file to resources
        ADVANCED("/sfx/q2dif3.wav"), // Add this file to resources
        MENU("/sfx/q2menu.wav"); //Add this file to resources

        private final String path;

        Sound(String path) {
            this.path = path;
        }
    }

    // Cache a single AudioClip instance per sound.
    // AudioClip internally handles overlapping playback (mixing).
    private static final Map<Sound, AudioClip> soundCache = new HashMap<>();

    private static final Map<Sound, MediaPlayer> activeLoops = new HashMap<>();
    private static boolean isMuted = false;

    public static void toggleMute() {
        isMuted = !isMuted;
        for(MediaPlayer player : activeLoops.values()){
            player.setMute(isMuted);
        }
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

    public static void stopAllLoops(){
        for(MediaPlayer player : activeLoops.values()){
            player.stop();
        }
        activeLoops.clear();
    }

    public static void playLoop(Sound sound, double volume){
        if (activeLoops.containsKey(sound)) {
            MediaPlayer existing = activeLoops.get(sound);
            existing.setVolume(volume);
            return;
        }

        try {
            URL resource = SoundManager.class.getResource(sound.path);
            if (resource != null) {
                Media media = new Media(resource.toExternalForm());
                MediaPlayer player = new MediaPlayer(media);
                player.setCycleCount(MediaPlayer.INDEFINITE);
                player.setVolume(volume);
                player.setMute(isMuted);
                player.play();
                activeLoops.put(sound, player);
            } else {
                System.err.println("SoundManager: Warning - Track file not found: " + sound.path);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void fadeToVolume(Sound sound, double targetVolume, double durationSeconds) {
        MediaPlayer player = activeLoops.get(sound);
        if (player == null) {
            System.err.println("SoundManager: Cannot fade - no active player for " + sound);
            return;
        }

        Timeline fadeTimeline = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(player.volumeProperty(), player.getVolume())),
                new KeyFrame(Duration.seconds(durationSeconds), new KeyValue(player.volumeProperty(), targetVolume))
        );
        fadeTimeline.play();
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