package util.sequence;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static util.sequence.Arrayz.array;
import static util.sequence.Streams.pairUp;
import static util.sequence.Streams.unpair;

import java.util.Arrays;
import java.util.stream.Stream;

import org.junit.Test;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import util.object.Pair;


@RunWith(Theories.class)
public class StreamsPairUpTest {

    @DataPoints
    public static Integer[] sizes = array(0, 1, 2, 3, 4);

    @DataPoints
    public static Long[] items = array(null, 10L, 20L);

    private static Long[] generateInput(int sz, Long...xs) {
        return Stream.of(xs)
                     .limit(sz)
                     .toArray(Long[]::new);
    }

    private static Long[] backToArray(Stream<Pair<Long, Long>> ps, int sz) {
        return unpair(ps).limit(sz).toArray(Long[]::new);
    }

    @Theory
    public void pairUpThenFlattenIsIdentity(int sz,
                                            long x1, long x2, long x3, long x4) {
        Long[] xs = generateInput(sz, x1, x2, x3, x4);

        Stream<Pair<Long, Long>> actual = pairUp(Arrays.stream(xs));
        assertNotNull(actual);

        Long[] ys = backToArray(actual, sz);
        assertArrayEquals(xs, ys);
    }

    @Theory
    public void sameAsArrayzPairUp(int sz,
                                   long x1, long x2, long x3, long x4) {
        Long[] xs = generateInput(sz, x1, x2, x3, x4);

        Pair<Long, Long>[] expected = Arrayz.pairUp(xs);
        Pair<Long, Long>[] actual = pairUp(Arrays.stream(xs))
                                   .toArray(Arrayz::newPairs);

        assertArrayEquals(expected, actual);
    }

    @Test (expected = NullPointerException.class)
    public void throwIfNullArg() {
        pairUp(null);
    }

    @Test
    public void returnEmptyIfEmptyInput() {
        Stream<?> actual = pairUp(Stream.empty());

        assertNotNull(actual);
        assertThat(actual.count(), is(0L));
    }

    @Test
    public void returnOnePairIfOneInputItem() {
        String item = "";
        Pair<String, String>[] actual = pairUp(Stream.of(item))
                                       .toArray(Arrayz::newPairs);

        assertThat(actual.length, is(1));
        assertThat(actual[0].fst(), is(item));
        assertThat(actual[0].snd(), nullValue());
    }

    @Test
    public void returnOnePairIfTwoInputItems() {
        String item1 = "", item2 = "2";
        Pair<String, String>[] actual = pairUp(Stream.of(item1, item2))
                                       .toArray(Arrayz::newPairs);

        assertThat(actual.length, is(1));
        assertThat(actual[0].fst(), is(item1));
        assertThat(actual[0].snd(), is(item2));
    }

    @Test
    public void returnTwoPairsIfThreeInputItems() {
        String item1 = "", item2 = "2", item3 = "3";
        Pair<String, String>[] actual = pairUp(Stream.of(item1, item2, item3))
                                       .toArray(Arrayz::newPairs);

        assertThat(actual.length, is(2));
        assertThat(actual[0].fst(), is(item1));
        assertThat(actual[0].snd(), is(item2));
        assertThat(actual[1].fst(), is(item3));
        assertThat(actual[1].snd(), nullValue());
    }

}
