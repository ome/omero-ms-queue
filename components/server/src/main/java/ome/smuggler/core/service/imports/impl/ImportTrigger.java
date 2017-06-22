package ome.smuggler.core.service.imports.impl;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.util.stream.Stream;

import ome.smuggler.core.service.imports.ImportRequestor;
import ome.smuggler.core.types.*;

/**
 * Implements the {@link ImportRequestor}.
 */
public class ImportTrigger implements ImportRequestor {

    private final ImportEnv env;
    
    public ImportTrigger(ImportEnv env) {
        requireNonNull(env, "env");
        this.env = env;
    }
    
    private void notifyQueued(QueuedImport task) throws IOException {
        ImportOutput out = new ImportOutput(
                env.importLogPathFor(task.getTaskId()), task);
        out.writeQueued();
    }

    private void addToQueue(QueuedImport task) {
        try {
            notifyQueued(task);
            env.queue().uncheckedSend(task);         // (1)

            env.log().importQueued(task);            // (2)
        } catch (Exception e) {
            env.log().transientError(this, e);       // (2)

            env.finaliser().onFailure(task);         // (3)
        }
    }
    /* NOTES.
     * 1. What if we get an exception here? It's probably never going to happen
     * as HornetQ and Smuggler run in the same process and all this method does
     * is put the message on the queue, asynchronously. But if something does
     * go wrong, we still notify the finaliser, so that import is added to the
     * failed set.
     * 2. Our log methods are guaranteed to never throw, see implementation.
     * 3. What if we get an exception here? The method just puts the message
     * on the queue, so, as in (1), it's very unlikely it'll ever fail. But
     * if it does, the batch will never transition to a completed state so
     * import outcome notification emails won't go out, the batch file will
     * never be deleted, etc. Given that this is quite unlikely, like very, I
     * don't think we should bother? Even if once in a blue moon we don't delete
     * some files and don't send the notification email, well I don't think it's
     * the end of the world---after all the user who requested the import will
     * eventually check herself if her files are in OMERO anyway...
     */

    @Override
    public ImportBatch enqueue(Stream<ImportInput> requests) {
        ImportBatch unprocessed = env.batchManager().createBatchFor(requests);
        unprocessed.imports().forEach(this::addToQueue);

        return unprocessed;
    }

}
