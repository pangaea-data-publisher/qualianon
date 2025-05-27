package org.qualiservice.qualianon.model.commands;

import org.qualiservice.qualianon.model.exports.Export;
import org.qualiservice.qualianon.model.exports.ExportList;


public class RenameExportCommand extends Command {

    private final String newName;
    private final Export export;
    private final ExportList exportList;
    private final String oldName;

    public RenameExportCommand(String newName, Export export, ExportList exportList) {
        this.newName = newName;
        this.export = export;
        this.exportList = exportList;
        oldName = export.getName();
    }

    @Override
    public String getDescription() {
        return "Rename export \"" + oldName + "\"";
    }

    @Override
    public boolean run() {
        final boolean success = exportList.renameExport(export, newName);
        if (!success) {
            messageLogger.logError(getDescription(), null);
            return false;
        }
        messageLogger.logInfo(getDescription(), this);
        return true;
    }

    @Override
    public boolean undo() {
        final boolean success = exportList.renameExport(export, oldName);
        if (!success) {
            messageLogger.logError("Undo " + getDescription(), null);
            return false;
        }
        messageLogger.logInfo("Undo " + getDescription(), this);
        return true;
    }

    @Override
    public String toString() {
        return "RenameExportCommand{" +
                "project=" + project +
                ", newName='" + newName + '\'' +
                ", export=" + export +
                ", oldName='" + oldName + '\'' +
                '}';
    }
}

