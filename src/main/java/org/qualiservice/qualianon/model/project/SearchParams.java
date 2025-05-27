package org.qualiservice.qualianon.model.project;

import org.qualiservice.qualianon.model.PositionRange;

import java.util.Locale;
import java.util.Objects;
import java.util.stream.Stream;


public class SearchParams {

    private final String text;
    private final PositionRange activeSelection;
    private AnonymizedFile activeDocument;
    private boolean wholeWords;
    private boolean matchCase;
    private boolean unmarked;
    private boolean activeDocumentFilter;

    public static SearchParams plain(String text) {
        return new SearchParams(text, null, null, false, false, false, false);
    }

    public SearchParams(String text, PositionRange activeSelection, AnonymizedFile activeDocument, boolean wholeWords, boolean matchCase, boolean unmarked, boolean activeDocumentFilter) {
        this.text = text;
        this.activeSelection = activeSelection;
        this.activeDocument = activeDocument;
        this.wholeWords = wholeWords;
        this.matchCase = matchCase;
        this.unmarked = unmarked;
        this.activeDocumentFilter = activeDocumentFilter;
    }

    public String getText() {
        return text;
    }

    public String getSearchString() {
        if (matchCase) return text;
        else return text.toLowerCase(Locale.getDefault());
    }

    public PositionRange getActiveSelection() {
        return activeSelection;
    }

    public AnonymizedFile getActiveDocument() {
        return activeDocument;
    }

    public SearchParams withActiveDocument(AnonymizedFile selectedBaseTab) {
        activeDocument = selectedBaseTab;
        return this;
    }

    public boolean isWholeWords() {
        return wholeWords;
    }

    public SearchParams withWholeWords() {
        wholeWords = true;
        return this;
    }

    public boolean isMatchCase() {
        return matchCase;
    }

    public SearchParams withMatchCase() {
        matchCase = true;
        return this;
    }

    public boolean isUnmarked() {
        return unmarked;
    }

    public SearchParams withUnmarked() {
        unmarked = true;
        return this;
    }

    public boolean isActiveDocumentFilter() {
        return activeDocumentFilter;
    }

    public SearchParams withActiveDocumentFilter() {
        activeDocumentFilter = true;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SearchParams that = (SearchParams) o;
        return wholeWords == that.wholeWords && matchCase == that.matchCase && unmarked == that.unmarked && activeDocumentFilter == that.activeDocumentFilter && Objects.equals(text, that.text) && Objects.equals(activeSelection, that.activeSelection) && Objects.equals(activeDocument, that.activeDocument);
    }

    @Override
    public int hashCode() {
        return Objects.hash(text, activeSelection, activeDocument, wholeWords, matchCase, unmarked, activeDocumentFilter);
    }

    public String getDescription() {
        return Stream.of(
                wholeWords ? "words" : null,
                matchCase ? "case" : null,
                unmarked ? "unmarked" : null,
                activeDocumentFilter ? "active document" : null
        )
                .filter(Objects::nonNull)
                .reduce((s, s2) -> s + ", " + s2)
                .orElse("no filters");
    }

    @Override
    public String toString() {
        return "SearchParams{" +
                "text='" + text + '\'' +
                ", activeSelection=" + activeSelection +
                ", wholeWords=" + wholeWords +
                ", matchCase=" + matchCase +
                ", unmarked=" + unmarked +
                ", activeDocumentFilter=" + activeDocumentFilter +
                '}';
    }
}
