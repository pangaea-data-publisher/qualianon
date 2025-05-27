package org.qualiservice.qualianon.model.commands;

import org.qualiservice.qualianon.model.SearchResult;
import org.qualiservice.qualianon.model.project.Replacement;
import org.qualiservice.qualianon.model.project.SearchParams;
import org.qualiservice.qualianon.utility.StringUtils;

import java.util.List;


public class AddMarkersAndAssignCommand extends NewMarkersCommandBase {

    public AddMarkersAndAssignCommand(SearchParams searchParams, List<SearchResult> selectedSearchResults, Replacement replacement) {
        super(searchParams, selectedSearchResults, replacement);
    }

    @Override
    public String getDescription() {
        return "Add markers and assign replacement";
    }

    @Override
    public boolean run() {
        final boolean success = super.run();
        if (!success) {
            return false;
        }

        messageLogger.logInfo("Marked and assigned " + searchResults.size() + " occurrences of \"" + StringUtils.ellipsis(searchParams.getText(), 30) + "\"", this);
        return true;
    }

    @Override
    public boolean undo() {
        messageLogger.logInfo("Undo marked and assigned " + searchResults.size() + " occurrences of \"" + StringUtils.ellipsis(searchParams.getText(), 30) + "\"", this);
        return super.undo();
    }

    @Override
    public String toString() {
        return "AddMarkersAndAssignCommand{" +
                "project=" + project +
                ", searchParams=" + searchParams +
                ", searchResults=" + searchResults +
                ", replacement=" + replacement +
                '}';
    }

}
