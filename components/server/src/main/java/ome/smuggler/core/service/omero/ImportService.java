package ome.smuggler.core.service.omero;

import ome.smuggler.core.types.ImportInput;

import java.nio.file.Path;

/**
 * Provides access to OMERO import functionality.
 */
public interface ImportService {

    /**
     * Runs an import.
     * @param data details what to import and where.
     * @param importLog the file where to output the OMERO import log.
     * @return {@code true} if the command succeeded, {@code false} otherwise.
     */
    boolean run(ImportInput data, Path importLog);

}
