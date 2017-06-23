package ome.smuggler.jclient.core.service.imports.impl;

import static java.util.Objects.requireNonNull;

import java.net.URI;

import ome.smuggler.jclient.core.config.ComponentsFactory;
import ome.smuggler.jclient.core.service.dto.imports.ImportResponse;
import ome.smuggler.jclient.core.service.http.RestResource;
import ome.smuggler.jclient.core.service.dto.imports.ImportRequest;
import ome.smuggler.jclient.core.service.imports.ImportService;


/**
 * Implements the {@link ImportService}.
 */
public class ImportServiceImpl implements ImportService {

    @Override
    public ImportResponse[] enqueue(URI target, ImportRequest...request) {
        requireNonNull(target, "target");
        requireNonNull(request, "request");

        RestResource<ImportRequest[]> client =
                ComponentsFactory.jsonResource(target);
        return client.post(request, ImportResponse[].class);
    }

}
