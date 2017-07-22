package ome.smuggler.core.types;

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

