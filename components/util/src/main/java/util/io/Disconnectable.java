package util.io;

import static java.util.Objects.requireNonNull;
import static util.error.Exceptions.unchecked;

import java.util.Optional;

import util.error.Exceptions;

/**
 * Convenience interface to provide auto-closable functionality with exception
 * handling.
 */
public interface Disconnectable extends AutoCloseable {

    /**
     * Calls the given resource's {@link AutoCloseable#close() close} method
     * catching any raised exception.
     * @param resource the resource to close.
     * @return any exception raised while closing or empty if no exception
     * was raised.
     * @throws NullPointerException if the argument is {@code null}.
     */
    static Optional<Exception> disconnect(AutoCloseable resource) {
       requireNonNull(resource, "resource");
        return Exceptions.runAndCatchE(resource::close);
    }

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
