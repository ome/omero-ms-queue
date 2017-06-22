package ome.smuggler.core.service.file.impl;

import static java.util.Objects.requireNonNull;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.function.Function;

import ome.smuggler.core.convert.SinkWriter;
import ome.smuggler.core.convert.SourceReader;
import ome.smuggler.core.io.ValueFilter;
import ome.smuggler.core.service.file.TaskFileStore;
import ome.smuggler.core.service.file.KeyValueStore;
import util.object.Identifiable;


/**
 * A {@link KeyValueStore} backed by a {@link TaskFileStore}.
 * Each value is stored in a file and the file is keyed by the corresponding
 * task ID. Values are (de-)serialised using provided readers/writers.
 */
public class KeyValueFileStore<K extends Identifiable, V>
    implements KeyValueStore<K, V> {

    private final TaskFileStore<K> store;
    private final SourceReader<InputStream, V> reader;
    private final SinkWriter<V, OutputStream> writer;

    /**
     * Creates a new instance.
     * @param store the file store to use to persist the key-value pairs.
     * @param reader deserialises the input stream.
     * @param writer serialises to the output stream.
     */
    public KeyValueFileStore(TaskFileStore<K> store,
                             SourceReader<InputStream, V> reader,
                             SinkWriter<V, OutputStream> writer) {
        requireNonNull(store, "store");
        requireNonNull(reader, "reader");
        requireNonNull(writer, "writer");

        this.store = store;
        this.reader = reader;
        this.writer = writer;
    }

    @Override
    public void put(K key, V value) {
        requireNonNull(key, "key");
        requireNonNull(value, "value");

        store.add(key, out -> writer.write(out, value));
    }

    @Override
    public void modify(K key, Function<V, V> operation) {
        requireNonNull(key, "key");
        requireNonNull(operation, "operation");

        store.replace(key, new ValueFilter<>(reader, writer, operation));
    }

    @Override
    public void remove(K key) {
        requireNonNull(key, "key");

        store.remove(key);
    }

}
