package ome.smuggler.core.service.file.impl;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import ome.smuggler.core.service.file.KeyValueStore;
import ome.smuggler.core.types.UuidString;
import org.junit.Before;
import org.junit.Test;


public class MemoryKeyValueStoreTest {

    private KeyValueStore<UuidString, Integer> target;

    @Before
    public void setup() {
        target = new MemoryKeyValueStore<>();
    }

    @Test
    public void putValue() throws Exception {
        UuidString key = new UuidString();
        int value = 123;
        target.put(key, value);

        int actualValue = target.get(key);
        assertThat(actualValue, is(value));
    }

    @Test
    public void modifyValue() throws Exception {
        UuidString key = new UuidString();
        int value = 123;
        target.put(key, value);
        target.modify(key, x -> x + 1);

        int actualValue = target.get(key);
        assertThat(actualValue, is(value + 1));
    }

    @Test (expected = IllegalArgumentException.class)
    public void modifyThrowsIfNoValueAssociatedToKey() {
        UuidString key = new UuidString();
        target.modify(key, x -> x);
    }

    @Test
    public void getValue() throws Exception {
        UuidString key = new UuidString();
        int value = 123;
        target.put(key, value);

        int actualValue = target.get(key);
        assertThat(actualValue, is(value));
    }

    @Test (expected = IllegalArgumentException.class)
    public void getThrowsIfNoValueAssociatedToKey() {
        UuidString key = new UuidString();
        target.get(key);
    }

    @Test (expected = IllegalArgumentException.class)
    public void removeValue() throws Exception {
        UuidString key = new UuidString();
        target.put(key, 123);
        target.remove(key);

        target.get(key);
        fail("didn't remove key");
    }

    @Test
    public void removeDoesNothingIfNoValueAssociatedToKey() {
        UuidString key = new UuidString();
        target.remove(key);
    }

    @Test (expected = NullPointerException.class)
    public void putThrowsIfNullKey() {
        target.put(null, 1);
    }

    @Test (expected = NullPointerException.class)
    public void putThrowsIfNullValue() {
        target.put(new UuidString(), null);
    }

    @Test (expected = NullPointerException.class)
    public void modifyThrowsIfNullKey() {
        target.modify(null, x -> x);
    }

    @Test (expected = NullPointerException.class)
    public void modifyThrowsIfNullOperation() {
        target.modify(new UuidString(), null);
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
