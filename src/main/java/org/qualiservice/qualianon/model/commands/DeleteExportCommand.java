package org.qualiservice.qualianon.model.commands;

import org.qualiservice.qualianon.model.exports.Export;
import org.qualiservice.qualianon.model.exports.ExportList;


public class DeleteExportCommand extends Command {

    private final Export export;
    private final ExportList exportList;

    public DeleteExportCommand(Export export, ExportList exportList) {
        this.export = export;
        this.exportList = exportList;
    }

    @Override
    public String getDescription() {
        return "Delete export \"" + export.getName() + "\"";
    }

    @Override
    public boolean run() {
        final boolean success = exportList.deleteExport(export);
        if (!success) {
            messageLogger.logError("Error " + getDescription(), null);
            return false;
        }
        messageLogger.logInfo(getDescription(), this);
        return true;
    }

    @Override
    public boolean undo() {
        final boolean success = exportList.restoreExport(export);
        if (!success) {
            messageLogger.logError("Error: Undo " + getDescription(), null);
            return false;
        }
        messageLogger.logInfo("Undo " + getDescription(), this);
        return true;
    }

    @Override
    public String toString() {
        return "DeleteExportCommand{" +
                "project=" + project +
                ", export=" + export +
                '}';
    }
}
