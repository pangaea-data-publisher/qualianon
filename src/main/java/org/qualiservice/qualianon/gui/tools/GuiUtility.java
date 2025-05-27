package org.qualiservice.qualianon.gui.tools;

import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;


public class GuiUtility {

    public static void extend(Node node) {
        AnchorPane.setTopAnchor(node, 0.0);
        AnchorPane.setBottomAnchor(node, 0.0);
        AnchorPane.setLeftAnchor(node, 0.0);
        AnchorPane.setRightAnchor(node, 0.0);
    }

    public static void setOnDoubleClick(Node node, DoubleClickHandler doubleClickHandler) {
        node.setOnMouseClicked(mouseEvent -> {
            mouseEvent.consume();
            if (mouseEvent.getClickCount() != 2) return;
            doubleClickHandler.onDoubleClick();
        });
    }

    public interface DoubleClickHandler {
        void onDoubleClick();
    }

}
