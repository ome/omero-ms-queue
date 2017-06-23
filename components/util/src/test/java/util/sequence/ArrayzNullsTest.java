package util.sequence;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import util.sequence.Arrayz;

public class ArrayzNullsTest {

    @Test
    public void isNullOrZero() {
        assertTrue(Arrayz.isNullOrZeroLength(null));
        assertTrue(Arrayz.isNullOrZeroLength(new String[0]));
        assertFalse(Arrayz.isNullOrZeroLength(Arrayz.array("")));
    }
    
    @Test
    public void asList() {
        assertThat(Arrayz.asList((String[])null).size(), is(0));
        assertThat(Arrayz.asList().size(), is(0));
        
        List<String> xs = Arrayz.asList("");
        assertThat(xs.size(), is(1));
        assertThat(xs.get(0), is(""));
    }
    
    @Test
    public void asStream() {
        assertThat(Arrayz.asStream((String[])null).count(), is(0L));
        assertThat(Arrayz.asStream().count(), is(0L));
        
        assertThat(Arrayz.asStream("").count(), is(1L));
        assertThat(Arrayz.asStream("").findFirst().get(), is(""));
    }
    
    @Test
    public void pruneNull() {
        Arrayz<String> op = Arrayz.op(String[]::new);
        
        assertThat(op.pruneNull(null).length, is(0));
        assertThat(op.pruneNull(new String[0]).length, is(0));
        
        String[] pruned = op.pruneNull(Arrayz.array(null, "", null));
        assertThat(pruned.length, is(1));
        assertThat(pruned[0], is(""));
    }
    
}
