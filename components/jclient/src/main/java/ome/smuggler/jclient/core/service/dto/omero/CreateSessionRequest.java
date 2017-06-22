package ome.smuggler.jclient.core.service.dto.omero;

/**
 * Web clients use instances of this class to open a new OMERO session.
 * This is just a data transfer object whose sole purpose is to facilitate the
 * transfer of information from the client to the server.
 */
public class CreateSessionRequest {

    /**
     * The hostname or IP address of the machine running the OMERO server.
     * This is a mandatory field.
     */
    public String omeroHost;

    /**
     * The port of the OMERO server. This is an optional field and must parse
     * to a non-negative integer if specified. Defaults to 4064.
     */
    public String omeroPort;

    /**
     * The username of the user for whom to open the session.
     * This is a mandatory field.
     */
    public String username;

    /**
     * The password of the user for whom to open the session.
     * This is a mandatory field.
     */
    public String password;

    /**
     * The desired keep-alive duration of the session, in milliseconds.
     * The new session is allowed to be inactive (i.e. no requests are made to
     * the OMERO server) for the specified duration, after which it will be
     * expired and no longer usable.
     * This is an optional field and must parse to a non-negative integer if
     * specified. Defaults to 600000 (=10 minutes).
     */
    public String keepAliveDuration;

}

