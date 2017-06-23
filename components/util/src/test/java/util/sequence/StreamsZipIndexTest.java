package util.sequence;

import static org.junit.Assert.*;
import static util.sequence.Arrayz.array;
import static util.sequence.Streams.zipIndex;

import java.util.stream.Stream;

import org.junit.Test;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import util.object.Pair;
import util.sequence.Arrayz;

@RunWith(Theories.class)
public class StreamsZipIndexTest {
    
    @DataPoints
    public static Integer[][] list = StreamsCycleTest.list;
    
    @Theory
    public void pairedUpWithIndex(Integer[] xs) {
        Pair<Integer, Integer>[] expected = Arrayz.zipIndex(xs);
        Pair<Integer, Integer>[] actual = zipIndex(Stream.of(xs))
                                         .toArray(Arrayz::newPairs);
        
        assertArrayEquals(expected, actual);
    }
    
    @Test(expected = NullPointerException.class)
    public void throwIfNullArg() {
        zipIndex(null);
    }
    
    @Test
    public void returnedStreamNonTerminal() {
        Integer[] actual = zipIndex(Stream.of(1, 2))
                           .map(Pair::fst) // i.e. can use returned stream
                           .toArray(Integer[]::new);
        
        assertArrayEquals(array(0, 1), actual);
    }
    
}
