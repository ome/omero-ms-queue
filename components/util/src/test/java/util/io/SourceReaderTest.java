package util.io;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import org.junit.Test;

import java.io.IOException;

public class SourceReaderTest {

    @Test
    public void uncheckedReadReturnsReadValueAsIs() {
        int expected = 2;
        SourceReader<Integer, Integer> target = x -> expected;
        int actual = target.uncheckedRead(1);

        assertThat(actual, is(expected));
    }

    @Test (expected = IOException.class)
    public void uncheckedReadRethrowsExceptionAsIs() {
        SourceReader<Integer, Integer> target = x -> {
            throw new IOException();
        };
        target.uncheckedRead(1);
    }

}
