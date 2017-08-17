package util.io;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class FileOpsDirectoryTest extends BaseFileOpsTest {

    @Test
    public void listChildFilesDoesntRecurse() throws IOException {
        Path f1 = createFileInTempDir("f1");
        Path f2 = createFileInTempDir("f2");
        Path subdir = createDirInTempDir("subdir");
        Path ignoredFile = subdir.resolve("ignored-file");
        Files.write(ignoredFile, new byte[] {});

        assertTrue(Files.exists(f1));
        assertTrue(Files.exists(f2));
        assertTrue(Files.exists(ignoredFile));

        Set<Path> fs = FileOps.listChildFiles(tempDirPath())
                              .collect(Collectors.toSet());
        assertThat(fs.size(), is(2));
        assertTrue(fs.contains(f1));
        assertTrue(fs.contains(f2));
    }

    @Test
    public void listChildFilesReturnsEmptyIfDirDoesntExist() {
        Path dir = pathInTempDir("no-dir");
        assertFalse(Files.exists(dir));

        Stream<Path> files = FileOps.listChildFiles(dir);
        assertNotNull(files);
        assertThat(files.count(), is(0L));
    }

    @Test
    public void ensureDirectoryCreatesDirIfItDoesntExist() {
        Path dir = pathInTempDir("new-dir");
        assertFalse(Files.exists(dir));

        FileOps.ensureDirectory(dir);
        assertTrue(Files.exists(dir));
    }

    @Test
    public void ensureDirectoryDoesNothingIfDirExists() throws IOException {
        Path dir = createDirInTempDir("dir");
        assertTrue(Files.exists(dir));

        FileOps.ensureDirectory(dir);
        assertTrue(Files.exists(dir));
    }

    @Test (expected = NullPointerException.class)
    public void ensureDirectoryThrowsIfNullDir() {
        FileOps.ensureDirectory(null);
    }

    @Test (expected = NullPointerException.class)
    public void listChildFilesThrowsIfNullDir() {
        FileOps.listChildFiles(null);
    }

}
