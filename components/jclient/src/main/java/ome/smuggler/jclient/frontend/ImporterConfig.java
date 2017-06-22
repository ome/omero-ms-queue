package ome.smuggler.jclient.frontend;

import java.net.URI;

import static java.util.Objects.requireNonNull;

/**
 * Holds configuration values for the {@link Importer}.
 */
public class ImporterConfig {

    /**
     * Keep-alive duration, in milliseconds, to use for an import session.
     * Currently set to three days.
     */
    public static final Long ImportSessionKeepAliveDuration = 86400 * 3 * 1000L;


    private final URI sessionServiceCreateUrl;
    private final URI importServiceUrl;
    private final String username;
    private final String password;

    /**
     * Creates a new instance.
     * @param sessionServiceBaseUrl the base URL of the Smuggler server where to
     * post requests to create a session. It must have a scheme, host, and
     * optionally a port.
     * @param importServiceBaseUrl the base URL of the Smuggler server where to
     * post import requests. It must have a scheme, host, and optionally a port.
     * This URL may be different than the above when a separate Smuggler server
     * is deployed to create and keep OMERO sessions alive.
     * @param username the name of the OMERO user who wants to import the data.
     * @param password the password of the OMERO user who wants to import the
     * data.
     * @throws NullPointerException if any argument is {@code null}.
     */
    public ImporterConfig(URI sessionServiceBaseUrl,
                          URI importServiceBaseUrl,
                          String username,
                          String password) {
        requireNonNull(username, "username");
        requireNonNull(password, "password");

        this.sessionServiceCreateUrl = new ProxyUrl(sessionServiceBaseUrl)
                                      .sessionServiceCreate();
        this.importServiceUrl = new ProxyUrl(importServiceBaseUrl)
                               .importService();
        this.username = username;
        this.password = password;
    }

    public URI sessionServiceCreateUrl() {
        return sessionServiceCreateUrl;
    }

    public URI importServiceUrl() {
        return importServiceUrl;
    }

    public String username() {
        return username;
    }

    public String password() {
        return password;
    }

    public String importSessionKeepAliveDuration() {
        return ImportSessionKeepAliveDuration.toString();
    }

}
