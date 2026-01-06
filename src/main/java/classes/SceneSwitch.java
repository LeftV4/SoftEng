package classes;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.kordamp.bootstrapfx.BootstrapFX;

import java.io.IOException;
import java.util.Objects;

public class SceneSwitch {
    public SceneSwitch(AnchorPane rootPane, String fxml) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(fxml)));
        //root.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        root.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());
        Stage primarystage = (Stage) rootPane.getScene().getWindow();
        primarystage.setScene(new Scene(root));
        primarystage.show();
    }
}
