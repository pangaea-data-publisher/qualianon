package org.qualiservice.qualianon.gui.components.markerstab;

import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import org.qualiservice.qualianon.gui.components.NewReplacementController;
import org.qualiservice.qualianon.gui.components.UIInterface;
import org.qualiservice.qualianon.gui.components.documentview.DocumentTab;
import org.qualiservice.qualianon.gui.components.replacementstab.ReplacementListComponent;
import org.qualiservice.qualianon.gui.tools.CenterPanel;
import org.qualiservice.qualianon.gui.tools.GuiUtility;
import org.qualiservice.qualianon.gui.tools.tabs.BaseTab;
import org.qualiservice.qualianon.model.Settings;
import org.qualiservice.qualianon.model.commands.*;
import org.qualiservice.qualianon.model.project.AnonymizedFile;
import org.qualiservice.qualianon.model.project.Project;
import org.qualiservice.qualianon.model.project.Replacement;
import org.qualiservice.qualianon.model.text.Coords;
import org.qualiservice.qualianon.model.text.MarkerRuntime;
import org.qualiservice.qualianon.utility.StringUtils;

import java.io.IOException;
import java.util.List;


public class ReplacementTab extends BaseTab implements MarkersTree.MarkerSelectListener, ReplacementListComponent.SelectionListener {

    private final CenterPanel placeholderPane;
    private final AnchorPane rightPane;
    private final Replacement replacement;
    private final ReplacementListComponent replacementListComponent;
    private final UIInterface uiInterface;
    private final Project project;
    private final Settings settings;
    private final Label selectedLabel;
    private final Button newReplacementButton;
    private final Button reassignButton;
    private final Button removeSelectedButton;
    private final MarkersTree markersTree;
    private Replacement selectedReplacement;
    private long selectedMarkersCount;

    public ReplacementTab(Replacement replacement, Project project, Settings settings, ReplacementListComponent replacementListComponent, UIInterface uiInterface) {
        super("Markers: " + StringUtils.ellipsis(project.getOneOriginalString(replacement), 20));

        this.replacement = replacement;
        this.replacementListComponent = replacementListComponent;
        this.uiInterface = uiInterface;
        this.replacementListComponent.addListener(this);
        final ObservableList<AnonymizedFile> documents = project.getAnonymizedFiles();
        this.project = project;
        this.settings = settings;

        selectedLabel = new Label("Selected: 0   ");
        selectedLabel.textAlignmentProperty().set(TextAlignment.CENTER);

        newReplacementButton = new Button("New replacement");
        newReplacementButton.setDisable(true);
        newReplacementButton.setOnAction(actionEvent -> {
            actionEvent.consume();
            onNewReplacementAction();
        });

        reassignButton = new Button("Reassign");
        reassignButton.setDisable(true);
        reassignButton.setOnAction(actionEvent -> {
            actionEvent.consume();
            onReassignAction();
        });

        removeSelectedButton = new Button("Remove");
        removeSelectedButton.setDisable(true);
        removeSelectedButton.setOnAction(actionEvent -> {
            actionEvent.consume();
            onRemoveSelectedAction();
        });

        final HBox buttonHBox = new HBox(selectedLabel, newReplacementButton, reassignButton, removeSelectedButton);
        buttonHBox.setAlignment(Pos.CENTER_RIGHT);

        final VBox leftVBox = new VBox(new Label("Placeholder"), buttonHBox);

        placeholderPane = new CenterPanel(new Label("Select an entry to preview its context"));

        rightPane = new AnchorPane();
        rightPane.getChildren().add(placeholderPane);
        GuiUtility.extend(rightPane);

        final SplitPane splitPane = new SplitPane(leftVBox, rightPane);
        GuiUtility.extend(splitPane);
        getChildren().add(splitPane);

        GuiUtility.extend(this);
        markersTree = new MarkersTree(this.replacement, documents);
        markersTree.addSelectionListener(this);
        leftVBox.getChildren().set(0, markersTree);
    }

    private void onNewReplacementAction() {
        try {
            final List<MarkerInDocument> selectedMarkers = markersTree.getSelectedMarkers();
            final MarkerInDocument firstMarkerInDocument = selectedMarkers.get(0);
            final String original = firstMarkerInDocument.getDocument().getDocument().getText().substring(
                    firstMarkerInDocument.getMarkerRuntime().getPositionRange().getStart(),
                    firstMarkerInDocument.getMarkerRuntime().getPositionRange().getEnd()
            );

            final NewReplacementController controller = NewReplacementController
                    .showForNew(project.getCategories(), original, uiInterface);
            if (controller.isCancelled()) return;

            final Command command = new NewReplacementForMarkersCommand(
                    selectedMarkers,
                    controller.getResultReplacement(),
                    project.getReplacementCollection()
            );
            project.runCommand(command);

        } catch (IOException e) {
            uiInterface.getMessageLogger().logError("New replacement error", e);
        }
    }

    private void onReassignAction() {
        final ReassignReplacementToMarkersCommand command = new ReassignReplacementToMarkersCommand(
                markersTree.getSelectedMarkers(),
                selectedReplacement,
                project.getReplacementCollection()
        );
        project.runCommand(command);
    }

    private void onRemoveSelectedAction() {
        final List<MarkerInDocument> markers = markersTree.getSelectedMarkers();
        project.runCommand(new RemoveMarkerCommand(markers));
    }

    public Replacement getReplacement() {
        return replacement;
    }

    @Override
    public void onClearSelection() {
        rightPane.getChildren().set(0, placeholderPane);
    }

    @Override
    public void onSelectMarker(MarkerRuntime marker, Coords coords, AnonymizedFile document) {
        final DocumentTab documentTab = new DocumentTab(document, settings, project, uiInterface);
        documentTab.scrollToLine(coords.getLine());
        rightPane.getChildren().set(0, documentTab);
    }

    @Override
    public void onCheckboxCount(long count) {
        selectedLabel.setText("Selected: " + count + "   ");
        selectedMarkersCount = count;
        newReplacementButton.setDisable(count == 0);
        removeSelectedButton.setDisable(count == 0);
        updateReassignButtonState();
    }

    @Override
    public void onClose() {
        markersTree.close();
        replacementListComponent.removeListener(this);
    }

    /**
     * Replacement selected in the replacement list component
     *
     * @param replacement
     * @param doubleClick
     */
    @Override
    public void onSelect(Replacement replacement, boolean doubleClick) {
        if (doubleClick) return;
        selectedReplacement = replacement;
        updateReassignButtonState();
    }

    private void updateReassignButtonState() {
        reassignButton.setDisable(selectedMarkersCount == 0 || selectedReplacement == null || selectedReplacement == replacement);
    }

}
