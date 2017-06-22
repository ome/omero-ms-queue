package ome.smuggler.jclient.frontend;

import static java.util.Objects.requireNonNull;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Factory class to build URI's for the proxy's Web service methods.
 */
public class ProxyUrl {

    private static final String ImportServicePath = "/ome/import";
    private static final String SessionServiceCreatePath = "/omero/session/create";


    private final URI baseUrl;

    /**
     * Creates a new instance.
     * @param baseUrl the host, port, and scheme part of the URL's where the
     *                proxy is deployed. The port is optional but scheme and
     *                host are mandatory.
     */
    public ProxyUrl(URI baseUrl) {
        requireNonNull(baseUrl, "baseUrl");
        this.baseUrl = baseUrl;
    }

    private URI buildFor(String path) {
        try {
            return new URI(baseUrl.getScheme(),
                           null,   // user info
                           baseUrl.getHost(),
                           baseUrl.getPort(),
                           path,
                           null,   // query
                           null);  // fragment
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @return the URL of the import Web service.
     */
    public URI importService() {
        return buildFor(ImportServicePath);
    }

    /**
     * @return the URL of the create session Web service.
     */
    public URI sessionServiceCreate() {
        return buildFor(SessionServiceCreatePath);
    }

}
