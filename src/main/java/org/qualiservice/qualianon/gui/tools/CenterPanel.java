package org.qualiservice.qualianon.gui.tools;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;


public class CenterPanel extends AnchorPane {

    public CenterPanel(Node child) {
        final HBox hBox = new HBox(child);
        hBox.setAlignment(Pos.CENTER);
        getChildren().add(hBox);
        GuiUtility.extend(this);
        GuiUtility.extend(hBox);
    }

}
