package org.qualiservice.qualianon.model.commands;

import org.qualiservice.qualianon.model.project.Replacement;
import org.qualiservice.qualianon.model.project.ReplacementCollection;


public class EditReplacementCommand extends Command {

    private final Replacement replacement;
    private final ReplacementCollection replacementCollection;
    private Replacement backupReplacement;

    public EditReplacementCommand(Replacement replacement, ReplacementCollection replacementCollection) {
        this.replacement = replacement;
        this.replacementCollection = replacementCollection;
    }

    @Override
    public String getDescription() {
        return "Edit replacement";
    }

    @Override
    public boolean run() {
        backupReplacement = new Replacement(replacementCollection.getById(replacement.getId()), messageLogger);
        replacementCollection.updateReplacement(replacement);
        messageLogger.logInfo("Replacement updated", this);
        return true;
    }

    @Override
    public boolean undo() {
        replacementCollection.updateReplacement(backupReplacement);
        messageLogger.logInfo("Undo replacement updated", this);
        return true;
    }

    @Override
    public String toString() {
        return "EditReplacementCommand{" +
                "project=" + project +
                ", replacement=" + replacement +
                '}';
    }
}
