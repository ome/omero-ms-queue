package ome.smuggler.core.service.imports.impl;

import static java.util.Objects.requireNonNull;
import static kew.core.msg.RepeatAction.*;
import static util.error.Exceptions.runAndCatch;

import java.nio.file.Path;
import java.util.Optional;
import java.util.stream.Stream;

import kew.core.msg.RepeatAction;
import ome.smuggler.core.service.imports.ImportFinaliser;
import ome.smuggler.core.types.ImportBatch;
import ome.smuggler.core.types.ProcessedImport;
import ome.smuggler.core.types.QueuedImport;
import util.io.FileOps;
import util.lambda.ActionE;


/**
 * Finalisation task to run after {@link BatchCompletedHandler batch completion}
 * to get rid of any resources still associated with the batch, such as import
 * logs and the batch store file.
 */
public class BatchDisposalHandler implements ImportFinaliser {

    private final ImportEnv env;

    /**
     * Creates a new instance.
     * @param env the import environment.
     * @throws NullPointerException if the argument is {@code null}.
     */
    public BatchDisposalHandler(ImportEnv env) {
        requireNonNull(env, "env");
        this.env = env;
    }

    private ActionE deleteImportLog(QueuedImport task) {
        return () -> {
            Path p = env.importLogPathFor(task.getTaskId()).get();
            FileOps.delete(p);
        };
    }

    private ActionE deleteBatch(ImportBatch batch) {
        return () -> env.batchManager().deleteBatch(batch.batchId());
    }

    private ActionE[] disposalActions(ImportBatch batch) {
        Stream<ActionE> xs = batch.imports().map(this::deleteImportLog);
        Stream<ActionE> ys = Stream.of(deleteBatch(batch));

        return Stream.concat(xs, ys).toArray(ActionE[]::new);
    }

    private boolean noErrors(Optional<Throwable>[] maybeE) {
        return Stream.of(maybeE).filter(Optional::isPresent).count() == 0;
    }

    @Override
    public RepeatAction consume(ProcessedImport task) {
        requireNonNull(task, "task");
        try {
            ImportBatch batch = env.batchManager()
                                   .getBatchStatusOf(task)
                                   .batch();

            Optional<Throwable>[] maybeE = runAndCatch(disposalActions(batch));
            env.log().transientError(this, maybeE);

            return noErrors(maybeE) ? Stop : Repeat;
        } catch (Exception batchManagerE) {
            env.log().transientError(this, batchManagerE);
            return Repeat;
        }
    }

}
