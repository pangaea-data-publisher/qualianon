package org.qualiservice.qualianon.gui.components.replacementstab;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ListChangeListener;
import javafx.scene.control.*;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import org.qualiservice.qualianon.gui.components.NewReplacementController;
import org.qualiservice.qualianon.gui.components.UIInterface;
import org.qualiservice.qualianon.gui.tools.GuiUtility;
import org.qualiservice.qualianon.gui.tools.StandardDialogs;
import org.qualiservice.qualianon.model.categories.CategoryScheme;
import org.qualiservice.qualianon.model.commands.DeleteReplacementCommand;
import org.qualiservice.qualianon.model.commands.EditReplacementCommand;
import org.qualiservice.qualianon.model.project.AnonymizedFile;
import org.qualiservice.qualianon.model.project.Project;
import org.qualiservice.qualianon.model.project.Replacement;
import org.qualiservice.qualianon.model.project.ReplacementCollection;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;


public class ReplacementListComponent extends AnchorPane {

    private final TreeTableView<MyItem> treeTableView;
    private final ReplacementCollection replacementCollection;
    private final Project project;
    private final List<SelectionListener> listeners;


    public ReplacementListComponent(ReplacementCollection replacementCollection, Project project, UIInterface uiInterface) {
        this.replacementCollection = replacementCollection;
        this.project = project;

        final TreeTableColumn<MyItem, String> categoryColumn = new TreeTableColumn<>("Category / Originals");
        categoryColumn.setCellValueFactory(new MyOriginalValueFactory());
        final TreeTableColumn<MyItem, String> originalColumn = new TreeTableColumn<>("Labels");
        originalColumn.setCellValueFactory(new MyLabelsValueFactory());
        final TreeTableColumn<MyItem, String> occurrencesColumn = new TreeTableColumn<>("#");
        occurrencesColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("occurrences"));

        treeTableView = new TreeTableView<>();
        treeTableView.setShowRoot(false);
        treeTableView.getColumns().add(categoryColumn);
        treeTableView.getColumns().add(originalColumn);
        treeTableView.getColumns().add(occurrencesColumn);
        treeTableView.setRowFactory(myItemTreeTableView -> {

            final MenuItem showMarkersMenuItem = new MenuItem("Show markers");
            final MenuItem editMenuItem = new MenuItem("Edit");
            final MenuItem deleteMenuItem = new MenuItem("Delete");

            final ContextMenu contextMenu = new ContextMenu();
            contextMenu.getItems().add(showMarkersMenuItem);
            contextMenu.getItems().add(editMenuItem);
            contextMenu.getItems().add(deleteMenuItem);

            final RowWithContextMenu row = new RowWithContextMenu(contextMenu);
            row.setOnMouseClicked(mouseEvent -> {
                mouseEvent.consume();
                final TreeItem<MyItem> treeItem = row.getTreeItem();
                if (mouseEvent.getClickCount() == 1) {
                    notifySelectionListeners(treeItem, false);
                } else if (mouseEvent.getClickCount() == 2) {
                    notifySelectionListeners(treeItem, true);
                }
            });

            showMarkersMenuItem.setOnAction(actionEvent -> {
                actionEvent.consume();
                notifySelectionListeners(row.getTreeItem(), true);
            });
            editMenuItem.setOnAction(actionEvent -> {
                actionEvent.consume();
                final MyItem value = row.getTreeItem().getValue();
                final Replacement replacement = value.replacement;
                try {
                    final NewReplacementController controller = NewReplacementController
                            .showForEdit(replacement, project.getCategories(), value.original.getValue(), uiInterface);
                    if (controller.isCancelled()) return;
                    final EditReplacementCommand command = new EditReplacementCommand(controller.getResultReplacement(), replacementCollection);
                    project.runCommand(command);

                } catch (IOException e) {
                    uiInterface.getMessageLogger().logError("Edit replacement error", e);
                }
            });
            deleteMenuItem.setOnAction(actionEvent -> {
                actionEvent.consume();
                if (!StandardDialogs.userConfirmation("Would you like to remove the replacement?", uiInterface.getStage())) {
                    return;
                }
                final Replacement replacement = row.getTreeItem().getValue().replacement;
                final DeleteReplacementCommand command = new DeleteReplacementCommand(replacement, replacementCollection, project.getAnonymizedFiles());
                project.runCommand(command);
            });

            return row;
        });

