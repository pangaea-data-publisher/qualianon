package org.qualiservice.qualianon.model;

public class Settings {

    private SelectionMode selectionMode;

    public Settings() {
        selectionMode = SelectionMode.WORDS;
    }

    public SelectionMode getSelectionMode() {
        return selectionMode;
    }

    public void setSelectionMode(SelectionMode selectionMode) {
        this.selectionMode = selectionMode;
    }

}
