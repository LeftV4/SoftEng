package Classes;
import eu.hansolo.tilesfx.Tile;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.util.Duration;

public class GameController {
    @FXML private Label questionLabel;
    @FXML private Tile timerTile;
    @FXML private Button btn1, btn2, btn3, btn4;

    private MathEngine engine;
    private Question currentQuestion;
    private int score = 0;
    private int difficulty = 1;
    private Timeline timeline;
    private static final int TIME_LIMIT = 10;


    private void startTimer() {
        // 1. Reset the visual tile
        timerTile.setValue(TIME_LIMIT);

        // 2. Stop any old timer if it's running
        if (timeline != null) {
            timeline.stop();
        }

        // 3. Create a new "Ticker" that runs every 1 second
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            // This code runs every second
            double currentTime = timerTile.getValue();

            if (currentTime > 0) {
                timerTile.setValue(currentTime - 1); // Tick down
            } else {
                // TIME IS UP!
                timeline.stop();
                handleGameOver(); // We need to move the "Game Over" logic here
            }
        }));

        // 4. Set it to repeat indefinitely (until we stop it)
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    public void initialize() {
        engine = new MathEngine();

        // This runs when the screen starts
        questionLabel.setText("Press a button to start!");

        timerTile.setSkinType(Tile.SkinType.CIRCULAR_PROGRESS);
        timerTile.setTitle("");
        timerTile.setText("Seconds");
        timerTile.setUnit("s");
        timerTile.setDecimals(0);
        timerTile.setValueVisible(true);
        timerTile.setMaxValue(10);
        timerTile.setValue(10);

        loadNextQuestion();

        // Configure the Timer Tile to look cool
        btn1.setOnAction(e -> checkAnswer(0));
        btn2.setOnAction(e -> checkAnswer(1));
        btn3.setOnAction(e -> checkAnswer(2));
        btn4.setOnAction(e -> checkAnswer(3));
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
            System.out.println("Correct!");
            score++;

            if (score % 3 == 0) difficulty++;

            loadNextQuestion();
        } else {
            System.out.println("Wrong! Game Over.");
            questionLabel.setText("GAME OVER! Score: " + score);
            btn1.setDisable(true);
            btn2.setDisable(true);
            btn3.setDisable(true);
            btn4.setDisable(true);
        }
    }
    private void handleGameOver() {
        System.out.println("GAME OVER!");
        questionLabel.setText("GAME OVER! Score: " + score);

        // Stop the timer so it doesn't keep ticking
        if (timeline != null) timeline.stop();

        // Disable buttons
        btn1.setDisable(true);
        btn2.setDisable(true);
        btn3.setDisable(true);
        btn4.setDisable(true);
    }
}


