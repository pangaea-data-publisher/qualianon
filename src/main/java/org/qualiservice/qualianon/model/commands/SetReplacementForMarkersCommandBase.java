package org.qualiservice.qualianon.model.commands;

import org.qualiservice.qualianon.gui.components.markerstab.MarkerInDocument;
import org.qualiservice.qualianon.model.project.Replacement;
import org.qualiservice.qualianon.model.project.ReplacementCollection;

import java.util.HashMap;
import java.util.List;

public abstract class SetReplacementForMarkersCommandBase extends Command {

    protected final List<MarkerInDocument> markers;
    protected final Replacement replacement;
    protected final ReplacementCollection replacementCollection;
    private HashMap<MarkerInDocument, Replacement> replacementBackups;

    public SetReplacementForMarkersCommandBase(List<MarkerInDocument> markers, Replacement replacement, ReplacementCollection replacementCollection) {
        this.markers = markers;
        this.replacement = replacement;
        this.replacementCollection = replacementCollection;
    }

    @Override
    public boolean run() {
        replacementBackups = new HashMap<>();
        markers.forEach(markerInDocument -> {
            replacementBackups.put(markerInDocument, markerInDocument.getMarkerRuntime().getReplacement());
            markerInDocument.getMarkerRuntime().setReplacement(replacement);
            markerInDocument.getDocument().finishUpdate(true);
        });
        return true;
    }

    @Override
    public boolean undo() {
        replacementBackups.forEach((markerInDocument, replacementBackup) -> {
            markerInDocument.getMarkerRuntime().setReplacement(replacementBackup);
            markerInDocument.getDocument().finishUpdate(true);
        });
        return true;
    }
}
