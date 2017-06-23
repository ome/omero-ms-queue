package ome.smuggler.core.service.imports;

import static java.util.Objects.requireNonNull;

import java.util.stream.Stream;

import ome.smuggler.core.types.ImportBatch;
import ome.smuggler.core.types.ImportId;
import ome.smuggler.core.types.ImportInput;
import ome.smuggler.core.types.QueuedImport;


/**
 * Triggers an OMERO import.
 */
public interface ImportRequestor {

    /**
     * Adds the import request to the queue and returns immediately. 
     * The request will subsequently be fetched from the import queue and 
     * serviced as needed.
     * What is added to the queue is an instance of {@link QueuedImport}.
     * @param request details what to import.
     * @return a token to use to get hold of this import run, for example to
     * get some feedback on progress.
     * @throws NullPointerException if the argument is {@code null}.
     */
    default ImportId enqueue(ImportInput request) {
        requireNonNull(request, "request");

        ImportBatch batch = enqueue(Stream.of(request));
        return batch.imports().findFirst().get().getTaskId();
    }

    /**
     * Adds the import requests to the queue and returns immediately.
     * The requests will subsequently be fetched from the import queue and
     * serviced as needed.
     * What is added to the queue is an instance of {@link QueuedImport} for
     * each import request.
     * @param requests detail what to import.
     * @return an object with all the {@link QueuedImport}s, each having its
     * own {@link ImportId} through which you can get hold of the corresponding
     * import run, for example to get some feedback on progress.
     * @throws NullPointerException if the stream or any of its elements is
     * {@code null}.
     * @throws IllegalArgumentException if the stream is empty.
     */
    ImportBatch enqueue(Stream<ImportInput> requests);
    
}
