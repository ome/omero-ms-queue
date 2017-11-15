package ome.smuggler.core.service.imports;

import kew.core.msg.RepeatAction;
import kew.core.msg.RepeatConsumer;
import ome.smuggler.core.types.ProcessedImport;

/**
 * Carries out completion procedures after the {@link ImportProcessor} or the
 * {@link FailedImportHandler} have finished their work.
 * These include notifying the user of the import outcome and cleaning up
 * allocated resources, e.g. scheduling deletion of import logs.
 * It consumes a processed import that was fetched from the import GC queue and
 * returns {@link RepeatAction#Repeat Repeat} if the finalisation procedure
 * failed because of a transient error condition and it should be retried or
 * {@link RepeatAction#Stop Stop} if finalisation should not be retried either
 * because it succeeded or because it failed but it's not possible to recover.
 */
public interface ImportFinaliser extends RepeatConsumer<ProcessedImport> {

}
