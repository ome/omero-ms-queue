package ome.smuggler.core.types;

/**
 * Enumerates the phases an import batch goes through from the moment we start
 * running the imports contained in the batch.
 * @see ProcessedImport
 */
public enum ImportFinalisationPhase {

    /**
     * Some imports in the batch still need to be run or are being run.
     */
    BatchStillInProgress,

    /**
     * All the imports in the batch have been run.
     */
    BatchCompleted,

    /**
     * All the imports in the batch have been run and any resources associated
     * to those imports (e.g. import logs) as well as the batch itself can be
     * discarded.
     */
    BatchCanBeDiscarded
}
