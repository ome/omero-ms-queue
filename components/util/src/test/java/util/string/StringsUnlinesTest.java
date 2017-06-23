package util.string;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static util.sequence.Arrayz.array;
import static util.string.Strings.lines;
import static util.string.Strings.unlines;

import java.util.stream.Stream;

import org.junit.Test;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

@RunWith(Theories.class)
public class StringsUnlinesTest {

    @DataPoints
    public static final String[][] linesSupply = new String[][] {
        array(""), array("1", ""), array("", "2"), array("1", "2", "3")
    };
    
    @Theory
    public void linesUndoUnlinesWhenElementsHaveNoNewline(String[] xs) {
        String[] actual = lines(unlines(Stream.of(xs))).toArray(String[]::new);
        assertArrayEquals(xs, actual);
    }
    
    @Test (expected = NullPointerException.class)
    public void throwIfNullArg() {
        unlines(null);
    }
    
    @Test
    public void emptyStringOnEmptyInputStream() {
        String actual = unlines(Stream.empty());
        assertThat(actual, is(""));
    }
    
    @Test
    public void turnsNullStreamElementIntoNewline() {
        String actual = unlines(Stream.of((String)null));
        assertThat(actual, is(String.format("%n", "")));
    }
    
}
