package util.io;

import org.junit.Test;

import static util.io.StreamOps.close;

import java.io.IOException;

public class StreamOpsCloseTest {

    @Test (expected = NullPointerException.class)
    public void closeThrowsIfNullArg() {
        close(null);
    }

    @Test
    public void closeSwallowsIoException() {
        close(() -> {
            throw new IOException();
        });
    }

    @Test (expected = RuntimeException.class)
    public void closeLetsRuntimeExceptionBubbleUp() {
        close(() -> {
            throw new RuntimeException();
        });
    }

}
