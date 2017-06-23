package ome.smuggler.core.service.omero;

import java.net.URI;
import java.time.Duration;
import java.util.Optional;

/**
 * Provides access to OMERO sessions functionality.
 */
public interface SessionService {

    /**
     * Starts a new session for the specified user.
     * @param omeroHostAndPort detail the server to connect to.
     * @param username the login name of the user this session is for.
     * @param password the password of the user this session is for.
     * @return the session key of the newly created session or empty if the
     * session could not be created.
     * @throws NullPointerException if the URI argument is {@code null}.
     * @throws IllegalArgumentException if the username or password is
     * {@code null} or empty.
     */
    Optional<String> create(URI omeroHostAndPort,
                            String username, String password);

    /**
     * Issues a keep-alive command for the specified session.
     * @param omeroHostAndPort detail the server to connect to.
     * @param sessionKey the session ID.
     * @return {@code true} if the command succeeded, {@code false} otherwise.
     * @throws NullPointerException if any argument is {@code null}.
     */
    boolean keepAlive(URI omeroHostAndPort, String sessionKey);

    /**
     * Closes the specified session.
     * @param omeroHostAndPort detail the server to connect to.
     * @param sessionKey the session ID.
     * @return {@code true} if the command succeeded, {@code false} otherwise.
     * @throws NullPointerException if any argument is {@code null}.
     */
    boolean close(URI omeroHostAndPort, String sessionKey);

    /**
     * Starts a new session for the specified user and keeps it alive for a
     * given amount of time.
     * @param omeroHostAndPort detail the server to connect to.
     * @param username the login name of the user this session is for.
     * @param password the password of the user this session is for.
     * @param howLong how long from now to keep the session alive.
     * @return the session key of the newly created session or empty if the
     * session could not be created.
     * @throws NullPointerException if the URI or time argument is {@code null}.
     * @throws IllegalArgumentException if the username or password is
     * {@code null} or empty.
     */
    Optional<String> createAndKeepAlive(
            URI omeroHostAndPort, String username, String password,
            Duration howLong);

}
