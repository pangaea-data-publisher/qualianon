package org.qualiservice.qualianon.gui.components;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.*;
import org.qualiservice.qualianon.Main;
import org.qualiservice.qualianon.Version;
import org.qualiservice.qualianon.audit.CompositeLogger;
import org.qualiservice.qualianon.audit.FileLogger;
import org.qualiservice.qualianon.audit.MessageLogger;
import org.qualiservice.qualianon.gui.components.categoriestab.CategoriesTreeComponent;
import org.qualiservice.qualianon.gui.components.documentview.DocumentTab;
import org.qualiservice.qualianon.gui.components.exportstab.ExportsTreeComponent;
import org.qualiservice.qualianon.gui.components.markerstab.ReplacementTab;
import org.qualiservice.qualianon.gui.components.replacementstab.ReplacementListComponent;
import org.qualiservice.qualianon.gui.components.searchtab.SearchTab;
import org.qualiservice.qualianon.gui.tools.Browser;
import org.qualiservice.qualianon.gui.tools.ContextualMainMenuItem;
import org.qualiservice.qualianon.gui.tools.StandardDialogs;
import org.qualiservice.qualianon.gui.tools.tabs.BaseTab;
import org.qualiservice.qualianon.gui.tools.tabs.CollapsingTabPane;
import org.qualiservice.qualianon.model.PositionRange;
import org.qualiservice.qualianon.model.SearchResult;
import org.qualiservice.qualianon.model.SelectionMode;
import org.qualiservice.qualianon.model.Settings;
import org.qualiservice.qualianon.model.anonymization.AnonymizationProfile;
import org.qualiservice.qualianon.model.categories.CategoryScheme;
import org.qualiservice.qualianon.model.commands.AddToListCommand;
import org.qualiservice.qualianon.model.commands.NewExportCommand;
import org.qualiservice.qualianon.model.exports.Export;
import org.qualiservice.qualianon.model.project.*;
import org.qualiservice.qualianon.utility.UserPreferences;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;


public class MainController implements Initializable, UIInterface {

    @FXML
    private AnchorPane documentsTabPane;
    @FXML
    private AnchorPane replacementsTabPane;
    @FXML
    private AnchorPane categoriesTabPane;
    @FXML
    private AnchorPane exportsTabPane;
    @FXML
    private Tab documentsTab;
    @FXML
    private Tab replacementsTab;
    @FXML
    private Tab categoriesTab;
    @FXML
    private Tab exportsTab;
    @FXML
    private Label statusLine;
    @FXML
    private SplitPane mainSplitPane;
    @FXML
    private SplitPane topSplitPane;
    @FXML
    private MenuItem undoMenu;
    @FXML
    private MenuItem redoMenu;
    @FXML
    private MenuItem renameMenu;
    @FXML
    private MenuItem deleteMenu;
    @FXML
    private ComboBox<String> selectionModeComboBox;
    @FXML
    private TabPane leftTabPane;
    @FXML
    MenuItem openRecentProjectMenu;

    private final Stage stage;
    private Project project;
    private MessageLoggerTab messageLoggerTab;
    private MessageLogger logger;
    private FileLogger fileLogger;
    private final ProjectFilesComponent projectFilesComponent;
    private CollapsingTabPane bottomTabPane;
    private CollapsingTabPane centerTabPane;
    private final HashMap<String, BaseTab> documentComponents;
    private final Settings settings;
    private ReplacementListComponent replacementListComponent;
    private ExportsTreeComponent exportsTreeComponent;
    private ContextualMainMenuItem renameMenuContextual;
    private SearchParams lastSearchParams;


    public static MainController openWindow() throws IOException {
        final Stage stage = new Stage(StageStyle.DECORATED);
        stage.initModality(Modality.NONE);

        final MainController controller = new MainController(stage);

        final FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(WelcomeController.class.getClassLoader().getResource("main.fxml"));
        fxmlLoader.setController(controller);
        final Parent fxml = fxmlLoader.load();

        final Scene scene = new Scene(fxml);
        stage.setScene(scene);
        stage.show();

        return controller;
    }

