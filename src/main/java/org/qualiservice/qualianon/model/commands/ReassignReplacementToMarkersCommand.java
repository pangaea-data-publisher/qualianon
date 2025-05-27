package org.qualiservice.qualianon.model.commands;

import org.qualiservice.qualianon.gui.components.markerstab.MarkerInDocument;
import org.qualiservice.qualianon.model.project.Replacement;
import org.qualiservice.qualianon.model.project.ReplacementCollection;

import java.util.List;

public class ReassignReplacementToMarkersCommand extends SetReplacementForMarkersCommandBase {

    public ReassignReplacementToMarkersCommand(List<MarkerInDocument> markers, Replacement replacement, ReplacementCollection replacementCollection) {
        super(markers, replacement, replacementCollection);
    }

    @Override
    public String getDescription() {
        return "Reassign markers to existing replacement";
    }

    @Override
    public boolean run() {
        super.run();
        messageLogger.logInfo("Reassigned " + markers.size() + " markers to existing replacement", this);
        return true;
    }

    @Override
    public boolean undo() {
        super.undo();
        messageLogger.logInfo("Undo reassigned " + markers.size() + " markers to existing replacement", this);
        return true;
    }

    @Override
    public String toString() {
        return "ReassignReplacementToMarkersCommand{" +
                "project=" + project +
                ", markers=" + markers +
                ", replacement=" + replacement +
                '}';
    }
}
