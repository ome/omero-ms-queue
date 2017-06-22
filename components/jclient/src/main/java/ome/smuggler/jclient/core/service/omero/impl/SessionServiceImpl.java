package ome.smuggler.jclient.core.service.omero.impl;

import static java.util.Objects.requireNonNull;

import java.net.URI;

import ome.smuggler.jclient.core.config.ComponentsFactory;
import ome.smuggler.jclient.core.service.dto.omero.CreateSessionRequest;
import ome.smuggler.jclient.core.service.dto.omero.CreateSessionResponse;
import ome.smuggler.jclient.core.service.http.RestResource;
import ome.smuggler.jclient.core.service.omero.SessionService;


/**
 * Implements the {@link SessionService}.
 */
public class SessionServiceImpl implements SessionService {

    @Override
    public CreateSessionResponse create(URI target,
                                        CreateSessionRequest request) {
        requireNonNull(target, "target");
        requireNonNull(request, "request");

        RestResource<CreateSessionRequest> client =
                ComponentsFactory.jsonResource(target);
        return client.post(request, CreateSessionResponse.class);
    }

}
