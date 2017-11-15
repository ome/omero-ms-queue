package ome.smuggler.core.service.imports.impl;

import static java.util.Objects.requireNonNull;
import static kew.core.msg.ChannelMessage.message;
import static kew.core.msg.RepeatAction.*;
import static ome.smuggler.core.types.ProcessedImport.batchCanBeDiscarded;

import java.util.Optional;

import kew.core.msg.RepeatAction;
import ome.smuggler.core.service.imports.ImportFinaliser;
import ome.smuggler.core.types.*;
import util.types.FutureTimepoint;

/**
 * Finalisation task to run when an import batch has completed, i.e. when all
 * of its imports have completed.
 */
public class BatchCompletedHandler implements ImportFinaliser {

    private final ImportEnv env;

    /**
     * Creates a new instance.
     * @param env the import environment.
     * @throws NullPointerException if the argument is {@code null}.
     */
    public BatchCompletedHandler(ImportEnv env) {
        requireNonNull(env, "env");
        this.env = env;
    }

    private void scheduleDeletion(ProcessedImport task) {
        FutureTimepoint when = env.importLogRetentionFromNow();
        ProcessedImport disposal = batchCanBeDiscarded(task);
        env.gcQueue().uncheckedSend(message(when, disposal));
    }

    private void cleanupSessions(ImportBatch batch) {
        BatchSessionsReaper reaper = new BatchSessionsReaper(env.session());
        Optional<Throwable>[] maybeE = reaper.closeSessions(batch);
        env.log().transientError(this, maybeE);
    }

    private void notifyUsers(ImportBatchStatus state) {
        try {
            ImportOutcomeNotifier notifier =
                    new ImportOutcomeNotifier(env, state);
            notifier.notifyOutcome();
        } catch (Exception e) {
            env.log().transientError(this, e);
        }
    }

    @Override
    public RepeatAction consume(ProcessedImport task) {
        requireNonNull(task, "task");
        try {
            ImportBatchStatus state = env.batchManager()
                                         .getBatchStatusOf(task);  // may throw
            scheduleDeletion(task);          // may throw
            cleanupSessions(state.batch());  // never throws
            notifyUsers(state);              // never throws

            return Stop;
        } catch (Exception e) {
            env.log().transientError(this, e);
            return Repeat;
        }
    }
    /* NOTE. Retries.
     * We do our best to make sure we get to send a batch disposal message so
     * that we can eventually complete the finalisation procedure. But we don't
     * retry closing sessions or emailing the user. As for closing the session
     * it's very unlikely to fail on our side. If OMERO goes down any active
     * session will die anyway, but there are other circumstances when we should
     * actually retry---e.g. network outage. So we can improve on this.
     * As for emails, the outcome notifier only creates mail messages and puts
     * them on the mail queue. This is very unlikely to fail as Smuggler and
     * HornetQ run in the same process, so we can safely assume it'll go well.
     * However, note that once messages are on the mail queue, they'll be
     * retried if for some reason---e.g. network outage---the mail agent can't
     * send them on to the mail server.
     */
}
