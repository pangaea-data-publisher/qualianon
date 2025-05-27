package org.qualiservice.qualianon.model.exports;

import javafx.collections.ObservableList;
import org.qualiservice.qualianon.audit.MessageLogger;
import org.qualiservice.qualianon.model.anonymization.AnonymizationProfile;
import org.qualiservice.qualianon.model.project.AnonymizedFile;
import org.qualiservice.qualianon.utility.UpdatableImpl;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;


public class ExportList extends UpdatableImpl {

    private List<Export> exports;
    private final File trashDirectory;
    private final File exportsDirectory;
    private final MessageLogger messageLogger;


    public ExportList(File exportsDirectory, File trashDirectory, MessageLogger messageLogger) {
        this.exportsDirectory = exportsDirectory;
        this.messageLogger = messageLogger;
        exports = Arrays.stream(Objects.requireNonNull(exportsDirectory.listFiles()))
                .filter(file -> !file.getName().startsWith("."))
                .map(file -> {
                    try {
                        return new Export(exportsDirectory, trashDirectory, messageLogger).load(file);
                    } catch (IOException e) {
                        messageLogger.logError("Export error", e);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        this.trashDirectory = trashDirectory;
    }

    public List<Export> getExports() {
        return exports;
    }

    public void setExports(List<Export> exports) {
        this.exports = exports;
        notifyUpdateListeners(true);
    }

    public Export newExport(String name, AnonymizationProfile anonymizationProfile, ObservableList<AnonymizedFile> anonymizedFiles) throws IOException {
        final Export export = new Export(exportsDirectory, trashDirectory, messageLogger)
                .setName(getUniqueName(name))
                .setAnonymizationProfile(anonymizationProfile)
                .saveSettingsAndDocuments(anonymizedFiles);
        exports.add(export);
        notifyUpdateListeners(true);
        return export;
    }

    public boolean deleteExport(Export export) {
        final boolean success = export.delete();
        if (!success) return false;
        exports.remove(export);
        notifyUpdateListeners(true);
        return true;
    }

    public boolean restoreExport(Export export) {
        final boolean success = export.restore();
        if (!success) return false;
        exports.add(export);
        notifyUpdateListeners(true);
        return true;
    }

    public boolean renameExport(Export export, String name) {
        final boolean success = export.rename(name);
        if (!success) return false;
        notifyUpdateListeners(true);
        return true;
    }

    public String getUniqueName(String name) {
        final AtomicInteger counter = new AtomicInteger(1);

        while (true) {
            final long found = Arrays.stream(Objects.requireNonNull(exportsDirectory.listFiles()))
                    .filter(file -> file.getName().equals(makeDuplicateName(name, counter.intValue())))
                    .count();
            if (found == 0) break;
            counter.incrementAndGet();
        }

        return makeDuplicateName(name, counter.intValue());
    }

    private String makeDuplicateName(String name, int count) {
        if (count == 1) return name;
        return name + " (" + count + ")";
    }

}
