package util.io;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static util.sequence.Arrayz.array;

import org.junit.Test;
import util.string.Strings;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;


public class StreamOpsReadLinesTest {

    @Test
    public void readUtf8Lines() {
        String[] lines = array("1", "2");
        byte[] content = Strings.unlines(Stream.of(lines))
                                .getBytes(StandardCharsets.UTF_8);
        ByteArrayInputStream input = new ByteArrayInputStream(content);

        String[] actual = StreamOps.readLines(input,
                                              ls -> ls.toArray(String[]::new),
                                              StandardCharsets.UTF_8);
        assertArrayEquals(lines, actual);
    }

    @Test
    public void readEmptyStream() {
        ByteArrayInputStream input = new ByteArrayInputStream(new byte[] {});

        String[] actual = StreamOps.readLines(input,
                                              ls -> ls.toArray(String[]::new),
                                              (Charset[]) null);
        assertNotNull(actual);
        assertThat(actual.length, is(0));
    }

    @Test (expected = IOException.class)
    public void rethrowReaderErrorAsIs() {
        ByteArrayInputStream input = new ByteArrayInputStream(new byte[] { 1 });

        StreamOps.readLines(input,
                            ls -> { throw new IOException(); },
                            new Charset[] { null });
    }

    @Test (expected = NullPointerException.class)
    public void throwIfNullInput() {
        StreamOps.readLines(null, xs -> 1);
    }

    @Test (expected = NullPointerException.class)
    public void throwIfNullReader() {
        StreamOps.readLines(new ByteArrayInputStream(new byte[] {}), null);
    }

}
