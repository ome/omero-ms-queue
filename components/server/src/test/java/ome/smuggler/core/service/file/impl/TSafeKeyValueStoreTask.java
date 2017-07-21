package ome.smuggler.core.service.file.impl;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static java.util.Objects.requireNonNull;
import static util.error.Exceptions.runUnchecked;

import java.util.concurrent.atomic.AtomicInteger;

import util.types.PositiveN;


public class TSafeKeyValueStoreTask implements Runnable {

    private final int taskId;
    private final AtomicInteger sharedState;
    private final long sleepInterval;

    public TSafeKeyValueStoreTask(int taskId, AtomicInteger sharedState,
                                  PositiveN sleepInterval) {
        requireNonNull(sharedState);
        requireNonNull(sleepInterval);

        this.taskId = taskId;
        this.sharedState = sharedState;
        this.sleepInterval = sleepInterval.get();
    }

    private void modifySharedState(int value) throws InterruptedException {
        sharedState.set(value);
        Thread.sleep(sleepInterval);     // (*)
        int actual = sharedState.get();

        assertThat(actual, is(value));
    }
    /* (*) if calls are serialised, then no other thread will be able to
     * write its own value while we sleep.
     */

    @Override
    public void run() {
        runUnchecked(() -> modifySharedState(taskId));
    }

}
