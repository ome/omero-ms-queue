package ome.smuggler.core.service.imports.impl;

import static java.util.Objects.requireNonNull;

import java.util.stream.Stream;

import ome.smuggler.core.types.*;

/**
 * Manages the life-cycle of {@link ImportBatch}es.
 */
public class BatchManager {

    private final ImportEnv env;

    /**
     * Creates a new instance.
     * @param env the import environment.
     * @throws NullPointerException if the argument is {@code null}.
     */
    public BatchManager(ImportEnv env) {
        requireNonNull(env, "env");
        this.env = env;
    }

    /**
     * Creates a new batch containing the specified import requests.
     * @param requests the import requests to queue.
     * @return the new batch.
     * @throws NullPointerException if the stream or any of its elements is
     * {@code null}.
     * @throws IllegalArgumentException if the stream is empty.
     */
    protected ImportBatch createBatchFor(Stream<ImportInput> requests) {
        requireNonNull(requests, "requests");
        Stream<ImportInput> rs = requests.map(r -> {
            requireNonNull(r, "request");
            return r;
        });

        ImportBatch batch = new ImportBatch(rs);
        ImportBatchStatus unprocessed = new ImportBatchStatus(batch);

        env.batchStore().put(batch.batchId(), unprocessed);
        return unprocessed.batch();
    }

    /**
     * Retrieves the current state of the batch containing the specified task.
     * @param task the import task.
     * @return the corresponding batch state.
     * @throws NullPointerException if the argument is {@code null}.
     */
    protected ImportBatchStatus getBatchStatusOf(ProcessedImport task) {
        requireNonNull(task, "task");

        return env.batchStore().get(task.batchId());
    }

    /**
     * Removes the specified batch from permanent storage.
     * @param batchId identifies the batch.
     * @throws NullPointerException if the argument is {@code null}.
     */
    protected void deleteBatch(ImportBatchId batchId) {
        requireNonNull(batchId, "batchId");

        env.batchStore().remove(batchId);
    }

    /**
     * Updates the batch containing the specified import task to mark the
     * import as completed within the batch.
     * @param task the import task.
     * @throws NullPointerException if the argument is {@code null}.
     */
    protected void updateBatchOf(ProcessedImport task) {
        requireNonNull(task, "task");

        env.batchStore().modify(task.batchId(), status -> {
            status.addToCompleted(task);
            if (status.allProcessed()) {  // see note below.
                env.finaliser().onBatchCompletion(task);
            }
            return status;
        });
    }
    /* NOTE. Concurrency.
     * More than one thread may be running this update method, but the modify
     * method locks on the batch ID so the adding of a completed task and the
     * checking if, after adding, the batch is complete are serialised, i.e.
     * only one thread at a time will be running the lambda passed to modify.
     * This way we can make sure onBatchCompletion is only ever called once
     * per batch.
     */
}
