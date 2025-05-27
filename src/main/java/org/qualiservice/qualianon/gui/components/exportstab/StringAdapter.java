package org.qualiservice.qualianon.gui.components.exportstab;

import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import org.qualiservice.qualianon.gui.tools.trees.ItemAdapter;


class StringAdapter implements ItemAdapter {

    private final Label label;

    public StringAdapter(String text) {
        label = new Label(text);
    }

    @Override
    public Node getNode() {
        return label;
    }

}
