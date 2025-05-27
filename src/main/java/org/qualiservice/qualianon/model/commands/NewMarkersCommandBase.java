package org.qualiservice.qualianon.model.commands;

import org.qualiservice.qualianon.model.SearchResult;
import org.qualiservice.qualianon.model.project.AnonymizedFile;
import org.qualiservice.qualianon.model.project.Replacement;
import org.qualiservice.qualianon.model.project.SearchParams;
import org.qualiservice.qualianon.model.text.MarkerRuntime;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public abstract class NewMarkersCommandBase extends Command {

    protected final SearchParams searchParams;
    protected final List<SearchResult> searchResults;
    protected final Replacement replacement;
    private Map<MarkerRuntime, AnonymizedFile> addedMarkers;
    private Map<String, AnonymizedFile> affectedDocuments;

    public NewMarkersCommandBase(SearchParams searchParams, List<SearchResult> searchResults, Replacement replacement) {
        this.searchParams = searchParams;
        this.searchResults = searchResults;
        this.replacement = replacement;
    }

    @Override
    public boolean run() {
        addedMarkers = new HashMap<>();
        affectedDocuments = new HashMap<>();
        searchResults.forEach(searchResult -> affectedDocuments.put(
                searchResult.getDocumentName(),
                project.getDocument(searchResult.getDocumentName())
        ));

        final LinkedList<String> errors = new LinkedList<>();
        for (final SearchResult searchResult : searchResults) {
            final AnonymizedFile document = affectedDocuments.get(searchResult.getDocumentName());
            final MarkerRuntime markerInSelection = document.getDocument().getMarkerInSelection(searchResult.getRange());
            if (markerInSelection != null) {
                errors.add(searchResult.getLineContext());
            }
        }
        if (!errors.isEmpty()) {
            errors.add(0, "No markers created! Cannot create marker(s) in an already marked area(s):");
            messageLogger.logError(errors.stream().reduce((s, s2) -> s + "\n" + s2).orElse("<none>"), null);
            return false;
        }

        for (final SearchResult searchResult : searchResults) {
            final AnonymizedFile document = affectedDocuments.get(searchResult.getDocumentName());
            final MarkerRuntime marker = new MarkerRuntime(searchResult.getRange(), replacement);
            document.getDocument().addMarker(marker);
            addedMarkers.put(marker, document);
        }

        finishUpdate();
        return true;
    }

    @Override
    public boolean undo() {
        addedMarkers.forEach((key1, value1) -> value1.getDocument().removeMarker(key1));
        finishUpdate();
        return true;
    }

    private void finishUpdate() {
        affectedDocuments.values().forEach(document -> document.finishUpdate(true));
    }

}
