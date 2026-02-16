package org.qualiservice.qualianon.model.project;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.qualiservice.qualianon.audit.MessageLogger;
import org.qualiservice.qualianon.files.Backup;
import org.qualiservice.qualianon.files.FileTools;
import org.qualiservice.qualianon.files.ReplacementsXlsxFile;
import org.qualiservice.qualianon.files.XmlFileHandler;
import org.qualiservice.qualianon.gui.tools.StandardDialogs;
import org.qualiservice.qualianon.model.SearchResult;
import org.qualiservice.qualianon.model.categories.Categories;
import org.qualiservice.qualianon.model.commands.Command;
import org.qualiservice.qualianon.model.commands.CommandList;
import org.qualiservice.qualianon.model.commands.CommandRunner;
import org.qualiservice.qualianon.model.exports.ExportList;
import org.qualiservice.qualianon.model.properties.BooleanProperty;
import org.qualiservice.qualianon.model.text.MarkerStorage;
import org.qualiservice.qualianon.utility.UserPreferences;

import java.io.*;
import java.util.*;
import java.util.prefs.Preferences;
import java.util.stream.Collectors;


public class Project implements CommandRunner {

    private final ReplacementCollection replacementCollection;
    private final ObservableList<AnonymizedFile> anonymizedFiles;
    private final File projectDirectory;
    private final MessageLogger logger;
    private final CommandList commandList;
    private Categories categories;
    private ExportList exportList;
    private final BooleanProperty anonymizedFilesModifiedProperty;
    private final String projectName;

    public Project(File projectDirectory, MessageLogger logger) {
        this.projectDirectory = projectDirectory;
        this.projectName= projectDirectory.getName();
        this.logger = logger;
        replacementCollection = new ReplacementCollection();
        anonymizedFiles = AnonymizedFile.observableArrayList();
        commandList = new CommandList(logger);
        anonymizedFilesModifiedProperty = new BooleanProperty();
    }

    /**
     * Initializes the project by ensuring directories exist and loading persisted state.
     */
    public void open() {
        createDirectoryStructureIfNotExists();
        categories = loadCategoriesFile();
        exportList = new ExportList(getExportsDirectory(), getTrashDirectory(), logger);
        loadReplacementsFile();
        loadDocuments();
    }

    public BooleanProperty getReplacementsModifiedProperty() {
        return replacementCollection.getModifiedProperty();
    }

    public BooleanProperty getCategoriesModifiedProperty() {
        return categories.getModifiedProperty();
    }

    public BooleanProperty getDocumentsModifiedProperty() {
        return anonymizedFilesModifiedProperty;
    }

    public File getProjectDirectory() {
        return projectDirectory;
    }

    public ObservableList<AnonymizedFile> getAnonymizedFiles() {
        return anonymizedFiles;
    }

    /**
     * Loads all anonymized documents from disk and wires modification listeners.
     */
    private void loadDocuments() {
        final File[] files = getAnonymizedDirectory().listFiles((FilenameFilter) new SuffixFileFilter(".docx"));
        for (final File file : Objects.requireNonNull(files)) {
            try {
                final AnonymizedFile document = new AnonymizedFile(replacementCollection, getIdentificationDirectory(), getTrashDirectory(), logger).load(file);
                anonymizedFiles.add(document);
                addDocumentListener(document);
                logger.logInfo("Loaded " + file.getName());
            } catch (Exception e) {
                logger.logError("Error loading file " + file.getName(), e);
            }
        }
        sortDocuments();
        anonymizedFilesModifiedProperty.setValue(false);
    }

    /**
     * Imports a DOCX into the project, normalizes line breaks, and persists it.
     */
    public AnonymizedFile importDocument(File file) throws IOException, DocumentAlreadyImportedException {
        final Backup backup = new Backup(getBackupDirectory(), logger);
        final AnonymizedFile document = new AnonymizedFile(
                replacementCollection,
                getIdentificationDirectory(),
                getTrashDirectory(),
                logger
        )
                //.importFile(getAnonymizedDirectory(), file, this.lineWidth, backup);
                .importFile(getAnonymizedDirectory(), file, UserPreferences.getLoadedDocLineWidth(), backup);
        Platform.runLater(() -> {
            anonymizedFiles.add(document);
            sortDocuments();
            addDocumentListener(document);
        });
        return document;
    }

    private void addDocumentListener(AnonymizedFile document) {
        document.addUpdateListener(isDirect -> {
            anonymizedFilesModifiedProperty.setValue(true);
        });
    }

