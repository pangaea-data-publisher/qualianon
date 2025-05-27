package org.qualiservice.qualianon.model.commands;

import org.qualiservice.qualianon.model.project.AnonymizedFile;


public class DeleteDocumentCommand extends Command {

    private final AnonymizedFile document;

    public DeleteDocumentCommand(AnonymizedFile document) {
        this.document = document;
    }

    @Override
    public String getDescription() {
        return "Delete document \"" + document.getName() + "\"";
    }

    @Override
    public boolean run() {
        final boolean success = project.deleteDocument(document);
        if (!success) {
            messageLogger.logError("Error " + getDescription(), null);
            return false;
        }
        messageLogger.logInfo(getDescription(), this);
        return true;
    }

    @Override
    public boolean undo() {
        final boolean success = project.restoreDocument(document);
        if (!success) {
            messageLogger.logError("Error undo " + getDescription(), null);
            return false;
        }
        messageLogger.logInfo("Undo " + getDescription(), this);
        return true;
    }

    @Override
    public String toString() {
        return "DeleteDocumentCommand{" +
                "project=" + project +
                ", document=" + document +
                '}';
    }
}
