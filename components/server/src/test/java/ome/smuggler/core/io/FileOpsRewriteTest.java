package ome.smuggler.core.io;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import util.types.Nat;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

public class FileOpsRewriteTest {

    private Path source;
    private String content = "\\r\\n some stuff \t nobody \\r really cares \\n about!";

    @Before
    public void setup() throws Exception {
        source = Files.createTempFile("test", UUID.randomUUID().toString());
        FileOps.writeNew(source, out -> {
            PrintWriter w = new PrintWriter(out);
            w.print(content);
            w.flush();
        });
    }

    @After
    public void tearDown() throws Exception {
        FileOps.delete(source);
    }

    private String readContent() throws IOException {
        return StreamOps.readLinesIntoString(
                Files.newInputStream(source));
    }

    @Test (expected = NullPointerException.class)
    public void throwIfNullTarget() throws Exception {
        FileOps.rewrite(null, (in, out) -> {});
    }

    @Test (expected = NullPointerException.class)
    public void throwIfNullFilter() throws Exception {
        FileOps.rewrite(source, null);
    }

    @Test
    public void overrideSourceWithEmptyFileWhenFilterOutputsNothing()
        throws Exception {
        FileOps.rewrite(source, (in, out) -> {});
        assertThat(FileOps.byteLength(source), is(Nat.of(0)));
    }

    @Test
    public void overrideSourceWithFilterOutput() throws Exception {
        String oldContent = readContent();
        assertThat(oldContent, is(content));

        String newContent = "overridden :-)";
        FileOps.rewrite(source, (in, out) -> {
            PrintWriter w = new PrintWriter(out);
            w.print(newContent);
            w.flush();
        });
        assertThat(readContent(), is(newContent));
    }

}
