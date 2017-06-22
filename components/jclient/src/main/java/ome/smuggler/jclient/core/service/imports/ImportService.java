package ome.smuggler.jclient.core.service.imports;

import java.net.URI;

import ome.smuggler.jclient.core.service.dto.imports.ImportRequest;
import ome.smuggler.jclient.core.service.dto.imports.ImportResponse;


/**
 * Offloads an OMERO import to an import proxy.
 */
public interface ImportService {

    /**
     * Adds import requests to Smuggler's import queue and returns immediately.
     * Smuggler will then execute the import asynchronously.
     * @param target the URL of the the Smuggler's import Web service where to
     *               post the requests.
     * @param request each details an import to run.
     * @return the service response.
     * @throws NullPointerException if any argument is {@code null}.
     * @throws RuntimeException if a error occurs while submitting the requests.
     */
    ImportResponse[] enqueue(URI target, ImportRequest...request);

}
