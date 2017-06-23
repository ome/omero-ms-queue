package util.sequence;

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

import util.sequence.Arrayz;

@RunWith(Theories.class)
public class StreamsPruneNullTest {

    @DataPoints
    public static Integer[][] list = new Integer[][] { 
        array(), array((Integer)null), array(1, null, 2), 
        array(1, null, null, 2, null, 3, null, null)
    };
    
    @Theory
    public void filterOutNulls(Integer[] xs) {
        Integer[] actual = pruneNull(Stream.of(xs)).toArray(Integer[]::new);
        Integer[] expected = Arrayz.op(Integer[]::new).pruneNull(xs);
        
        assertArrayEquals(expected, actual);
    }
    
    @Test
    public void returnEmptyWhenNullArg() {
        Stream<String> pruned = pruneNull((List<String>)null);
        assertThat(pruned.count(), is(0L));
    }
    
}
