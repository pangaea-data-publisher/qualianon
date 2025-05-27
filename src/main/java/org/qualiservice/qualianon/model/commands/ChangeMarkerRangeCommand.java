package org.qualiservice.qualianon.model.commands;

import org.qualiservice.qualianon.model.PositionRange;
import org.qualiservice.qualianon.model.project.AnonymizedFile;
import org.qualiservice.qualianon.model.project.ReplacementCollection;
import org.qualiservice.qualianon.model.text.MarkerRuntime;
import org.qualiservice.qualianon.utility.StringUtils;


public class ChangeMarkerRangeCommand extends Command {

    private final AnonymizedFile document;
    private final MarkerRuntime marker;
    private final PositionRange range;
    private final ReplacementCollection replacementCollection;
    private PositionRange backupRange;

    public ChangeMarkerRangeCommand(AnonymizedFile document, MarkerRuntime marker, PositionRange range, ReplacementCollection replacementCollection) {
        this.document = document;
        this.marker = marker;
        this.range = range;
        this.replacementCollection = replacementCollection;
    }

    @Override
    public String getDescription() {
        return "Change marker range";
    }

    @Override
    public boolean run() {
        backupRange = marker.getPositionRange();
        marker.setPositionRange(range);
        document.finishUpdate(true);
        replacementCollection.notifyUpdateListeners(true);
        messageLogger.logInfo("Changed marker range to \"" + StringUtils.ellipsis(document.getDocument().getTextInRange(range), 30) + "\"", this);
        return true;
    }

    @Override
    public boolean undo() {
        marker.setPositionRange(backupRange);
        document.finishUpdate(true);
        replacementCollection.notifyUpdateListeners(true);
        messageLogger.logInfo("Undo changed marker range to \"" + StringUtils.ellipsis(document.getDocument().getTextInRange(range), 30) + "\"", this);
        return true;
    }

    @Override
    public String toString() {
        return "ChangeMarkerRangeCommand{" +
                "document=" + document +
                ", marker=" + marker +
                ", range=" + range +
                ", project=" + project +
                '}';
    }
}
