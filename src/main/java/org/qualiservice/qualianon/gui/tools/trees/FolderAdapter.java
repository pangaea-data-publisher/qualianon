package org.qualiservice.qualianon.gui.tools.trees;

public class FolderAdapter implements CheckboxAdapter {

    private final String name;

    public FolderAdapter(String name) {
        this.name = name;
    }

    @Override
    public void toggle() {
    }

    @Override
    public String toString() {
        return name;
    }

}
