package ome.smuggler.core.types;

import util.types.UuidString;

/**
 * Identifies an import batch.
 */
public class ImportBatchId extends UuidString {

    public ImportBatchId(String uuid) {
        super(uuid);
    }

    public ImportBatchId() {
        super();
    }

}

