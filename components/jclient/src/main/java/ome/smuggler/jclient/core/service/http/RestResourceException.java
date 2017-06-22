package ome.smuggler.jclient.core.service.http;

/**
 * Wraps any error occurred while operating on a {@link RestResource} into a
 * a runtime exception.
 */
public class RestResourceException extends RuntimeException {

    /**
     * Wraps the given exception and copies its error message into this
     * exception's own message.
     * @param cause the cause of the error.
     */
    public RestResourceException(Exception cause) {
        super(cause.getMessage(), cause);
    }

}
