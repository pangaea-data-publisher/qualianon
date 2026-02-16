package org.qualiservice.qualianon.model.project;

import javafx.application.Platform;
import org.qualiservice.qualianon.audit.MessageLogger;
import org.qualiservice.qualianon.files.Backup;
import org.qualiservice.qualianon.files.DocxFile;
import org.qualiservice.qualianon.files.MarkerXlsxFile;
import org.qualiservice.qualianon.gui.tools.UpdatableElement;
import org.qualiservice.qualianon.model.exports.Export;
import org.qualiservice.qualianon.model.text.IndexedText;
import org.qualiservice.qualianon.model.text.LineBreaker;
import org.qualiservice.qualianon.model.text.MarkerStorage;
import org.qualiservice.qualianon.utility.StringUtils;
import org.qualiservice.qualianon.utility.UpdateListener;
import org.qualiservice.qualianon.utility.UserPreferences;

import java.io.File;
import java.io.IOException;
import java.util.*;


public class AnonymizedFile extends UpdatableElement {

    private IndexedText indexedText;
    private File file;
    private UUID trashFileId;
    private final File identificationDirectory;
    private boolean modified;
    private final List<UpdateListener> updateListeners;
    private final ReplacementCollection replacementCollection;
    private final File trashDirectory;
    private final MessageLogger messageLogger;


    public AnonymizedFile(ReplacementCollection replacementCollection, File identificationDirectory, File trashDirectory, MessageLogger messageLogger) {
        this.replacementCollection = replacementCollection;
        this.identificationDirectory = identificationDirectory;
        this.trashDirectory = trashDirectory;
        this.messageLogger = messageLogger;
        updateListeners = new LinkedList<>();
    }

    public void addUpdateListener(UpdateListener updateListener) {
        updateListeners.add(updateListener);
    }

    private void notifyUpdateListeners() {
        updateListeners.forEach(updateListener -> updateListener.onUpdate(true));
    }

    public File getFile() {
        return file;
    }

    public String getName() {
        return StringUtils.stripFileEnding(file.getName());
    }

    public String getExportName(Export export) {
        return getName() + " [" + export.getName() + "]";
    }

    public String getExportFilename() {
        return getName() + " [export].docx";
    }

    public boolean isModified() {
        return modified;
    }

    /**
     * Loads an existing anonymized DOCX and its marker storage into memory.
     */
    public AnonymizedFile load(File file) throws IOException {
        this.file = file;
        reload();
        return this;
    }

    /**
     * Reloads the document text and markers from disk.
     */
    public void reload() throws IOException {
        final String anonymized = DocxFile.read(file);
        final List<MarkerStorage> markerStorages = MarkerXlsxFile.read(getMarkerStorageFile());
        final IndexedText indexedText = IndexedText.fromAnonymized(anonymized, markerStorages, replacementCollection, messageLogger);
        updateDocument(indexedText, false);
    }

    /**
     * Imports a DOCX, applies line-breaking rules, and persists as a project document.
     */
    public AnonymizedFile importFile(File directory, File importFile, int lineLength, Backup backup) throws IOException, DocumentAlreadyImportedException {
        final File file = new File(directory.getPath() + "/" + importFile.getName());
        if (file.exists()) {
            throw new DocumentAlreadyImportedException();
        }

        final String imported = DocxFile.read(importFile);
        final String formatted = LineBreaker.formatDocument(imported, lineLength, UserPreferences.getLoadedLinebreakSettings());
        final IndexedText indexedText = IndexedText.fromAnonymized(formatted, Collections.emptyList(), replacementCollection, messageLogger);
        updateDocument(indexedText, false);
        saveAs(file, backup);
        return this;
    }

    public void updateDocument(IndexedText document, boolean modified) {
        this.indexedText = document;
        finishUpdate(modified);
    }

    public IndexedText getDocument() {
        return indexedText;
    }

    /*
     * Call finishUpdate after this
     */
    public void setDocument(IndexedText document) {
        this.indexedText = document;
    }

    public void finishUpdate(boolean modified) {
        this.modified = modified;
        update();
        notifyUpdateListeners();
    }

    /**
     * Saves the current document and marker storage, creating backups first.
     */
    public boolean save(Backup backup) {
        try {
            backup.add(file);
            DocxFile.write(file, indexedText.toAnonymized());
            backup.add(getMarkerStorageFile());
            MarkerXlsxFile.write(getMarkerStorageFile(), indexedText.getMarkersForStorage());

            modified = false;
            Platform.runLater(this::update);
            messageLogger.logInfo("Saved document " + getName());
            return true;

        } catch (Exception e) {
            messageLogger.logError("Error saving document " + getName(), e);
            return false;
        }
    }

    public void saveAs(File file, Backup backup) {
        this.file = file;
        save(backup);
    }

    /**
     * Moves the document and marker files into the trash directory.
     */
    public boolean delete() {
        if (trashFileId != null) return false;
        trashFileId = UUID.randomUUID();
        final boolean successDocument = file.renameTo(getDocumentTrashFile());
        final boolean successMarker = getMarkerStorageFile().renameTo(getMarkerTrashFile());
        return successDocument && successMarker;
    }

    /**
     * Restores the document and marker files from trash.
     */
    public boolean restore() {
        if (trashFileId == null) return false;
        final boolean successDocument = getDocumentTrashFile().renameTo(file);
        final boolean successMarker = getMarkerTrashFile().renameTo(getMarkerStorageFile());
        trashFileId = null;
        return successDocument && successMarker;
    }

    /**
     * Writes an anonymized export DOCX using the provided profile.
     */
    public void export(Export export) throws IOException {
        final File exportFile = new File(export.getMyDirectory(), getExportFilename());
        DocxFile.write(exportFile, indexedText.toExport(export.getAnonymizationProfile(), UserPreferences.getExportLineNumbers()));
    }

    public void setFile(File file) {
        this.file = file;
    }

    public File getMarkerStorageFile() {
        final String name = file.getName();
        final int dotIndex = name.lastIndexOf('.');
        final String markersName = name.substring(0, dotIndex) + "_markers.xlsx";
        return new File(identificationDirectory.getAbsoluteFile(), markersName);
    }

    private File getDocumentTrashFile() {
        return new File(trashDirectory.getAbsolutePath(), getName() + "." + trashFileId + ".docx");
    }

    private File getMarkerTrashFile() {
        return new File(trashDirectory.getAbsolutePath(), StringUtils.stripFileEnding(getMarkerStorageFile().getName()) + "." + trashFileId + ".xlsx");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AnonymizedFile that = (AnonymizedFile) o;
        return modified == that.modified && Objects.equals(indexedText, that.indexedText) && Objects.equals(file, that.file) && Objects.equals(updateListeners, that.updateListeners);
    }

    @Override
    public int hashCode() {
        return Objects.hash(indexedText, file, modified, updateListeners);
    }

    @Override
    public String toString() {
        return "AnonymizedFile{" +
                "file=" + file +
                '}';
    }
}