    private MainController(Stage stage) {
        this.stage = stage;
        documentComponents = new HashMap<>();
        projectFilesComponent = new ProjectFilesComponent(this)
                .setDocumentSelectionListener(this::showDocument);
        settings = new Settings();
        lastSearchParams = SearchParams.plain("");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        final CompositeLogger compositeLogger = new CompositeLogger();
        fileLogger = new FileLogger();
        compositeLogger.addLogger(fileLogger);
        messageLoggerTab = new MessageLoggerTab(statusLine);
        compositeLogger.addLogger(messageLoggerTab);
        compositeLogger.addLogger(new AlertLogger(this));

        logger = compositeLogger;

        logger.logInfo("Starting " + Version.QUALI_ANON_TITLE + " " + Version.VERSION);
        documentsTabPane.getChildren().add(projectFilesComponent);

        centerTabPane = new CollapsingTabPane(false);
        topSplitPane.getItems().add(centerTabPane);

        bottomTabPane = new CollapsingTabPane(true);
        bottomTabPane.addOpenPaneListener(() -> mainSplitPane.setDividerPosition(0, 0.7f));
        mainSplitPane.getItems().add(bottomTabPane);

        showMessageLog();

        statusLine.setOnMouseClicked(mouseEvent -> {
            mouseEvent.consume();
            showMessageLog();
        });

        undoMenu.setDisable(true);
        redoMenu.setDisable(true);
        renameMenuContextual = new ContextualMainMenuItem(renameMenu);

        deleteMenu.setDisable(true);

        selectionModeComboBox.setItems(FXCollections.observableArrayList(
                Arrays.stream(SelectionMode.values()).map(SelectionMode::getDisplay).collect(Collectors.toList())
        ));
        selectionModeComboBox.getSelectionModel().select(0);
        selectionModeComboBox.getSelectionModel().selectedIndexProperty().addListener(
                (observableValue, number, t1) -> settings.setSelectionMode(SelectionMode.values()[t1.intValue()])
        );

        stage.setOnCloseRequest(event -> {
            if (!confirmQuitAction()) {
                event.consume();
            }
        });
        if (UserPreferences.loadProjectPathStorage().isEmpty()) {
            openRecentProjectMenu.setDisable(true);
        } else {
            openRecentProjectMenu.setDisable(false);
        }
    }

    public void newProject(File projectFolder) throws ProjectAlreadyExistsException {
        project = new Project(projectFolder, logger);

        boolean openProject = false;
        if (project.isDirectoryStructureOk()) {
            openProject = StandardDialogs.userConfirmation("A project already exists at the given location. Would you like to open the existing project?", stage.getOwner());
            if (!openProject) {
                stage.close();
                throw new ProjectAlreadyExistsException();
            }
        }

        openOrCreateProject(project);
        logProjectNewOrOpen(projectFolder, !openProject);
    }

    public void openProject(File projectFolder) throws ProjectNotInitializedException {
        project = new Project(projectFolder, logger);

        boolean createProject = false;
        if (!project.isDirectoryStructureOk()) {
            createProject = StandardDialogs.userConfirmation("There is no project at the given location. Would you like to create a new project?", stage.getOwner());
            if (!createProject) {
                stage.close();
                throw new ProjectNotInitializedException();
            }
        }

        openOrCreateProject(project);
        logProjectNewOrOpen(projectFolder, createProject);
    }

    private void openOrCreateProject(Project project) {
        stage.setTitle(project.getProjectDirectory().getName() + " - " + Version.QUALI_ANON_TITLE);
        fileLogger.openFile(project.getAuditFile());
        StandardDialogs.progressDialog(() -> {
            project.open();
            return true;
        });

        projectFilesComponent.setProject(project);
        project.getDocumentsModifiedProperty().addUpdateListener(isDirect -> {
            Platform.runLater(() -> {
                String text = "Documents";
                if (project.getDocumentsModifiedProperty().isValue()) {
                    text += " *";
                }
                documentsTab.setText(text);
            });
        });


        replacementListComponent = new ReplacementListComponent(project.getReplacementCollection(), project, this);
        replacementListComponent.addListener((replacement, doubleClick) -> {
            if (!doubleClick || replacement == null) return;
            onShowReplacements(replacement);
        });
        replacementsTabPane.getChildren().add(replacementListComponent);
        project.getReplacementsModifiedProperty().addUpdateListener(isDirect -> {
            Platform.runLater(() -> {
                String text = "Replacements";
                if (project.getReplacementCollection().isModified()) {
                    text += " *";
                }
                replacementsTab.setText(text);
            });
        });


        exportsTreeComponent = new ExportsTreeComponent(project, this)
                .addSelectionListener(this::showAnonymizedDocument);
        exportsTabPane.getChildren().add(exportsTreeComponent);


        final CategoriesTreeComponent categoriesTreeComponent = new CategoriesTreeComponent(project, this);
        categoriesTabPane.getChildren().add(categoriesTreeComponent);
        project.getCategoriesModifiedProperty().addUpdateListener(isDirect -> {
            Platform.runLater(() -> {
                String text = "Categories";
                if (project.getCategories().isModified()) {
                    text += " *";
                }
                categoriesTab.setText(text);
            });
        });


        project.getCommandList().addListener((undoEnabled, undoDescription, redoEnabled, redoDescription) -> {
            undoMenu.setDisable(!undoEnabled);
            undoMenu.setText("Undo " + undoDescription);
            redoMenu.setDisable(!redoEnabled);
            redoMenu.setText("Redo " + redoDescription);
        });

        project.getAnonymizedFiles().addListener((ListChangeListener<? super AnonymizedFile>) change -> {
            while (change.next()) {
                change.getRemoved().forEach(anonymizedFile -> {
                    closeTabIfOpen(anonymizedFile.getName());
                    documentComponents.remove(anonymizedFile.getName());
                });
            }
            change.reset();
        });
    }

