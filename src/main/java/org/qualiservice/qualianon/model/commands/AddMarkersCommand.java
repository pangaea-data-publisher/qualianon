package org.qualiservice.qualianon.model.commands;

import org.qualiservice.qualianon.audit.TimedRunner;
import org.qualiservice.qualianon.model.SearchResult;
import org.qualiservice.qualianon.model.project.Replacement;
import org.qualiservice.qualianon.model.project.SearchParams;
import org.qualiservice.qualianon.utility.StringUtils;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;


public class AddMarkersCommand extends NewMarkersCommandBase {

    public AddMarkersCommand(SearchParams searchParams, List<SearchResult> searchResults, Replacement replacement) {
        super(searchParams, searchResults, replacement);
    }

    @Override
    public String getDescription() {
        return "Add markers with new replacement";
    }

    @Override
    public boolean run() {
        final AtomicBoolean success = new AtomicBoolean(false);

        final long time = TimedRunner.run(() -> {
            success.set(super.run());
            if (!success.get()) return;
            project.getReplacementCollection().add(replacement);
        });

        if (!success.get()) {
            return false;
        }

        messageLogger.logInfo("Marked " + searchResults.size() + " occurrences of \"" + StringUtils.ellipsis(searchParams.getText(), 30) + "\" with new replacement " + TimedRunner.format(time), this);
        return true;
    }

    @Override
    public boolean undo() {
        super.undo();
        project.getReplacementCollection().remove(replacement);
        messageLogger.logInfo("Undo marked " + searchResults.size() + " occurrences of \"" + StringUtils.ellipsis(searchParams.getText(), 30) + "\" with new replacement", this);
        return true;
    }

    @Override
    public String toString() {
        return "AddMarkersCommand{" +
                "project=" + project +
                ", searchParams=" + searchParams +
                ", searchResults=" + searchResults +
                ", replacement=" + replacement +
                '}';
    }
}