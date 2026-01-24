package classes;

import javafx.animation.FadeTransition;
import javafx.scene.Parent;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public class ThemeManager {
    private static boolean isDarkMode = false;

    public static void toggleTheme(Parent root) {
        // Set the background color behind the root node to match the target theme
        if (root.getScene() != null) {
            // If currently Light (isDarkMode=false), we are going to Dark -> Fade to Black
            // If currently Dark (isDarkMode=true), we are going to Light -> Fade to White
            root.getScene().setFill(isDarkMode ? Color.WHITE : Color.BLACK);
        }

        FadeTransition fadeOut = new FadeTransition(Duration.millis(150), root);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        
        fadeOut.setOnFinished(e -> {
            isDarkMode = !isDarkMode;
            applyTheme(root);
            
            FadeTransition fadeIn = new FadeTransition(Duration.millis(150), root);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            fadeIn.play();
        });
        
        fadeOut.play();
    }

    public static void applyTheme(Parent root) {
        if (root == null) return;

        if (isDarkMode) {
            if (!root.getStyleClass().contains("dark-mode")) {
                root.getStyleClass().add("dark-mode");
            }
        } else {
            root.getStyleClass().remove("dark-mode");
        }
    }
}