package classes;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.ParallelTransition;
import javafx.animation.PauseTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.io.IOException;

public class GameController {

    @FXML private AnchorPane gameRoot;
    @FXML private VBox rootBox;
    @FXML private Label scoreLabel;
    @FXML private StackPane timerPane;
    @FXML private ScrollPane historyScrollPane;
    @FXML private VBox questionBox;
    @FXML private Label timerLabel;
    @FXML private Arc timerArc;
    @FXML private GridPane answerGrid;
    @FXML private Button pauseBtn;
    @FXML private Button muteBtn;
    @FXML private Button restartBtn;
    @FXML private Button mainMenuBtn;
    @FXML private Label questionLabel;
    @FXML private Button btn1, btn2, btn3, btn4;
    @FXML private Label livesLabel;
    @FXML private Label comboLabel;
    @FXML private Label levelLabel;

    private VBox gameOverPane; // Pane for name input

    private MathEngine engine;
    private Question currentQuestion;
    private int score = 0;
    private int difficulty = 1;
    private Timeline timeline;
    private int lives = 3;
    private int combo = 0;
    private static final int TIME_LIMIT = 10;

    private List<QuestionHistory> history = new ArrayList<>();
    private boolean isGameActive = false;
    private boolean isPaused = false;
    private long startTime;
    private long totalPauseDuration;
    private long pauseStartTimestamp;

