package org.qualiservice.qualianon.gui.components.listlookup;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.qualiservice.qualianon.gui.tools.ColorConvert;
import org.qualiservice.qualianon.model.categories.CategoryScheme;
import org.qualiservice.qualianon.model.categories.CodingList;
import org.qualiservice.qualianon.utility.StringUtils;

import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;


public class SingleListMarkController implements Initializable, ListLookupWindow {

    @FXML
    public TextField searchTextField;
    @FXML
    public Button okButton;
    @FXML
    public Label patternLabel;
    @FXML
    public TableView<List<StringProperty>> searchTable;

    private CodingList codingList;
    private int selectedRow;
    private Map<String, String> selectedInfo;
    private List<String> parameters;

    /**
     * filling the window with pattern and the Replacements from the listCodingCategory
     *
     * @param pattern of the marked text -> will be used as default for the searchTextField
     */
    public void createWindow(String pattern, CodingList codingList, CategoryScheme category) {

        this.codingList = codingList;
        if (!StringUtils.isBlank(pattern)) {
            patternLabel.setText("Selected pattern in text: " + pattern);
        }
        searchTextField.setText(pattern);
        searchTextField.setOnAction(event -> {
            event.consume();
            searchAction(null);
        });
        parameters = codingList.getHeader();

        for (int i = 0; i < parameters.size(); i++) {
            String parameter = parameters.get(i);
            TableColumn<List<StringProperty>, String> parameterColumn = new TableColumn<>(parameter);
            final int finalI = i;
            parameterColumn.setCellValueFactory(cellValue -> cellValue.getValue().get(finalI));
            searchTable.getColumns().add(parameterColumn);
        }

        final String color = ColorConvert.toCss(category.getColor());
        searchTable.setStyle("-fx-selection-bar: " + color + "; -fx-selection-bar-non-focused: " + color + ";");
        searchTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        selectedRow = -1;
        searchAction(null);
    }

    /**
     * searching for Identifiers of the Replacements (triggers by clicking the search Button)
     *
     * @param e -> MouseEvent
     */
    public void searchAction(MouseEvent e) {
        searchTable.getItems().clear();
        okButton.setDisable(true);
        final String searchValue = searchTextField.getText().toLowerCase().trim();

        final List<List<StringProperty>> results = codingList.getData().stream()
                .filter(strings -> matchesSearch(strings, searchValue))
                .map(row -> row.stream()
                        .map(s -> (StringProperty) new SimpleStringProperty(s))
                        .collect(Collectors.toList()))
                .collect(Collectors.toList());

        searchTable.getItems().addAll(results);
        if (e != null) {
            e.consume();
        }
    }

    public static boolean matchesSearch(List<String> strings, String searchValue) {
        for (String string : strings) {
            if (string.toLowerCase().contains(searchValue)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Two Actions, which triggers by Clicking on the SearchTable
     * 1. chosing the selected Replacement and save it in selectedReplacement
     * 2. doubleClick -> confirm selection (okButton-Click)
     *
     * @param e -> MouseEvent
     */
    public void searchTableClick(MouseEvent e) {
        if (searchTable.getSelectionModel() != null) {
            Object row = searchTable.getSelectionModel().getSelectedItem();

            if (row instanceof ArrayList) {
                ArrayList rowList = (ArrayList) row;
                selectedRow = 1;
                okButton.setDisable(false);

                selectedInfo = new HashMap<>();
                for (int i = 0; i < parameters.size(); i++) {
                    StringProperty prop = (StringProperty) rowList.get(i);
                    selectedInfo.put(parameters.get(i), prop.getValue());
                }
            }
            if (e.getClickCount() == 2 && !e.isConsumed() && row instanceof ArrayList) {
                okAction(new ActionEvent());
            }
        } else {
            selectedRow = -1;
            selectedInfo = null;
            okButton.setDisable(true);
        }
        e.consume();
    }

    /**
     * ActionEvent: clicking the "OK"-Button:
     * confirms the changes and returns to Main-Frame
     *
     * @param e ActionEvent
     */
    public void okAction(ActionEvent e) {
        if (selectedRow > -1) {
            Stage stage = (Stage) okButton.getScene().getWindow();
            stage.close();
        }
        e.consume();
    }

    /**
     * ActionEvent: clicking the "Cancel"-Button:
     * discards the changes and returns to Main-Frame
     *
     * @param e ActionEvent
     */
    public void cancelAction(ActionEvent e) {
        e.consume();
        selectedInfo = null;
        selectedRow = -1;
        Stage stage = (Stage) okButton.getScene().getWindow();
        stage.close();
    }

    /**
     * initialize-Method, which is called directly after the construction
     */
    public void initialize(URL location, ResourceBundle resources) {
    }

    public Map<String, String> getSelectedInfo() {
        return selectedInfo;
    }
}
