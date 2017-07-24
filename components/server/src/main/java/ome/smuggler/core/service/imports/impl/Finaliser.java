package ome.smuggler.core.service.imports.impl;

import static java.util.Objects.requireNonNull;
import static ome.smuggler.core.msg.ChannelMessage.message;
import static ome.smuggler.core.types.ProcessedImport.*;

import ome.smuggler.core.msg.RepeatAction;
import ome.smuggler.core.service.imports.ImportFinaliser;
import ome.smuggler.core.types.ImportFinalisationPhase;
import ome.smuggler.core.types.ProcessedImport;
import ome.smuggler.core.types.QueuedImport;
import util.types.FutureTimepoint;


/**
 * Carries out completion procedures after the {@link ImportRunner} or the
 * {@link ImportFailureHandler} have finished their work.
 * These include notifying the user of the import outcome and cleaning up
 * allocated resources, e.g. OMERO sessions, scheduling deletion of import
 * logs, etc.
 */
public class Finaliser implements ImportFinaliser {

    private final ImportEnv env;

    /**
     * Creates a new instance.
     * @param env the import environment.
     * @throws NullPointerException if the argument is {@code null}.
     */
    public Finaliser(ImportEnv env) {
        requireNonNull(env, "env");
        this.env = env;
    }

    private void enqueue(ProcessedImport task) {
        FutureTimepoint when = FutureTimepoint.now();
        env.gcQueue().uncheckedSend(message(when, task));
    }

    /**
     * Triggers the completion of an import task when the OMERO import has run
     * successfully.
     * @param task the import task.
     * @throws NullPointerException if the argument is {@code null}.
     */
    public void onSuccess(QueuedImport task) {
        requireNonNull(task, "task");

        env.log().importSuccessful(task);
        enqueue(succeeded(task));
    }

    /**
     * Triggers the completion of an import task when the OMERO import has
     * failed.
     * @param task the import task.
     * @throws NullPointerException if the argument is {@code null}.
     */
    public void onFailure(QueuedImport task) {
        requireNonNull(task, "task");

        env.log().importPermanentFailure(task);
        enqueue(failed(task));
    }

    /**
     * Triggers the procedure to finalise the import batch after all import
     * tasks within it have run.
     * @param task the last import task that was processed and completed the
     *             batch.
     * @throws NullPointerException if the argument is {@code null}.
     */
    public void onBatchCompletion(ProcessedImport task) {
        requireNonNull(task, "task");

        env.log().importBatchCompleted(task);
        enqueue(batchCompleted(task));
    }

    /**
     * Processes messages from the GC queue to carry out each step in the batch
     * finalisation procedure.
     * @param task which step to carry out.
     * @return {@link RepeatAction#Repeat Repeat} if the current step of the
     * finalisation procedure failed because of a transient error condition and
     * it should be retried or {@link RepeatAction#Stop Stop} if it should not
     * be retried either because it succeeded or because it failed but it's not
     * possible to recover.
     */
    @Override
    public RepeatAction consume(ProcessedImport task) {
        requireNonNull(task, "task");
        return handlerFor(task.status()).consume(task);
    }

    protected ImportFinaliser handlerFor(ImportFinalisationPhase status) {
        switch (status) {
            case BatchStillInProgress:                   // (1, 2, 3)
                return new BatchUpdateHandler(env);
                // calls updateBatchOf(processed import)
            case BatchCompleted:                         // (1, 3)
                return new BatchCompletedHandler(env);
                // cleans up sessions, emails users, sends BatchCanBeDiscarded
            case BatchCanBeDiscarded:
                return new BatchDisposalHandler(env);
                // deletes import logs and batch file
            default:                                     // (4)
                throw new UnsupportedOperationException(status.toString());
        }
    }
    /* NOTES
     * 1. Nesting of calls.
     * BatchManager.updateBatchOf calls onBatchCompletion which will put a
     * BatchCompleted message on the queue, asynchronously. So there's no
     * harm in nesting the calls, besides that done to my brain---feels like
     * ice-pick lobotomy. (I'll refactor if I get the time...)
     * 2. Locking.
     * Because onBatchCompletion returns immediately we can safely have it run
     * within the lambda (inside updateBatchOf) that does the update in the
     * key-value store. The lambda locks (indirectly) the batch, so we want it
     * to run quick.
     * 3. Resilience.
     * Because the queue is persistent, even if we crash here we can still
     * resume execution and hopefully complete the action. This is especially
     * important for BatchManager.updateBatchOf which needs to succeed
     * eventually otherwise we'll never get the BatchCompleted message.
     * 4. Whooooops!
     * This will never happen as long as we never add new statuses to the enum
     * or, if we do, then we never forget to associate an action to the status.
     */
}
