package ome.smuggler.core.service.imports.impl;

import static java.util.Objects.requireNonNull;
import static kew.core.msg.RepeatAction.*;

import kew.core.msg.RepeatAction;
import ome.smuggler.core.service.imports.ImportProcessor;
import ome.smuggler.core.types.ImportLogPath;
import ome.smuggler.core.types.QueuedImport;

/**
 * Implements the {@link ImportProcessor}.
 */
public class ImportRunner implements ImportProcessor {

    private final ImportEnv env;
    private final ImportErrorNotifier err;

    /**
     * Creates a new instance.
     * @param env the import environment.
     */
    public ImportRunner(ImportEnv env) {
        requireNonNull(env, "env");
        this.env = env;
        this.err = new ImportErrorNotifier(env);
    }

    @Override
    public RepeatAction consume(QueuedImport task) {
        env.log().importStart(task);

        ImportLogPath importLog = env.importLogPathFor(task.getTaskId());
        ImportOutput output = new ImportOutput(importLog, task);

        RepeatAction action = Repeat;
        try {
            output.writeHeader();
            boolean succeeded = env.importer()
                                   .run(task.getRequest(), importLog.get());
            output.writeFooter(succeeded);

            if (succeeded) {
                env.finaliser().onSuccess(task);
                action = Stop;
            } else {
                err.notifyFailure(task);
            }
        } catch (Exception e) {
            output.writeFooter(e);
            env.log().transientError(this, e);
            err.notifyFailure(task, e);
        }
        return action;
    }

}
