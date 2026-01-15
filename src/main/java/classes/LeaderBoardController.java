package classes;

import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.concurrent.Task;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.ColumnConstraints;

import java.io.IOException;
import java.util.List;

public class LeaderBoardController {

    @FXML private AnchorPane rootPane;
    @FXML private VBox leaderboardBox;
    @FXML private StackPane loadingLayer;
    @FXML private ImageView loadingSpinner;

    @FXML
    public void initialize() {
        // 1. Show the loading screen immediately
        loadingLayer.setVisible(true);
        leaderboardBox.getChildren().clear();

        // Start rotation animation for the sand clock
        RotateTransition rotate = new RotateTransition(Duration.seconds(2), loadingSpinner);
        rotate.setByAngle(360);
        rotate.setCycleCount(Animation.INDEFINITE);
        rotate.setInterpolator(Interpolator.LINEAR);
        rotate.play();

        // 2. Create a background task to fetch scores
        Task<List<String>> fetchTask = new Task<>() {
            @Override
            protected List<String> call() throws Exception {
                return LeaderBoardManager.getSortedScores();
            }
        };

        // 3. Update UI when the task finishes successfully
        fetchTask.setOnSucceeded(event -> {
            loadingLayer.setVisible(false);
            List<String> scores = fetchTask.getValue();

            // Create a GridPane for the table layout
            GridPane grid = new GridPane();
            grid.setAlignment(Pos.CENTER);
            grid.setHgap(20);
            grid.setVgap(10);
            grid.setPadding(new Insets(20));
            grid.getStyleClass().add("leaderboard-grid");

            // Define Columns: Rank (15%), Name (60%), Score (25%)
            ColumnConstraints col1 = new ColumnConstraints(); col1.setPercentWidth(15); col1.setHalignment(HPos.CENTER);
            ColumnConstraints col2 = new ColumnConstraints(); col2.setPercentWidth(60);
            ColumnConstraints col3 = new ColumnConstraints(); col3.setPercentWidth(25); col3.setHalignment(HPos.RIGHT);
            grid.getColumnConstraints().addAll(col1, col2, col3);

            // Add Headers
            Label rankHeader = new Label("RANK"); rankHeader.getStyleClass().add("leaderboard-header");
            Label nameHeader = new Label("PLAYER"); nameHeader.getStyleClass().add("leaderboard-header");
            Label scoreHeader = new Label("SCORE"); scoreHeader.getStyleClass().add("leaderboard-header");
            grid.add(rankHeader, 0, 0);
            grid.add(nameHeader, 1, 0);
            grid.add(scoreHeader, 2, 0);

            for (int i = 0; i < scores.size(); i++) {
                String rawData = scores.get(i);
                // Parse "Name:Score"
                String[] parts = rawData.split(":");
                String name = parts[0];
                String score = parts.length > 1 ? parts[1] : "0";

                Label rankLbl = new Label(String.valueOf(i + 1));
                Label nameLbl = new Label(name);
                Label scoreLbl = new Label(score);
                
                // Apply colors based on rank
                String style = "-fx-font-family: 'Segoe UI Black'; -fx-font-size: 18px; ";
                if (i == 0) {
                    style += "-fx-text-fill: #FFD700; -fx-effect: dropshadow(one-pass-box, rgba(255,215,0,0.8), 10, 0, 0, 0);"; // Gold + Glow
                } else if (i == 1) {
                    style += "-fx-text-fill: #C0C0C0; -fx-effect: dropshadow(one-pass-box, rgba(192,192,192,0.8), 10, 0, 0, 0);"; // Silver + Glow
                } else if (i == 2) {
                    style += "-fx-text-fill: #CD7F32; -fx-effect: dropshadow(one-pass-box, rgba(205,127,50,0.8), 10, 0, 0, 0);"; // Bronze + Glow
                } else {
                    style += "-fx-text-fill: white;";
                }

                rankLbl.setStyle(style);
                nameLbl.setStyle(style);
                scoreLbl.setStyle(style);

                // Add to grid (row i+1 because of header)
                grid.add(rankLbl, 0, i + 1);
                grid.add(nameLbl, 1, i + 1);
                grid.add(scoreLbl, 2, i + 1);
            }
            
            leaderboardBox.getChildren().add(grid);
        });

        // 4. Handle connection errors
        fetchTask.setOnFailed(event -> {
            loadingLayer.setVisible(false);
            Label errorLabel = new Label("Could not load scores.");
            errorLabel.setStyle("-fx-text-fill: #ff5f6d; -fx-font-family: 'Segoe UI Black'; -fx-font-size: 18px;");
            leaderboardBox.getChildren().add(errorLabel);
        });

        // 5. Start the background thread
        new Thread(fetchTask).start();
    }

    @FXML
    public void exitLeaderboard() throws IOException {
        SoundManager.play(SoundManager.Sound.CLICK);
        new SceneSwitch(rootPane, "/MainMenu.fxml");
    }
}