package org.qualiservice.qualianon.gui.components.markerstab;

import org.qualiservice.qualianon.model.project.AnonymizedFile;
import org.qualiservice.qualianon.model.text.MarkerRuntime;

public class MarkerInDocument {

    private final MarkerRuntime markerRuntime;
    private final AnonymizedFile document;

    public MarkerInDocument(MarkerRuntime markerRuntime, AnonymizedFile document) {
        this.markerRuntime = markerRuntime;
        this.document = document;
    }

    public MarkerRuntime getMarkerRuntime() {
        return markerRuntime;
    }

    public AnonymizedFile getDocument() {
        return document;
    }

    @Override
    public String toString() {
        return "MarkerInDocument{" +
                "markerRuntime=" + markerRuntime +
                ", document=" + document +
                '}';
    }
}
