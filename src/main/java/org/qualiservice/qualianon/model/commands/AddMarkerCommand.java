package org.qualiservice.qualianon.model.commands;

import org.qualiservice.qualianon.audit.TimedRunner;
import org.qualiservice.qualianon.model.PositionRange;
import org.qualiservice.qualianon.model.project.AnonymizedFile;
import org.qualiservice.qualianon.model.project.Replacement;
import org.qualiservice.qualianon.model.text.MarkerRuntime;
import org.qualiservice.qualianon.utility.StringUtils;


public class AddMarkerCommand extends Command {

    private final PositionRange range;
    private final AnonymizedFile document;
    private final Replacement replacement;
    private MarkerRuntime addedMarker;

    public AddMarkerCommand(PositionRange range, AnonymizedFile document, Replacement replacement) {
        this.range = range;
        this.document = document;
        this.replacement = replacement;
    }

    @Override
    public String getDescription() {
        return "Add marker with new replacement";
    }

    @Override
    public boolean run() {
        final long time = TimedRunner.run(() -> {
            project.getReplacementCollection().add(replacement);
            addedMarker = new MarkerRuntime(range, replacement);
            document.getDocument().addMarker(addedMarker);
            document.finishUpdate(true);
        });
        messageLogger.logInfo("Marked \"" + StringUtils.ellipsis(document.getDocument().getTextInRange(range), 30) + "\" with new replacement " + TimedRunner.format(time), this);
        return true;
    }

    @Override
    public boolean undo() {
        document.getDocument().removeMarker(addedMarker);
        project.getReplacementCollection().remove(replacement);
        document.finishUpdate(true);
        messageLogger.logInfo("Undo marked \"" + StringUtils.ellipsis(document.getDocument().getTextInRange(range), 30) + "\" with new replacement", this);
        return true;
    }

    @Override
    public String toString() {
        return "AddMarkerCommand{" +
                "range=" + range +
                ", document=" + document +
                ", replacement=" + replacement +
                ", project=" + project +
                '}';
    }
}
