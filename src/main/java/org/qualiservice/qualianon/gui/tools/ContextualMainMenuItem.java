package org.qualiservice.qualianon.gui.tools;

import javafx.scene.control.MenuItem;


public class ContextualMainMenuItem {

    private final MenuItem menuItem;
    private Callback callback;

    public ContextualMainMenuItem(MenuItem menuItem) {
        this.menuItem = menuItem;
        menuItem.setDisable(true);
    }

    public void setContext(Callback callback) {
        this.callback = callback;
        menuItem.setDisable(false);
    }

    public void clearContext() {
        callback = null;
        menuItem.setDisable(true);
    }

    public void clearContext(Callback callback) {
        if(this.callback != callback) return;
        this.callback = null;
        menuItem.setDisable(true);
    }

    public void call() {
        callback.call();
    }

}
