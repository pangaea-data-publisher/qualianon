package org.qualiservice.qualianon.utility;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;


public class UpdatableImplTest {

    private UpdateListener mock;
    private UpdatableImpl updatable;

    @Before
    public void setup() {
        mock = mock(UpdateListener.class);
        updatable = new UpdatableImpl();
        updatable.addUpdateListener(mock);
    }

    @After
    public void cleanup() {
        verifyNoMoreInteractions(mock);
    }

    @Test
    public void batchDirectTest() {
        updatable.beginBatch();
        updatable.notifyUpdateListeners(true);
        updatable.notifyUpdateListeners(true);
        updatable.endBatch();
        verify(mock).onUpdate(eq(true));
    }

    @Test
    public void batchIndirectTest() {
        updatable.beginBatch();
        updatable.notifyUpdateListeners(false);
        updatable.notifyUpdateListeners(false);
        updatable.endBatch();
        verify(mock).onUpdate(eq(false));
    }

    @Test
    public void batchDirectAndIndirectTest() {
        updatable.beginBatch();
        updatable.notifyUpdateListeners(true);
        updatable.notifyUpdateListeners(false);
        updatable.endBatch();
        verify(mock).onUpdate(eq(true));
    }

    @Test
    public void endBatchCleansUpTest() {
        // 1st batch sends update
        updatable.beginBatch();
        updatable.notifyUpdateListeners(true);
        updatable.notifyUpdateListeners(false);
        updatable.endBatch();

        // The 2nd batch will not send an update
        updatable.beginBatch();
        updatable.endBatch();

        verify(mock).onUpdate(eq(true));
    }

    @Test(expected = IllegalStateException.class)
    public void onlyOneConcurrentBatchTest() {
        updatable.beginBatch();
        updatable.beginBatch();
    }

}
