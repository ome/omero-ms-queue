package ome.smuggler.core.service.imports;

import java.util.function.Consumer;

import ome.smuggler.core.types.QueuedImport;

/**
 * Handles imports that were run by the {@link ImportProcessor} but did not
 * complete successfully.
 */
public interface FailedImportHandler extends Consumer<QueuedImport> {

}