    /**
     * Runs a search across the active document or all documents, marking selected hits.
     */
    public List<SearchResult> search(SearchParams searchParams) {

        if (searchParams.isActiveDocumentFilter()) {
            final AnonymizedFile activeDocument = searchParams.getActiveDocument();
            return activeDocument.getDocument().search(searchParams, activeDocument.getName());
        }

        final List<SearchResult> allResults = new LinkedList<>();
        for (final AnonymizedFile document : anonymizedFiles) {
            final List<SearchResult> documentResults = document.getDocument().search(searchParams, document.getName());
            allResults.addAll(documentResults);
            if (searchParams.getActiveDocument() == null) continue;
            if (!document.getName().equals(searchParams.getActiveDocument().getName())) continue;
            documentResults.forEach(searchResult -> {
                if (!searchResult.getRange().equals(searchParams.getActiveSelection())) return;
                searchResult.setSelected(true);
            });
        }

        return allResults;
    }

    public AnonymizedFile getDocument(String name) {
        return anonymizedFiles.stream()
                .filter(anonymizedFile -> name.equals(anonymizedFile.getName()))
                .findAny()
                .orElse(null);
    }

    public boolean hasUnsavedData() {
        return replacementCollection.isModified()
                || categories.isModified()
                || anonymizedFiles.stream()
                .map(AnonymizedFile::isModified)
                .reduce((isAModified, isBModified) -> isAModified || isBModified)
                .orElse(false);
    }

    /**
     * Exports the project directories into a ZIP for transfer/archival.
     */
    public void exportProject(File targetZipPath) throws IOException {
        Map<String, List<File>> pathsToZip = new HashMap<>();
        pathsToZip.put("anonymized", Arrays.asList(
                getAnonymizedDirectory(),
                getCategoriesDirectory()
        ));
        pathsToZip.put("id", Arrays.asList(
                getIdentificationDirectory()
        ));
        //String targetZipPath = "/path/to/output/selected_folders.zip";
        FileZipper fileZipper = new FileZipper.Builder()
                .setPathsToZip(pathsToZip)
                .setProjectDirectory(projectDirectory)
                .setZipTargetDirectory(targetZipPath)
                .setProjectName(projectName)
                .build();
        fileZipper.exportProject();
    }

    /**
     * Saves categories, replacements, and modified documents with backups.
     */
    public boolean saveAllDocuments() {
        return StandardDialogs.progressDialog(() -> {
            final Backup backup = new Backup(getBackupDirectory(), logger);
            saveCategoriesFile(backup);
            saveReplacementsFile(backup);
            boolean allSuccess = true;
            for (final AnonymizedFile document : anonymizedFiles) {
                if (!document.isModified()) continue;
                final boolean success = document.save(backup);
                if (!success) {
                    allSuccess = false;
                }
            }
            anonymizedFilesModifiedProperty.setValue(false);
            return allSuccess;
        });
    }

    public boolean deleteDocument(AnonymizedFile document) {
        final boolean success = document.delete();
        if (!success) return false;
        anonymizedFiles.remove(document);
        return true;
    }

    public boolean restoreDocument(AnonymizedFile document) {
        boolean success = document.restore();
        if (!success) return false;
        anonymizedFiles.add(document);
        sortDocuments();
        return true;
    }

    private void sortDocuments() {
        anonymizedFiles.sort(Comparator.comparing(anonymizedFile -> anonymizedFile.getFile().getName()));
    }

    /**
     * Loads replacements from the project XLSX and updates the in-memory collection.
     */
    private void loadReplacementsFile() {
        try {
            final List<Replacement> replacements = ReplacementsXlsxFile.read(getReplacementsFile(), categories, logger);
            replacementCollection.setReplacements(replacements);
            replacementCollection.setModified(false);
            logger.logInfo("Loaded replacements file");
        } catch (FileNotFoundException ignored) {
        } catch (Exception e) {
            logger.logError("Error loading replacements.xlsx file", e);
        }
    }

