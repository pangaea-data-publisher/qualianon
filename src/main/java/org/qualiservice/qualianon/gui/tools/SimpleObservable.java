package org.qualiservice.qualianon.gui.tools;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;

import java.util.LinkedList;
import java.util.List;


public class SimpleObservable implements Observable {

    private final List<InvalidationListener> listeners = new LinkedList<>();

    @Override
    public void addListener(InvalidationListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeListener(InvalidationListener listener) {
        listeners.remove(listener);
    }

    public void invalidate() {
        for (InvalidationListener listener : listeners) {
            try {
                listener.invalidated(this);
            } catch (RuntimeException ignored) {
            }
        }
    }

}
