package util.config;

import static util.error.Exceptions.unchecked;

import java.util.stream.Stream;

/**
 * Reads some configuration items of type {@code T}.
 */
public interface ConfigProvider<T> {

    /**
     * Reads items from configuration.
     * @return the items; if no items were found, the stream will be empty.
     * The returned stream shall never be {@code null}.
     * @throws Exception if an error occurred while reading from configuration.
     */
    Stream<T> readConfig() throws Exception;
    
    /**
     * Calls {@link ConfigProvider#readConfig() readConfig} converting any
     * checked exception into an unchecked one that will bubble up without
     * requiring a 'throws' clause on this method.
     * This is because configuration exceptions are typically non-recoverable
     * (i.e. we can't start the app if we have no sound config) and so it's
     * sort of pointless to have checked exceptions in this case.
     * @return whatever {@code readConfig} would return.
     */
    default Stream<T> defaultReadConfig() {
        return unchecked(this::readConfig).get();
    }
    
    /**
     * Calls {@link #defaultReadConfig() defaultReadConfig} to get the first
     * configuration item.
     * @return the first configuration item if the configuration stream is not
     * empty; an exception is thrown otherwise.
     */
    default T first() {
        return defaultReadConfig().findFirst().get();
    }
    
}
