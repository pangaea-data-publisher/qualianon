package org.qualiservice.qualianon.gui.components;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.stage.DirectoryChooser;
import org.qualiservice.qualianon.utility.YJDirectoryChooser;
import javafx.stage.Stage;
import org.qualiservice.qualianon.Main;
import org.qualiservice.qualianon.audit.MessageLogger;
import org.qualiservice.qualianon.model.project.Project;
import org.qualiservice.qualianon.utility.FilePathValidator;
import org.qualiservice.qualianon.utility.StringUtils;
import org.qualiservice.qualianon.utility.UserPreferences;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Logger;


public class ExportProjectController implements Initializable {

    public Label locationLabel;
    public Button exportButton;
    private Project project;

    private final Stage stage;
    private File location;
    private MessageLogger logger;

    public ExportProjectController(Stage stage, Project project, MessageLogger logger) {
        this.stage = stage;
        this.project = project;
        this.logger = logger;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        exportButton.setDisable(true);
        setLocation(project.getProjectDirectory());
    }

    @SuppressWarnings("unused")
    public void onInputProjectName(KeyEvent keyEvent) {
        exportButton.setDisable(FilePathValidator.isValidFile(location));
    }

    public void onBrowse(ActionEvent actionEvent) {
        actionEvent.consume();
        final DirectoryChooser directoryChooser = new YJDirectoryChooser();
        directoryChooser.setTitle("Select Location");

        final File directory = directoryChooser.showDialog(stage.getOwner());
        setLocation(directory);
    }

    @SuppressWarnings("unused")
    public void onCancel(ActionEvent actionEvent) throws IOException {
        actionEvent.consume();
        stage.close();
    }

    @SuppressWarnings("unused")
    public void onExport(ActionEvent actionEvent) throws IOException {
        actionEvent.consume();

        final File directory = location;
        //final File directory =  new File ("/Users/egor/Downloads");
        if (directory == null) return;

        try {
            project.exportProject(directory);
            stage.close(); // Close the stage only if export is successful
            logger.logInfo(String.format(
                    "Project successfully exported to %s", location.toString()
            ));
        } catch (IOException e) {
            logger.logError("Error exporting project", e);
        }

    }

    private void setLocation(File location) {
        if (FilePathValidator.isValidFile(location)) {
            this.location = location;
            locationLabel.setText(location.getAbsolutePath());
            exportButton.setDisable(false);
        }
    }
}
