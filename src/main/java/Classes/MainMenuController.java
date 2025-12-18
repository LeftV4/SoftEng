package Classes;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.kordamp.bootstrapfx.BootstrapFX;

import java.io.IOException;
import java.util.Objects;

public class MainMenuController extends Application {

    public void start(Stage primaryStage) throws IOException {
        MathEngine engine = new MathEngine();
        System.out.println("--- TESTING MATH ENGINE ---");
        System.out.println(engine.generateQuestion(1));
        System.out.println(engine.generateQuestion(7));
        System.out.println(engine.generateQuestion(15));
        System.out.println("---------------------------");

        FXMLLoader fxmlLoader = new FXMLLoader(GameController.class.getResource("/GameScene.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 600);

        scene.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());
        primaryStage.setScene(scene);
        primaryStage.setTitle("Math Quiz!");
        primaryStage.setResizable(false);
        primaryStage.show();
    }
}
