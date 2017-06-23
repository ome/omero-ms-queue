package ome.smuggler.core.types;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

import java.net.URI;
import java.util.*;
import java.util.stream.Stream;

import util.object.Pair;

/**
 * Groups multiple import runs into a single batch.
 * Currently only used for collapsing email notifications so that users get
 * one outcome notification per batch as opposed to one per import run.
 */
public class ImportBatch {

    private final ImportBatchId batchId;
    private final Set<QueuedImport> imports;


    /**
     * Creates a new batch consisting of the specified imports.
     * @param xs data detailing what each import must do.
     * @throws NullPointerException if the argument or any of its elements is
     * {@code null}.
     * @throws IllegalArgumentException if the argument has no elements.
     */
    public ImportBatch(Stream<ImportInput> xs) {
        requireNonNull(xs, "xs");

        batchId = new ImportBatchId();
        imports = xs.map(x -> new QueuedImport(new ImportId(batchId), x))
                    .collect(toSet());

        if (imports.size() == 0) {
            throw new IllegalArgumentException("no imports");
        }
    }

    /**
     * @return this batch's ID.
     */
    public ImportBatchId batchId() {
        return batchId;
    }

    /**
     * @return the imports in this batch.
     */
    public Stream<QueuedImport> imports() {
        return imports.stream();
    }

    /**
     * Associates each import target URI to the corresponding import ID.
     * @return the association list.
     */
    public Stream<Pair<ImportId, URI>> identifyTargets() {
        return imports().map(
                x -> new Pair<>(x.getTaskId(),
                                x.getRequest().getTarget()));
    }

    @Override
    public boolean equals(Object x) {
        if (this == x) {
            return true;
        }
        if (x instanceof ImportBatch) {
            ImportBatch other = (ImportBatch) x;
            return Arrays.equals(sortFields(), other.sortFields());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(sortFields());
    }

    private Object[] sortFields() {
        List<Object> xs = new ArrayList<>();

        xs.add(batchId);
        xs.addAll(imports().sorted(
                (x, y) -> {
                    String idx = x.getTaskId().get();
                    String idy = y.getTaskId().get();
                    return idx.compareTo(idy);
                })
                .collect(toList()));

        return xs.toArray();
    }

}
