package org.qualiservice.qualianon.utility;

public interface Updatable {

    void addUpdateListener(UpdateListener updateListener);

    void removeUpdateListener(UpdateListener updateListener);

    void notifyUpdateListeners(boolean isDirect);

    void beginBatch();

    void endBatch();

}
