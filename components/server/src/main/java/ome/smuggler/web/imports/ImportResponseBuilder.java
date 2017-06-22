package ome.smuggler.web.imports;

import static java.util.Objects.requireNonNull;

import ome.smuggler.core.types.ImportBatch;
import ome.smuggler.core.types.ImportId;
import org.springframework.web.util.UriComponentsBuilder;
import util.object.Pair;

import java.net.URI;

/**
 * {@link #build(ImportBatch) Builds} an {@link ImportResponse} for each import
 * in a submitted batch.
 */
public class ImportResponseBuilder {

    private String buildStatusUpdateUri(ImportId submittedRequest) {
        return UriComponentsBuilder.newInstance()
                .path(ImportController.ImportUrl)
                .path("/")
                .path(submittedRequest.id())
                .toUriString();
    }

    private ImportResponse buildResponse(Pair<ImportId, URI> target) {
        ImportResponse response = new ImportResponse();
        response.statusUri = buildStatusUpdateUri(target.fst());
        response.targetUri = target.snd().toString();

        return response;
    }

    /**
     * Builds an {@link ImportResponse} for each import request found in the
     * given batch.
     * @param submitted the batch containing the import requests that were
     *                  submitted to the underlying import service.
     * @return the responses.
     * @throws NullPointerException if the argument is {@code null}.
     */
    public ImportResponse[] build(ImportBatch submitted) {
        requireNonNull(submitted, "submitted");
        return submitted.identifyTargets()
                        .map(this::buildResponse)
                        .toArray(ImportResponse[]::new);
    }

}
