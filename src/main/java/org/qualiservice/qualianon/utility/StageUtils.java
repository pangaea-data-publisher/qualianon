package org.qualiservice.qualianon.utility;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.qualiservice.qualianon.gui.components.UIInterface;
import org.qualiservice.qualianon.gui.components.WelcomeController;

import java.io.IOException;


public class StageUtils {

    public static Stage initStage(UIInterface uiInterface, String title) {
        final Stage stage = new Stage(StageStyle.DECORATED);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.setTitle(title);
        stage.initOwner(uiInterface.getStage());
        return stage;
    }

    public static void open(Stage stage, Object controller, String fxmlFilename) throws IOException {
        final FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(WelcomeController.class.getClassLoader()
                .getResource(fxmlFilename)
        );
        fxmlLoader.setController(controller);
        final Parent fxml = fxmlLoader.load();

        final Scene scene = new Scene(fxml);
        stage.setScene(scene);
        stage.showAndWait();
    }
}
