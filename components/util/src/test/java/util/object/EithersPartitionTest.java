package util.object;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static java.util.stream.Collectors.toList;
import static util.object.Either.*;
import static util.object.Eithers.partitionEithers;
import static util.sequence.Arrayz.array;
import static util.sequence.Arrayz.asStream;
import static util.sequence.Streams.concat;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import org.junit.Test;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

@RunWith(Theories.class)
public class EithersPartitionTest {

    @DataPoints
    public static String[][] leftSupply = new String[][] {
        array((String) null), array(null, "", null), array("", null, "x")
    };

    @DataPoints
    public static Integer[][] rightSupply = new Integer[][] {
        array((Integer) null), array(null, 0, null), array(0, null, 1)
    };


    @Theory
    public void collectInEncounterOrder(String[] ls, Integer[] rs) {
        Stream<Either<String, Integer>> xs = concat(
                asStream(ls).map(Either::left),
                asStream(rs).map(Either::right));
        Pair<List<String>, List<Integer>> actual = partitionEithers(xs);

        assertThat(actual.fst(), contains(ls));
        assertThat(actual.snd(), contains(rs));
    }

    @Theory
    public void collectInEncounterOrder1(String[] ls, Integer[] rs) {
        Stream<Either<String, Integer>> xs = concat(
                asStream(rs).map(Either::right),
                asStream(ls).map(Either::left));
        Pair<List<String>, List<Integer>> actual = partitionEithers(xs);

        assertThat(actual.fst(), contains(ls));
        assertThat(actual.snd(), contains(rs));
    }

    @Theory
    public void leaveLeftNullsBe(String[] ls) {
        Stream<Either<String, Integer>> xs = asStream(ls).map(Either::left);
        List<String> actual = partitionEithers(xs).fst();

        assertThat(actual, contains(ls));
    }

    @Theory
    public void leaveRightNullsBe(Integer[] rs) {
        Stream<Either<String, Integer>> xs = asStream(rs).map(Either::right);
        List<Integer> actual = partitionEithers(xs).snd();

        assertThat(actual, contains(rs));
    }

    @Theory
    public void filterOutNulls(Integer[] rs) {
        Stream<Either<String, Integer>> xs =
                asStream(rs).map(r -> r == null ? null : right(r));
        List<Integer> expected = asStream(rs).filter(Objects::nonNull)
                                             .collect(toList());
        List<Integer> actual = partitionEithers(xs).snd();

        assertThat(actual, is(expected));
    }

    @Test(expected = NullPointerException.class)
    public void throwIfNullStream() {
        partitionEithers(null);
    }

}
