package org.qualiservice.qualianon.gui.tools.trees;

import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;


public interface ItemAdapter {

    Node getNode();

    default ContextMenu getContextMenu() {
        return null;
    }

    default MenuItem getEditMenuItem() {
        return null;
    }

    default TextField getEditableTextField() {
        return null;
    }

    default void commitEdit() {
    }

}
