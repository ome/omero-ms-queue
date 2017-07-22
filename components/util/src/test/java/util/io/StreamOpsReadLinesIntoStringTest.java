package util.io;

import static java.util.stream.Collectors.joining;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static util.sequence.Arrayz.array;
import static util.io.StreamOps.readLinesIntoString;

import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.stream.Stream;


@RunWith(Theories.class)
public class StreamOpsReadLinesIntoStringTest {

    @DataPoints
    public static String[][] linesSupply = new String[][]{
            array(""), array("x"), array("", "x"),
            array(" "), array("x "), array(" ", "x "), array("x ", " "),
            array(" x", "y ", " z ")
    };

    private static String buildTextInput(String[] lines) {
        String sep = System.lineSeparator();
        return Stream.of(lines).collect(joining(sep));
    }

    private static InputStream toInputStream(String text) {
        return new ByteArrayInputStream(text.getBytes());
    }

    @Theory
    public void joiningLinesThenReadingIsTheIdentity(String[] lines) {
        String expected = buildTextInput(lines);
        InputStream input = toInputStream(expected);
        String actual = readLinesIntoString(input);

        assertThat(actual, is(expected));
    }

}
