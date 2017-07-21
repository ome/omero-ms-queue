package ome.smuggler.core.service.file.impl;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import util.io.SinkWriter;
import util.io.SourceReader;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import ome.smuggler.core.service.file.KeyValueStore;
import ome.smuggler.core.service.file.TaskFileStore;
import ome.smuggler.core.types.BaseStringId;


public class KeyValueFileStoreTest {

    @Rule
    public final TemporaryFolder storeDir = new TemporaryFolder();

    private TaskFileStore<BaseStringId> store;
    private KeyValueStore<BaseStringId, Integer> target;


    private SourceReader<InputStream, Integer> reader() {
        return InputStream::read;
    }

    private SinkWriter<Integer, OutputStream> writer() {
        return OutputStream::write; //(out, v) -> out.write(v);
    }

    @Before
    public void setup() {
        Path p = Paths.get(storeDir.getRoot().getPath());
        store = new TaskIdPathStore<>(p, BaseStringId::new);
        target = new KeyValueFileStore<>(store, reader(), writer());
    }

    @Test
    public void putValue() throws Exception {
        BaseStringId key = new BaseStringId();
        int value = 123;
        target.put(key, value);

        Path storedValue = store.pathFor(key);
        int actualValue = Files.newInputStream(storedValue).read();
        assertThat(actualValue, is(value));
    }

    @Test
    public void modifyValue() throws Exception {
        BaseStringId key = new BaseStringId();
        int value = 123;
        target.put(key, value);
        target.modify(key, x -> x + 1);

        Path storedValue = store.pathFor(key);
        int actualValue = Files.newInputStream(storedValue).read();
        assertThat(actualValue, is(value + 1));
    }

    @Test (expected = IllegalArgumentException.class)
    public void modifyThrowsIfNoValueAssociatedToKey() {
        BaseStringId key = new BaseStringId();
        Path storedValue = store.pathFor(key);
        assertFalse(Files.exists(storedValue));

        target.modify(key, x -> x);
    }

    @Test
    public void getValue() throws Exception {
        BaseStringId key = new BaseStringId();
        int value = 123;
        target.put(key, value);

        int actualValue = target.get(key);
        assertThat(actualValue, is(value));
    }

    @Test (expected = IllegalArgumentException.class)
    public void getThrowsIfNoValueAssociatedToKey() {
        BaseStringId key = new BaseStringId();
        Path storedValue = store.pathFor(key);
        assertFalse(Files.exists(storedValue));

        target.get(key);
    }

    @Test
    public void removeValue() throws Exception {
        BaseStringId key = new BaseStringId();
        target.put(key, 123);
        target.remove(key);

        Path storedValue = store.pathFor(key);
        assertFalse(Files.exists(storedValue));
    }

    @Test
    public void removeDoesNothingIfNoValueAssociatedToKey() {
        BaseStringId key = new BaseStringId();
        Path storedValue = store.pathFor(key);
        assertFalse(Files.exists(storedValue));

        target.remove(key);
    }

    @Test (expected = NullPointerException.class)
    public void ctorThrowsIfNullStore() {
        new KeyValueFileStore<>(null, reader(), writer());
    }

    @Test (expected = NullPointerException.class)
    public void ctorThrowsIfNullReader() {
        new KeyValueFileStore<>(store, null, writer());
    }

    @Test (expected = NullPointerException.class)
    public void ctorThrowsIfNullWriter() {
        new KeyValueFileStore<>(store, reader(), null);
    }

    @Test (expected = NullPointerException.class)
    public void putThrowsIfNullKey() {
        target.put(null, 1);
    }

    @Test (expected = NullPointerException.class)
    public void putThrowsIfNullValue() {
        target.put(new BaseStringId(), null);
    }

    @Test (expected = NullPointerException.class)
    public void modifyThrowsIfNullKey() {
        target.modify(null, x -> x);
    }

    @Test (expected = NullPointerException.class)
    public void modifyThrowsIfNullOperation() {
        target.modify(new BaseStringId(), null);
    }

    @Test (expected = NullPointerException.class)
    public void removeThrowsIfNullKey() {
        target.remove(null);
    }

    @Test (expected = NullPointerException.class)
    public void getThrowsIfNullKey() {
        target.get(null);
    }

}
