package ome.smuggler.core.service.imports.impl;

import static java.util.Objects.requireNonNull;

import java.nio.file.Path;

import ome.smuggler.core.service.imports.FailedImportHandler;
import ome.smuggler.core.types.ImportId;
import ome.smuggler.core.types.QueuedImport;

/**
 * Handles an import that failed permanently, i.e. after all configured retries
 * failed.
 * All we can do with such an import task at this stage is copy the import log
 * to the failed directory so that the sys admin can have a look at what went
 * wrong. Note that even though the original import log will eventually be
 * deleted automatically, it's up to the sys admin to delete the copy we make
 * in the failed directory.
 */
public class ImportFailureHandler implements FailedImportHandler {

    private final ImportEnv env;
    
    public ImportFailureHandler(ImportEnv env) {
        requireNonNull(env, "env");
        this.env = env;
    }
    
    private void storeAsFailedImport(QueuedImport task) {
        ImportId taskId = task.getTaskId();
        Path importLog = env.importLogPathFor(taskId).get();
        
        env.failedImportLogStore().add(taskId, importLog);
    }
    
    @Override
    public void accept(QueuedImport task) {
        requireNonNull(task, "task");
        
        try {
            storeAsFailedImport(task);
        } finally {
            env.finaliser().onFailure(task);
        }
    }

}
