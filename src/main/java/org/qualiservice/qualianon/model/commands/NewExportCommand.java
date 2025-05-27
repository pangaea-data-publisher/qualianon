package org.qualiservice.qualianon.model.commands;

import org.qualiservice.qualianon.model.anonymization.AnonymizationProfile;
import org.qualiservice.qualianon.model.exports.Export;

import java.io.IOException;


public class NewExportCommand extends Command {

    private final String name;
    private final AnonymizationProfile anonymizationProfile;
    private Export export;


    public NewExportCommand(String name, AnonymizationProfile anonymizationProfile) {
        this.name = name;
        this.anonymizationProfile = anonymizationProfile;
    }

    @Override
    public String getDescription() {
        return "New export";
    }

    @Override
    public boolean run() {
        try {
            export = project.getExportList().newExport(name, anonymizationProfile, project.getAnonymizedFiles());
            messageLogger.logInfo("Created new export \"" + export.getName() + "\"", this);
            return true;
        } catch (IOException e) {
            messageLogger.logError("Error creating new export", e);
            return false;
        }
    }

    @Override
    public boolean undo() {
        final boolean success = project.getExportList().deleteExport(export);
        if (!success) {
            messageLogger.logError("Error: Undo created new export \"" + export.getName() + "\"", null);
            return false;
        }
        messageLogger.logInfo("Undo created new export \"" + export.getName() + "\"", this);
        return true;
    }

    public Export getExport() {
        return export;
    }

    @Override
    public String toString() {
        return "NewExportCommand{" +
                "project=" + project +
                ", name='" + name + '\'' +
                ", anonymizationProfile=" + anonymizationProfile +
                ", export=" + export +
                '}';
    }
}
