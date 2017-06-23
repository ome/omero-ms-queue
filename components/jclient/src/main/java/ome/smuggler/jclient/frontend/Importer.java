package ome.smuggler.jclient.frontend;

import static java.util.Objects.requireNonNull;

import ome.smuggler.jclient.core.config.ComponentsFactory;
import ome.smuggler.jclient.core.service.dto.imports.ImportRequest;
import ome.smuggler.jclient.core.service.dto.omero.CreateSessionRequest;
import ome.smuggler.jclient.core.service.imports.ImportService;
import ome.smuggler.jclient.core.service.omero.SessionService;


/**
 * Offloads OMERO imports to a Smuggler server.
 */
public class Importer {

    private final ImporterConfig config;

    /**
     * Creates a new instance to POST import requests to Smuggler's import Web
     * service.
     * @param config the configuration.
     * @throws NullPointerException if the argument is {@code null}.
     */
    public Importer(ImporterConfig config) {
        requireNonNull(config, "config");
        this.config = config;
    }

    private CreateSessionRequest buildCreateSessionRequest(ImportRequest r) {
        CreateSessionRequest request = new CreateSessionRequest();
        request.omeroHost = r.omeroHost;
        request.omeroPort = r.omeroPort;
        request.username = config.username();
        request.password = config.password();
        request.keepAliveDuration = config.importSessionKeepAliveDuration();

        return request;
    }

    private String requestNewSessionKey(ImportRequest r) {
        SessionService service = ComponentsFactory.omeroSession();
        return service.create(config.sessionServiceCreateUrl(),
                              buildCreateSessionRequest(r)).sessionKey;
    }

    private ImportRequest[] prepareBatch(ImportRequest...rs) {
        String newSessionKey = null;
        for (ImportRequest r : rs) {
            requireNonNull(r, "request");
            if (r.sessionKey == null || r.sessionKey.isEmpty()) {
                if (newSessionKey == null) {
                    newSessionKey = requestNewSessionKey(r);  // (*)
                }
                r.sessionKey = newSessionKey;
            }
        }
        return rs;
    }
    // (*) all requests are assumed to be for the same OMERO server.

    /**
     * Adds import requests to Smuggler's import queue and returns immediately.
     * Smuggler will then execute the import asynchronously.
     * @param rs each details an import to run. If some of the requests have no
     *           session key, this method will fill one in by creating a session
     *           that Smuggler will keep alive for a configured {@link
     *           ImporterConfig#ImportSessionKeepAliveDuration duration}. This
     *           session key will then be shared by all the requests that don't
     *           have one.
     * @throws NullPointerException if any argument is {@code null}.
     * @throws RuntimeException if a error occurs while submitting the requests.
     */
    public void handover(ImportRequest...rs) {
        ImportRequest[] batch = prepareBatch(rs);
        ImportService service = ComponentsFactory.importer();
        service.enqueue(config.importServiceUrl(), batch);
    }

}
