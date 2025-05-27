package org.qualiservice.qualianon.gui.components;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.qualiservice.qualianon.model.project.ProjectNotInitializedException;
import org.qualiservice.qualianon.utility.UserPreferences;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


public class RecentProjectController {

    private final Stage parentStage;
    private final Stage currentStage;
    private VBox wrapperVbox = new VBox();

    public RecentProjectController(Stage parentStage, ArrayList<String> projectPathStorage) {
        this.parentStage = parentStage;
        this.currentStage =  new Stage(StageStyle.DECORATED);
        this.currentStage.initModality(Modality.APPLICATION_MODAL);
        this.currentStage.setTitle("Open Recent Projects");
        initVbox(projectPathStorage);
    }
    private void initVbox (ArrayList<String> projectPathStorage){
        VBox vbox = new VBox();
        vbox.setSpacing(10);
        vbox.setPadding(new Insets(40)); // Set padding inside the VBox

        ListView<String> listView = new ListView<>();
        listView.setPrefSize(800,300);
        ObservableList<String> observableProjectPaths = FXCollections.observableArrayList(projectPathStorage);

        listView.setItems( observableProjectPaths);
        listView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                String selectedPath = listView.getSelectionModel().getSelectedItem();
                if (selectedPath != null) {
                    try {
                        onOpenParticularRecentProject(selectedPath);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });

        wrapperVbox.getChildren().add(listView);
    }

    private VBox getVbox() {
        return wrapperVbox;
    }
    public void show(){
        final Scene scene = new Scene(this.getVbox());
        this.currentStage.setScene(scene);
        this.currentStage.sizeToScene();
        this.currentStage.show();
    }

    private void onOpenParticularRecentProject(String projPath) throws IOException {
        File directory = new File(projPath);
        if (directory != null && directory.exists()) {

            try {
                MainController
                        .openWindow()
                        .openProject(directory);
                UserPreferences.saveCurrentProjectPath(directory.getAbsolutePath());
                this.parentStage.close();
                this.currentStage.close();
            } catch (ProjectNotInitializedException ignored) {
                System.out.println("PrNotInit");
            }
        }
    }

}
