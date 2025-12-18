package classes;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.util.Duration;

public class GameController {

    @FXML private Label scoreLabel;
    @FXML private VBox questionBox;
    @FXML private Label timerLabel;
    @FXML private Arc timerArc;
    @FXML private Button restartBtn;
    @FXML private Label questionLabel;
    @FXML private Button btn1, btn2, btn3, btn4;

    private MathEngine engine;
    private Question currentQuestion;
    private int score = 0;
    private int difficulty = 1;
    private Timeline timeline;
    private static final int TIME_LIMIT = 10;


    public void initialize() {
        engine = new MathEngine();

        // This runs when the screen starts
        questionLabel.setText("Press a button to start!");

        loadNextQuestion();

        // Configure the Timer Tile to look cool
        btn1.setOnAction(e -> checkAnswer(0));
        btn2.setOnAction(e -> checkAnswer(1));
        btn3.setOnAction(e -> checkAnswer(2));
        btn4.setOnAction(e -> checkAnswer(3));
        restartBtn.setOnAction(e -> restartGame());
    }

    private void restartGame() {
            // 1. Reset Game State
            score = 0;
            difficulty = 1;

            // 2. Reset UI
            questionLabel.setText("Get Ready...");
            restartBtn.setVisible(false);
            restartBtn.setManaged(false); // Hide it again

            // 3. Re-enable Buttons
            btn1.setDisable(false);
            btn2.setDisable(false);
            btn3.setDisable(false);
            btn4.setDisable(false);

            // 4. Start fresh!
            loadNextQuestion();
        }

    private void startTimer() {
        timerLabel.setText(String.valueOf(TIME_LIMIT));
        timerArc.setLength(360);
        timerArc.setStartAngle(90);
        timerArc.setStroke(Color.GREEN);

        if (timeline != null) timeline.stop();

        long startTime = System.currentTimeMillis();

        timeline = new Timeline(new KeyFrame(Duration.millis(16), e -> {

            long now = System.currentTimeMillis();
            long elapsedMillis = now - startTime;

            // Formula: 360 - (360 * (elapsed / total_duration))
            double totalDurationMillis = TIME_LIMIT * 1000.0;
            double remainingAngle = 360.0 - (360.0 * (elapsedMillis / totalDurationMillis));

            if (remainingAngle <= 0) {
                timerArc.setLength(0);
                timerLabel.setText("0");
                timeline.stop();
                handleGameOver();
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

        startTimer();
        currentQuestion = engine.generateQuestion(difficulty);

        questionLabel.setText(currentQuestion.getExpression() + " = ?");

        btn1.setText(String.valueOf(currentQuestion.getChoices().get(0)));
        btn2.setText(String.valueOf(currentQuestion.getChoices().get(1)));
        btn3.setText(String.valueOf(currentQuestion.getChoices().get(2)));
        btn4.setText(String.valueOf(currentQuestion.getChoices().get(3)));
    }

    private void checkAnswer(int buttonIndex) {
        int selectedValue = currentQuestion.getChoices().get(buttonIndex);

        if (selectedValue == currentQuestion.getCorrectAnswer()) {
            score++;
            scoreLabel.setText("Score: " + score);

            if (score % 3 == 0) difficulty += 3;

            loadNextQuestion();
        } else {
            handleGameOver();
        }
    }
    private void handleGameOver() {
        questionLabel.setText("GAME OVER! Score: " + score);

        if (timeline != null) timeline.stop();

        // Disable buttons
        btn1.setDisable(true);
        btn2.setDisable(true);
        btn3.setDisable(true);
        btn4.setDisable(true);

        restartBtn.setVisible(true);
        restartBtn.setManaged(true);
    }
}


