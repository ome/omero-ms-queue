package util.config.props;

import static util.string.Strings.requireString;
import static util.string.Strings.requireStrings;
import util.object.Wrapper;


/**
 * Represents a property key in a Java properties store.
 */
public class JPropKey extends Wrapper<String> {

    /**
     * Joins the given components into a property key.
     * E.g. {@code key("a", "b") = "a.b"}.
     * @param components The elements to join.
     * @return the joined components.
     * @throws IllegalArgumentException if the argument is {@code null} or has
     * zero length or any of the components is {@code null} or empty. 
     */
    public static final JPropKey key(String...components) {
        requireStrings(components);
        String keyName = String.join(".", components);
        return new JPropKey(keyName);
    }
    
    /**
     * Creates a new instance to represent the specified key.
     * @param keyName the key name as it should be used in the Java properties
     * store, e.g. {@code akey}, {@code my.key}, etc.
     * @throws IllegalArgumentException if the argument is {@code null} or 
     * empty.
     */
    public JPropKey(String keyName) {
        super(keyName);
        requireString(keyName, "keyName");
    }

}
