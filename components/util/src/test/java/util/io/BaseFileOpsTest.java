package util.io;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class BaseFileOpsTest {

    @Rule
    public final TemporaryFolder tempDir = new TemporaryFolder();

    public Path createFileInTempDir(String name) throws IOException {
        return tempDir.newFile(name).toPath().toAbsolutePath();
    }

    public Path createFileInTempDir(String name, byte[] content)
            throws IOException {
        Path target = createFileInTempDir(name);
        Files.write(target, content);
        return target;
    }

    public Path createDirInTempDir(String name) throws IOException {
        return tempDir.newFolder(name).toPath();
    }

    public Path tempDirPath() {
        return tempDir.getRoot().toPath().toAbsolutePath();
    }

    public Path pathInTempDir(String name) {
        return tempDirPath().resolve(name);
    }

    public long countEntriesInTempDir() throws IOException {
        return Files.list(tempDirPath()).count();
    }

    @Test
    public void ctor() {
        new FileOps();  // only to get 100% coverage.
    }

}