    public void initialize() {
        engine = new MathEngine();
        ThemeManager.applyTheme(gameRoot);
        animateBackground();

        // This runs when the screen starts
        questionLabel.setText("Press a button to start!");
        updateLivesUI();

        loadNextQuestion();
        isGameActive = true;

        // Configure the Timer Tile to look cool
        btn1.setOnAction(e -> checkAnswer(0));
        btn2.setOnAction(e -> checkAnswer(1));
        btn3.setOnAction(e -> checkAnswer(2));
        btn4.setOnAction(e -> checkAnswer(3));
        restartBtn.setOnAction(e -> restartGame());
        mainMenuBtn.setOnAction(e -> returnToMenu());
        pauseBtn.setOnAction(e -> togglePause());
        muteBtn.setOnAction(e -> toggleMute());
        updateMuteButton();

        // Trigger sound on MousePressed for instant feedback
        btn1.setOnMousePressed(e -> SoundManager.play(SoundManager.Sound.CLICK));
        btn2.setOnMousePressed(e -> SoundManager.play(SoundManager.Sound.CLICK));
        btn3.setOnMousePressed(e -> SoundManager.play(SoundManager.Sound.CLICK));
        btn4.setOnMousePressed(e -> SoundManager.play(SoundManager.Sound.CLICK));
        restartBtn.setOnMousePressed(e -> SoundManager.play(SoundManager.Sound.CLICK));
        mainMenuBtn.setOnMousePressed(e -> SoundManager.play(SoundManager.Sound.CLICK));
        pauseBtn.setOnMousePressed(e -> SoundManager.play(SoundManager.Sound.CLICK));
        muteBtn.setOnMousePressed(e -> SoundManager.play(SoundManager.Sound.CLICK));

        // Add Keyboard Support
        gameRoot.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.setOnKeyPressed(e -> {
                    if (!isGameActive) return;
                    switch (e.getCode()) {
                        case DIGIT1: case NUMPAD1: checkAnswer(0); break;
                        case DIGIT2: case NUMPAD2: checkAnswer(1); break;
                        case DIGIT3: case NUMPAD3: checkAnswer(2); break;
                        case DIGIT4: case NUMPAD4: checkAnswer(3); break;
                    }
                });
            }
        });
    }

    private void restartGame() {
        // 1. Reset Game State
        score = 0;
        difficulty = 1;
        history.clear();
        lives = 3;
        updateLivesUI();
        combo = 0;
        isGameActive = true;
        isPaused = false;

        // Reset UI Elements visibility
        timerPane.setVisible(true);
        timerPane.setManaged(true);
        answerGrid.setVisible(true);
        answerGrid.setManaged(true);
        pauseBtn.setVisible(true);
        pauseBtn.setText("⏸");
        muteBtn.setVisible(true);
        answerGrid.setDisable(false);
        historyScrollPane.setPrefHeight(150.0); // Reset to original size
        if (gameOverPane != null) {
            rootBox.getChildren().remove(gameOverPane);
        }

        // 2. Reset UI
        questionLabel.setText("Get Ready...");
        questionLabel.setStyle("-fx-font-size: 48px; -fx-font-weight: bold;"); // Reset font size
        questionBox.getChildren().removeIf(node -> node instanceof Label && node != questionLabel);
        questionBox.getChildren().add(questionLabel);
        restartBtn.setVisible(false);
        restartBtn.setManaged(false); // Hide it again
        mainMenuBtn.setVisible(false);
        mainMenuBtn.setManaged(false);

        // 3. Re-enable Buttons
        resetAnswerButtons();

        // 4. Start fresh!
        loadNextQuestion();
    }

    private void toggleMute() {
        SoundManager.toggleMute();
        updateMuteButton();
        SoundManager.play(SoundManager.Sound.CLICK);
    }

    private void updateMuteButton() {
        muteBtn.setText(SoundManager.isMuted() ? "🔇" : "🔊");
    }

    private void togglePause() {
        if (isPaused) {
            resumeGame();
        } else {
            if (isGameActive) {
                pauseGame();
            }
        }
    }

    private void pauseGame() {
        isPaused = true;
        isGameActive = false;
        if (timeline != null) timeline.pause();
        pauseStartTimestamp = System.currentTimeMillis();

        pauseBtn.setText("▶");
        questionLabel.setText("PAUSED");
        answerGrid.setDisable(true);
    }

    private void resumeGame() {
        isPaused = false;
        isGameActive = true;
        
        long pauseEndTimestamp = System.currentTimeMillis();
        totalPauseDuration += (pauseEndTimestamp - pauseStartTimestamp);
        
        if (timeline != null) timeline.play();

        pauseBtn.setText("⏸");
        questionLabel.setText(currentQuestion.getExpression() + " = ?");
        answerGrid.setDisable(false);
    }

    private void startTimer() {
        timerLabel.setText(String.valueOf(TIME_LIMIT));
        timerArc.setLength(360);
        timerArc.setStartAngle(90);
        timerArc.setStroke(Color.GREEN);

        if (timeline != null) timeline.stop();

        startTime = System.currentTimeMillis();
        totalPauseDuration = 0;

        timeline = new Timeline(new KeyFrame(Duration.millis(16), e -> {

            long now = System.currentTimeMillis();
            long elapsedMillis = (now - startTime) - totalPauseDuration;

            // Formula: 360 - (360 * (elapsed / total_duration))
            double totalDurationMillis = TIME_LIMIT * 1000.0;
            double remainingAngle = 360.0 - (360.0 * (elapsedMillis / totalDurationMillis));

            if (remainingAngle <= 0) {
                timerArc.setLength(0);
                timerLabel.setText("0");
                timeline.stop();
                lives--;
                updateLivesUI();
                if (lives <= 0) {
                    handleGameOver();
                } else {
                    loadNextQuestion();
                }
            } else {
                timerArc.setLength(remainingAngle);

                // Update Text
                int secondsLeft = (int) Math.ceil(remainingAngle / 36.0);
                timerLabel.setText(String.valueOf(secondsLeft));

                // Panic colors
                if (secondsLeft <= 3) timerArc.setStroke(Color.RED);
                else if (secondsLeft <= 6) timerArc.setStroke(Color.ORANGE);
            }
        }));

        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void loadNextQuestion() {

        resetAnswerButtons();
        startTimer();
        currentQuestion = engine.generateQuestion(difficulty);

        questionLabel.setText(currentQuestion.getExpression() + " = ?");

        btn1.setText(String.valueOf(currentQuestion.getChoices().get(0)));
        btn2.setText(String.valueOf(currentQuestion.getChoices().get(1)));
        btn3.setText(String.valueOf(currentQuestion.getChoices().get(2)));
        btn4.setText(String.valueOf(currentQuestion.getChoices().get(3)));
    }

    private void checkAnswer(int buttonIndex) {
        if (!isGameActive) return;

        int selectedValue = currentQuestion.getChoices().get(buttonIndex);
        boolean isCorrect = (selectedValue == currentQuestion.getCorrectAnswer());

        history.add(new QuestionHistory(currentQuestion,selectedValue, isCorrect));
        
        // Trigger Visual Feedback (Flash & Floating Text)
        triggerVisualFeedback(isCorrect, getButtonByIndex(buttonIndex));

        if (selectedValue == currentQuestion.getCorrectAnswer()) {
            SoundManager.play(SoundManager.Sound.CORRECT);
            combo++;
            score += (combo >= 5) ? 2 : 1; // Double points if combo is 5 or higher
            scoreLabel.setText("Score: " + score);
            showComboEffect();

            if (score % 3 == 0) {
                difficulty += 3;
                showLevelUpEffect();
            }

            loadNextQuestion();
        } else {
            SoundManager.play(SoundManager.Sound.WRONG);
            lives--;
            combo = 0; // Reset combo
            updateLivesUI();

            // Shake the wrong button
            Button wrongBtn = getButtonByIndex(buttonIndex);
            shakeAnimation(wrongBtn);

            if (lives <= 0) {
                handleGameOver();
            } else {
                // Delay slightly so the user sees the shake before the question changes
                isGameActive = false;
                PauseTransition pause = new PauseTransition(Duration.millis(400));
                pause.setOnFinished(e -> {
                    isGameActive = true;
                    loadNextQuestion();
                });
                pause.play();
            }
        }
    }
    private void handleGameOver() {
        isGameActive = false;
        SoundManager.play(SoundManager.Sound.GAMEOVER);

        // 1. Kill Game Elements
        timerPane.setVisible(false);
        timerPane.setManaged(false);
        answerGrid.setVisible(false);
        answerGrid.setManaged(false);
        pauseBtn.setVisible(false);
        muteBtn.setVisible(false);

        // 2. Expand History Pane
        historyScrollPane.setPrefHeight(350.0);
        questionBox.getChildren().clear();

        // 3. Populate History Pane
        Label summaryLabel = new Label("RESULTS SUMMARY");
        summaryLabel.setStyle("-fx-font-size: 28px; -fx-font-weight: bold;");
        Label finalScoreLabel = new Label("Final Score: " + score);
        finalScoreLabel.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-padding: 0 0 10 0;");
        questionBox.getChildren().addAll(summaryLabel, finalScoreLabel);

        if (timeline != null) timeline.stop();

        // Show History
        for (QuestionHistory entry : history) {
            String resultText = entry.isCorrect
                    ? String.format("%s = %d ✓", entry.question.getExpression(), entry.selectedAnswer)
                    : String.format("%s = %d (Correct: %d) ✗", entry.question.getExpression(), entry.selectedAnswer, entry.question.getCorrectAnswer());

            Label historyLabel = new Label(resultText);
            // Paint correct answers green and wrong answers red
            String colorStyle = entry.isCorrect ? "-fx-text-fill: #28a745;" : "-fx-text-fill: #dc3545;";
            historyLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-padding: 5; " + colorStyle);
            questionBox.getChildren().add(historyLabel);
        }

        // 4. Show Name Input Pane
        askForNameAndSave();
    }

    private void askForNameAndSave() {
        // Create the pane if it doesn't exist
        if (gameOverPane == null) {
            gameOverPane = new VBox(10);
            gameOverPane.setAlignment(javafx.geometry.Pos.CENTER);
            gameOverPane.setStyle("-fx-padding: 10;");
        }
        gameOverPane.getChildren().clear();

        // Add it to the root box if not present (inserting before restart button)
        if (!rootBox.getChildren().contains(gameOverPane)) {
            int index = rootBox.getChildren().indexOf(answerGrid);
            if (index != -1) rootBox.getChildren().add(index + 1, gameOverPane);
            else rootBox.getChildren().add(gameOverPane);
        }

        Label promptLabel = new Label("Save your score! Enter your name:");
        promptLabel.setStyle("-fx-font-size: 18px;");

        TextField nameField = new TextField("Player");
        nameField.setMaxWidth(200);
        nameField.setStyle("-fx-font-size: 16px;");

        Button saveBtn = new Button("Save Score");
        saveBtn.getStyleClass().addAll("game-button", "btn-opt4");

        saveBtn.setOnMousePressed(e -> SoundManager.play(SoundManager.Sound.CLICK));
        saveBtn.setOnAction(e -> {
            String name = nameField.getText().isEmpty() ? "Player" : nameField.getText();
            LeaderBoardManager.saveScore(name, score);
            displayLeaderboard();
        });

        gameOverPane.getChildren().addAll(promptLabel, nameField, saveBtn);

        restartBtn.setVisible(true);
        restartBtn.setManaged(true);
        mainMenuBtn.setVisible(true);
        mainMenuBtn.setManaged(true);
    }

    private void displayLeaderboard() {
        // Remove the name input pane
        if (gameOverPane != null) {
            rootBox.getChildren().remove(gameOverPane);
        }

        // Clear the history/summary UI to make room for the Leaderboard
        questionBox.getChildren().clear();

        Label titleLabel = new Label("🏆 LEADERBOARD 🏆");
        titleLabel.setStyle("-fx-font-size: 36px; -fx-font-weight: bold; -fx-text-fill: #FFD700; -fx-padding: 10;");
        questionBox.getChildren().add(titleLabel);

        List<String> topScores = LeaderBoardManager.getSortedScores();

        if (topScores.isEmpty()) {
            questionBox.getChildren().add(new Label("No scores yet!"));
        } else {
            for (int i = 0; i < topScores.size(); i++) {
                Label scoreEntry = new Label((i + 1) + ". " + topScores.get(i));
                scoreEntry.setStyle("-fx-font-size: 22px; -fx-padding: 2;");
                questionBox.getChildren().add(scoreEntry);
            }
        }

        // Ensure the restart button is visible
        restartBtn.setVisible(true);
        restartBtn.setManaged(true);
        mainMenuBtn.setVisible(true);
        mainMenuBtn.setManaged(true);
    }

    private void cleanup() {
        if (timeline != null) {
            timeline.stop();
        }
    }

    private void returnToMenu() {
        cleanup();
        try {
            new SceneSwitch(gameRoot, "/MainMenu.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateLivesUI() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 3; i++) {
            if (i < lives) {
                sb.append("\u2764");
            } else {
                sb.append("\uD83D\uDC94");
            }
        }
        livesLabel.setText(sb.toString());
        
        // Pulse animation for hearts
        ScaleTransition st = new ScaleTransition(Duration.millis(100), livesLabel);
        st.setFromX(1.0); st.setFromY(1.0);
        st.setToX(1.2); st.setToY(1.2);
        st.setAutoReverse(true);
        st.setCycleCount(2);
        st.play();
    }

    private Button getButtonByIndex(int index) {
        switch (index) {
            case 0: return btn1;
            case 1: return btn2;
            case 2: return btn3;
            case 3: return btn4;
            default: return null;
        }
    }

    private void shakeAnimation(javafx.scene.Node node) {
        if (node == null) return;
        TranslateTransition tt = new TranslateTransition(Duration.millis(50), node);
        tt.setFromX(0);
        tt.setByX(10);
        tt.setCycleCount(6);
        tt.setAutoReverse(true);
        tt.play();
    }

    private void showComboEffect() {
        if (combo < 5) return;

        comboLabel.setText("COMBO x" + combo + "!");
        comboLabel.setVisible(true);
        comboLabel.setManaged(true);

        // Pop animation
        ScaleTransition st = new ScaleTransition(Duration.millis(200), comboLabel);
        st.setFromX(0.5); st.setFromY(0.5);
        st.setToX(1.2); st.setToY(1.2);
        st.setAutoReverse(true);
        st.setCycleCount(2);

        FadeTransition ft = new FadeTransition(Duration.millis(500), comboLabel);
        ft.setFromValue(1.0);
        ft.setToValue(0.0);
        ft.setDelay(Duration.millis(500));

        ParallelTransition pt = new ParallelTransition(st, ft);
        pt.setOnFinished(e -> {
            comboLabel.setVisible(false);
            comboLabel.setManaged(false);
        });
        pt.play();
    }

    private void showLevelUpEffect() {
        levelLabel.setVisible(true);
        levelLabel.setManaged(true);

        ScaleTransition st = new ScaleTransition(Duration.millis(300), levelLabel);
        st.setFromX(0.0); st.setFromY(0.0);
        st.setToX(1.5); st.setToY(1.5);

        FadeTransition ft = new FadeTransition(Duration.millis(800), levelLabel);
        ft.setFromValue(1.0);
        ft.setToValue(0.0);
        ft.setDelay(Duration.millis(500));

        ParallelTransition pt = new ParallelTransition(st, ft);
        pt.setOnFinished(e -> {
            levelLabel.setVisible(false);
            levelLabel.setManaged(false);
        });
        pt.play();
    }

    private void animateBackground() {
        for (javafx.scene.Node node : gameRoot.lookupAll(".math-symbol")) {
            TranslateTransition tt = new TranslateTransition(Duration.seconds(2 + Math.random() * 2), node);
            tt.setByY(20);
            tt.setCycleCount(Animation.INDEFINITE);
            tt.setAutoReverse(true);
            tt.setDelay(Duration.seconds(Math.random()));
            tt.play();
        }
    }

    private void resetAnswerButtons() {
        for (int i = 0; i < 4; i++) {
            Button btn = getButtonByIndex(i);
            if (btn != null) {
                btn.setDisable(false);
                btn.setOpacity(1.0);
            }
        }
    }

    private void triggerVisualFeedback(boolean isCorrect, Button sourceBtn) {
        // Visual Feedback: Floating Text
        if (sourceBtn != null) {
            String text = isCorrect ? "Perfect!" : "MISS";
            Label floatingLabel = new Label(text);
            floatingLabel.getStyleClass().addAll("floating-score", isCorrect ? "correct" : "wrong");
            
            // Calculate position relative to the scene/root
            Bounds bounds = sourceBtn.localToScene(sourceBtn.getBoundsInLocal());
            Point2D localPoint = gameRoot.sceneToLocal(bounds.getMinX() + bounds.getWidth() / 2, bounds.getMinY());
            
            floatingLabel.setLayoutX(localPoint.getX() - 20); // Center horizontally
            floatingLabel.setLayoutY(localPoint.getY());
            
            gameRoot.getChildren().add(floatingLabel);
            
            // Animate Up and Fade Out
            TranslateTransition tt = new TranslateTransition(Duration.millis(800), floatingLabel);
            tt.setByY(-50);
            
            FadeTransition ftText = new FadeTransition(Duration.millis(800), floatingLabel);
            ftText.setFromValue(1.0);
            ftText.setToValue(0.0);
            
            ParallelTransition pt = new ParallelTransition(tt, ftText);
            pt.setOnFinished(e -> gameRoot.getChildren().remove(floatingLabel));
            pt.play();
        }
    }
}
