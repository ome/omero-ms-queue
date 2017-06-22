package util.sequence;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static util.sequence.Arrayz.asStream;
import static util.sequence.Arrayz.array;
import static util.sequence.Streams.concat;

import java.util.stream.Stream;

import org.junit.Test;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;


@RunWith(Theories.class)
public class StreamsConcatTest {

    @DataPoints
    public static final Integer[][] supply = new Integer[][] {
        array(), array(1), array(1, 2), array(1, 2, 3)
    };
    
    @Theory
    public void streamsConcatSameAsArrayzConcat(Integer[] xs, Integer[] ys, Integer[] zs) {
        Integer[] actual = concat(Stream.of(xs), Stream.of(ys), Stream.of(zs))
                          .toArray(Integer[]::new);
        Integer[] expected = Arrayz.op(Integer[]::new).concat(xs, ys, zs);
        
        assertArrayEquals(expected, actual);
    }
    
    @Test (expected = NullPointerException.class)
    public void throwIfNullArg() {
        concat((Stream<Object>[]) null);
    }
    
    @Test
    public void emptyIfNoArgs() {
        Object[] joined = concat().toArray();
        
        assertNotNull(joined);
        assertThat(joined.length, is(0));
    }
    
    @Test
    public void emptyIfAllArgsNull() {
        Object[] joined = concat(null, null).toArray();
        
        assertNotNull(joined);
        assertThat(joined.length, is(0));
    }
    
    @Test
    public void filterNullArgsOut() {
        Integer[] actual = concat(asStream(1, 2), null, asStream(3, 4))
                          .toArray(Integer[]::new);
        Integer[] expected = array(1, 2, 3, 4);
        
        assertArrayEquals(expected, actual);
    }
    
}
