package org.qualiservice.qualianon.model.commands;

import org.qualiservice.qualianon.gui.components.markerstab.MarkerInDocument;
import org.qualiservice.qualianon.model.project.AnonymizedFile;
import org.qualiservice.qualianon.model.project.Replacement;
import org.qualiservice.qualianon.model.project.ReplacementCollection;

import java.util.List;
import java.util.stream.Collectors;


public class ReplacementDeleter {

    private final Replacement replacement;
    private final ReplacementCollection replacementCollection;
    private final List<MarkerInDocument> markerBackupList;


    public ReplacementDeleter(Replacement replacement, ReplacementCollection replacementCollection, List<AnonymizedFile> documents) {
        this.replacement = replacement;
        this.replacementCollection = replacementCollection;

        markerBackupList = documents.stream()
                .flatMap(anonymizedFile -> anonymizedFile.getDocument().getMarkers().stream()
                        .filter(markerRuntime -> markerRuntime.getReplacement().equals(replacement))
                        .map(markerRuntime -> new MarkerInDocument(markerRuntime, anonymizedFile))
                )
                .collect(Collectors.toList());
    }

    public void run() {
        markerBackupList.forEach(markerInDocument ->
                markerInDocument.getDocument().getDocument().removeMarker(markerInDocument.getMarkerRuntime())
        );
        replacementCollection.remove(replacement);

        notifyDocuments();
    }

    public void undo() {
        markerBackupList.forEach(markerInDocument ->
                markerInDocument.getDocument().getDocument().addMarker(markerInDocument.getMarkerRuntime())
        );
        replacementCollection.add(replacement);

        notifyDocuments();
    }

    private void notifyDocuments() {
        markerBackupList.stream()
                .map(MarkerInDocument::getDocument)
                .collect(Collectors.toSet())
                .forEach(anonymizedFile -> anonymizedFile.finishUpdate(true));
    }

    @Override
    public String toString() {
        return "ReplacementDeleter{" +
                "replacement=" + replacement +
                '}';
    }
}
