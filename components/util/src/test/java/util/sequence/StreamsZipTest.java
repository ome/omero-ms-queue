package util.sequence;

import static org.junit.Assert.*;
import static util.sequence.Arrayz.array;
import static util.sequence.Streams.zip;

import java.util.stream.Stream;

import org.junit.Test;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import util.object.Pair;
import util.sequence.Arrayz;

@RunWith(Theories.class)
public class StreamsZipTest {
    
    @DataPoints
    public static Integer[][] list = StreamsCycleTest.list;
    
    @Theory
    public void pairedUpToShortest(Integer[] xs, Integer[] ys) {
        Pair<Integer, Integer>[] expected = Arrayz.zip(xs, ys);
        Pair<Integer, Integer>[] actual = zip(Stream.of(xs), Stream.of(ys))
                                         .toArray(Arrayz::newPairs);
        
        assertArrayEquals(expected, actual);
    }
    
    @Test(expected = NullPointerException.class)
    public void throwIfFirstArgNull() {
        zip(null, Stream.of(1));
    }
    
    @Test(expected = NullPointerException.class)
    public void throwIfSecondArgNull() {
        zip(Stream.of(1), null);
    }
    
    @Test
    public void returnedStreamNonTerminal() {
        Integer[] actual = zip(Stream.of(1, 2), Stream.of(""))
                           .map(Pair::fst) // i.e. can use returned stream
                           .toArray(Integer[]::new);
        
        assertArrayEquals(array(1), actual);
    }
    
}
