package util.sequence;

import static org.junit.Assert.*;
import static util.sequence.Arrayz.array;

import java.util.stream.Stream;

import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import util.alg.Monoids;

@RunWith(Theories.class)
public class StreamMonoidTest {

    @DataPoints
    public static final Integer[][] values = new Integer[][] {
            array(), array(1), array(1, 2), array(1, 2, 3),
            array(1, 2, 3, 4), array(1, 2, 3, 4, 5)
    };
    
    @Theory
    public void multiplyIsJoin(Integer[] xs, Integer[] ys) {
        StreamMonoid<Integer> monoid = new StreamMonoid<>();
        Integer[] actual = monoid.multiply(Stream.of(xs), Stream.of(ys))
                                 .toArray(Integer[]::new);
        
        Integer[] expected = new Integer[xs.length + ys.length];
        System.arraycopy(xs, 0, expected, 0, xs.length);
        System.arraycopy(ys, 0, expected, xs.length, ys.length);
        
        assertArrayEquals(expected, actual);
    }
    
    @Theory
    public void foldStreamOfStreamIsFlatJoin(Integer[] xs, Integer[] ys) {
        StreamMonoid<Integer> monoid = new StreamMonoid<>();
        Monoids<Stream<Integer>> op = new Monoids<>(monoid);
        
        Integer[] actual = op.fold(Stream.of(xs).map(Stream::of))
                             .toArray(Integer[]::new);
        assertArrayEquals(xs, actual);
        
        actual = op.fold(Stream.of(xs, ys).map(Stream::of))
                   .toArray(Integer[]::new);
        Integer[] expected = Stream.of(xs, ys)
                            .map(Stream::of)
                            .flatMap(s -> s)
                            .toArray(Integer[]::new);
        assertArrayEquals(expected, actual);
    }
    
}
