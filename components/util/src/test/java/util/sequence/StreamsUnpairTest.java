package util.sequence;

import static org.junit.Assert.*;
import static util.object.Pair.pair;
import static util.sequence.Arrayz.array;
import static util.sequence.Streams.pairUp;
import static util.sequence.Streams.unpair;

import java.util.stream.Stream;

import org.junit.Test;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import util.object.Pair;


@RunWith(Theories.class)
public class StreamsUnpairTest {

    @DataPoints
    public static Integer[] sizes = array(0, 1, 2, 3, 4);

    @DataPoints
    public static Pair<Long, Long>[] items = array(
            pair(null, null), pair(10L, null), pair(null, 20L), pair(30L, 40L));

    @SafeVarargs
    private static Pair<Long, Long>[] generateInput(int sz,
                                                    Pair<Long, Long>...ps) {
        return Stream.of(ps)
                     .limit(sz)
                     .toArray(Arrayz::newPairs);
    }

    @Theory
    public void unpairThenPairUpIsIdentity(int sz,
        Pair<Long, Long> p1, Pair<Long, Long> p2, Pair<Long, Long> p3,
                                           Pair<Long, Long> p4) {
        Pair<Long, Long>[] ps = generateInput(sz, p1, p2, p3, p4);

        Stream<Long> flattened = unpair(Stream.of(ps));
        assertNotNull(flattened);

        Pair<Long, Long>[] pairedBack = pairUp(flattened)
                                       .toArray(Arrayz::newPairs);
        assertArrayEquals(ps, pairedBack);
    }

    @Theory
    public void sameAsArrayzUnpair(int sz,
        Pair<Long, Long> p1, Pair<Long, Long> p2, Pair<Long, Long> p3,
                                   Pair<Long, Long> p4) {
        Pair<Long, Long>[] ps = generateInput(sz, p1, p2, p3, p4);

        Long[] xs = unpair(Stream.of(ps)).toArray(Long[]::new);
        Long[] ys = Arrayz.op(Long[]::new).unpair(ps);

        assertArrayEquals(xs, ys);
    }

    @Test (expected = NullPointerException.class)
    public void throwIfNullStream() {
        unpair(null);
    }

    @Test (expected = NullPointerException.class)
    public void throwIfStreamHasNulls() {
        unpair(Stream.of(pair(1, 2), null, pair(5, 6)));
    }

}
