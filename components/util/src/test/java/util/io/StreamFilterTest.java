package util.io;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class StreamFilterTest {

    @Test (expected = IOException.class)
    public void processRethrowsExceptionAsIs() {
        StreamFilter target = (in, out) -> {
            throw new IOException();
        };
        target.process(new ByteArrayInputStream(new byte[] {}),
                       new ByteArrayOutputStream());
    }

}
