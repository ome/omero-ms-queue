package util.config.props;

import java.util.Optional;
import java.util.Properties;

/**
 * Encapsulates the extraction of a typed property from a Java 
 * {@link Properties} store.
 */
public interface JPropGetter<T> {

    /**
     * Looks up a typed property value from the given store.
     * @param db the {@link Properties} store.
     * @return a {@code T} representation of the property value if the property
     * is found; empty otherwise. (Must never return a {@code null} {@link 
     * Optional} though.) 
     * @throws NullPointerException if the argument is {@code null}.
     * @throws RuntimeException if the property string value is found but an
     * error occurred while converting it to a {@code T} instance.
     */
    Optional<T> get(Properties db);
    
}
/* NOTE. This is just an exploration of a design alternative to that of 
 * ConfigProvider and ConfigMapper. Here we're putting together the two
 * operations of fetching the value and converting it. 
 * This design is less generic though as it assumes the configuration 
 * data base is a Java Properties store.
 */ 
