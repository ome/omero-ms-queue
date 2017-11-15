package util.io;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;


@RunWith(Theories.class)
public class StreamOpsReadAllTest {

    @DataPoints
    public static byte[][] inputs = new byte[][] {
        new byte[0], new byte[] { 1 }, new byte[] { 1, 2 },
        new byte[] { 1, 2, 3 }
    };

    @Theory
    public void readAllBytes(byte[] input) {
        ByteArrayInputStream in = new ByteArrayInputStream(input);
        byte[] output = StreamOps.readAll(in);

        assertNotNull(output);
        assertArrayEquals(input, output);
    }

    @Test (expected = IOException.class)
    public void throwIfReadError() throws IOException {
        InputStream in = mock(InputStream.class);
        when(in.read(any(), anyInt(), anyInt())).thenThrow(new IOException());

        StreamOps.readAll(in);
    }

    @Test (expected = NullPointerException.class)
    public void throwIfNullStream() {
        StreamOps.readAll(null);
    }

    @Test
    public void ctor() {
        new StreamOps();  // only to get 100% coverage.
    }

}
