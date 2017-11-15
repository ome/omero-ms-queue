package ome.smuggler.core.service.imports;

import kew.core.msg.RepeatAction;
import kew.core.msg.RepeatConsumer;
import ome.smuggler.core.types.QueuedImport;

/**
 * Carries out an OMERO import.
 * It consumes an import request that was fetched from the import queue and
 * returns {@link RepeatAction#Repeat Repeat} if the import failed because of 
 * a transient error condition and it should be retried or {@link 
 * RepeatAction#Stop Stop} if the import should not be retried because it 
 * succeeded or it failed but it's not possible to recover.
 */
public interface ImportProcessor extends RepeatConsumer<QueuedImport> {

}