    private String stackTraceToString(Throwable t) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        t.printStackTrace(pw);
        String stackTraceAsString = sw.toString();
        return stackTraceAsString;
    }

    /**
     * Persists replacements to XLSX and records a backup before overwriting.
     */
    private void saveReplacementsFile(Backup backup) {
        logger.logInfo("Starting to save replacements file...");
        try {
            backup.add(getReplacementsFile());
            ReplacementsXlsxFile.write(getReplacementsFile(), replacementCollection.getReplacements());
            replacementCollection.setModified(false);
            logger.logInfo("Saved replacements file " + getReplacementsFile());
            //use Throwable instead of Exception to catch all errors.
        } catch (Throwable t) {
            logger.logInfo("Error saving replacements.xlsx file" + t.getMessage());
            logger.logInfo("Error stack trace" + stackTraceToString(t));
        } finally {
            logger.logInfo("Finishing replacement save attempt..");
        }
    }

    public ReplacementCollection getReplacementCollection() {
        return replacementCollection;
    }

    public Categories getCategories() {
        return categories;
    }

    /**
     * Loads categories from XML and initializes list-backed resources.
     */
    private Categories loadCategoriesFile() {
        Categories categories;
        try {
            categories = XmlFileHandler.load(getCategoriesXmlFile(), Categories.class);
            logger.logInfo("Loaded categories.xml");
        } catch (FileNotFoundException ignored) {
            categories = new Categories();
        } catch (Exception ioe) {
            logger.logError("Error loading categories file (categories.xml)", ioe);
            categories = new Categories();
        }

        categories.init(getCategoriesDirectory(), logger);
        categories.setModified(false);
        return categories;
    }

    /**
     * Persists categories to XML and records a backup before overwriting.
     */
    private void saveCategoriesFile(Backup backup) {
        try {
            backup.add(getCategoriesXmlFile());
            XmlFileHandler.save(getCategoriesXmlFile(), categories);
            categories.setModified(false);
            logger.logInfo("Saved categories file " + getCategoriesXmlFile());
        } catch (Exception ioe) {
            logger.logError("Error saving categories file (categories.xml)", ioe);
        }
    }

    @Override
    public void runCommand(Command command) {
        command.setProject(this, logger);
        commandList.runCommand(command);
    }

    public CommandList getCommandList() {
        return commandList;
    }

    public int countOccurrences(Replacement replacement) {
        return anonymizedFiles.stream()
                .map(anonymizedFile -> anonymizedFile.getDocument().getMarkers().stream()
                        .filter(markerRuntime -> markerRuntime.getReplacement().equals(replacement))
                        .count()
                )
                .reduce(Long::sum)
                .orElse(0L)
                .intValue();
    }

    public String getAllOriginalStrings(Replacement replacement) {
        return anonymizedFiles.stream()
                .flatMap(anonymizedFile -> anonymizedFile.getDocument().getMarkersForStorage().stream()
                        .filter(markerRuntime -> markerRuntime.getReplacementId().equals(replacement.getId()))
                        .map(MarkerStorage::getOriginal)
                )
                .collect(Collectors.toSet())
                .stream()
                .reduce((s, s2) -> s + ",\n" + s2)
                .orElse("<none>");
    }

    public String getOneOriginalString(Replacement replacement) {
        return anonymizedFiles.stream()
                .flatMap(anonymizedFile ->
                        anonymizedFile.getDocument().getMarkersForStorage().stream()
                                .filter(markerRuntime -> markerRuntime.getReplacementId().equals(replacement.getId()))
                                .map(MarkerStorage::getOriginal)
                )
                .findAny()
                .orElse("<none>");
    }

    public ExportList getExportList() {
        return exportList;
    }

    public boolean isDirectoryStructureOk() {
        return getAnonymizedDirectory().exists() &&
                getCategoriesDirectory().exists();
    }

    /**
     * Ensures the on-disk project directory structure exists.
     */
    private void createDirectoryStructureIfNotExists() {
        FileTools.createDirectoryIfNotExists(getExportsDirectory());
        FileTools.createDirectoryIfNotExists(getAnonymizedDirectory());
        FileTools.createDirectoryIfNotExists(getIdentificationDirectory());
        FileTools.createDirectoryIfNotExists(getCategoriesDirectory());
        FileTools.createDirectoryIfNotExists(getTrashDirectory());
        FileTools.createDirectoryIfNotExists(getBackupDirectory());
    }

    private File getCategoriesXmlFile() {
        return new File(getCategoriesDirectory().getAbsoluteFile(), "categories.xml");
    }

    private File getReplacementsFile() {
        return new File(getIdentificationDirectory().getAbsoluteFile(), "replacements.xlsx");
    }

    private File getExportsDirectory() {
        return getSubDirectory("exports");
    }

    private File getAnonymizedDirectory() {
        return getSubDirectory("anonymized");
    }

    private File getIdentificationDirectory() {
        return getSubDirectory("identification");
    }

    public File getCategoriesDirectory() {
        return getSubDirectory("categories");
    }

    public File getTrashDirectory() {
        return getSubDirectory("trash");
    }

    public File getBackupDirectory() {
        return getSubDirectory("backup");
    }

    public File getAuditFile() {
        return new File(getProjectDirectory().getAbsoluteFile(), "auditfile.txt");
    }

    /**
     * Returns a subdirectory path under the project root.
     */
    private File getSubDirectory(String subDirectory) {
        return new File(projectDirectory.getAbsoluteFile(), subDirectory);
    }

    @Override
    public String toString() {
        return "Project{" +
                "projectDirectory=" + projectDirectory +
                '}';
    }
}
