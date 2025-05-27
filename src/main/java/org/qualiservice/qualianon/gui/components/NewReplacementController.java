package org.qualiservice.qualianon.gui.components;

import javafx.collections.ObservableList;
import org.controlsfx.control.SearchableComboBox;
import org.qualiservice.qualianon.model.categories.Categories;
import org.qualiservice.qualianon.model.categories.CategoryScheme;
import org.qualiservice.qualianon.model.categories.LabelScheme;
import org.qualiservice.qualianon.gui.components.listlookup.ListLookupWindow;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import org.qualiservice.qualianon.model.project.Replacement;
import org.qualiservice.qualianon.utility.StageUtils;
import org.qualiservice.qualianon.utility.StringUtils;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;


public class NewReplacementController implements Initializable {

    public static final String FXML_FILENAME = "newReplacement.fxml";
    @FXML
    private Button insertButton;
    @FXML
    private SearchableComboBox<String> categoriesComboBox;
    @FXML
    private GridPane gridPane;
    @FXML
    private Label selectedTextLabel;
    @FXML
    private Label functionLabel;

    private final Categories categories;
    private final String selectedText;
    private final Stage stage;
    private final UIInterface uiInterface;
    private HashMap<String, Node> labelNodeIndex;
    private boolean isCancelled;
    private Replacement initialReplacement;


    public static NewReplacementController showForNew(Categories categories, String selectedText, UIInterface uiInterface) throws IOException {
        final Stage stage = StageUtils.initStage(uiInterface, "New Replacement");
        final NewReplacementController controller = new NewReplacementController(categories, selectedText, stage, uiInterface);
        StageUtils.open(stage, controller, FXML_FILENAME);
        return controller;
    }

    public static NewReplacementController showForEdit(Replacement replacement, Categories categories, String selectedText, UIInterface uiInterface) throws IOException {
        final Stage stage = StageUtils.initStage(uiInterface, "Update Replacement");
        final NewReplacementController controller = new NewReplacementController(categories, selectedText, stage, uiInterface);
        controller.initialReplacement = replacement;
        StageUtils.open(stage, controller, FXML_FILENAME);
        return controller;
    }

    private NewReplacementController(Categories categories, String selectedText, Stage stage, UIInterface uiInterface) {
        this.categories = categories;
        this.selectedText = selectedText;
        this.stage = stage;
        this.uiInterface = uiInterface;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        selectedTextLabel.setText(StringUtils.ellipsis(selectedText, 20));
        initializeCategoryComboBox();
        insertButton.setDisable(true);
        if (initialReplacement != null) {
            initializeFromReplacement(initialReplacement);
        }
    }

    private void initializeFromReplacement(Replacement replacement) {
        final int i = categories.getCategories().indexOf(replacement.getCategoryScheme());
        categoriesComboBox.getSelectionModel().select(i);
        onCategoryChange(initialReplacement.getCategoryScheme());
        initialReplacement.getLabels().forEach(label -> {
            final Node node = labelNodeIndex.get(label.getLevel());
            if (node == null) return;
            if (node instanceof TextField) {
                final TextField textField = (TextField) node;
                textField.setText(label.getValue());
            } else if (node instanceof ComboBox) {
                final ComboBox<String> comboBox = (ComboBox<String>) node;
                comboBox.getSelectionModel().select(label.getValue());
            }
        });

        insertButton.setText("Update");
        functionLabel.setText("Update");
    }

    private void initializeCategoryComboBox() {
        final List<String> categoryNames = categories.getCategories().stream()
                .map(CategoryScheme::getName)
                .collect(Collectors.toList());
        categoriesComboBox.setItems(FXCollections.observableList(categoryNames));

        final SingleSelectionModel<String> selectionModel = categoriesComboBox.getSelectionModel();
        selectionModel.selectedItemProperty().addListener((observableValue, o, t1) -> {
            onCategoryChange(getSelectedCategoryScheme());
            insertButton.setDisable(false);
        });
    }

    private CategoryScheme getSelectedCategoryScheme() {
        final int selectedIndex = categoriesComboBox.getSelectionModel().getSelectedIndex();
        return categories.getCategories().get(selectedIndex);
    }

    private void onCategoryChange(CategoryScheme categoryScheme) {
        gridPane.getChildren().clear();
        labelNodeIndex = new HashMap<>();

        int row = 0;
        for (final LabelScheme labelScheme : categoryScheme.getLabels()) {
            gridPane.add(new Label(labelScheme.getName()), 0, row);
            final Node inputNode = getInputNode(labelScheme);
            gridPane.add(inputNode, 1, row);
            labelNodeIndex.put(labelScheme.getName(), inputNode);
            row++;
        }

        if (categoryScheme.hasCategoryList()) {
            final Button listLookupButton = makeListLookupButton(categoryScheme);
            gridPane.add(listLookupButton, 2, 0);
        }
    }

    private Button makeListLookupButton(CategoryScheme categoryScheme) {
        final Button listLookupButton = new Button("List Lookup");
        listLookupButton.setPrefWidth(195.0);
        listLookupButton.setOnAction(actionEvent -> {
            actionEvent.consume();
            final Map<String, String> result = ListLookupWindow
                    .openWindow(categoryScheme, categories.getCodingList(categoryScheme), selectedText, stage.getOwner(), uiInterface.getMessageLogger());
            if (result == null) return;
            for (final Map.Entry<String, String> entry : result.entrySet()) {
                final Node node = labelNodeIndex.get(entry.getKey());
                if (node instanceof TextField) {
                    ((TextField) node).setText(entry.getValue());
                }
            }
        });
        return listLookupButton;
    }

    private Node getInputNode(LabelScheme labelScheme) {
        if (labelScheme.hasChoices()) {
            final ComboBox<String> comboBox = new ComboBox<>();
            final ObservableList<String> items = FXCollections.observableList(labelScheme.getChoices());
            items.add(0, null);
            comboBox.setItems(items);
            return comboBox;

        } else {
            return new TextField();
        }
    }

    private String getLabelValueFromInputNode(Node inputNode) {
        if (inputNode instanceof TextField) {
            return ((TextField) inputNode).getText();
        } else if (inputNode instanceof ComboBox) {
            final String selectedItem = ((ComboBox<String>) inputNode).getSelectionModel().getSelectedItem();
            if (selectedItem == null) return "";
            return selectedItem;
        }
        return null;
    }

    @SuppressWarnings("unused")
    public void onCancel(ActionEvent actionEvent) {
        actionEvent.consume();
        isCancelled = true;
        stage.close();
    }

    public void onInsert(ActionEvent actionEvent) {
        actionEvent.consume();
        stage.close();
    }

    public boolean isCancelled() {
        return isCancelled;
    }

    public Replacement getResultReplacement() {
        final CategoryScheme categoryScheme = getSelectedCategoryScheme();
        final List<org.qualiservice.qualianon.model.project.Label> labels = categoryScheme.getLabels().stream()
                .map(labelScheme -> {
                    final String labelName = labelScheme.getName();
                    final Node inputNode = labelNodeIndex.get(labelName);
                    final String labelValue = getLabelValueFromInputNode(inputNode);
                    return new org.qualiservice.qualianon.model.project.Label(labelName, labelValue);
                })
                .collect(Collectors.toList());

        final Replacement replacement = new Replacement(categoryScheme, uiInterface.getMessageLogger())
                .setLabels(labels);
        if (initialReplacement != null) {
            replacement.setId(initialReplacement.getId());
        }
        return replacement;
    }
}
