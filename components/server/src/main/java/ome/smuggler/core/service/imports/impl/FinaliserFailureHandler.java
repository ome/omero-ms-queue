package ome.smuggler.core.service.imports.impl;

import static java.util.Objects.requireNonNull;

import ome.smuggler.core.service.imports.FailedFinalisationHandler;
import ome.smuggler.core.types.ProcessedImport;

/**
 * Implements the {@link FailedFinalisationHandler}.
 */
public class FinaliserFailureHandler implements FailedFinalisationHandler {

    private final ImportEnv env;

    /**
     * Creates a new instance.
     * @param env the import environment.
     * @throws NullPointerException if the argument is {@code null}.
     */
    public FinaliserFailureHandler(ImportEnv env) {
        requireNonNull(env, "env");
        this.env = env;
    }

    /**
     * Called when a finalisation task failed permanently, i.e. after being
     * retried for the configured number of times.
     * @param task the task that failed.
     * @throws NullPointerException if the argument is {@code null}.
     */
    @Override
    public void accept(ProcessedImport task) {
        requireNonNull(task, "task");

        switch (task.status()) {
            case BatchStillInProgress:
                env.log().importBatchUpdateFinalisationFailed(task);
                return;
            case BatchCompleted:
                env.log().importBatchCompletionFinalisationFailed(task);
                return;
            case BatchCanBeDiscarded:
                env.log().importBatchDisposalFinalisationFailed(task);
                return;
            default:
                throw new UnsupportedOperationException(
                        task.status().toString());
        }
    }
    /* NOTE.
     * May be called multiple times, possibly once per processed import
     * in the case of a permanent failure when BatchStillInProgress.
     */
}
