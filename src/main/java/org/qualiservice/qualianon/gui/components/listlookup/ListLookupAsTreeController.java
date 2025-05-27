package org.qualiservice.qualianon.gui.components.listlookup;

import org.qualiservice.qualianon.model.categories.CodingList;
import org.qualiservice.qualianon.model.categories.CategoryScheme;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.qualiservice.qualianon.utility.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class ListLookupAsTreeController implements ListLookupWindow {

    @FXML
    public Label patternLabel;
    @FXML
    public TextField searchTextField;
    @FXML
    public TreeView<String> treeView;
    @FXML
    public Button okButton;

    private CodingList codingList;
    private CategoryScheme category;
    private Map<String, String> selectedInfo;

    @Override
    public void createWindow(String pattern, CodingList codingList, CategoryScheme category) {
        this.codingList = codingList;
        this.category = category;
        if (!StringUtils.isBlank(pattern)) {
            patternLabel.setText("Selected pattern in text: " + pattern);
        }
        searchTextField.setText(pattern);
        searchTextField.setOnAction(event -> {
            event.consume();
            populateTreeView(false);
        });
        populateTreeView(true);
    }

    private void populateTreeView(boolean searchForAllIfNothingFound) {
        final int levels = codingList.getHeader().size() + 1;// first one for root
        final TreeItem<String>[] levelItems = new TreeItem[levels];
        levelItems[0] = new TreeItem<>(category.getName());
        final String searchValue = searchTextField.getText().toLowerCase().trim();

        int entryCount = 0;
        for (int row = 0; row < codingList.getData().size(); row++) {
            if (!SingleListMarkController.matchesSearch(codingList.getData().get(row), searchValue)) continue;
            for (int col = 1; col < levels; col++) {
                if (codingList.getData().get(row).size() < col) continue;
                final String levelValue = codingList.getData().get(row).get(col - 1);
                if (levelItems[col] == null || !levelItems[col].getValue().equals(levelValue)) {
                    levelItems[col] = new TreeItem<>(levelValue);
                    levelItems[col - 1].getChildren().add(levelItems[col]);
                    entryCount++;
                }
            }
        }
        levelItems[0].setExpanded(true);
        treeView.setRoot(levelItems[0]);

        if (entryCount == 0 && !StringUtils.isBlank(searchValue) && searchForAllIfNothingFound) {
            searchTextField.setText("");
            populateTreeView(false);
        }
        if (entryCount < 20) {
            expandTreeView(levelItems[0]);
        }
    }

    private void expandTreeView(TreeItem<?> item) {
        if (item != null && !item.isLeaf()) {
            item.setExpanded(true);
            for (TreeItem<?> child : item.getChildren()) {
                expandTreeView(child);
            }
        }
    }

    @Override
    public Map<String, String> getSelectedInfo() {
        return selectedInfo;
    }

    public void okAction(ActionEvent actionEvent) {
        actionEvent.consume();
        final TreeItem<String> selectedItem = treeView.getSelectionModel().getSelectedItem();
        if (selectedItem == null) return;

        TreeItem<String> searchItem = selectedItem;
        int selectionLevel = 0;
        while (searchItem.getParent() != null) {
            selectionLevel++;
            searchItem = searchItem.getParent();
        }

        searchItem = selectedItem;
        selectedInfo = new HashMap<>();
        for (int i = selectionLevel - 1; i >= 0; i--) {
            selectedInfo.put(codingList.getHeader().get(i), searchItem.getValue());
            searchItem = searchItem.getParent();
        }
        for (int i = selectionLevel; i < codingList.getHeader().size(); i++) {
            selectedInfo.put(codingList.getHeader().get(i), "");
        }
        closeWindow();
    }

    public void cancelAction(ActionEvent actionEvent) {
        actionEvent.consume();
        selectedInfo = null;
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) okButton.getScene().getWindow();
        stage.close();
    }

    public void searchAction(MouseEvent mouseEvent) {
        mouseEvent.consume();
        populateTreeView(false);
    }

}
