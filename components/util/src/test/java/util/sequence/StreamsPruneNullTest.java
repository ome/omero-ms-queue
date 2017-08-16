package util.sequence;

import static java.util.stream.Collectors.toList;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static util.sequence.Arrayz.array;
import static util.sequence.Streams.pruneNull;

import java.util.List;
import java.util.stream.Stream;

import org.junit.Test;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;


@RunWith(Theories.class)
public class StreamsPruneNullTest {

    private static <T> void assertEmptyStream(Stream<T> ts) {
        assertNotNull(ts);
        assertThat(ts.count(), is(0L));
    }

    private static void assertFilterOutNulls(Integer[] xs,
                                             Stream<Integer> pruned) {
        Integer[] actual = pruned.toArray(Integer[]::new);
        Integer[] expected = Arrayz.op(Integer[]::new).pruneNull(xs);
        assertArrayEquals(expected, actual);
    }


    @DataPoints
    public static Integer[][] list = new Integer[][] { 
        array(), array((Integer)null), array(1, null, 2), 
        array(1, null, null, 2, null, 3, null, null)
    };
    
    @Theory
    public void filterNullsOutOfStream(Integer[] xs) {
        assertFilterOutNulls(xs, pruneNull(Stream.of(xs)));
    }

    @Theory
    public void filterNullsOutOfList(Integer[] xs) {
        assertFilterOutNulls(xs, pruneNull(
                                    Stream.of(xs).collect(toList())));
    }

    @Theory
    public void filterNullsOutOfArray(Integer[] xs) {
        assertFilterOutNulls(xs, pruneNull(xs));
    }

    @Test
    public void returnEmptyWhenNullStreamArg() {
        Stream<String> pruned = pruneNull((Stream<String>)null);
        assertEmptyStream(pruned);
    }

    @Test
    public void returnEmptyWhenNullListArg() {
        Stream<String> pruned = pruneNull((List<String>)null);
        assertEmptyStream(pruned);
    }

    @Test
    public void returnEmptyWhenNullArrayArg() {
        Stream<String> pruned = pruneNull((String[])null);
        assertEmptyStream(pruned);
    }

    @Test
    public void emptyIfNullReturnsEmptyStreamIfNullInput() {
        Stream<String> xs = Streams.emptyIfNull(null);
        assertEmptyStream(xs);
    }

    @Test
    public void emptyIfNullReturnsInputStreamIfNotNull() {
        Stream<Integer> original = Stream.of(1, 2);
        Stream<Integer> xs = Streams.emptyIfNull(original);

        assertNotNull(xs);
        assertTrue(original == xs);
    }

    @Test
    public void ctor() {
        new Streams();  // only to get 100% coverage.
    }

}
