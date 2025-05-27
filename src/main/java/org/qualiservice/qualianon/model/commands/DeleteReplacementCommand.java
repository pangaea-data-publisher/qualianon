package org.qualiservice.qualianon.model.commands;

import org.qualiservice.qualianon.model.project.AnonymizedFile;
import org.qualiservice.qualianon.model.project.Replacement;
import org.qualiservice.qualianon.model.project.ReplacementCollection;

import java.util.List;


public class DeleteReplacementCommand extends Command {

    private final ReplacementDeleter replacementDeleter;

    public DeleteReplacementCommand(Replacement replacement, ReplacementCollection replacementCollection, List<AnonymizedFile> documents) {
        replacementDeleter = new ReplacementDeleter(replacement, replacementCollection, documents);
    }

    @Override
    public String getDescription() {
        return "Delete replacement";
    }

    @Override
    public boolean run() {
        replacementDeleter.run();
        messageLogger.logInfo("Removed replacement", this);
        return true;
    }

    @Override
    public boolean undo() {
        replacementDeleter.undo();
        messageLogger.logInfo("Undo removed replacement", this);
        return true;
    }

    @Override
    public String toString() {
        return "DeleteReplacementCommand{" +
                "project=" + project +
                ", replacementDeleter=" + replacementDeleter +
                '}';
    }
}
