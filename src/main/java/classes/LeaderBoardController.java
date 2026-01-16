package classes;

import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.io.IOException;
import java.util.List;

public class LeaderBoardController {

    @FXML private AnchorPane rootPane;
    @FXML private VBox leaderboardBox;
    @FXML private StackPane loadingLayer;
    @FXML private Label loadingSpinner;
    @FXML private Button backBtn;

    @FXML
    public void initialize() {
        // Apply the current theme (Light/Dark) to this scene
        ThemeManager.applyTheme(rootPane);
        animateBackground();
        backBtn.setOnMousePressed(e -> SoundManager.play(SoundManager.Sound.CLICK));

        // Initialize loading state
        loadingLayer.setVisible(true);
        leaderboardBox.setVisible(false);

        // Setup Spinner Animation
        RotateTransition rotate = new RotateTransition(Duration.millis(1500), loadingSpinner);
        rotate.setByAngle(360);
        rotate.setCycleCount(Animation.INDEFINITE);
        rotate.setInterpolator(Interpolator.LINEAR);
        rotate.play();

        // Load scores in background
        new Thread(() -> {
            try {
                Thread.sleep(800); // Fake delay for effect
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            List<String> scores = LeaderBoardManager.getSortedScores();

            Platform.runLater(() -> {
                loadScoresToUI(scores);
                rotate.stop();
                loadingLayer.setVisible(false);
                leaderboardBox.setVisible(true);
            });
        }).start();
    }

    private void loadScoresToUI(List<String> scores) {
        leaderboardBox.getChildren().clear();

        if (scores.isEmpty()) {
            Label emptyLabel = new Label("No scores yet!");
            emptyLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
            leaderboardBox.getChildren().add(emptyLabel);
        } else {
            for (int i = 0; i < scores.size(); i++) {
                Label scoreLabel = new Label((i + 1) + ". " + scores.get(i));
                scoreLabel.setStyle("-fx-font-size: 24px; -fx-padding: 5;");
                leaderboardBox.getChildren().add(scoreLabel);
            }
        }
    }

    @FXML
    public void exitLeaderboard() throws IOException {
        new SceneSwitch(rootPane, "/MainMenu.fxml");
    }

    private void animateBackground() {
        for (javafx.scene.Node node : rootPane.lookupAll(".math-symbol")) {
            TranslateTransition tt = new TranslateTransition(Duration.seconds(2 + Math.random() * 2), node);
            tt.setByY(20);
            tt.setCycleCount(Animation.INDEFINITE);
            tt.setAutoReverse(true);
            tt.setDelay(Duration.seconds(Math.random()));
            tt.play();
        }
    }
}