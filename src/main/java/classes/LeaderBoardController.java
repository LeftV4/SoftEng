package classes;

import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
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
    @FXML private Button standardBtn;
    @FXML private Button binaryBtn;
    private RotateTransition rotate;

    @FXML
    public void initialize() {
        // Apply the current theme (Light/Dark) to this scene
        ThemeManager.applyTheme(rootPane);
        animateBackground();
        backBtn.setOnMousePressed(e -> SoundManager.play(SoundManager.Sound.CLICK));
        standardBtn.setOnMousePressed(e -> SoundManager.play(SoundManager.Sound.CLICK));
        binaryBtn.setOnMousePressed(e -> SoundManager.play(SoundManager.Sound.CLICK));

        standardBtn.setOnAction(e -> loadLeaderboard("STANDARD"));
        binaryBtn.setOnAction(e -> loadLeaderboard("BINARY"));

        // Initialize loading state

        // Setup Spinner Animation
        rotate = new RotateTransition(Duration.millis(1500), loadingSpinner);
        rotate.setByAngle(360);
        rotate.setCycleCount(Animation.INDEFINITE);
        rotate.setInterpolator(Interpolator.LINEAR);
        
        loadLeaderboard("STANDARD");
    }

    private void loadLeaderboard(String mode) {
        loadingLayer.setVisible(true);
        leaderboardBox.setVisible(false);
        rotate.play();

        new Thread(() -> {
            try {
                Thread.sleep(500); // Fake delay for effect
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            List<String> scores = LeaderBoardManager.getSortedScores(mode);

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
                String raw = scores.get(i);
                // Parse "Name: Score" safely
                int lastColon = raw.lastIndexOf(": ");
                String name = (lastColon != -1) ? raw.substring(0, lastColon) : raw;
                String scoreVal = (lastColon != -1) ? raw.substring(lastColon + 2) : "";

                HBox row = new HBox();
                row.setAlignment(Pos.CENTER_LEFT);
                row.setSpacing(15);
                row.setStyle("-fx-background-color: rgba(255,255,255,0.1); -fx-background-radius: 10; -fx-padding: 10;");

                Label rankLabel = new Label("#" + (i + 1));
                rankLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

                Label nameLabel = new Label(name);
                nameLabel.setStyle("-fx-font-size: 20px;");

                Region spacer = new Region();
                HBox.setHgrow(spacer, Priority.ALWAYS);

                Label scoreLabel = new Label(scoreVal);
                scoreLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

                // Apply Gold/Silver/Bronze styling
                if (i == 0) {
                    String gold = "-fx-text-fill: #FFD700;";
                    rankLabel.setStyle(rankLabel.getStyle() + gold);
                    nameLabel.setStyle(nameLabel.getStyle() + gold);
                    scoreLabel.setStyle(scoreLabel.getStyle() + gold);
                    row.setStyle(row.getStyle() + "-fx-border-color: #FFD700; -fx-border-width: 2; -fx-border-radius: 10;");

                    DropShadow glow = new DropShadow(20, Color.web("#FFD700"));
                    glow.setSpread(0.3);
                    row.setEffect(glow);
                } else if (i == 1) {
                    String silver = "-fx-text-fill: #C0C0C0;";
                    rankLabel.setStyle(rankLabel.getStyle() + silver);
                    nameLabel.setStyle(nameLabel.getStyle() + silver);
                    scoreLabel.setStyle(scoreLabel.getStyle() + silver);
                    row.setStyle(row.getStyle() + "-fx-border-color: #C0C0C0; -fx-border-width: 2; -fx-border-radius: 10;");

                    DropShadow glow = new DropShadow(20, Color.web("#C0C0C0"));
                    glow.setSpread(0.3);
                    row.setEffect(glow);
                } else if (i == 2) {
                    String bronze = "-fx-text-fill: #CD7F32;";
                    rankLabel.setStyle(rankLabel.getStyle() + bronze);
                    nameLabel.setStyle(nameLabel.getStyle() + bronze);
                    scoreLabel.setStyle(scoreLabel.getStyle() + bronze);
                    row.setStyle(row.getStyle() + "-fx-border-color: #CD7F32; -fx-border-width: 2; -fx-border-radius: 10;");

                    DropShadow glow = new DropShadow(20, Color.web("#CD7F32"));
                    glow.setSpread(0.3);
                    row.setEffect(glow);
                }

                row.getChildren().addAll(rankLabel, nameLabel, spacer, scoreLabel);
                leaderboardBox.getChildren().add(row);
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