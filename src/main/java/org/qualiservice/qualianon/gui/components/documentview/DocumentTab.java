package org.qualiservice.qualianon.gui.components.documentview;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.IndexRange;
import javafx.scene.control.MenuItem;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.InlineCssTextArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.qualiservice.qualianon.gui.components.NewReplacementController;
import org.qualiservice.qualianon.gui.components.UIInterface;
import org.qualiservice.qualianon.gui.components.markerstab.MarkerInDocument;
import org.qualiservice.qualianon.gui.tools.ColorConvert;
import org.qualiservice.qualianon.gui.tools.GuiUtility;
import org.qualiservice.qualianon.gui.tools.tabs.BaseTab;
import org.qualiservice.qualianon.model.PositionRange;
import org.qualiservice.qualianon.model.Settings;
import org.qualiservice.qualianon.model.commands.AddMarkerCommand;
import org.qualiservice.qualianon.model.commands.ChangeMarkerRangeCommand;
import org.qualiservice.qualianon.model.commands.EditReplacementCommand;
import org.qualiservice.qualianon.model.commands.RemoveMarkerCommand;
import org.qualiservice.qualianon.model.project.AnonymizedFile;
import org.qualiservice.qualianon.model.project.Project;
import org.qualiservice.qualianon.model.text.MarkerRuntime;

import java.io.IOException;
import java.util.Collections;


public class DocumentTab extends BaseTab {

    private final AnonymizedFile document;
    private final Settings settings;
    private final Project project;
    private final UIInterface uiInterface;
    private final InlineCssTextArea textArea;
    private ContextMenu contextMenu;
    private final DisplayFont displayFont;


    public DocumentTab(AnonymizedFile document, Settings settings, Project project, UIInterface uiInterface) {

        super(document.getName());

        this.document = document;
        this.settings = settings;
        this.project = project;
        this.uiInterface = uiInterface;
        textArea = new InlineCssTextArea();
        textArea.setEditable(false);
        textArea.setOnMouseClicked(this::onMouseClick);
        textArea.setWrapText(true);
        textArea.setOnContextMenuRequested(this::onShowContextMenu);
        textArea.setParagraphGraphicFactory(LineNumberFactory.get(textArea));

        displayFont = new DisplayFont(FontFamily.sansserif, 18);
        updateTextAreaFont();

        document.addUpdateListener(isDirect -> showDocument());
        project.getCategories().addUpdateListener(isDirect -> showDocument());
        showDocument();
/*
        textArea.applyCss();
        textArea.layout();
        final Text t = (Text) textArea.lookup(".text");
        lineHeight = t.getBoundsInLocal().getHeight();
*/
        final VirtualizedScrollPane<InlineCssTextArea> scrollPane = new VirtualizedScrollPane<>(textArea);
        GuiUtility.extend(scrollPane);
        GuiUtility.extend(this);
        getChildren().add(scrollPane);
    }

    public AnonymizedFile getDocument() {
        return document;
    }

    public void setSelection(PositionRange range, int line) {
        textArea.selectRange(range.getStart(), range.getEnd());
        scrollToLine(line);
    }

    public void scrollToLine(int line) {
        textArea.showParagraphAtTop(line - 1 - 3);
    }

    private void updateTextAreaFont() {
        textArea.setStyle(displayFont.getCss());
    }

    private void showDocument() {
        textArea.clearStyle(0, textArea.getLength());
        textArea.replaceText(document.getDocument().getText());
        document.getDocument().getMarkers()
                .stream().filter(markerRuntime -> markerRuntime.getReplacement().getCategoryScheme() != null)
                .forEach(markerRuntime ->
                        textArea.setStyle(
                                markerRuntime.getPositionRange().getStart(),
                                markerRuntime.getPositionRange().getEnd(),
                                ColorConvert.toHighlightStyle(markerRuntime.getReplacement().getCategoryScheme().getColor())
                        )
                );
    }

    private void onMouseClick(MouseEvent mouseEvent) {
        mouseEvent.consume();
        if (mouseEvent.getButton() == MouseButton.PRIMARY) {
            closeContextMenu();
        }

        final IndexRange selection = getSelectionWithSettings();
        if (selection == null || selection.getLength() == 0) {
            textArea.deselect();
            return;
        }
        textArea.selectRange(selection.getStart(), selection.getEnd());
    }

