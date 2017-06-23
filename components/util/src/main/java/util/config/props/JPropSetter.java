package util.config.props;

import java.util.Optional;
import java.util.Properties;

/**
 * Encapsulates the setting of a typed property to a Java {@link Properties} 
 * store.
 */
public interface JPropSetter<T> {

    /**
     * Sets the property value to be the string representation of the
     * given typed value.
     * @param db the {@link Properties} store.
     * @param value the value to set after converting it to its string
     * representation. The value must not be {@code null} or an exception
     * will be thrown; if you really need to set an empty value use the
     * {@link #setEmpty(Properties) setEmpty} method.
     * @throws NullPointerException if any argument is {@code null}.
     */
    void set(Properties db, T value);
    
    /**
     * Sets the property to empty.
     * @param db the {@link Properties} store.
     */
    void setEmpty(Properties db);
    
    /**
     * Removes the property from the store
     * @param db the {@link Properties} store.
     * @return the property string value if the property was found and deleted;
     * empty if the property was not found.
     */
    Optional<String> remove(Properties db);
    
}
/* NOTE. This is just an exploration of a design alternative to that of 
 * ConfigProvider and ConfigMapper. (See note in JPropGetter.)
 * Indeed, ConfigProvider and ConfigMapper don't provide the means to set
 * configuration values. 
 * Also note this design is less generic as it assumes the configuration 
 * data base is a Java Properties store.
 */