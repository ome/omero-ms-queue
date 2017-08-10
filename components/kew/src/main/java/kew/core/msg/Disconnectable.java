package kew.core.msg;

import static util.error.Exceptions.unchecked;

/**
 * Convenience interface to provide auto-closable functionality for
 * channel sources and sinks.
 */
public interface Disconnectable extends AutoCloseable {

    /**
     * Disconnects from the underlying channel, releasing any associated
     * resources. This method is the same as {@link #close() close} but
     * masks any exception as a runtime (unchecked) exception and throws
     * it as such without any wrapping.
     */
    default void disconnect() {
        unchecked(this::close).run();
    }

}
