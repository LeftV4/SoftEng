package classes;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.animation.Animation;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.kordamp.bootstrapfx.BootstrapFX;

import java.io.IOException;



public class MainMenuController extends Application {

    // Static variable to access the selected mode from GameController
    public static String gameMode = "STANDARD";

    @FXML private AnchorPane rootPane;
    @FXML private Label titleLabel;
    @FXML private Button playBtn;
    @FXML private Button leaderboardBtn;
    @FXML private Button themeBtn;
    @FXML private Button quitBtn;
    @FXML private Button muteBtn;
    @FXML private StackPane modeSelectionPopup;

    @FXML
    public void initialize() {
        SoundManager.playLoop(SoundManager.Sound.MENU,0.50);
        ThemeManager.applyTheme(rootPane);
        animateBackground();
        animateTitle();
        playBtn.setOnMousePressed(e -> SoundManager.play(SoundManager.Sound.CLICK));
        leaderboardBtn.setOnMousePressed(e -> SoundManager.play(SoundManager.Sound.CLICK));
        themeBtn.setOnMousePressed(e -> SoundManager.play(SoundManager.Sound.CLICK));
        quitBtn.setOnMousePressed(e -> SoundManager.play(SoundManager.Sound.CLICK));
        
        updateMuteButton();
        muteBtn.setOnAction(e -> toggleMute());
    }

    public void start(Stage primaryStage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(GameController.class.getResource("/MainMenu.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        scene.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());
        primaryStage.setScene(scene);
        primaryStage.setTitle("Math Quiz!");
        primaryStage.setResizable(false);
        primaryStage.setOnCloseRequest(e -> {
            Platform.exit();
            System.exit(0);
        });
        primaryStage.show();
        SoundManager.preloadSounds();
    }



    @FXML
    public void PlayGame() {
        modeSelectionPopup.setVisible(true);
    }

    @FXML
    public void closePopup() {
        modeSelectionPopup.setVisible(false);
    }

    @FXML
    public void startStandard() throws IOException {
        gameMode = "STANDARD";
        SoundManager.stopAllLoops();
        new SceneSwitch(rootPane, "/GameScene.fxml");
    }

    @FXML
    public void startBinary() throws IOException {
        gameMode = "BINARY";
        SoundManager.stopAllLoops();
        new SceneSwitch(rootPane, "/GameScene.fxml");
    }

    @FXML
    public void LeaderBoard() throws IOException {
        new SceneSwitch(rootPane, "/Leaderboard.fxml");
    }

    @FXML
    public void ChangeTheme() {
        ThemeManager.toggleTheme(rootPane);
    }

    @FXML
    public void Quit() {
        Platform.exit();
    }

    private void toggleMute() {
        SoundManager.toggleMute();
        updateMuteButton();
        SoundManager.play(SoundManager.Sound.CLICK);
    }

    private void updateMuteButton() {
        muteBtn.setText(SoundManager.isMuted() ? "🔇" : "🔊");
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

    private void animateTitle() {
        ScaleTransition st = new ScaleTransition(Duration.seconds(1.5), titleLabel);
        st.setFromX(1.0); st.setFromY(1.0);
        st.setToX(1.1); st.setToY(1.1);
        st.setCycleCount(Animation.INDEFINITE);
        st.setAutoReverse(true);
        st.play();
    }
}
