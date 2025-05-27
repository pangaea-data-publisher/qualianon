package org.qualiservice.qualianon.model.exports;

import org.qualiservice.qualianon.files.XmlFileHandler;
import org.qualiservice.qualianon.audit.MessageLogger;
import org.qualiservice.qualianon.model.anonymization.AnonymizationProfile;
import org.qualiservice.qualianon.model.project.AnonymizedFile;
import org.qualiservice.qualianon.utility.UpdateListener;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;


public class Export {

    private String name;
    private AnonymizationProfile anonymizationProfile;
    private final File exportsDirectory;
    private final File trashDirectory;
    private final MessageLogger messageLogger;
    private File trashFile;


    public Export(File exportsDirectory, File trashDirectory, MessageLogger messageLogger) {
        this.exportsDirectory = exportsDirectory;
        this.trashDirectory = trashDirectory;
        this.messageLogger = messageLogger;
    }

    public String getName() {
        return name;
    }

    public Export setName(String name) {
        this.name = name;
        return this;
    }

    public AnonymizationProfile getAnonymizationProfile() {
        return anonymizationProfile;
    }

    public Export setAnonymizationProfile(AnonymizationProfile anonymizationProfile) {
        this.anonymizationProfile = anonymizationProfile;
        return this;
    }

    public Export saveSettingsAndDocuments(List<AnonymizedFile> anonymizedFiles) throws IOException {
        AtomicBoolean error = new AtomicBoolean(false);
        try {
            XmlFileHandler.save(getProfileXmlFile(), anonymizationProfile);
            anonymizationProfile.setPersisted();
            messageLogger.logInfo("Saved export settings \"" + name + "\"");
            anonymizedFiles.forEach(anonymizedFile -> {
                try {
                    anonymizedFile.export(this);
                    messageLogger.logInfo("Exported " + anonymizedFile.getExportFilename());
                } catch (IOException e) {
                    messageLogger.logError("Error exporting " + anonymizedFile.getExportFilename(), e);
                    error.set(true);
                }

            });
        } catch (IOException e) {
            messageLogger.logError("Error saving export settings \"" + name + "\"", e);
            error.set(true);
        }
        if (error.get()) {
            throw new IOException("Could not save all files");
        }
        return this;
    }

    public Export load(File myDirectory) throws IOException {
        name = myDirectory.getName();
        final AnonymizationProfile anonymizationProfile = XmlFileHandler
                .load(getProfileXmlFile(), AnonymizationProfile.class)
                .setPersisted();
        setAnonymizationProfile(anonymizationProfile);
        return this;
    }

    public boolean delete() {
        trashFile = new File(trashDirectory.getAbsolutePath(), name + "." + UUID.randomUUID());
        return getMyDirectory().renameTo(trashFile);
    }

    public boolean restore() {
        final boolean success = trashFile.renameTo(getMyDirectory());
        if (!success) return false;
        trashFile = null;
        return true;
    }

    public boolean rename(String newName) {
        final File newFile = new File(exportsDirectory.getAbsolutePath(), newName);
        final boolean success = getMyDirectory().renameTo(newFile);
        if (!success) return false;
        name = newName;
        return true;
    }

    private File getProfileXmlFile() {
        return new File(getMyDirectory().getAbsolutePath(), "profile.xml");
    }

    public File getMyDirectory() {
        final File directory = new File(exportsDirectory.getAbsolutePath(), name);
        if (!directory.exists()) {
            //noinspection ResultOfMethodCallIgnored
            directory.mkdir();
        }
        return directory;
    }

    public void addUpdateListener(UpdateListener listener) {
        anonymizationProfile.addUpdateListener(listener);
    }

    public void removeUpdateListener(UpdateListener listener) {
        anonymizationProfile.removeUpdateListener(listener);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Export export = (Export) o;
        return name.equals(export.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "Export{" +
                "name='" + name + '\'' +
                ", anonymizationProfile=" + anonymizationProfile +
                ", exportsDirectory=" + exportsDirectory +
                ", trashDirectory=" + trashDirectory +
                ", trashFile=" + trashFile +
                '}';
    }
}
