package org.qualiservice.qualianon.gui.tools;

import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;


/**
 * Use this class for elements in ObservableList, that can be updated.
 */
public class UpdatableElement {

    protected final SimpleObservable observable = new SimpleObservable();

    public static <T extends UpdatableElement> ObservableList<T> observableArrayList() {
        return FXCollections.observableArrayList(e -> new Observable[]{e.observable});
    }

    public SimpleObservable getObservable() {
        return observable;
    }

    protected void update() {
        observable.invalidate();
    }

}
