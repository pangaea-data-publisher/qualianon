package org.qualiservice.qualianon.utility;

import java.util.LinkedList;
import java.util.List;


public class UpdatableImpl implements Updatable {

    protected final List<UpdateListener> listeners;
    private boolean isInBatch;
    private boolean batchTriggeredDirectUpdate;
    private boolean batchTriggeredIndirectUpdate;

    public UpdatableImpl() {
        listeners = new LinkedList<>();
    }

    @Override
    public void addUpdateListener(UpdateListener updateListener) {
        listeners.add(updateListener);
    }

    @Override
    public void removeUpdateListener(UpdateListener updateListener) {
        listeners.remove(updateListener);
    }

    @Override
    public void notifyUpdateListeners(boolean isDirect) {
        if (isInBatch) {
            if (isDirect) {
                batchTriggeredDirectUpdate = true;
            } else {
                batchTriggeredIndirectUpdate = true;
            }
        } else {
            callNotification(isDirect);
        }
    }

    @Override
    public void beginBatch() {
        if (isInBatch) throw new IllegalStateException("There is already an open batch.");
        isInBatch = true;
        batchTriggeredDirectUpdate = false;
        batchTriggeredIndirectUpdate = false;
    }

    @Override
    public void endBatch() {
        if (batchTriggeredDirectUpdate) {
            callNotification(true);
        } else if (batchTriggeredIndirectUpdate) {
            callNotification(false);
        }
        isInBatch = false;
    }

    private void callNotification(boolean isDirect) {
        // clone to save ConcurrentModificationExceptions
        final List<UpdateListener> clonedListeners = new LinkedList<>(listeners);
        clonedListeners.forEach(updateListener -> updateListener.onUpdate(isDirect));
    }

}
