package util.sequence;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static java.util.stream.Collectors.*;
import static util.sequence.Streams.inits;

import java.util.List;
import java.util.stream.Stream;

import org.junit.Test;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import util.sequence.Arrayz;

@RunWith(Theories.class)
public class StreamsInitsTest {

    @DataPoints
    public static Integer[][] list = StreamsCycleTest.list;

    @Theory
    public void sameResultAsRefImplementation(Integer[] ts) {
        List<Integer[]> expected = Arrayz.op(Integer[]::new).inits(ts);
        List<Integer[]> actual = inits(Stream.of(ts))
                                .map(s -> s.toArray(Integer[]::new))
                                .collect(toList());
        
        assertThat(actual.size(), is(expected.size()));
        for (int k = 0; k < expected.size(); ++k) {
            assertArrayEquals(expected.get(k), actual.get(k));
        }
    }
    
    @Test(expected = NullPointerException.class)
    public void throwIfNullArg() {
        inits(null);
    }
    
}
