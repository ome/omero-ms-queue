package util.object;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static util.sequence.Arrayz.array;

import org.junit.Test;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;


@RunWith(Theories.class)
public class IdentifiableHashTest {

    static class H<T> implements Identifiable {

        final T id;

        H(T id) {
            this.id = id;
        }

        @Override
        public String id() {
            return id.toString();
        }
    }

    static <T> Map<Integer, List<H<T>>> buckets(int nBuckets, Stream<T> ids) {
        Map<Integer, List<H<T>>> m = new HashMap<>();
        ids.map(H::new).forEach(x -> {
            int hx = x.hashedId(nBuckets);
            if (!m.containsKey(hx)) {
                m.put(hx, new ArrayList<>());
            }
            m.get(hx).add(x);
        });
        return m;
    }

    public static void print(int nBuckets, int bucketSize) {
        buckets(nBuckets, range(0, nBuckets*bucketSize)).forEach((h, ids) -> {
            List<String> xs = ids.stream().map(Identifiable::id)
                    .collect(Collectors.toList());
            System.out.printf("%s\t%s\n", h, xs);
        });
    }

    static <T> Set<String> ids(List<H<T>> xs) {
        List<String> ys = xs.stream().map(Identifiable::id)
                .collect(Collectors.toList());
        return new HashSet<>(ys);
    }

    static Stream<Integer> range(int a, int b) {
        return IntStream.range(a, b).mapToObj(x -> x);
    }

    static <T> void assertConstantFunction(Map<Integer, List<H<T>>> buckets) {
        assertThat(buckets.keySet().size(), is(1));
    }

    static <T> void assertSameBucket(List<H<T>> xs, List<H<T>> ys) {
        assertThat(xs.size(), is(ys.size()));
        assertThat(ids(xs), is(ids(ys)));
    }

    static <T> void assertSameFunction(Map<Integer, List<H<T>>> f,
                                       Map<Integer, List<H<T>>> g) {
        assertThat(f.keySet(), is(g.keySet()));
        f.keySet().forEach(k -> {
            assertSameBucket(f.get(k), g.get(k));
        });
    }

    @DataPoints
    public static Integer[] supply = array(2, 3, 4, 5);

    @Theory
    public void negativeNumberOfBucketsIsSameAsPositive(Integer nBuckets) {
        assertSameFunction(
                buckets(nBuckets, range(-100, 100)),
                buckets(-nBuckets, range(-100, 100))
        );
    }

    @Theory
    public void spreadInputsEvenly(Integer bucketSize) {
        buckets(3, range(0, bucketSize*3))
                .forEach((h, ids) -> {
                    assertThat(ids.size(), is(bucketSize));
                });
    }

    @Test
    public void hashWith0BucketsIsConstantFunction() {
        assertConstantFunction(
                buckets(0, range(-100, 100))
        );
    }

    @Test
    public void hashWith1BucketIsConstantFunction() {
        assertConstantFunction(
                buckets(1, range(-100, 100))
        );
    }

}
