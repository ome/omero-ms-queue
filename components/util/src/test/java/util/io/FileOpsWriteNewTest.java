package util.io;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileOpsWriteNewTest extends BaseFileOpsTest {

    @Test
    public void writeNewFile() throws IOException {
        Path target = pathInTempDir("x");
        assertFalse(Files.exists(target));

        byte[] content = new byte[] { 50, 51 };
        FileOps.writeNew(target, out -> out.write(content));
        assertTrue(Files.exists(target));

        byte[] actualContent = Files.readAllBytes(target);
        assertArrayEquals(actualContent, content);
    }

    @Test
    public void writeEmptyFile() throws IOException {
        Path target = pathInTempDir("x");
        FileOps.writeNew(target, out -> {});

        byte[] content = Files.readAllBytes(target);
        assertNotNull(content);
        assertThat(content.length, is(0));
    }

    @Test
    public void overrideExistingFile() throws IOException {
        Path target = pathInTempDir("x");
        assertFalse(Files.exists(target));

        FileOps.writeNew(target, out -> {});
        assertTrue(Files.exists(target));

        byte[] content = Files.readAllBytes(target);
        assertNotNull(content);
        assertThat(content.length, is(0));
    }

    @Test (expected = IOException.class)
    public void throwAnyWriteError() throws IOException {
        Path target = pathInTempDir("x");
        FileOps.writeNew(target, out -> { throw new IOException(); });
    }

    @Test (expected = NullPointerException.class)
    public void throwIfNullFilePath() {
        FileOps.writeNew(null, out -> {});
    }

    @Test (expected = NullPointerException.class)
    public void throwIfNullConsumer() {
        FileOps.writeNew(pathInTempDir("x"), null);
    }

}
