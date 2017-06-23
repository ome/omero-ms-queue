package util.sequence;

import static org.junit.Assert.*;
import static util.sequence.Arrayz.array;
import static util.sequence.Streams.cycle;

import java.util.stream.Stream;

import org.junit.Test;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import util.sequence.Arrayz;

@RunWith(Theories.class)
public class StreamsCycleTest {

    @DataPoints
    public static int[] times = new int[] { -1, 0, 1, 2, 3 };
    
    @DataPoints
    public static final Integer[][] list = new Integer[][] {
        array(), array(1), array(1, 2), array(1, 2, 3)
    };
    
    @Theory
    public void wholeListIsRepeated(int times, Integer[] list) {
        Stream<Integer> ts = Stream.of(list);
        Stream<Integer> repeated = cycle(times, ts);
        
        if (times <= 0) {
            assertEquals(0, repeated.count());
        }
        else {
            Integer[] actual = repeated.toArray(Integer[]::new);
            Integer[] expected = Arrayz.op(Integer[]::new).cycle(times, list);
            
            assertArrayEquals(expected, actual);
        }
    }
    
    @Test(expected = NullPointerException.class)
    public void throwIfNull() {
        cycle(1, null);
    }

    @Test
    public void returnedStreamNonTerminal() {
        Integer[] actual = cycle(1, Stream.of(1, 2))
                          .map(x -> x * 2) // i.e. can use returned stream
                          .toArray(Integer[]::new);
        Integer[] expected = array(2, 4);
        
        assertArrayEquals(expected, actual);
    }
    
}
