package org.qualiservice.qualianon.model.commands;

import org.qualiservice.qualianon.gui.components.markerstab.MarkerInDocument;

import java.util.List;


public class RemoveMarkerCommand extends Command {

    private final List<MarkerInDocument> markers;

    public RemoveMarkerCommand(List<MarkerInDocument> markers) {
        this.markers = markers;
    }

    @Override
    public String getDescription() {
        return "Remove " + markers.size() + " marker(s)";
    }

    @Override
    public boolean run() {
        markers.forEach(markerInDocument -> {
            markerInDocument.getDocument().getDocument().removeMarker(markerInDocument.getMarkerRuntime());
            markerInDocument.getDocument().finishUpdate(true);
        });
        messageLogger.logInfo("Removed " + markers.size() + " marker(s)", this);
        return true;
    }

    @Override
    public boolean undo() {
        markers.forEach(markerInDocument -> {
            markerInDocument.getDocument().getDocument().addMarker(markerInDocument.getMarkerRuntime());
            markerInDocument.getDocument().finishUpdate(true);
        });
        messageLogger.logInfo("Undo removed " + markers.size() + " marker(s)", this);
        return true;
    }

    @Override
    public String toString() {
        return "RemoveMarkerCommand{" +
                "project=" + project +
                ", markers=" + markers +
                '}';
    }
}