    private void logProjectNewOrOpen(File projectFolder, boolean createProject) {
        if (createProject) {
            logger.logInfo("Created new project " + projectFolder.getName());
        } else {
            logger.logInfo("Opened project " + projectFolder.getName());
        }
    }


    // Menu Actions

    @SuppressWarnings("unused")
    public void onOpenProjectAction(ActionEvent actionEvent) throws IOException {
        actionEvent.consume();

        final DirectoryChooser directoryChooser = new YJDirectoryChooser();
        directoryChooser.setTitle("Select Project Folder");

        final File directory = directoryChooser.showDialog(stage.getOwner());
        if (directory == null) return;

        try {
            openProject(directory);
            UserPreferences.saveCurrentProjectPath(directory.getAbsolutePath());
        } catch (ProjectNotInitializedException e) {
            Main.welcome();
        }
    }

    @SuppressWarnings("unused")
    public void onOpenRecentProjectMenu(ActionEvent e) throws IOException {
        e.consume(); //is it really necessary to consume it?

        final RecentProjectController controller = new RecentProjectController(this.stage, UserPreferences.loadProjectPathStorage());
        controller.show();

    }

    @SuppressWarnings("unused")
    public void onExportProjectAction(ActionEvent actionEvent) throws IOException {
        actionEvent.consume();
        final Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Export Project");

        final ExportProjectController controller = new ExportProjectController(stage, project, logger);

        final FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getClassLoader().getResource("exportProject.fxml"));
        fxmlLoader.setController(controller);
        final Parent fxml = fxmlLoader.load();

