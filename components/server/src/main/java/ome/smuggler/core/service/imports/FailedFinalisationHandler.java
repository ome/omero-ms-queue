package ome.smuggler.core.service.imports;

import java.util.function.Consumer;

import ome.smuggler.core.types.ProcessedImport;

/**
 * Called when a finalisation task failed permanently, i.e. after being retried
 * for the configured number of times.
 */
public interface FailedFinalisationHandler extends Consumer<ProcessedImport> {

}
