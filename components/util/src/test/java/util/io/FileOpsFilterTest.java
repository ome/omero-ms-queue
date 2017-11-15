package util.io;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import org.junit.Test;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;

public class FileOpsFilterTest extends BaseFileOpsTest {

    @Test
    public void filterFileContent() throws IOException {
        byte[] sourceContent = new byte[] { 100 };
        Path source = createFileInTempDir("source", sourceContent);
        Path dest = pathInTempDir("dest");

        FileOps.filter(source, dest, (in, out) -> {
            int x = in.read();
            out.write(x + 1);
        });
        byte[] destContent = Files.readAllBytes(dest);

        assertNotNull(destContent);
        assertThat(destContent.length, is(1));
        assertThat(destContent[0], is((byte)(sourceContent[0] + 1)));
    }

    @Test (expected = NoSuchFileException.class)
    public void throwIfSourceDoesntExist() throws IOException {
        Path source = pathInTempDir("source");
        Path dest = pathInTempDir("dest");
        FileOps.filter(source, dest, (in, out) -> {});
    }

    @Test (expected = AccessDeniedException.class)
    public void throwIfDestinationNotWritable() throws IOException {
        byte[] sourceContent = new byte[] { 100 };
        Path source = createFileInTempDir("source", sourceContent);
        Path dest = createFileInTempDir("dest");

        boolean isReadOnly = dest.toFile().setReadOnly();
        assertTrue(isReadOnly);
        FileOps.filter(source, dest, (in, out) -> {});
    }

    @Test (expected = RuntimeException.class)
    public void rethrowAnyFilterErrorAsIs() throws IOException {
        Path source = createFileInTempDir("source");
        Path dest = pathInTempDir("dest");
        FileOps.filter(source, dest, (in, out) -> {
            throw new RuntimeException();
        });
    }

    @Test(expected = NullPointerException.class)
    public void throwIfNullSource() throws Exception {
        FileOps.filter(null, pathInTempDir("x"), (in, out) -> {});
    }

    @Test(expected = NullPointerException.class)
    public void throwIfNullDestination() throws Exception {
        FileOps.filter(pathInTempDir("x"), null, (in, out) -> {});
    }

    @Test(expected = NullPointerException.class)
    public void throwIfNullFilter() throws Exception {
        FileOps.filter(pathInTempDir("x"), pathInTempDir("y"), null);
    }

}