        final Scene scene = new Scene(fxml);
        stage.setScene(scene);
        stage.show();

    }

    @SuppressWarnings("unused")
    public void onCloseProjectAction(ActionEvent actionEvent) throws IOException {
        actionEvent.consume();
        if (!confirmQuitAction()) {
            return;
        }

        fileLogger.logInfo("Closing project");
        fileLogger.closeFile();
        stage.close();
        Main.welcome();
    }

    @SuppressWarnings("unused")
    public void onSaveProjectAction(ActionEvent actionEvent) {
        actionEvent.consume();
        project.saveAllDocuments();
    }

    @SuppressWarnings("unused")
    public void onImportAction(ActionEvent actionEvent) {
        actionEvent.consume();

        final FileChooser fileChooser = new YJFileChooser();
        fileChooser.setTitle("Import Document(s)");
        final List<String> extensions = Collections.singletonList("*.docx");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("docx-files", extensions));
        final List<File> multipleFilesFromFileChooser = fileChooser.showOpenMultipleDialog(stage.getOwner());

        if (multipleFilesFromFileChooser == null) return;
        final AtomicReference<AnonymizedFile> firstImported = new AtomicReference<>(null);

        StandardDialogs.progressDialog(() -> {
            for (final File file : multipleFilesFromFileChooser) {
                try {
                    final AnonymizedFile importedDocument = project.importDocument(file);
                    logger.logInfo("Imported document " + file.getName());
                    if (firstImported.get() == null) {
                        firstImported.set(importedDocument);
                    }
                } catch (Exception e) {
                    logger.logError("Error importing file " + file.getName(), e);
                } catch (DocumentAlreadyImportedException e) {
                    logger.logError(file.getName() + ": a document with the same name already exists, it was not reimported.", null);
                }
            }
            return true;
        });

        if (firstImported.get() != null) {
            showDocument(firstImported.get());
        }
    }

    public void onOpenPreferenceMenu(ActionEvent actionEvent) throws IOException {
        actionEvent.consume();
        final Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);

        final PreferencesController preferencesController = new PreferencesController(stage, this);
        final FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getClassLoader().getResource("preferences.fxml"));
        fxmlLoader.setController(preferencesController);
        final Parent fxml = fxmlLoader.load();

        final Scene scene = new Scene(fxml);
        stage.setScene(scene);
        stage.show();


    }

    @SuppressWarnings("unused")
    public void onQuitAction(ActionEvent actionEvent) {
        actionEvent.consume();

        if (confirmQuitAction()) {
            Platform.exit();
        }
    }

    @SuppressWarnings("unused")
    public void onUndoAction(ActionEvent actionEvent) {
        actionEvent.consume();
        project.getCommandList().undo();
    }

    @SuppressWarnings("unused")
    public void onRedoAction(ActionEvent actionEvent) {
        actionEvent.consume();
        project.getCommandList().redo();
    }

    @SuppressWarnings("unused")
    public void onFindAction(ActionEvent actionEvent) {
        actionEvent.consume();
        try {
            final DocumentTab documentTab = (DocumentTab) centerTabPane.getSelectedTab();

            final SearchController controller = SearchController.show(this, lastSearchParams, documentTab != null);
            if (controller.isCancelled()) return;

            lastSearchParams = controller.getSearchParams();
            if (documentTab != null) {
                lastSearchParams.withActiveDocument(documentTab.getDocument());
            }
            search(lastSearchParams);

        } catch (IOException e) {
            logger.logError("Search error", e);
        }
    }

    @SuppressWarnings("unused")
    public void onRenameAction(ActionEvent actionEvent) {
        actionEvent.consume();
        renameMenuContextual.call();
    }

    @SuppressWarnings("unused")
    public void onDeleteAction(ActionEvent actionEvent) {
        actionEvent.consume();
        // TODO
    }

    @SuppressWarnings("unused")
    public void onNewCategoryAction(ActionEvent actionEvent) {
        actionEvent.consume();
        final String newName = project.getCategories().makeNewName();
        final AddToListCommand<CategoryScheme> command = new AddToListCommand<>(
                new CategoryScheme(newName),
                project.getCategories().getCategoriesProperty()
        );
        project.runCommand(command);
        leftTabPane.getSelectionModel().select(categoriesTab);
    }

    @SuppressWarnings("unused")
    public void onNewExportAction(ActionEvent actionEvent) {
        actionEvent.consume();
        StandardDialogs.textInput(
                "New export's name",
                project.getExportList().getUniqueName("New Export"),
                stage
        ).ifPresent(exportName -> {
            final NewExportCommand command = new NewExportCommand(
                    exportName.trim(),
                    AnonymizationProfile.fromCategories(project.getCategories())
            );
            project.runCommand(command);
            leftTabPane.getSelectionModel().select(exportsTab);
            exportsTreeComponent.openExport(command.getExport());
        });
    }

    @SuppressWarnings("unused")
    public void onShowMessageLogAction(ActionEvent actionEvent) {
        actionEvent.consume();
        showMessageLog();
    }

    @SuppressWarnings("unused")
    public void onCloseAllBottomTabsAction(ActionEvent actionEvent) {
        actionEvent.consume();
        bottomTabPane.closeAllTabs();
    }

    @SuppressWarnings("unused")
    public void onCloseActiveBottomTabAction(ActionEvent actionEvent) {
        actionEvent.consume();
        bottomTabPane.closeActiveTab();
    }

    @SuppressWarnings("unused")
    public void onShowUserManual(ActionEvent actionEvent) {
        actionEvent.consume();
        Browser.openBrowser("https://docs.google.com/document/d/1fLLYvsgXjh_p9p_E1fhikkIPQb19VUiltbRgaWQoD-M/edit?usp=sharing", getMessageLogger());
    }

    @SuppressWarnings("unused")
    public void onShowQualiservice(ActionEvent actionEvent) {
        actionEvent.consume();
        Browser.openBrowser("https://www.qualiservice.org", getMessageLogger());
    }

    @SuppressWarnings("unused")
    public void onShowGithub(ActionEvent actionEvent) {
        actionEvent.consume();
        Browser.openBrowser("https://github.com/pangaea-data-publisher/qualianon", getMessageLogger());
    }

    // Utility methods

    private void showDocument(AnonymizedFile document) {
        openExistingOrNewTab(
                document.getName(),
                () -> new DocumentTab(document, settings, project, this),
                null
        );
    }

    private void showAnonymizedDocument(AnonymizedFile document, Export export) {
        openExistingOrNewTab(
                document.getName() + "/////" + export.getName(),
                () -> new AnonymizedDocumentTab(document, export, project.getReplacementCollection()),
                null
        );
    }

    private void openExistingOrNewTab(String name, Supplier<BaseTab> newTabSupplier, Consumer<BaseTab> existingTabConsumer) {
        if (documentComponents.containsKey(name)) {
            final BaseTab baseTab = documentComponents.get(name);
            centerTabPane.openTab(baseTab);
            if (existingTabConsumer != null) {
                existingTabConsumer.accept(baseTab);
            }
            return;
        }
        final BaseTab newTab = newTabSupplier.get();
        documentComponents.put(name, newTab);
        centerTabPane.openTab(newTab);
    }

    private void closeTabIfOpen(String name) {
        if (!documentComponents.containsKey(name)) return;
        final BaseTab baseTab = documentComponents.get(name);
        centerTabPane.closeTab(baseTab);
    }

    private void showMessageLog() {
        bottomTabPane.openTab(messageLoggerTab);
    }

    private void search(SearchParams searchParams) {
        final SearchTab tab = bottomTabPane.getTabs().stream()
                .filter(baseTab -> baseTab instanceof SearchTab)
                .map(baseTab -> (SearchTab) baseTab)
                .filter(searchTab -> searchTab.getSearch().equals(searchParams))
                .findAny()
                .orElseGet(() -> new SearchTab(searchParams, project, logger, this, settings, replacementListComponent));
        bottomTabPane.openTab(tab);
    }

    private void onShowReplacements(Replacement replacement) {
        final ReplacementTab tab = bottomTabPane.getTabs().stream()
                .filter(baseTab -> baseTab instanceof ReplacementTab)
                .map(baseTab -> (ReplacementTab) baseTab)
                .filter(replacementTab -> replacementTab.getReplacement() == replacement)
                .findAny()
                .orElseGet(() -> new ReplacementTab(replacement, project, settings, replacementListComponent, this));
        bottomTabPane.openTab(tab);
    }

    private boolean confirmQuitAction() {

        if (!project.hasUnsavedData()) {
            logger.logInfo("Quit, project has no unsaved data");
            return true;
        }

        final Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Attention");
        alert.setHeaderText("There are unsaved files");
        alert.initOwner(stage.getOwner());

        final ButtonType buttonSaveAndQuit = new ButtonType("Save and quit");
        final ButtonType buttonNoQuit = new ButtonType("Don't quit");
        final ButtonType buttonQuitNoSave = new ButtonType("Quit without saving");
        alert.getButtonTypes().setAll(buttonSaveAndQuit, buttonNoQuit, buttonQuitNoSave);

        alert.setResizable(true);
        alert.getDialogPane().setMinWidth(600);
        final ButtonType result = alert.showAndWait().orElse(buttonNoQuit);

        if (result == buttonSaveAndQuit) {
            final boolean success = project.saveAllDocuments();
            logger.logInfo("Quit, save all documents. Success=" + success);
            return success;

        } else if (result == buttonNoQuit) {
            logger.logDebug("Quit, cancel");
            return false;

        } else if (result == buttonQuitNoSave) {
            logger.logInfo("Quit, do not save changed documents");
            return true;
        }

        return false;
    }

    @Override
    public Stage getStage() {
        return stage;
    }

    @Override
    public void showReplacement(Replacement replacement) {
        leftTabPane.getSelectionModel().select(1);
        replacementListComponent.selectAndScrollToReplacement(replacement);
    }

    @Override
    public void searchUnmarkedOccurrences(PositionRange range, String selectedText, AnonymizedFile document) {
        search(new SearchParams(selectedText, range, document, false, false, true, false));
    }

    @Override
    public void showMarkersForReplacement(Replacement replacement) {
        onShowReplacements(replacement);
    }

    @Override
    public MessageLogger getMessageLogger() {
        return logger;
    }

    @Override
    public ContextualMainMenuItem getRenameMenuItem() {
        return renameMenuContextual;
    }

    @Override
    public void showCategoriesTab() {
        leftTabPane.getSelectionModel().select(categoriesTab);
    }

    @Override
    public void showSearchResultInMainView(SearchResult searchResult) {
        final AnonymizedFile document = project.getDocument(searchResult.getDocumentName());
        if (document == null) return;

        openExistingOrNewTab(
                document.getName(),
                () -> {
                    final DocumentTab tab = new DocumentTab(document, settings, project, this);
                    tab.setSelection(searchResult.getRange(), searchResult.getCoords().getLine());
                    return tab;
                },
                baseTab -> {
                    final DocumentTab existingTab = (DocumentTab) baseTab;
                    existingTab.setSelection(searchResult.getRange(), searchResult.getCoords().getLine());
                }
        );
    }

}
