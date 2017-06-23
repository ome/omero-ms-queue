package util.string;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static util.sequence.Arrayz.array;
import static util.string.Strings.write;

import org.junit.Test;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.function.Consumer;

@RunWith(Theories.class)
public class StringsWriteTest {

    @DataPoints
    public static String[] contents = array("", "a", "ab");
    
    @Theory
    public void consumerControlsWhatWritten(String s, String t) {
        String actual = write(out -> {
            out.print(s);
            out.print(t);
        });
        
        assertThat(actual, is(s + t));
    }

    @Theory
    public void consumerControlsWhatWritten2(String s, String t) {
        ByteArrayOutputStream destination = new ByteArrayOutputStream();
        write(destination, out -> {
            out.print(s);
            out.print(t);
        });
        String actual = destination.toString();

        assertThat(actual, is(s + t));
    }

    @Theory
    public void writeStringAsIs(String s, String t) {
        ByteArrayOutputStream destination = new ByteArrayOutputStream();
        write(destination, s + t);
        String actual = destination.toString();

        assertThat(actual, is(s + t));
    }

    @Test(expected = NullPointerException.class)
    public void throwIfNullWriter() {
        write(null);
    }

    @Test(expected = NullPointerException.class)
    public void throwIfNullWriter2() {
        write(new ByteArrayOutputStream(), (Consumer<PrintWriter>) null);
    }

    @Test(expected = NullPointerException.class)
    public void throwIfNullStream() {
        write(null, w -> {});
    }

    @Test(expected = NullPointerException.class)
    public void throwIfNullStream2() {
        write(null, "");
    }

    @Test
    public void writeNull() {
        ByteArrayOutputStream destination = new ByteArrayOutputStream();
        write(destination, (String) null);
        String actual = destination.toString();

        assertThat(actual, is("null"));
    }
}
