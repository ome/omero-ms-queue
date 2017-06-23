package ome.smuggler.core.service.imports;

import ome.smuggler.core.types.ImportId;
import ome.smuggler.core.types.ImportLogPath;

/**
 * Tracking of OMERO imports.
 * The implementation tracks the import requests while it sits in the queue, 
 * waiting to be processed, so that its session can be kept alive until the
 * requested import is finally run. As the import runs, the implementation 
 * makes available the file where the import output is being written.
 */
public interface ImportTracker {

    /**
     * Determines the path to the import log for a given import.
     * @param taskId identifies the import run.
     * @return the path to the import log file.
     */
    ImportLogPath importLogPathFor(ImportId taskId);
     
}
