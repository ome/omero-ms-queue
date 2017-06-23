package ome.smuggler.core.service.file.impl;

import static java.util.Objects.requireNonNull;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import ome.smuggler.core.service.file.KeyValueStore;
import util.object.Identifiable;

/**
 * A {@link KeyValueStore} backed by an in-memory map; mainly useful for
 * testing.
 */
public class MemoryKeyValueStore<K extends Identifiable, V>
        implements KeyValueStore<K, V> {

    private final Map<K, V> store;

    /**
     * Creates a new instance.
     */
    public MemoryKeyValueStore() {
        store = new HashMap<>();
    }

    @Override
    public void put(K key, V value) {
        requireNonNull(key, "key");
        requireNonNull(value, "value");

        store.put(key, value);
    }

    @Override
    public void modify(K key, Function<V, V> operation) {
        requireNonNull(key, "key");
        requireNonNull(operation, "operation");
        if (!store.containsKey(key)) {
            throw new IllegalArgumentException("unknown key: " + key.id());
        }

        V oldValue = store.get(key);
        V newValue = operation.apply(oldValue);
        store.put(key, newValue);
    }

    @Override
    public void remove(K key) {
        requireNonNull(key, "key");

        store.remove(key);
    }

    /**
     * @return the underlying map where the data is stored.
     */
    public Map<K, V> store() {
        return store;
    }

}
