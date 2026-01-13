package classes;


import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;

import java.io.IOException;
import java.util.List;

public class LeaderBoardController {

    @FXML private VBox leaderboardBox;
    @FXML private AnchorPane rootPane;

    @FXML
    public void initialize() {
        loadLeaderboardData();
    }

    private void loadLeaderboardData() {
        // Clear any placeholder content
        leaderboardBox.getChildren().clear();

        // 1. Create and style the title (similar to GameController)
        Label titleLabel = new Label("🏆 LEADERBOARD 🏆");
        titleLabel.setStyle("-fx-font-size: 36px; -fx-font-weight: bold; -fx-text-fill: #FFD700; -fx-padding: 20;");
        leaderboardBox.getChildren().add(titleLabel);

        // 2. Fetch scores from Postgres via the Manager
        List<String> topScores = LeaderBoardManager.getSortedScores();

        // 3. Populate the VBox
        if (topScores.isEmpty()) {
            Label noScores = new Label("No scores recorded yet. Be the first!");
            noScores.setStyle("-fx-font-size: 18px; -fx-text-fill: #666;");
            leaderboardBox.getChildren().add(noScores);
        } else {
            for (int i = 0; i < topScores.size(); i++) {
                Label scoreEntry = new Label((i + 1) + ". " + topScores.get(i));

                // Styling to match the "GameController" look
                String baseStyle = "-fx-font-size: 24px; -fx-padding: 8; -fx-text-fill: #333;";

                // Highlight the top 3 players
                if (i == 0) {
                    scoreEntry.setStyle(baseStyle + "-fx-font-weight: bold; -fx-text-fill: #DAA520;"); // Gold
                } else if (i == 1) {
                    scoreEntry.setStyle(baseStyle + "-fx-font-weight: bold; -fx-text-fill: #C0C0C0;"); // Silver
                } else if (i == 2) {
                    scoreEntry.setStyle(baseStyle + "-fx-font-weight: bold; -fx-text-fill: #CD7F32;"); // Bronze
                } else {
                    scoreEntry.setStyle(baseStyle);
                }

                leaderboardBox.getChildren().add(scoreEntry);
            }
        }
    }

    @FXML
    public void exitLeaderboard() throws IOException {
        new SceneSwitch(rootPane, "/MainMenu.fxml");
    }

}
