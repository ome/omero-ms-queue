package ome.smuggler.core.service.file.impl;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static java.util.Objects.requireNonNull;

import ome.smuggler.core.types.BaseStringId;
import util.types.PositiveN;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;


public class TSafeKeyValueStoreRunner {

    private final PositiveN nThreads;
    private final PositiveN sleepInterval;
    private final TSafeKeyValueStore<BaseStringId, Integer> store;
    private final BaseStringId key;
    private final AtomicInteger sharedState;


    public TSafeKeyValueStoreRunner(PositiveN nThreads) {
        requireNonNull(nThreads);

        this.nThreads = nThreads;
        this.sleepInterval = PositiveN.of(500);
        this.key = new BaseStringId();
        this.sharedState = new AtomicInteger();
        this.store = new TSafeKeyValueStore<>(
                mock(TSafeKeyValueStoreTest.TestStore.class),
                PositiveN.of(nThreads.get() * 2));
    }

    private Runnable taskRunner(int taskId) {
        TSafeKeyValueStoreTask task =
                new TSafeKeyValueStoreTask(taskId, sharedState, sleepInterval);
        return () -> store.withLock(key, task);
    }

    private Thread[] newRunners() {
        return IntStream.range(1, nThreads.get().intValue() + 1)
                        .mapToObj(this::taskRunner)
                        .map(Thread::new)
                        .toArray(Thread[]::new);
    }

    private void waitForCompletion(Thread[] runners) {
        for (Thread t : runners) {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void runAndAssertNoInterference() {
        long startTime = System.currentTimeMillis();

        Thread[] ts = newRunners();
        for (Thread t : ts) {
            t.start();       // (1)
        }
        waitForCompletion(ts);

        long actualDuration = System.currentTimeMillis() - startTime;     // (2)
        long expectedMinimalDuration = nThreads.get() * sleepInterval.get();

        assertThat(actualDuration, greaterThan(expectedMinimalDuration));
    }
    /* NOTES.
     * 1. Each task will assert there's no interference, see modifySharedState
     * method.
     * 2. Each thread sleeps for sleepInterval millis. So if threads have been
     * serialised, then the elapsed time since the start should be at least:
     * nThreads * sleepInterval. Obviously the converse is not true, so ideally
     * we should get the execution interval of each thread and check those time
     * intervals don't overlap...
     */
}
