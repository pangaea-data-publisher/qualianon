package org.qualiservice.qualianon;

import org.qualiservice.qualianon.gui.components.WelcomeController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URL;

public class Main extends Application {

    /**
     * creating and starting the GUI with Controller
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        welcome();
    }

    public static void welcome() throws IOException {

        final Stage stage = new Stage(StageStyle.DECORATED);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Welcome to " + Version.QUALI_ANON_TITLE);

        final WelcomeController controller = new WelcomeController(stage);

        final ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        final FXMLLoader fxmlLoader = new FXMLLoader();
        final URL location = classloader.getResource("welcome.fxml");
        fxmlLoader.setLocation(location);
        fxmlLoader.setController(controller);
        final Parent fxml = fxmlLoader.load();

        final Scene scene = new Scene(fxml);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * second main()-Methode: launches the GUI-creation
     */
    public static void main(String[] args) {
        launch(args);
    }

}
