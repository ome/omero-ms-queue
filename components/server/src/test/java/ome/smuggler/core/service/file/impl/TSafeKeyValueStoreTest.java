package ome.smuggler.core.service.file.impl;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static util.sequence.Arrayz.array;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.function.Function;
import java.util.stream.Stream;
import org.junit.Test;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import ome.smuggler.core.service.file.KeyValueStore;
import util.types.UuidString;
import util.types.PositiveN;


@RunWith(Theories.class)
public class TSafeKeyValueStoreTest {

    interface TestStore extends KeyValueStore<UuidString, Integer> {}

    @DataPoints
    public static Integer[] stripeSupply = array(1, 2, 3);

    @DataPoints
    public static UuidString[] keySupply =
            Stream.generate(UuidString::new)
                  .limit(200)
                  .toArray(UuidString[]::new);

    @Theory
    public void sameKeysShareSameLock(UuidString key, Integer stripes) {
        TSafeKeyValueStore<UuidString, Integer> target =
                new TSafeKeyValueStore<>(mock(TestStore.class),
                                         PositiveN.of(stripes));
        UuidString sameKey = new UuidString(key.id());
        assertThat(target.lookupLock(key), is(target.lookupLock(sameKey)));
    }

    @Test
    public void differentKeysMayHaveDifferentLocks() {
        int stripes = 2;
        TSafeKeyValueStore<UuidString, Integer> target =
                new TSafeKeyValueStore<>(mock(TestStore.class),
                                         PositiveN.of(stripes));
        Set<Lock> lookedUpLocks = new HashSet<>();

        for (UuidString key : keySupply) {
            lookedUpLocks.add(target.lookupLock(key));
        }
        assertThat(lookedUpLocks.size(), is(stripes));
    }

    @Test
    public void forwardPutCalls() {
        TestStore target = mock(TestStore.class);
        TSafeKeyValueStore<UuidString, Integer> wrapper =
                new TSafeKeyValueStore<>(target, PositiveN.of(1));

        wrapper.put(keySupply[0], 1);
        verify(target).put(keySupply[0], 1);
    }

    @Test
    public void forwardModifyCalls() {
        TestStore target = mock(TestStore.class);
        TSafeKeyValueStore<UuidString, Integer> wrapper =
                new TSafeKeyValueStore<>(target, PositiveN.of(1));

        Function<Integer, Integer> id = x -> x;
        wrapper.modify(keySupply[0], id);
        verify(target).modify(keySupply[0], id);
    }

    @Test
    public void forwardRemoveCalls() {
        TestStore target = mock(TestStore.class);
        TSafeKeyValueStore<UuidString, Integer> wrapper =
                new TSafeKeyValueStore<>(target, PositiveN.of(1));

        wrapper.remove(keySupply[0]);
        verify(target).remove(keySupply[0]);
    }

    @Test (expected = NullPointerException.class)
    public void ctorThrowsIfNullTarget() {
        new TSafeKeyValueStore<>(null, PositiveN.of(1));
    }

    @Test (expected = NullPointerException.class)
    public void ctorThrowsIfNullStripes() {
        new TSafeKeyValueStore<>(mock(TestStore.class), null);
    }

}
