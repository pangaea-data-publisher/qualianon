package org.qualiservice.qualianon.gui.components;

import javafx.scene.control.Button;
import org.qualiservice.qualianon.Version;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.qualiservice.qualianon.model.project.ProjectNotInitializedException;
import org.qualiservice.qualianon.utility.UserPreferences;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class WelcomeController implements Initializable {

    @FXML
    public Label versionLabel;
    @FXML
    public Button openRecentProjectBtn;

    private final Stage stage;
    private final ArrayList<String> projectPathStorage = UserPreferences.loadProjectPathStorage();

    public WelcomeController(Stage stage) {
        this.stage = stage;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        versionLabel.setText("Version " + Version.VERSION);
        if (projectPathStorage.isEmpty()){
           openRecentProjectBtn.setDisable(true);
        } else {
            openRecentProjectBtn.setDisable(false);
        }
    }

    @SuppressWarnings("unused")
    public void onNewProject(ActionEvent actionEvent) throws IOException {
        actionEvent.consume();
        this.stage.close();

        final Stage stage = new Stage(StageStyle.DECORATED);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("New Project");

        final NewProjectController controller = new NewProjectController(stage);

        final FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getClassLoader().getResource("newProject.fxml"));
        fxmlLoader.setController(controller);
        final Parent fxml = fxmlLoader.load();

        final Scene scene = new Scene(fxml);
        stage.setScene(scene);
        stage.show();
    }

    @SuppressWarnings("unused")
    public void onOpenProject(ActionEvent e) throws IOException {
        e.consume();
        final DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select Project Folder");

        File directory = directoryChooser.showDialog(stage.getOwner());
        if (directory != null && directory.exists()) {

            try {
                MainController
                        .openWindow()
                        .openProject(directory);
                UserPreferences.saveCurrentProjectPath(directory.getAbsolutePath());
                stage.close();
            } catch (ProjectNotInitializedException ignored) {
            }
        }
    }
    @SuppressWarnings("unused")
    public void onOpenRecentProject(ActionEvent e) {
        e.consume(); //is it really necessary to consume it?

        final RecentProjectController controller = new RecentProjectController(this.stage,projectPathStorage);
        controller.show();

    }

}
