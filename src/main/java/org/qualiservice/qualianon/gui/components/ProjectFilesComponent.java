package org.qualiservice.qualianon.gui.components;

import javafx.collections.FXCollections;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import org.qualiservice.qualianon.gui.tools.GuiUtility;
import org.qualiservice.qualianon.gui.tools.StandardDialogs;
import org.qualiservice.qualianon.model.commands.Command;
import org.qualiservice.qualianon.model.commands.DeleteDocumentCommand;
import org.qualiservice.qualianon.model.project.AnonymizedFile;
import org.qualiservice.qualianon.model.project.Project;
import org.qualiservice.qualianon.utility.StringUtils;


public class ProjectFilesComponent extends AnchorPane {

    private final ListView<AnonymizedFile> listView;
    private final UIInterface uiInterface;
    private DocumentSelectionListener listener;
    private Project project;


    public ProjectFilesComponent(UIInterface uiInterface) {
        this.uiInterface = uiInterface;
        listView = new ListView<>();
        listView.setCellFactory(anonymizedFileListView -> new FileCell());
        listView.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() != KeyCode.ENTER) return;
            keyEvent.consume();
            listener.documentSelected(listView.getSelectionModel().getSelectedItem());
        });
        getChildren().add(listView);
        GuiUtility.extend(listView);
        GuiUtility.extend(this);
    }

    public void setProject(Project project) {
        this.project = project;
        if (project == null) {
            listView.setItems(FXCollections.emptyObservableList());
        } else {
            listView.setItems(project.getAnonymizedFiles());
        }
    }

    public ProjectFilesComponent setDocumentSelectionListener(DocumentSelectionListener listener) {
        this.listener = listener;
        return this;
    }

    class FileCell extends ListCell<AnonymizedFile> {

        @Override
        protected void updateItem(AnonymizedFile document, boolean empty) {
            super.updateItem(document, empty);
            if (document == null) {
                setText(null);
                setContextMenu(null);
                return;
            }

            if (document.isModified()) {
                setTextFill(Color.DARKRED);
            } else {
                setTextFill(Color.BLACK);
            }
            setText(StringUtils.textWithIndicator(document.getName(), document.isModified()));

            GuiUtility.setOnDoubleClick(this, () -> listener.documentSelected(document));
            setContextMenu(getContextMenu(document));
        }

        private ContextMenu getContextMenu(AnonymizedFile document) {
            final MenuItem deleteItem = new MenuItem("Delete");
            deleteItem.setOnAction(actionEvent -> {
                actionEvent.consume();
                if (!StandardDialogs.userConfirmation("Would you like to remove the document from your project?", uiInterface.getStage())) {
                    return;
                }
                final Command command = new DeleteDocumentCommand(document);
                project.runCommand(command);
            });

            final ContextMenu contextMenu = new ContextMenu();
            contextMenu.getItems().add(deleteItem);
            return contextMenu;
        }

    }

    public interface DocumentSelectionListener {
        void documentSelected(AnonymizedFile anonymizedFile);
    }

}
