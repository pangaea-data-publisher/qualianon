package org.qualiservice.qualianon.gui.components;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import org.qualiservice.qualianon.Main;
import org.qualiservice.qualianon.utility.UserPreferences;
import org.qualiservice.qualianon.utility.StringUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;


public class NewProjectController implements Initializable {

    public TextField projectNameInput;
    public Label locationLabel;
    public Button createButton;

    private final Stage stage;
    private File location;

    public NewProjectController(Stage stage) {
        this.stage = stage;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        createButton.setDisable(true);
        setLocation(new File(System.getProperty("user.dir")));
    }

    @SuppressWarnings("unused")
    public void onInputProjectName(KeyEvent keyEvent) {
        createButton.setDisable(StringUtils.isBlank(projectNameInput.getText()));
    }

    public void onBrowse(ActionEvent actionEvent) {
        actionEvent.consume();
        final DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select Location");

        final File directory = directoryChooser.showDialog(stage.getOwner());
        if (directory != null && directory.exists()) {
            setLocation(directory);
        }
    }

    @SuppressWarnings("unused")
    public void onCancel(ActionEvent actionEvent) throws IOException {
        actionEvent.consume();
        stage.close();
        Main.welcome();
    }

    @SuppressWarnings("unused")
    public void onCreate(ActionEvent actionEvent) throws IOException {
        actionEvent.consume();
        final File projectFolder = new File(location.getAbsolutePath() + "/" + projectNameInput.getText());
        //noinspection ResultOfMethodCallIgnored
        projectFolder.mkdirs();

        try {
            MainController
                    .openWindow()
                    .newProject(projectFolder);
            UserPreferences.saveCurrentProjectPath(projectFolder.getAbsolutePath());
            stage.close();
        } catch (ProjectAlreadyExistsException ignored) {
        }
    }

    private void setLocation(File location) {
        this.location = location;
        locationLabel.setText(location.getAbsolutePath());
    }
}
