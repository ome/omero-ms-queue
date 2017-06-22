package util.sequence;

import static org.junit.Assert.*;
import static util.sequence.Arrayz.array;
import static util.sequence.Arrayz.op;
import static util.sequence.Streams.intersperse;

import java.util.function.Supplier;
import java.util.stream.Stream;

import org.junit.Test;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

@RunWith(Theories.class)
public class StreamsIntersperseTest {

    @DataPoints
    public static String[][] inputs = new String[][] {
        array(), array("a"), array("a", "b"), array("a", "b", "c")
    };
    
    private static final Supplier<String> separator = () -> "/";
    
    private static String[] arrayIntersperse(String[] xs) {
        return op(String[]::new).intersperse(separator, xs);
    }
    
    private static String[] streamIntersperse(String[] xs) {
        Stream<String> ys = Stream.of(xs); 
        return intersperse(separator, ys).toArray(String[]::new);
    }
    
    @Theory
    public void streamIntersperseSameAsArrayIntersperse(String[] xs) {
        String[] actual = streamIntersperse(xs);
        String[] expected = arrayIntersperse(xs);
        
        assertArrayEquals(expected, actual);
    }
    
    @Test(expected = NullPointerException.class)
    public void throwIfFirstArgNull() {
        intersperse(null, Stream.empty());
    }
    
    @Test(expected = NullPointerException.class)
    public void throwIfSecondArgNull() {
        intersperse(separator, null);
    }
    
}
