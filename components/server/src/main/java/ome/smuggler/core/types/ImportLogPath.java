package ome.smuggler.core.types;

import java.nio.file.Path;

/**
 * A path to the log file of an import run.
 */
public class ImportLogPath extends TaskIdPath {
    
    /**
     * Creates a new import log path where to store the output of the specified
     * import run.
     * @param importLogDir the directory in which the import logs are kept.
     * @param taskId the id of the import run.
     * @throws NullPointerException if any argument is {@code null}.
     */
    public ImportLogPath(Path importLogDir, ImportId taskId) {
        super(importLogDir, taskId);
    }

}
