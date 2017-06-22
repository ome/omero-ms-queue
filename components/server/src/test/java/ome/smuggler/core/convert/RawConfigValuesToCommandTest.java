package ome.smuggler.core.convert;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static java.util.stream.Collectors.joining;
import static ome.smuggler.core.convert.RawConfigValues.toCommand;
import static util.sequence.Arrayz.array;

import org.junit.Test;


public class RawConfigValuesToCommandTest {

    private static String parsedAsString(String input) {
        return toCommand(input).tokens().collect(joining(" "));
    }

    @Test
    public void unixCommand() {
        String actual = parsedAsString("nice -n 10");
        assertThat(actual, is("nice -n 10"));
    }

    @Test
    public void windowsCommand() {
        String actual = parsedAsString("start /belownormal /wait /b");
        assertThat(actual, is("start /belownormal /wait /b"));
    }

    @Test
    public void whitespace() {
        String actual = parsedAsString(" \tnice -n\t3 ");
        assertThat(actual, is("nice -n 3"));
    }

    @Test
    public void doesntHandleQuotesProperly() { // just to document limitations...
        String[] actual = toCommand("  pgm \"this is one arg\"  ")
                         .tokens()
                         .toArray(String[]::new);
        String[] expected = array("pgm", "\"this", "is", "one", "arg\"");

        assertArrayEquals(expected, actual);
    }

}
