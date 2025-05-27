package org.qualiservice.qualianon.model.commands;

import org.qualiservice.qualianon.gui.components.markerstab.MarkerInDocument;
import org.qualiservice.qualianon.model.project.Replacement;
import org.qualiservice.qualianon.model.project.ReplacementCollection;

import java.util.List;

public class NewReplacementForMarkersCommand extends SetReplacementForMarkersCommandBase {

    public NewReplacementForMarkersCommand(List<MarkerInDocument> markers, Replacement replacement, ReplacementCollection replacementCollection) {
        super(markers, replacement, replacementCollection);
    }

    @Override
    public String getDescription() {
        return "New replacement for markers";
    }

    @Override
    public boolean run() {
        super.run();
        replacementCollection.add(replacement);
        messageLogger.logInfo("Assigned " + markers.size() + " markers to new replacement", this);
        return true;
    }

    @Override
    public boolean undo() {
        super.undo();
        replacementCollection.remove(replacement);
        messageLogger.logInfo("Undo assigned " + markers.size() + " markers to new replacement", this);
        return true;
    }

    @Override
    public String toString() {
        return "NewReplacementForMarkersCommand{" +
                "project=" + project +
                ", markers=" + markers +
                ", replacement=" + replacement +
                '}';
    }
}
