package util.sequence;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static util.sequence.Arrayz.array;
import static util.sequence.Streams.asList;
import static util.sequence.Streams.asStream;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.junit.Test;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import util.sequence.Arrayz;

@RunWith(Theories.class)
public class StreamsAsTest {

    @DataPoints
    public static Integer[][] list = new Integer[][] { 
        array(), array(1), array(1, 2), array(1, null, 3)
    };
    
    @Theory
    public void streamToList(Integer[] xs) {
        List<Integer> actual = asList(Stream.of(xs));
        assertNotNull(actual);
        
        Integer[] extracted = actual.toArray(new Integer[0]);
        assertArrayEquals(xs, extracted);
    }
    
    @Theory
    public void listToStream(Integer[] xs) {
        Stream<Integer> actual = asStream(Arrayz.asList(xs));
        assertNotNull(actual);
        
        Integer[] extracted = actual.toArray(Integer[]::new);
        assertArrayEquals(xs, extracted);
    }
    
    @Test
    public void streamToListConvertsNullToEmpty() {
        List<Boolean> actual = asList(null);
        
        assertNotNull(actual);
        assertThat(actual.size(), is(0));
    }
    
    @Test
    public void listToStreamConvertsNullToEmpty() {
        Stream<Boolean> actual = asStream((List<Boolean>)null);
        
        assertNotNull(actual);
        assertThat(actual.count(), is(0L));
    }
    
    @Test
    public void optionalToStreamConvertsNullToEmpty() {
        Stream<Boolean> actual = asStream((Optional<Boolean>)null);
        
        assertNotNull(actual);
        assertThat(actual.count(), is(0L));
    }
    
    @Test
    public void optionalToStreamConvertsEmptyToEmpty() {
        Stream<Boolean> actual = asStream(Optional.ofNullable(null));
        
        assertNotNull(actual);
        assertThat(actual.count(), is(0L));
    }
    
    @Test
    public void optionalToStreamPutsValueIntoStream() {
        Stream<Boolean> actual = asStream(Optional.of(true));
        
        assertNotNull(actual);
        Boolean[] extracted = actual.toArray(Boolean[]::new);
        
        assertThat(extracted.length, is(1));
        assertThat(extracted[0], is(true));
    }
    
}
