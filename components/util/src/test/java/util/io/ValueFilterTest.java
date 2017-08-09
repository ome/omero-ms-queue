package util.io;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static java.util.function.Function.identity;
import static util.sequence.Arrayz.array;

import org.junit.Test;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import java.io.*;

@RunWith(Theories.class)
public class ValueFilterTest {

    private static SourceReader<InputStream, Integer> dummyReader = in -> 1;

    @DataPoints
    public static String[] inputs = array("", "a", "ab");


    @Theory
    public void identitySetterResultsInCopyingInputToOutput(String s) {
        StringFilter target = new StringFilter(identity());
        ByteArrayInputStream in = new ByteArrayInputStream(s.getBytes());
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        target.process(in, out);
        String actual = out.toString();

        assertThat(actual, is(s));
    }

    @Theory
    public void canTransformInputAndWriteResultToOutput(String s) {
        StringFilter target = new StringFilter(String::toUpperCase);
        ByteArrayInputStream in = new ByteArrayInputStream(s.getBytes());
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        target.process(in, out);
        String actual = out.toString();

        assertThat(actual, is(s.toUpperCase()));
    }

    @Test(expected = NullPointerException.class)
    public void ctorThrowsIfNullReader() {
        new ValueFilter<Integer>(null, (out, v) -> {}, identity());
    }

    @Test(expected = NullPointerException.class)
    public void ctorThrowsIfNullWriter() {
        new ValueFilter<>(dummyReader, null, identity());
    }

    @Test(expected = NullPointerException.class)
    public void ctorThrowsIfNullSetter() {
        new ValueFilter<>(dummyReader, (out, v) -> {}, null);
    }

    @Test(expected = NullPointerException.class)
    public void processThrowsIfNullInStream() {
        ValueFilter<Integer> target =
                new ValueFilter<>(dummyReader, (out, v) -> {}, null);
        target.process(null, new ByteArrayOutputStream());
    }

    @Test(expected = NullPointerException.class)
    public void processThrowsIfNullOutStream() {
        ValueFilter<Integer> target =
                new ValueFilter<>(dummyReader, (out, v) -> {}, null);
        target.process(new ByteArrayInputStream(new byte[2]), null);
    }

}
