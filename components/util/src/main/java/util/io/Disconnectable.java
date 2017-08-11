package util.io;

import static util.error.Exceptions.unchecked;

/**
 * Convenience interface to provide auto-closable functionality for
 * queue consumers, producers, and sessions.
 */
public interface Disconnectable extends AutoCloseable {

    /**
     * Disconnects from the underlying entity, releasing any associated
     * resources. This method is the same as {@link #close() close} but
     * masks any exception as a runtime (unchecked) exception and throws
     * it as such without any wrapping.
     */
    default void disconnect() {
        unchecked(this::close).run();
    }

}