        treeTableView.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() != KeyCode.ENTER) return;
            keyEvent.consume();
            final TreeItem<MyItem> selectedItem = treeTableView.getSelectionModel().getSelectedItem();
            if (selectedItem.getValue().getReplacement() == null) return;
            notifySelectionListeners(selectedItem, true);
        });
        treeTableView
                .getSelectionModel()
                .selectedItemProperty()
                .addListener((observableValue, myItemTreeItem, t1) -> notifySelectionListeners(t1, false));

        fillView();
        replacementCollection.addUpdateListener((boolean isDirect) -> fillView());
        project.getAnonymizedFiles().addListener((ListChangeListener<AnonymizedFile>) change -> fillView());

        getChildren().add(treeTableView);
        GuiUtility.extend(treeTableView);
        GuiUtility.extend(this);
        autosizeColumns();
        listeners = new LinkedList<>();
    }

    public void addListener(SelectionListener selectionListener) {
        listeners.add(selectionListener);
    }

    public void removeListener(SelectionListener selectionListener) {
        listeners.remove(selectionListener);
    }

    private void notifySelectionListeners(TreeItem<MyItem> treeItem, boolean doubleClick) {
        if (treeItem == null) return;
        final MyItem myItem = treeItem.getValue();
        // We clone the list so that we can add more listeners in response to onSelect event
        final List<SelectionListener> clonedListeners = new LinkedList<>(listeners);
        clonedListeners.forEach(selectionListener -> selectionListener.onSelect(myItem.replacement, doubleClick));
    }

    private void fillView() {
        final List<CategoryScheme> categories = replacementCollection.getCategories();
        final TreeItem<MyItem> rootItem = new TreeItem<>(new MyItem());
        rootItem.setExpanded(true);

        for (final CategoryScheme categoryScheme : categories) {
            final TreeItem<MyItem> categoryItem = new TreeItem<>(new MyItem(categoryScheme));
            categoryItem.setExpanded(true);
            for (final Replacement replacement : replacementCollection.getForCategory(categoryScheme)) {
                final int occurrences = project.countOccurrences(replacement);
                final MyItem replacementItem = new MyItem(replacement, project.getAllOriginalStrings(replacement), occurrences);
                categoryItem.getChildren().add(new TreeItem<>(replacementItem));
            }
            rootItem.getChildren().add(categoryItem);
        }
        treeTableView.setRoot(rootItem);
    }

    private void autosizeColumns() {
        // To generalize the columns width proportions in relation to the table width,
        // you do not need to put pixel related values, you can use small float numbers if you wish,
        // because it's the relative proportion of each columns width what matters here:
        final float[] widths = {0.45f, 0.45f, 0.1f};// define the relational width of each column

        // calculates sum of widths
        float sum = 0f;
        for (double i : widths) {
            sum += i;
        }

        // set the width to the columns
        for (int i = 0; i < widths.length; i++) {
            treeTableView.getColumns().get(i).prefWidthProperty().bind(
                    treeTableView.widthProperty().subtract(4).multiply((widths[i] / sum))
            );
        }
    }

    public void selectAndScrollToReplacement(Replacement replacement) {
        treeTableView.getRoot().getChildren().stream()
                .flatMap(myItemTreeItem -> myItemTreeItem.getChildren().stream())
                .filter(myItemTreeItem -> myItemTreeItem.getValue().getReplacement().equals(replacement))
                .findAny()
                .ifPresent(myItemTreeItem -> {
                    treeTableView.getSelectionModel().select(myItemTreeItem);
                    treeTableView.scrollTo(treeTableView.getSelectionModel().getSelectedIndex());
                });
    }

    public static class MyItem {

        private StringProperty original;
        private StringProperty labels;
        private String occurrences = "";
        private Replacement replacement;

        public MyItem() { // Root item
            original = new SimpleStringProperty("Replacements");
            labels = new SimpleStringProperty();
        }

        public MyItem(CategoryScheme categoryScheme) { // Category Item
            this();
            original = new SimpleStringProperty(categoryScheme.getName());
            categoryScheme.getNameProperty().addUpdateListener((isDirect) -> original.setValue(categoryScheme.getName()));
        }

        public MyItem(Replacement replacement, String original, int occurrences) { // Replacement Item
            this();
            this.original.setValue(original);
            this.labels.setValue(replacement.getLabelsTextBlock());
            this.occurrences = String.valueOf(occurrences);
            this.replacement = replacement;
            replacement.getLabelsTextBlockProperty().addUpdateListener(isDirect -> labels.setValue(replacement.getLabelsTextBlock()));
        }

        public StringProperty getOriginal() {
            return original;
        }

        public StringProperty getLabels() {
            return labels;
        }

        public String getOccurrences() {
            return occurrences;
        }

        public void setOccurrences(String occurrences) {
            this.occurrences = occurrences;
        }

        public Replacement getReplacement() {
            return replacement;
        }

        public void setReplacement(Replacement replacement) {
            this.replacement = replacement;
        }
    }

    public interface SelectionListener {
        void onSelect(Replacement replacement, boolean doubleClick);
    }

    public static class RowWithContextMenu extends TreeTableRow<MyItem> {
        private final ContextMenu contextMenu;

        public RowWithContextMenu(ContextMenu contextMenu) {
            this.contextMenu = contextMenu;
        }

        @Override
        protected void updateItem(MyItem item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item.replacement == null) {
                setContextMenu(null);
            } else {
                setContextMenu(contextMenu);
            }
        }
    }

}
