package ome.smuggler.core.service.imports.impl;

import static java.util.Objects.requireNonNull;

import ome.smuggler.core.service.imports.ImportTracker;
import ome.smuggler.core.types.ImportId;
import ome.smuggler.core.types.ImportLogPath;


/**
 * Implementation of the {@link ImportTracker import tracking} service.
 */
public class ImportMonitor implements ImportTracker {

    private final ImportEnv env;
    
    /**
     * Creates a new instance.
     * @param env the import environment to use.
     * @throws NullPointerException if the argument is {@code null}.
     */
    public ImportMonitor(ImportEnv env) {
        requireNonNull(env, "env");
        
        this.env = env;
    }
    
    @Override
    public ImportLogPath importLogPathFor(ImportId taskId) {
        return env.importLogPathFor(taskId);
    }

}
