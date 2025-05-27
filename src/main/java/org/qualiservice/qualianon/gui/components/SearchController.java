package org.qualiservice.qualianon.gui.components;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import org.qualiservice.qualianon.model.project.SearchParams;
import org.qualiservice.qualianon.utility.StageUtils;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;


public class SearchController implements Initializable {

    @FXML
    private Button findButton;
    @FXML
    private TextField searchText;
    @FXML
    private CheckBox unmarkedCheckbox;
    @FXML
    private CheckBox matchCaseCheckbox;
    @FXML
    private CheckBox wholeWordsCheckbox;
    @FXML
    private CheckBox activeDocumentCheckbox;

    private final SearchParams initialSearchParams;
    private final Stage stage;
    private final boolean activeDocumentAvailable;
    private boolean isCancelled;


    public static SearchController show(UIInterface uiInterface, SearchParams initialSearchParams, boolean activeDocumentAvailable) throws IOException {
        final Stage stage = StageUtils.initStage(uiInterface, "Find");
        final SearchController controller = new SearchController(initialSearchParams, activeDocumentAvailable, stage);
        StageUtils.open(stage, controller, "search.fxml");
        return controller;
    }

    public SearchController(SearchParams initialSearchParams, boolean activeDocumentAvailable, Stage stage) {
        this.initialSearchParams = initialSearchParams;
        this.activeDocumentAvailable = activeDocumentAvailable;
        this.stage = stage;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        findButton.setDisable(true);
        searchText.setText(initialSearchParams.getText());
        unmarkedCheckbox.setSelected(initialSearchParams.isUnmarked());
        matchCaseCheckbox.setSelected(initialSearchParams.isMatchCase());
        wholeWordsCheckbox.setSelected(initialSearchParams.isWholeWords());
        if (activeDocumentAvailable) {
            activeDocumentCheckbox.setSelected(initialSearchParams.isActiveDocumentFilter());
        } else {
            activeDocumentCheckbox.setSelected(false);
            activeDocumentCheckbox.setDisable(true);
        }
        onSearchTextChanged(null);
    }

    @SuppressWarnings("unused")
    public void onSearchTextChanged(KeyEvent actionEvent) {
        findButton.setDisable(searchText.getText().length() < 2);
    }

    @SuppressWarnings("unused")
    public void onFind(ActionEvent actionEvent) {
        stage.close();
    }

    @SuppressWarnings("unused")
    public void onCancel(ActionEvent actionEvent) {
        actionEvent.consume();
        isCancelled = true;
        stage.close();
    }

    public boolean isCancelled() {
        return isCancelled;
    }

    public SearchParams getSearchParams() {
        return new SearchParams(
                searchText.getText().trim(),
                null,
                null,
                wholeWordsCheckbox.isSelected(),
                matchCaseCheckbox.isSelected(),
                unmarkedCheckbox.isSelected(),
                activeDocumentCheckbox.isSelected()
        );
    }
}