    private void onShowContextMenu(ContextMenuEvent event) {
        event.consume();
        closeContextMenu();

        final PositionRange selectionRange = new PositionRange(textArea.getSelection());
        final String selectedText = textArea.getText(selectionRange.asIndexRange());

        final MenuItem addMarkerWithNewReplacement = new MenuItem("Add marker with new replacement");
        addMarkerWithNewReplacement.setOnAction(actionEvent -> {
            actionEvent.consume();
            try {
                MarkerRuntime.validateSelectionRange(selectionRange);

                final NewReplacementController newReplacementController = NewReplacementController
                        .showForNew(project.getCategories(), selectedText, uiInterface);
                if (newReplacementController.isCancelled()) return;

                final AddMarkerCommand command = new AddMarkerCommand(
                        selectionRange,
                        getDocument(),
                        newReplacementController.getResultReplacement()
                );
                project.runCommand(command);
            } catch (Exception e) {
                uiInterface.getMessageLogger().logError("Add marker error", e);
            }
        });

        final MenuItem searchForSelection = new MenuItem("Search for unmarked occurrences");
        searchForSelection.setOnAction(actionEvent -> {
            actionEvent.consume();
            uiInterface.searchUnmarkedOccurrences(selectionRange, selectedText, document);
        });

        final MenuItem deleteMarker = new MenuItem("Delete marker");
        deleteMarker.setOnAction(actionEvent -> {
            actionEvent.consume();
            final MarkerRuntime marker = getSelectedMarker(selectionRange);
            final RemoveMarkerCommand command = new RemoveMarkerCommand(Collections.singletonList(new MarkerInDocument(marker, document)));
            project.runCommand(command);
        });

        final MenuItem changeMarkerToSelection = new MenuItem("Change marker to selection");
        changeMarkerToSelection.setOnAction(actionEvent -> {
            actionEvent.consume();
            final MarkerRuntime marker = getSelectedMarker(selectionRange);
            final ChangeMarkerRangeCommand command = new ChangeMarkerRangeCommand(document, marker, selectionRange, project.getReplacementCollection());
            project.runCommand(command);
        });

        final MenuItem editReplacement = new MenuItem("Edit replacement");
        editReplacement.setOnAction(actionEvent -> {
            actionEvent.consume();
            try {
                final MarkerRuntime marker = getSelectedMarker(selectionRange);
                final NewReplacementController controller = NewReplacementController.showForEdit(
                        marker.getReplacement(),
                        project.getCategories(),
                        document.getDocument().getTextInRange(marker.getPositionRange()),
                        uiInterface
                );
                if (controller.isCancelled()) return;
                final EditReplacementCommand command = new EditReplacementCommand(controller.getResultReplacement(), project.getReplacementCollection());
                project.runCommand(command);
            } catch (Exception e) {
                uiInterface.getMessageLogger().logError("Edit replacement error", e);
            }
        });

        final MenuItem showReplacement = new MenuItem("Show replacement");
        showReplacement.setOnAction(actionEvent -> {
            actionEvent.consume();
            final MarkerRuntime marker = getSelectedMarker(selectionRange);
            uiInterface.showReplacement(marker.getReplacement());
        });

        final MenuItem showAllMarkersForReplacement = new MenuItem("Show all markers for replacement");
        showAllMarkersForReplacement.setOnAction(actionEvent -> {
            actionEvent.consume();
            final MarkerRuntime marker = getSelectedMarker(selectionRange);
            uiInterface.showMarkersForReplacement(marker.getReplacement());
        });

        final int markersInRangeCount = document.getDocument().countMarkersInRange(selectionRange);
        if (markersInRangeCount > 0 || selectionRange.length() == 0) {
            addMarkerWithNewReplacement.setDisable(true);
        }
        if (markersInRangeCount != 1 || selectionRange.length() == 0) {
            deleteMarker.setDisable(true);
            changeMarkerToSelection.setDisable(true);
            editReplacement.setDisable(true);
            showReplacement.setDisable(true);
            showAllMarkersForReplacement.setDisable(true);
        }
        if (selectionRange.length() == 0) {
            searchForSelection.setDisable(true);
        }

        contextMenu = new ContextMenu();
        contextMenu.getItems().add(addMarkerWithNewReplacement);
        contextMenu.getItems().add(searchForSelection);
        contextMenu.getItems().add(changeMarkerToSelection);
        contextMenu.getItems().add(showReplacement);
        contextMenu.getItems().add(showAllMarkersForReplacement);
        contextMenu.getItems().add(editReplacement);
        contextMenu.getItems().add(deleteMarker);
        contextMenu.setAutoFix(true);
        contextMenu.setAutoHide(true);
        contextMenu.show(textArea, event.getScreenX(), event.getScreenY());
    }

    private MarkerRuntime getSelectedMarker(PositionRange selectionRange) {
        return document.getDocument().getMarkerInSelection(selectionRange);
    }

    private void closeContextMenu() {
        if (contextMenu != null && contextMenu.isShowing()) {
            contextMenu.hide();
        }
    }

    private IndexRange getSelectionWithSettings() {
        switch (settings.getSelectionMode()) {
            case WORDS:
            default:
                return SelectionManager.extendSelection(document.getDocument().getText(), textArea.getSelection());
            case CHARACTERS:
                return textArea.getSelection();
        }
    }

}
