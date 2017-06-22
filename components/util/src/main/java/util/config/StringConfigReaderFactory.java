package util.config;

import static java.util.Objects.requireNonNull;
import static util.error.Exceptions.unchecked;

import java.net.URI;
import java.util.function.Function;

import util.lambda.FunctionE;

/**
 * Factory methods to create configuration providers for common cases where
 * strings are read from an underlying configuration store and transformed
 * into objects.
 */
public class StringConfigReaderFactory {

    /**
     * @return the parser used to create boolean configuration readers.
     */
    public static Function<String, Boolean> boolParser() {
        return Boolean::valueOf;
    }
    
    /**
     * @return the parser used to create integer configuration readers.
     */
    public static Function<String, Integer> intParser() {
        return Integer::valueOf;
    }
    
    /**
     * @return the parser used to create URI configuration readers.
     */
    public static Function<String, URI> uriParser() {
        return unchecked((FunctionE<String, URI>) URI::new);
    }
    
    /**
     * Returns the parser used to create enum configuration readers.
     * @param <T> enum type.
     * @param enumType the enum type.
     * @return the parser.
     */
    public static 
    <T extends Enum<T>> Function<String, T> enumParser(Class<T> enumType) {
        requireNonNull(enumType);
        
        return s -> Enum.valueOf(enumType, s); 
    }
    
    /**
     * Creates a boolean configuration reader.
     * String values are read from the provided source configuration store 
     * using Boolean's {@link Boolean#valueOf(String) valueOf} method.
     * @param configSource reads string items from configuration.
     * @return a configuration provider to read boolean items.
     * @throws NullPointerException if any argument is {@code null}.
     */
    public static 
    ConfigProvider<Boolean> makeBool(ConfigProvider<String> configSource) {
        return ConfigReader.newReader(configSource, boolParser());
    }
    
    /**
     * Creates an integer configuration reader.
     * String values are read from the provided source configuration store 
     * using Integer's {@link Integer#valueOf(String) valueOf} method.
     * @param configSource reads string items from configuration.
     * @return a configuration provider to read integer items.
     * @throws NullPointerException if any argument is {@code null}.
     */
    public static 
    ConfigProvider<Integer> makeInt(ConfigProvider<String> configSource) {
        return ConfigReader.newReader(configSource, intParser());
    }
    
    /**
     * Creates a URI configuration reader.
     * String values are read from the provided source configuration store 
     * using URI's {@link Integer#valueOf(String) valueOf} method.
     * @param configSource reads string items from configuration.
     * @return a configuration provider to read integer items.
     * @throws NullPointerException if any argument is {@code null}.
     */
    public static 
    ConfigProvider<URI> makeURI(ConfigProvider<String> configSource) {
        return ConfigReader.newReader(configSource, uriParser());
    }

    /**
     * Creates an enum configuration reader.
     * String values are read from the provided source configuration store 
     * using the enum's {@link Enum#valueOf(Class, String) valueOf} method.
     * @param <T> enum type.
     * @param enumType the the enum type for which to create the reader.
     * @param configSource reads string items from configuration.
     * @return a configuration provider to read enum items.
     * @throws NullPointerException if any argument is {@code null}.
     */
    public static <T extends Enum<T>> ConfigProvider<T> makeEnum(
            Class<T> enumType, ConfigProvider<String> configSource) {
        return ConfigReader.newReader(configSource, enumParser(enumType));
    }
    
}
