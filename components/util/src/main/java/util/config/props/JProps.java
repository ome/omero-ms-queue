package util.config.props;

import static java.util.Objects.requireNonNull;

import java.util.Optional;
import java.util.Properties;
import java.util.function.Consumer;
import java.util.stream.Stream;


/**
 * Provides typed access to a Java {@link Properties} store.
 */
public class JProps {

    private final Properties db;
    
    /**
     * Creates a new instance to access the given store.
     * @param db the Java {@link Properties} store.
     * @throws NullPointerException if the argument is {@code null}.
     */
    public JProps(Properties db) {
        requireNonNull(db, "db");
        this.db = db;
    }
    
    /**
     * Uses the given getter to look up a property in the underlying Java
     * {@link Properties} store.
     * @param <T> property type.
     * @param prop the property getter.
     * @return the property value if found; empty otherwise.
     * @throws NullPointerException if the argument is {@code null}.
     */
    public <T> Optional<T> get(JPropGetter<T> prop) {
        requireNonNull(prop, "prop");
        return prop.get(db);
    }
    
    /**
     * Uses the given setter to store a property in the underlying Java
     * {@link Properties} store.
     * @param <T> property type.
     * @param prop the property setter.
     * @param value the value to set.
     * @return itself to facilitate fluent API style.
     * @throws NullPointerException if any argument is {@code null}.
     */
    public <T> JProps set(JPropSetter<T> prop, T value) {
        requireNonNull(prop, "prop");
        prop.set(db, value);
        return this;
    }
    
    /**
     * Lets the specified consumer set a property.
     * @param prop the property setter.
     * @return itself to facilitate fluent API style.
     * @throws NullPointerException if any argument is {@code null}.
     */
    public JProps set(Consumer<Properties> prop) {
        requireNonNull(prop, "prop");
        prop.accept(db);
        return this;
    }
    
    /**
     * Calls {@link #set(JPropSetter, Object) set} to store the same value in
     * all listed properties.
     * @param <T> property type.
     * @param props the property setters.
     * @param value the value to set.
     * @return itself to facilitate fluent API style.
     * @throws NullPointerException if any argument is {@code null}.
     */
    public <T> JProps setAll(Stream<JPropSetter<T>> props, T value) {
        requireNonNull(props, "props");
        props.forEach(p -> set(p, value));
        return this;
    }
    
    /**
     * Uses the given setter to store an empty property in the underlying Java
     * {@link Properties} store.
     * @param prop the property setter.
     * @return itself to facilitate fluent API style.
     * @throws NullPointerException if the argument is {@code null}.
     */
    public JProps setEmpty(JPropSetter<?> prop) {
        requireNonNull(prop, "prop");
        prop.setEmpty(db);
        return this;
    }
    
    /**
     * Uses the given setter to remove a property from the underlying Java
     * {@link Properties} store.
     * @param prop the property setter.
     * @return itself to facilitate fluent API style.
     * @throws NullPointerException if the argument is {@code null}.
     */
    public JProps remove(JPropSetter<?> prop) {
        requireNonNull(prop, "prop");
        prop.remove(db);
        return this;
    }
    
    /**
     * Calls {@link #remove(JPropSetter) remove} to delete all the listed 
     * properties from the property store.
     * @param props the property setters.
     * @return itself to facilitate fluent API style.
     * @throws NullPointerException if any argument is {@code null}.
     */
    public JProps removeAll(Stream<JPropSetter<?>> props) {
        requireNonNull(props, "props");
        props.forEach(this::remove);
        return this;
    }
    
    /**
     * @return the underlying properties store.
     */
    public Properties getProps() {
        return db;
    }
    
}
