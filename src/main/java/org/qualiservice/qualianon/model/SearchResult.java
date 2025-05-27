package org.qualiservice.qualianon.model;

import org.qualiservice.qualianon.model.text.Coords;

import java.util.Objects;


public class SearchResult {

    private final String documentName;
    private final Coords coords;
    private final String lineContext;
    private final PositionRange range;
    private boolean selected;


    public SearchResult(String documentName, Coords coords, String lineContext, PositionRange range) {
        this.documentName = documentName;
        this.coords = coords;
        this.lineContext = lineContext;
        this.range = range;
    }

    public String getDocumentName() {
        return documentName;
    }

    public Coords getCoords() {
        return coords;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getLineContext() {
        return lineContext;
    }

    public PositionRange getRange() {
        return range;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SearchResult that = (SearchResult) o;
        return selected == that.selected && Objects.equals(documentName, that.documentName) && Objects.equals(coords, that.coords) && Objects.equals(lineContext, that.lineContext) && Objects.equals(range, that.range);
    }

    @Override
    public int hashCode() {
        return Objects.hash(documentName, coords, lineContext, range, selected);
    }

    @Override
    public String toString() {
        return "SearchResult{" +
                "documentName='" + documentName + '\'' +
                ", coords=" + coords +
                ", lineContext='" + lineContext + '\'' +
                ", range=" + range +
                ", selected=" + selected +
                '}';
    }
}
