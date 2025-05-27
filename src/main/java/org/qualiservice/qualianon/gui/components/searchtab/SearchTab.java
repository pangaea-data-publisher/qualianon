package org.qualiservice.qualianon.gui.components.searchtab;

import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import org.qualiservice.qualianon.audit.MessageLogger;
import org.qualiservice.qualianon.gui.components.NewReplacementController;
import org.qualiservice.qualianon.gui.components.UIInterface;
import org.qualiservice.qualianon.gui.components.documentview.DocumentTab;
import org.qualiservice.qualianon.gui.components.replacementstab.ReplacementListComponent;
import org.qualiservice.qualianon.gui.tools.CenterPanel;
import org.qualiservice.qualianon.gui.tools.GuiUtility;
import org.qualiservice.qualianon.gui.tools.tabs.BaseTab;
import org.qualiservice.qualianon.model.SearchResult;
import org.qualiservice.qualianon.model.Settings;
import org.qualiservice.qualianon.model.commands.AddMarkersAndAssignCommand;
import org.qualiservice.qualianon.model.commands.AddMarkersCommand;
import org.qualiservice.qualianon.model.project.AnonymizedFile;
import org.qualiservice.qualianon.model.project.Project;
import org.qualiservice.qualianon.model.project.Replacement;
import org.qualiservice.qualianon.model.project.SearchParams;
import org.qualiservice.qualianon.utility.StringUtils;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;


public class SearchTab extends BaseTab implements SearchResultsTree.SearchResultSelectListener, ListChangeListener<AnonymizedFile>, ReplacementListComponent.SelectionListener {

    private final Project project;
    private final MessageLogger messageLogger;
    private final UIInterface uiInterface;
    private final Settings settings;
    private final AnchorPane rightPane;
    private final AnchorPane placeholderPane;
    private final Button newReplacementButton;
    private final SearchParams searchParams;
    private final VBox leftVBox;
    private final Label selectedLabel;
    private final Button assignReplacementButton;
    private final ReplacementListComponent replacementListComponent;
    private List<SearchResult> searchResults;
    private long selectedMarkersCount;
    private Replacement selectedReplacement;
    private SearchResult selectedSearchResult;
    private final Button toMainViewButton;


    public SearchTab(SearchParams searchParams, Project project, MessageLogger messageLogger, UIInterface uiInterface, Settings settings, ReplacementListComponent replacementListComponent) {
        super("Search: " + StringUtils.ellipsis(searchParams.getText(), 20));

        this.searchParams = searchParams;
        this.project = project;
        this.messageLogger = messageLogger;
        this.uiInterface = uiInterface;
        this.settings = settings;

        this.replacementListComponent = replacementListComponent;
        this.replacementListComponent.addListener(this);

        selectedLabel = new Label("Selected: 0   ");
        selectedLabel.textAlignmentProperty().set(TextAlignment.CENTER);

        newReplacementButton = new Button("New replacement");
        newReplacementButton.setOnAction(actionEvent -> {
            actionEvent.consume();
            onMarkSelectedAction();
        });

        assignReplacementButton = new Button("Assign replacement");
        assignReplacementButton.setDisable(true);
        assignReplacementButton.setOnAction(actionEvent -> {
            actionEvent.consume();
            onAssignSelectedAction();
        });

        final HBox buttonHBox = new HBox(selectedLabel, newReplacementButton, assignReplacementButton);
        buttonHBox.setAlignment(Pos.CENTER_RIGHT);

        leftVBox = new VBox(new Label("Placeholder"), buttonHBox);

        placeholderPane = new CenterPanel(new Label("Select an entry to preview its context"));

        toMainViewButton = new Button("To main view");
        toMainViewButton.setDisable(true);
        toMainViewButton.setOnAction(event -> uiInterface.showSearchResultInMainView(selectedSearchResult));
        AnchorPane.setRightAnchor(toMainViewButton, 18.0);

        rightPane = new AnchorPane();
        rightPane.getChildren().add(placeholderPane);
        rightPane.getChildren().add(toMainViewButton);
        GuiUtility.extend(rightPane);

        final SplitPane splitPane = new SplitPane(leftVBox, rightPane);
        GuiUtility.extend(splitPane);
        getChildren().add(splitPane);

        GuiUtility.extend(this);

        runSearch();
        project.getAnonymizedFiles().addListener(this);
    }

    public SearchParams getSearch() {
        return searchParams;
    }

    private void onMarkSelectedAction() {
        try {
            final NewReplacementController newReplacementController = NewReplacementController
                    .showForNew(project.getCategories(), searchParams.getText(), uiInterface);
            if (newReplacementController.isCancelled()) return;

            final AddMarkersCommand command = new AddMarkersCommand(
                    searchParams,
                    getSelectedSearchResults(),
                    newReplacementController.getResultReplacement()
            );
            project.runCommand(command);

        } catch (IOException e) {
            messageLogger.logError("Error opening replacement window", e);
        }
    }

    private void onAssignSelectedAction() {
        final AddMarkersAndAssignCommand command = new AddMarkersAndAssignCommand(
                searchParams,
                getSelectedSearchResults(),
                selectedReplacement
        );
        project.runCommand(command);
    }

    private List<SearchResult> getSelectedSearchResults() {
        return searchResults.stream()
                .filter(SearchResult::isSelected)
                .collect(Collectors.toList());
    }

    private void runSearch() {
        this.searchResults = project.search(searchParams);
        final SearchResultsTree searchResultsTree = new SearchResultsTree(searchParams, searchResults);
        searchResultsTree.addSelectionListener(this);
        Platform.runLater(() -> leftVBox.getChildren().set(0, searchResultsTree));
    }

    @Override
    public void onSelectSearchResult(SearchResult searchResult) {
        selectedSearchResult = searchResult;
        toMainViewButton.setDisable(false);
        final AnonymizedFile document = project.getDocument(searchResult.getDocumentName());
        final DocumentTab documentTab = new DocumentTab(document, settings, project, uiInterface);
        documentTab.setSelection(searchResult.getRange(), searchResult.getCoords().getLine());
        rightPane.getChildren().set(0, documentTab);
    }

    @Override
    public void onClearSearchResult() {
        selectedSearchResult = null;
        toMainViewButton.setDisable(true);
        rightPane.getChildren().set(0, placeholderPane);
    }

    @Override
    public void onCheckboxCount(long count) {
        selectedLabel.setText("Selected: " + count + "   ");
        selectedMarkersCount = count;
        newReplacementButton.setDisable(count == 0);
        updateReassignButtonState();
    }

    /*
     * Replacement selected in the replacement list component
     */
    @Override
    public void onSelect(Replacement replacement, boolean doubleClick) {
        if (doubleClick) return;
        selectedReplacement = replacement;
        updateReassignButtonState();
    }

    private void updateReassignButtonState() {
        assignReplacementButton.setDisable(selectedMarkersCount == 0 || selectedReplacement == null);
    }

    @Override
    public void onClose() {
        project.getAnonymizedFiles().removeListener(this);
        replacementListComponent.removeListener(this);
    }

    @Override
    public void onChanged(Change<? extends AnonymizedFile> change) {
        runSearch();
    }
}
