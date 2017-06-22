package ome.smuggler.jclient.core.service.omero;

import ome.smuggler.jclient.core.service.dto.omero.CreateSessionRequest;
import ome.smuggler.jclient.core.service.dto.omero.CreateSessionResponse;

import java.net.URI;


/**
 * Provides access to OMERO sessions functionality.
 */
public interface SessionService {

    /**
     * Creates a new OMERO session.
     * @param target the URL of the Smuggler's session Web service where to post
     *               the request.
     * @param request data to create the session.
     * @return the ID of the newly created session.
     * @throws NullPointerException if the argument is {@code null}.
     * @throws RuntimeException if a error occurs while submitting the request.
     */
    CreateSessionResponse create(URI target, CreateSessionRequest request);

}
