package util.io;

import static org.junit.Assert.*;
import static util.io.StreamOps.readAll;

import org.junit.Test;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import java.io.ByteArrayInputStream;


@RunWith(Theories.class)
public class StreamOpsReadAllTest {

    @DataPoints
    public static byte[][] inputs = new byte[][] {
        new byte[0], new byte[] { 1 }, new byte[] { 1, 2 },
        new byte[] { 1, 2, 3 }
    };

    @Theory
    public void t(byte[] input) {
        ByteArrayInputStream in = new ByteArrayInputStream(input);
        byte[] output = readAll(in);

        assertNotNull(output);
        assertArrayEquals(input, output);
    }

    @Test (expected = NullPointerException.class)
    public void throwIfNullStream() {
        readAll(null);
    }
}
