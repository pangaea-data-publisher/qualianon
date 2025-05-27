package org.qualiservice.qualianon.gui.tools.tabs;

import javafx.scene.layout.AnchorPane;


public class BaseTab extends AnchorPane {

    protected String title;

    public BaseTab(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void onClose() {
    }

}
