package kew.core.qchan.spi;

import java.util.Optional;

/**
 * Methods to set and query metadata the underlying messaging middleware
 * uses to deliver messages. Specifically, we expect the underlying
 * middleware has some kind of metadata facility where you can associate
 * a properties map to a message. Typically these props are stored as
 * metadata in the queue message itself.
 */
public interface HasProps {

    /**
     * Sets a key-value pair in the metadata.
     * @param key the key.
     * @param value the associated value.
     * @throws NullPointerException if any argument is {@code null}.
     */
    void putProp(String key, String value);

    /**
     * Sets a key-value pair in the metadata.
     * @param key the key.
     * @param value the associated value.
     * @throws NullPointerException if any argument is {@code null}.
     */
    void putProp(String key, long value);

    /**
     * Retrieves the value associated to the specified key.
     * @param key the lookup key.
     * @return the value associated to the specified key or empty if the key
     * is not bound to any value.
     * @throws NullPointerException if any argument is {@code null}.
     */
    Optional<String> lookupStringValue(String key);

    /**
     * Retrieves the value associated to the specified key.
     * @param key the lookup key.
     * @return the value associated to the specified key or empty if the key
     * is not bound to any value.
     * @throws NullPointerException if any argument is {@code null}.
     */
    Optional<Long> lookupLongValue(String key);


}
/* Sucks like a black hole, but works.
 * I wish I could've had this instead:
 *
 *     interface HasProp<V> {
 *         void put(String key, V value);
 *         Optional<V> lookup(String key);
 *
 * And then be able to say a type T extends HasProp<Long>, HasProp<String>,
 * and so on. But type erasure makes it impossible to implement an I/F with
 * different type params cos I<X> and I<Y> become just I after nuking their
 * respective type params!
 */