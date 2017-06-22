package ome.smuggler.web.imports;

import static java.util.Objects.requireNonNull;

import java.util.List;

import ome.smuggler.core.service.imports.ImportRequestor;
import ome.smuggler.core.types.ImportBatch;
import ome.smuggler.core.types.ImportInput;
import ome.smuggler.web.Error;
import util.object.Either;

/**
 * Submits a batch of import requests to the import service.
 * This entails request validation, changing data format, and producing an
 * import response from the submitted batch returned by the import service.
 */
public class ImportBatchSubmitter {

    private final ImportRequestor service;

    /**
     * Creates a new instance.
     * @param service the underlying import service.
     * @throws NullPointerException if the argument is {@code null}.
     */
    public ImportBatchSubmitter(ImportRequestor service) {
        requireNonNull(service, "service");
        this.service = service;
    }

    private ImportResponse[] doSubmit(List<ImportInput> requests) {
        if (requests.isEmpty()) {
            return new ImportResponse[0];
        }
        ImportBatch submitted = service.enqueue(requests.stream());
        return new ImportResponseBuilder().build(submitted);
    }

    /**
     * Submits the given import requests as a batch to the underlying service.
     * If the given array is {@code null} or empty, or if it only contains
     * {@code null} elements, no batch will be submitted and an empty array
     * will be returned. Otherwise any {@code null} elements are filtered out,
     * while the remaining non-{@code null} requests are {@link
     * ImportRequestValidator validated}. If validation fails for some of them,
     * an {@link Error} will be returned that details the detected validation
     * errors for each request that didn't pass validation. On the other hand,
     * if all the non-{@code null} requests pass validation, they're submitted
     * as a batch to the underlying service and an import response is generated
     * in correspondence of each of them. Note that the responses may be
     * produced in a different sequence than the original requests.
     * @param batch the requests to submit.
     * @return either an import response for each successfully submitted request
     * or an error if validation failed.
     */
    public Either<Error, ImportResponse[]> submit(ImportRequest...batch) {
        return new ImportBatchBuilder().build(batch).map(this::doSubmit);
    }

}
