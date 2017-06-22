package ome.smuggler.core.types;

import static java.util.Objects.requireNonNull;
import static ome.smuggler.core.types.ImportFinalisationPhase.*;

import java.util.Objects;

/**
 * Same as a {@link QueuedImport} but only used after the import task has
 * completed and needs to be garbage-collected.
 */
public class ProcessedImport {

    /**
     * Creates a new instance for an import that's completed successfully.
     * @param task the completed import.
     * @return the new instance.
     * @throws NullPointerException if the argument is {@code null}.
     */
    public static ProcessedImport succeeded(QueuedImport task) {
        return new ProcessedImport(task, true, BatchStillInProgress);
    }

    /**
     * Creates a new instance for an import that's completed with a failure.
     * @param task the completed import.
     * @return the new instance.
     * @throws NullPointerException if the argument is {@code null}.
     */
    public static ProcessedImport failed(QueuedImport task) {
        return new ProcessedImport(task, false, BatchStillInProgress);
    }

    /**
     * Creates a new instance to signal that the batch the specified import is
     * in has been completed.
     * @param task the completed import.
     * @return the new instance.
     * @throws NullPointerException if the argument is {@code null}.
     */
    public static ProcessedImport batchCompleted(ProcessedImport task) {
        return new ProcessedImport(task.queued(), task.succeeded(),
                                   BatchCompleted);
    }

    /**
     * Creates a new instance to signal that the batch the specified import is
     * in has been completed and can be discarded.
     * @param task the completed import.
     * @return the new instance.
     * @throws NullPointerException if the argument is {@code null}.
     */
    public static ProcessedImport batchCanBeDiscarded(ProcessedImport task) {
        return new ProcessedImport(task.queued(), task.succeeded(),
                                   BatchCanBeDiscarded);
    }


    private final QueuedImport task;
    private final boolean succeeded;
    private final ImportFinalisationPhase status;

    /**
     * Creates a new instance.
     * @param task the completed import.
     * @param succeeded {@code true} just in case the import completed
     *                  successfully.
     * @param status the status of the batch this import is in.
     * @throws NullPointerException if any argument is {@code null}.
     */
    private ProcessedImport(QueuedImport task, boolean succeeded,
                            ImportFinalisationPhase status) {
        requireNonNull(task, "task");
        requireNonNull(status, "status");

        this.task = task;
        this.succeeded = succeeded;
        this.status = status;
    }

    /**
     * @return the completed import task as it was originally queued.
     */
    public QueuedImport queued() {
        return task;
    }

    /**
     * @return {@code true} just in case the import completed successfully.
     */
    public boolean succeeded() {
        return succeeded;
    }

    /**
     * @return the status of the batch this import is in.
     */
    public ImportFinalisationPhase status() {
        return status;
    }

    /**
     * @return the ID of the batch this import belongs in.
     */
    public ImportBatchId batchId() {
        return task.getTaskId().batchId();
    }

    @Override
    public boolean equals(Object x) {
        if (this == x) {
            return true;
        }
        if (x instanceof ProcessedImport) {
            ProcessedImport other = (ProcessedImport) x;
            return Objects.equals(task, other.task)
                    && Objects.equals(succeeded, other.succeeded)
                    && Objects.equals(status, other.status);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(task, succeeded, status);
    }

}
/* NOTE. Spring beans.
 * We only need this class because we create a QChannelFactory<QueuedImport>
 * bean (in ImportQBeans) but we'd also need another instance of the same
 * bean (i.e. another QChannelFactory<QueuedImport>) in ImportGcBeans. Which
 * would confuse the hell out of Spring, so, in ImportGcBeans, we create a
 * QChannelFactory<ProcessedImport> instead.
 */
