package ome.smuggler.core.types;

import static java.util.Objects.requireNonNull;
import static util.string.Strings.requireString;

import java.net.URI;
import java.util.Objects;

import util.types.FutureTimepoint;

/**
 * Represents an OMERO session keep-alive request that has been queued and is
 * waiting to be serviced.
 */
public class QueuedOmeroKeepAlive {

    private final URI omero;
    private final String sessionKey;
    private final FutureTimepoint untilWhen;


    /**
     * Creates a new instance.
     * @param omero the host and port of the OMERO server holding the session
     *              to keep alive.
     * @param sessionKey the ID of the OMERO session to keep alive.
     * @param untilWhen until what time to keep the session alive.
     * @throws NullPointerException if the OMERO or the future time point
     * argument is {@code null}.
     * @throws IllegalArgumentException if the session key argument is {@code
     * null} or empty.
     */
    public QueuedOmeroKeepAlive(URI omero, String sessionKey,
                                FutureTimepoint untilWhen) {
        requireNonNull(omero, "omero");
        requireString(sessionKey, "sessionKey");
        requireNonNull(untilWhen, "untilWhen");

        this.omero = omero;
        this.sessionKey = sessionKey;
        this.untilWhen = untilWhen;
    }

    /**
     * @return the host and port of the OMERO server holding the session to
     * keep alive.
     */
    public URI getOmero() {
        return omero;
    }

    /**
     * @return the ID of the OMERO session to keep alive.
     */
    public String getSessionKey() {
        return sessionKey;
    }

    /**
     * @return until what time to keep the session alive.
     */
    public FutureTimepoint getUntilWhen() {
        return untilWhen;
    }

    @Override
    public boolean equals(Object x) {
        if (this == x) {
            return true;
        }
        if (x instanceof QueuedOmeroKeepAlive) {
            QueuedOmeroKeepAlive other = (QueuedOmeroKeepAlive) x;
            return Objects.equals(omero, other.omero)
                && Objects.equals(sessionKey, other.sessionKey)
                && Objects.equals(untilWhen, other.untilWhen);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(omero, sessionKey, untilWhen);
    }

}
