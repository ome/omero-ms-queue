package util.string;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static util.string.Strings.readAsString;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;


public class StringsReadAsStringTest {

    @Test
    public void readUtf8String() throws Exception {
        String expected = "123";
        InputStream input = new ByteArrayInputStream(
                expected.getBytes(StandardCharsets.UTF_8));
        String actual = readAsString(input);

        assertNotNull(actual);
        assertThat(actual, is(expected));
    }

    @Test (expected = IOException.class)
    public void rethrowException() throws Exception {
        Readable input = buf -> { throw new IOException(); };
        readAsString(input);
    }

    @Test
    public void ctor() {
        new Strings();  // only to get 100% coverage.
    }

}
