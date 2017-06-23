package ome.smuggler.core.service.file;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

import util.object.Identifiable;


/**
 * A persistent key-value store that associates a value {@code V} to an
 * {@link Identifiable} key.
 */
public interface KeyValueStore<K extends Identifiable, V> {

    /**
     * Maps a key to a value.
     * If the specified key is already associated to a value, the old value
     * will be overwritten.
     * @param key identifies the mapping.
     * @param value the value to store.
     * @throws NullPointerException if any argument is {@code null}.
     * <p>The following checked exceptions are rethrown as unchecked (i.e.
     * the exception is masked as a runtime exception and thrown as is without
     * wrapping it in a {@code RuntimeException}):
     * <br>{@link IOException} if an I/O occurs.
     * </p>
     */
    void put(K key, V value);

    /**
     * Gets the value associated to the specified key.
     * @param key identifies the mapping.
     * @return the value.
     * @throws NullPointerException if the argument is {@code null}.
     * @throws IllegalArgumentException if no value is associated to the given
     * key.
     * <p>The following checked exceptions are rethrown as unchecked (i.e.
     * the exception is masked as a runtime exception and thrown as is without
     * wrapping it in a {@code RuntimeException}):
     * <br>{@link IOException} if an I/O occurs.
     * </p>
     */
    default V get(K key) {
        AtomicReference<V> holder = new AtomicReference<>();  // (*)
        modify(key, v -> {
            holder.set(v);
            return v;
        });
        return holder.get();
    }
    // (*) using it out of convenience as a value holder, nothing to do with
    // concurrency.

    /**
     * Updates the value associated to the specified key.
     * @param key identifies the mapping.
     * @param operation is given the current value, does something with it and
     *                  then returns the new value.
     * @throws NullPointerException if any argument is {@code null}.
     * @throws IllegalArgumentException if no value is associated to the given
     * key.
     * <p>The following checked exceptions are rethrown as unchecked (i.e.
     * the exception is masked as a runtime exception and thrown as is without
     * wrapping it in a {@code RuntimeException}):
     * <br>{@link IOException} if an I/O occurs.
     * </p>
     */
    void modify(K key, Function<V, V> operation);

    /**
     * Deletes the mapping identified by the specified key.
     * Does nothing if no value is currently associated to the key.
     * @param key identifies the mapping.
     * @throws NullPointerException if the argument is {@code null}.
     * <p>The following checked exceptions are rethrown as unchecked (i.e.
     * the exception is masked as a runtime exception and thrown as is without
     * wrapping it in a {@code RuntimeException}):
     * <br>{@link IOException} if an I/O occurs.
     * </p>
     */
    void remove(K key);

}
/* NOTE. There are better ways of doing this.
 * So ya, we could use a key-value DB (e.g. Redis) or JPA (e.g. Hibernate) or,
 * well you name it. And we should probably do that if things get hairier, e.g.
 * ensure ACID props across the board. But for now it seems overkill to deploy
 * heavy artillery to shoot down...a fly?
 */