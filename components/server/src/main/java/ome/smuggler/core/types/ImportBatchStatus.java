package ome.smuggler.core.types;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toSet;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;


/**
 * Tracks progress of an {@link ImportBatch}.
 */
public class ImportBatchStatus {

    private final ImportBatch batch;
    private final Set<ImportId> succeeded;
    private final Set<ImportId> failed;

    /**
     * Creates a new instance.
     * @param batch the batch to track.
     * @throws NullPointerException if the argument is {@code null}.
     */
    public ImportBatchStatus(ImportBatch batch) {
        requireNonNull(batch, "batch");

        this.batch = batch;
        this.succeeded = new HashSet<>();
        this.failed = new HashSet<>();
    }

    private ImportId checkImportIsInBatch(QueuedImport importInBatch) {
        ImportId id = importInBatch.getTaskId();
        boolean in = batch.imports()
                          .map(QueuedImport::getTaskId)
                          .anyMatch(x -> x.equals(id));
        if (!in) {
            throw new IllegalArgumentException("import not in batch: " + id);
        }
        return id;
    }

    private Set<QueuedImport> collect(Set<ImportId> qs) {
        return batch.imports()
                    .filter(q -> qs.contains(q.getTaskId()))
                    .collect(toSet());
    }

    /**
     * @return the batch we're tracking.
     */
    public ImportBatch batch() {
        return batch;
    }

    /**
     * Marks the specified import in the batch as completed successfully.
     * @param importInBatch the import.
     * @throws NullPointerException if the argument is {@code null}.
     * @throws IllegalArgumentException if the specified import is not that of
     * an import in the batch or it has already been
     * {@link #addToFailed(QueuedImport) marked as failed}.
     * @see #addToFailed(QueuedImport) addToFailed
     */
    private void addToSucceeded(QueuedImport importInBatch) {
        ImportId id = checkImportIsInBatch(importInBatch);
        if (failed.contains(id)) {
            throw new IllegalArgumentException(
                    "already marked as failed: " + id);
        }

        succeeded.add(id);
    }

    /**
     * Marks the specified import in the batch as terminated with a failure.
     * @param importInBatch the import.
     * @throws NullPointerException if the argument is {@code null}.
     * @throws IllegalArgumentException if the specified import is not that of
     * an import in the batch or it has already been
     * {@link #addToSucceeded(QueuedImport) marked as succeeded}.
     * @see #addToSucceeded(QueuedImport) addToSucceeded
     */
    private void addToFailed(QueuedImport importInBatch) {
        ImportId id = checkImportIsInBatch(importInBatch);
        if (succeeded.contains(id)) {
            throw new IllegalArgumentException(
                    "already marked as succeeded: " + id);
        }

        failed.add(id);
    }

    /**
     * Updates the batch status to reflect that the specified import has
     * completed either successfully or with a failure depending on the
     * value returned by {@link ProcessedImport#succeeded()}.
     * This method will check that all the following conditions are {@code
     * true}:
     * <ul>
     * <li>The queued task is that of an import in the batch.</li>
     * <li>If this method was previously called with an argument {@code t},
     * then {@code task.succeeded() == t.succeeded()}. In other words, you
     * can call this method multiple times for the same import task as long as
     * the specified import {@link ProcessedImport#succeeded() outcome} is
     * always the same.</li>
     * </ul>
     * If any of the above is {@code false}, then an exceptions is thrown and
     * the status is not updated.
     * @param task the import.
     * @throws NullPointerException if the argument is {@code null}.
     * @throws IllegalArgumentException if any of the above conditions are not
     * met.
     */
    public void addToCompleted(ProcessedImport task) {
        requireNonNull(task, "task");

        if (task.succeeded()) {
            addToSucceeded(task.queued());
        } else {
            addToFailed(task.queued());
        }
    }

    /**
     * Have all the imports in this batch been processed?
     * @return {@code true} for yes, {@code false} for no.
     */
    public boolean allProcessed() {
        return batch.imports().count() == (succeeded.size() + failed.size());
    }

    /**
     * Have all the imports in this batch been processed successfully?
     * @return {@code true} for yes, {@code false} for no.
     */
    public boolean allSucceeded() {
        return allProcessed() && failed.size() == 0;
    }

    /**
     * @return the imports in the batch that completed successfully.
     */
    public Set<QueuedImport> succeeded() {
        return collect(succeeded);
    }

    /**
     * @return the imports in the batch that terminated with a failure.
     */
    public Set<QueuedImport> failed() {
        return collect(failed);
    }

    @Override
    public boolean equals(Object x) {
        if (this == x) {
            return true;
        }
        if (x instanceof ImportBatchStatus) {
            ImportBatchStatus other = (ImportBatchStatus) x;
            return this.batch.equals(other.batch)
                && this.succeeded.equals(other.succeeded)
                && this.failed.equals(other.failed);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(batch, succeeded, failed);
    }

}
