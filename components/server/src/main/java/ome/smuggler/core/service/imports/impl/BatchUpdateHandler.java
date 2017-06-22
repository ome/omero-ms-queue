package ome.smuggler.core.service.imports.impl;

import static java.util.Objects.requireNonNull;
import static ome.smuggler.core.msg.RepeatAction.*;

import ome.smuggler.core.msg.RepeatAction;
import ome.smuggler.core.service.imports.ImportFinaliser;
import ome.smuggler.core.types.ProcessedImport;

/**
 * Finalisation task to update an import batch to reflect one of its imports
 * has completed.
 */
public class BatchUpdateHandler implements ImportFinaliser {

    private final ImportEnv env;

    /**
     * Creates a new instance.
     * @param env the import environment.
     * @throws NullPointerException if the argument is {@code null}.
     */
    public BatchUpdateHandler(ImportEnv env) {
        requireNonNull(env, "env");
        this.env = env;
    }

    @Override
    public RepeatAction consume(ProcessedImport task) {
        requireNonNull(task, "task");
        try {
            env.batchManager().updateBatchOf(task);
            return Stop;
        } catch (Exception e) {
            env.log().transientError(this, e);
            return Repeat;
        }
    }
    /* NOTE.
     * What if we keep on getting exceptions here and go past the configured
     * number of reties?
     * All we do here is write to disk, so it's very unlikely we won't
     * eventually succeed unless there's a structural problem---e.g. the disk is
     * full, we have no write permission, etc. Anyhoo, if we fail permanently,
     * the batch will never transition to a completed state so import outcome
     * notification emails won't go out, the batch file will never be deleted,
     * etc. Given that this is quite unlikely, like very, I don't think we
     * should bother? Even if once in a blue moon we don't delete some files and
     * don't send the notification email, well I don't think it's the end of the
     * world---after all the user who requested the import will eventually check
     * herself if her files are in OMERO anyway...
     */
}
