package org.qualiservice.qualianon.gui.components.listimport;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.qualiservice.qualianon.audit.MessageLogger;
import org.qualiservice.qualianon.gui.components.UIInterface;
import org.qualiservice.qualianon.gui.components.WelcomeController;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;


public class ListImportController implements Initializable {

    @FXML
    private ListView<ImportSettings> typeList;
    @FXML
    private VBox infoPanel;

    private final Stage stage;
    private final MessageLogger messageLogger;
    private boolean isCancelled;
    private Importer importer;
    private File file;

    public static ListImportController show(UIInterface uiInterface) throws IOException {
        final Stage stage = new Stage(StageStyle.DECORATED);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.setTitle("Import List");
        stage.initOwner(uiInterface.getStage());

        final ListImportController controller = new ListImportController(stage, uiInterface.getMessageLogger());

        final FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(WelcomeController.class.getClassLoader()
                .getResource("listImport.fxml")
        );
        fxmlLoader.setController(controller);
        final Parent fxml = fxmlLoader.load();

        final Scene scene = new Scene(fxml);
        stage.setScene(scene);
        stage.showAndWait();

        return controller;
    }

    private ListImportController(Stage stage, MessageLogger messageLogger) {
        this.stage = stage;
        this.messageLogger = messageLogger;
        isCancelled = true;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        final ObservableList<ImportSettings> items = typeList.getItems();
        items.addAll(ImportSettingsFactory.make(messageLogger));

        typeList.getSelectionModel().selectedItemProperty().addListener((observableValue, importSettings, t1) -> {
            infoPanel.getChildren().clear();
            infoPanel.getChildren().addAll(t1.getInstructions());
        });

        typeList.getSelectionModel().select(0);
    }

    public boolean isCancelled() {
        return isCancelled;
    }

    public Importer getImporter() {
        return importer;
    }

    public File getFile() {
        return file;
    }

    @SuppressWarnings("unused")
    public void onOkAction(ActionEvent actionEvent) {
        actionEvent.consume();
        final ImportSettings importSettings = typeList.getSelectionModel().getSelectedItem();

        final FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Import " + importSettings.getTitle());
        fileChooser.getExtensionFilters().addAll(importSettings.getExtensionFilter());
        file = fileChooser.showOpenDialog(stage);
        if (file == null) return;

        importer = importSettings.getImporter();
        isCancelled = false;
        stage.close();
    }

    @SuppressWarnings("unused")
    public void onCancelAction(ActionEvent actionEvent) {
        actionEvent.consume();
        stage.close();
    }

}
