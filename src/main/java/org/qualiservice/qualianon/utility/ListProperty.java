package org.qualiservice.qualianon.utility;

import java.util.Collection;
import java.util.LinkedList;


public class ListProperty<E extends Updatable> extends LinkedList<E> implements Updatable {

    private final UpdatableImpl updatable;
    private final UpdateListener updateListener = isDirect -> notifyUpdateListeners(false);

    public ListProperty() {
        updatable = new UpdatableImpl();
    }

    @Override
    public void addUpdateListener(UpdateListener updateListener) {
        updatable.addUpdateListener(updateListener);
    }

    @Override
    public void removeUpdateListener(UpdateListener updateListener) {
        updatable.removeUpdateListener(updateListener);
    }

    @Override
    public void notifyUpdateListeners(boolean isDirect) {
        updatable.notifyUpdateListeners(isDirect);
    }

    @Override
    public void beginBatch() {
        updatable.beginBatch();
    }

    @Override
    public void endBatch() {
        updatable.endBatch();
    }

    @Override
    public void clear() {
        forEach(element -> element.removeUpdateListener(updateListener));
        super.clear();
        notifyUpdateListeners(true);
    }

    @Override
    public boolean addAll(Collection<? extends E> collection) {
        final boolean result = super.addAll(collection);
        collection.forEach(e -> e.addUpdateListener(updateListener));
        notifyUpdateListeners(true);
        return result;
    }

    @Override
    public boolean add(E element) {
        final boolean result = super.add(element);
        element.addUpdateListener(updateListener);
        notifyUpdateListeners(true);
        return result;
    }

    @Override
    public void add(int index, E element) {
        super.add(index, element);
        element.addUpdateListener(updateListener);
        notifyUpdateListeners(true);
    }

    @Override
    public boolean remove(Object o) {
        final E element = (E) o;
        element.removeUpdateListener(updateListener);
        final boolean result = super.remove(element);
        notifyUpdateListeners(true);
        return result;
    }

}
