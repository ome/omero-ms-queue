package ome.smuggler.core.types;

import java.nio.file.Path;
import java.time.Duration;
import java.util.List;

import ome.smuggler.config.items.ImportConfig;
import util.runtime.CommandBuilder;

/**
 * Provides read-only, type-safe access to the import configuration.
 * @see ImportConfig
 */
public interface ImportConfigSource {

    /**
     * Default number of lock stripes to use with the import batch status
     * key-value store.
     * @see #batchStatusDbLockStripes()
     */
    PositiveN DefaultBatchStatusDbLockStripes = PositiveN.of(64);

    /**
     * @return path to the directory where to keep import logs.
     */
    Path importLogDir();
    
    /**
     * @return how long to keep import logs before deleting them.
     */
    Duration logRetentionPeriod();
    
    /**
     * @return intervals at which to retry failed imports.
     */
    List<Duration> retryIntervals();
    
    /**
     * @return path to the directory where to keep logs of failed imports.
     */
    Path failedImportLogDir();

    /**
     * Optional command to use to set the priority of processes that run OMERO
     * imports.
     * @return the configured command or empty if not found in the
     * configuration file.
     */
    CommandBuilder niceCommand();

    /**
     * @return path to the directory where to keep the files of the import
     * batch status key-value store.
     */
    Path batchStatusDbDir();

    /**
     * Optional number of lock stripes to use with the import batch status
     * key-value store.
     * @return the configured number or a default value if none is found in the
     * configuration file.
     */
    PositiveN batchStatusDbLockStripes();

}
