package util.sequence;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import org.junit.Test;


public class ArrayzNullsTest {

    @Test
    public void isNullOrZero() {
        assertTrue(Arrayz.isNullOrZeroLength(null));
        assertTrue(Arrayz.isNullOrZeroLength(new String[0]));
        assertFalse(Arrayz.isNullOrZeroLength(Arrayz.array("")));
    }

    @Test
    public void hasNulls() {
        assertFalse(Arrayz.hasNulls(null));
        assertFalse(Arrayz.hasNulls(new Integer[] {}));
        assertFalse(Arrayz.hasNulls(new Integer[] { 1 }));
        assertFalse(Arrayz.hasNulls(new Integer[] { 1, 2 }));
        assertTrue(Arrayz.hasNulls(new Integer[] { null }));
        assertTrue(Arrayz.hasNulls(new Integer[] { 1, null }));
        assertTrue(Arrayz.hasNulls(new Integer[] { 1, null, 3 }));
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

    @Test
    public void requireArrayOfMinLengthZero() {
        Arrayz.requireArrayOfMinLength(0, new Object[] {});
    }

    @Test
    public void requireArray() {
        Integer[] ts = new Integer[] { 1 };
        Arrayz.requireArray(ts);

        ts = new Integer[] { 1, 2 };
        Arrayz.requireArray(ts);

        ts = new Integer[] { 1, 2, 3 };
        Arrayz.requireArray(ts);
    }

    @Test (expected = IllegalArgumentException.class)
    public void requireArrayThrowsIfNullArray() {
        Arrayz.requireArray(null);
    }

    @Test (expected = IllegalArgumentException.class)
    public void requireArrayThrowsIfEmptyArray() {
        Arrayz.requireArray(new Object[] {});
    }

    @Test (expected = IllegalArgumentException.class)
    public void requireArrayThrowsIfArrayHasNulls() {
        Arrayz.requireArray(new Object[] { new Object(), null, new Object() });
    }

}
