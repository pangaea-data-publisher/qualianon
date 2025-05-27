package org.qualiservice.qualianon.model.commands;

import org.qualiservice.qualianon.model.exports.Export;
import org.qualiservice.qualianon.model.exports.ExportList;

import java.io.IOException;


public class DuplicateExportCommand extends Command {

    private final String name;
    private final Export export;
    private final ExportList exportList;
    private Export duplicatedExport;


    public DuplicateExportCommand(String name, Export export, ExportList exportList) {
        this.name = name;
        this.export = export;
        this.exportList = exportList;
    }

    @Override
    public String getDescription() {
        return "Duplicate export \"" + export.getName() + "\"";
    }

    @Override
    public boolean run() {
        try {
            duplicatedExport = exportList.newExport(name, export.getAnonymizationProfile().clone(messageLogger), project.getAnonymizedFiles());
            messageLogger.logInfo(getDescription(), this);
            return true;
        } catch (IOException e) {
            messageLogger.logError("Error: " + getDescription(), e);
            return false;
        }
    }

    @Override
    public boolean undo() {
        final boolean success = project.getExportList().deleteExport(duplicatedExport);
        if (!success) {
            messageLogger.logError("Error: Undo " + getDescription(), null);
            return false;
        }
        messageLogger.logInfo("Undo " + getDescription(), this);
        return true;
    }

    @Override
    public String toString() {
        return "DuplicateExportCommand{" +
                "project=" + project +
                ", name='" + name + '\'' +
                ", export=" + export +
                '}';
    }
}
