package util.io;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import util.types.Nat;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileOpsRewriteTest extends BaseFileOpsTest {

    @Test (expected = NullPointerException.class)
    public void throwIfNullTarget() throws Exception {
        FileOps.rewrite(null, (in, out) -> {});
    }

    @Test (expected = NullPointerException.class)
    public void throwIfNullFilter() throws Exception {
        FileOps.rewrite(pathInTempDir("x"), null);
    }

    @Test
    public void overrideSourceWithEmptyFileWhenFilterOutputsNothing()
        throws Exception {
        Path source = createFileInTempDir("x", new byte[] { 1, 2 });
        assertThat(FileOps.byteLength(source), is(Nat.of(2)));

        FileOps.rewrite(source, (in, out) -> {});

        assertThat(FileOps.byteLength(source), is(Nat.of(0)));
    }

    @Test
    public void overrideSourceWithFilterOutput() throws Exception {
        Path source = createFileInTempDir("x", new byte[] { 1, 2 });
        byte[] newContent = new byte[] { 99 };
        FileOps.rewrite(source, (in, out) -> out.write(newContent));

        byte[] actualContent = Files.readAllBytes(source);
        assertArrayEquals(actualContent, newContent);
    }

    @Test (expected = RuntimeException.class)
    public void throwAnyFilterError() throws IOException {
        Path source = createFileInTempDir("x", new byte[] { 1, 2 });
        FileOps.rewrite(source, (in, out) -> { throw new RuntimeException(); });
    }

}
