package org.qualiservice.qualianon.model;

public enum SelectionMode {

    WORDS("Words"),
    CHARACTERS("Characters");

    private final String display;

    SelectionMode(String display) {
        this.display = display;
    }

    public String getDisplay() {
        return display;
    }
}
