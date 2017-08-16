package util.io;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Path;

public class FileOpsCopyTest extends BaseFileOpsTest {

    @Test
    public void canCopyEmptyFile() throws Exception {
        Path source = createFileInTempDir("source");
        Path dest = pathInTempDir("dest");

        assertFalse(Files.exists(dest));
        FileOps.copy(source, dest);
        assertTrue(Files.exists(dest));
    }

    @Test
    public void overrideExistingFile() throws Exception {
        byte[] sourceContent = new byte[] { 50 };
        Path source = createFileInTempDir("source", sourceContent);
        Path dest = createFileInTempDir("dest", new byte[] { 100, 102 });

        assertTrue(Files.exists(dest));
        FileOps.copy(source, dest);

        byte[] destNewContent = Files.readAllBytes(dest);
        assertArrayEquals(sourceContent, destNewContent);
    }

    @Test
    public void doNothingIfSourceDoesntExist() throws Exception {
        Path source = pathInTempDir("source");
        Path dest = pathInTempDir("dest");

        assertFalse(Files.exists(source));
        assertThat(countEntriesInTempDir(), is(0L));

        FileOps.copy(source, dest);

        assertThat(countEntriesInTempDir(), is(0L));
    }

    @Test (expected = NullPointerException.class)
    public void throwsIfNullSource() {
        FileOps.copy(null, pathInTempDir("x"));
    }

    @Test (expected = NullPointerException.class)
    public void throwsIfNullDestination() {
        FileOps.copy(pathInTempDir("x"), null);
    }

}
