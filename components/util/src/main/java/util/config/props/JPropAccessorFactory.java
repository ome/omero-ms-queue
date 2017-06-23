package util.config.props;

import static util.config.StringConfigReaderFactory.boolParser;
import static util.config.StringConfigReaderFactory.intParser;
import static util.config.StringConfigReaderFactory.uriParser;
import static util.config.StringConfigReaderFactory.enumParser;

import java.net.URI;
import java.util.Properties;
import java.util.function.Function;


/**
 * Factory methods to create typed property accessors for common cases.
 */
public class JPropAccessorFactory {
    
    /**
     * Convenience factory method to create an accessor with object to string
     * conversion done using String's {@link String#valueOf(Object) valueOf}
     * method.
     * @param <T> any type.
     * @param key the property key.
     * @param fromString converts the property raw string value in the Java 
     * {@link Properties} store to a {@code T}.
     * @return a new accessor.
     * @throws NullPointerException if any argument is {@code null}.
     */
    public static <T> JPropAccessor<T> make(JPropKey key, 
                                            Function<String, T> fromString) {
        return new JPropAccessor<>(key, fromString, String::valueOf);
    }
    
    /**
     * Convenience factory method to create a string accessor.
     * String values are read as is from the property store, whereas string 
     * values are written using String's {@link String#valueOf(Object) valueOf} 
     * method.
     * @param key the property key.
     * @return a new accessor.
     * @throws NullPointerException if any argument is {@code null}.
     */
    public static JPropAccessor<String> makeString(JPropKey key) {
        return make(key, Function.identity());
    }
    
    /**
     * Convenience factory method to create a boolean accessor.
     * String values are read from the property store using Boolean's {@link 
     * Boolean#valueOf(String) valueOf} method, whereas boolean values are
     * written using String's {@link String#valueOf(Object) valueOf} method.
     * @param key the property key.
     * @return a new accessor.
     * @throws NullPointerException if any argument is {@code null}.
     */
    public static JPropAccessor<Boolean> makeBool(JPropKey key) {
        return make(key, boolParser());
    }
    
    /**
     * Convenience factory method to create an integer accessor.
     * String values are read from the property store using Integer's {@link 
     * Integer#valueOf(String) valueOf} method, whereas integer values are
     * written using String's {@link String#valueOf(Object) valueOf} method.
     * @param key the property key.
     * @return a new accessor.
     * @throws NullPointerException if any argument is {@code null}.
     */
    public static JPropAccessor<Integer> makeInt(JPropKey key) {
        return make(key, intParser());
    }
    
    /**
     * Convenience factory method to create an URI accessor.
     * String values are read from the property store using URI's {@link 
     * URI#URI(String) constructor}, whereas URI values are written using URI's 
     * {@link URI#toASCIIString() toASCIIString} method.
     * @param key the property key.
     * @return a new accessor.
     * @throws NullPointerException if any argument is {@code null}.
     */
    public static JPropAccessor<URI> makeURI(JPropKey key) {
        return new JPropAccessor<>(key, uriParser(), URI::toASCIIString);
    }
    
    /**
     * Convenience factory method to create an enum accessor.
     * String values are read from the property store using the enum's {@link 
     * Enum#valueOf(Class, String) valueOf} method, whereas enum values are 
     * written using the enum's {@link Enum#name() name} method.
     * @param <T> enum type.
     * @param enumType the the enum type for which to create the accessor.
     * @param key the property key.
     * @return a new accessor.
     * @throws NullPointerException if any argument is {@code null}.
     */
    public static <T extends Enum<T>> JPropAccessor<T> makeEnum(
            Class<T> enumType, JPropKey key) { 
        return new JPropAccessor<>(key, enumParser(enumType), Enum::name);
    }
    
}
