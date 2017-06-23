package util.sequence;

import static org.junit.Assert.*;
import static util.sequence.Streams.map;

import java.util.function.BiFunction;
import java.util.stream.Stream;

import org.junit.Test;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import util.object.Pair;
import util.sequence.Arrayz;

@RunWith(Theories.class)
public class StreamsMapTest {
    
    @DataPoints
    public static Integer[][] list = StreamsCycleTest.list;
    
    @SuppressWarnings("unchecked")
    @Theory
    public void mapWithPairIsZip(Integer[] list) {
        Stream<Integer> ts = Stream.of(list);
        BiFunction<Integer, Integer, Pair<Integer, Integer>> pair =
                Pair::new;
        
        Pair<Integer, Integer>[] actual = map(pair, ts)
                                         .toArray(Arrayz::newPairs);
        Pair<Integer, Integer>[] expected = 
                Arrayz.op(sz -> (Pair<Integer, Integer>[]) new Pair[sz])
                      .map(pair, list);
        
        assertArrayEquals(expected, actual);
    }
    
    @Test(expected = NullPointerException.class)
    public void throwIfNullMapper() {
        map(null, Stream.of(1, 2, 3));
    }
    
    @Test(expected = NullPointerException.class)
    public void throwIfNullStream() {
        map(Pair::new, null);
    }
    
}
