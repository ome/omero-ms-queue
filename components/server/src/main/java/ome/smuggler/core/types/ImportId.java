package ome.smuggler.core.types;

import static java.util.Objects.requireNonNull;
import static util.string.Strings.requireString;

import util.object.AbstractWrapper;
import util.object.Identifiable;


/**
 * Identifies an import run.
 */
public class ImportId
        extends AbstractWrapper<String> implements Identifiable {

    private static final String separator = "__";

    private final String compositeId;

    public ImportId(String compositeId) {
        requireString(compositeId);
        this.compositeId = compositeId;
    }
    
    public ImportId(ImportBatchId batchId) {
        requireNonNull(batchId, "batchId");
        this.compositeId = String.format("%s%s%s",
                batchId, separator, new BaseStringId());
    }

    public ImportBatchId batchId() {
        String batchId = compositeId.split(separator)[0];
        return new ImportBatchId(batchId);
    }

    @Override
    public String get() {
        return compositeId;
    }

    @Override
    public String id() {
        return get();
    }

}
