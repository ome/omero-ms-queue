package util.io;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import org.junit.Test;
import util.types.Nat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;

public class FileOpsTransferTest extends BaseFileOpsTest {

    @Test
    public void transferNothing() throws IOException {
        byte[] sourceContent = new byte[] { 100, 101 };
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Path source = createFileInTempDir("source", sourceContent);

        int bytesCopiedCount = (int) FileOps.transfer(source, Nat.of(0), out);
        byte[] bytesCopied = out.toByteArray();

        assertThat(bytesCopiedCount, is(0));
        assertThat(bytesCopied.length, is(bytesCopiedCount));
    }

    @Test
    public void transferLessThanAvailableContent() throws IOException {
        byte[] sourceContent = new byte[] { 100, 101 };
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Path source = createFileInTempDir("source", sourceContent);

        int bytesCopiedCount = (int) FileOps.transfer(source, Nat.of(1), out);
        byte[] bytesCopied = out.toByteArray();

        assertThat(bytesCopiedCount, is(1));
        assertThat(bytesCopied.length, is(bytesCopiedCount));
        assertThat(bytesCopied[0], is(sourceContent[0]));
    }

    @Test
    public void transferAllContent() throws IOException {
        byte[] sourceContent = new byte[] { 100, 101 };
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Path source = createFileInTempDir("source", sourceContent);

        int bytesCopiedCount = (int) FileOps.transfer(
                source, Nat.of(sourceContent.length), out);
        byte[] bytesCopied = out.toByteArray();

        assertThat(bytesCopiedCount, is(sourceContent.length));
        assertArrayEquals(bytesCopied, sourceContent);
    }

    @Test
    public void transferAllContentIfRequestedMoreThanAvailable()
            throws IOException {
        byte[] sourceContent = new byte[] { 100, 101 };
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Path source = createFileInTempDir("source", sourceContent);

        int bytesCopiedCount = (int) FileOps.transfer(
                source, Nat.of(sourceContent.length + 1), out);
        byte[] bytesCopied = out.toByteArray();

        assertThat(bytesCopiedCount, is(sourceContent.length));
        assertArrayEquals(bytesCopied, sourceContent);
    }

    @Test (expected = IOException.class)
    public void throwIfWriteError() throws IOException {
        byte[] sourceContent = new byte[] { 100, 101 };
        Path source = createFileInTempDir("source", sourceContent);
        OutputStream out = mock(OutputStream.class);
        doThrow(new IOException()).when(out).write(any());
        doThrow(new IOException()).when(out).write(any(), anyInt(), anyInt());

        FileOps.transfer(source, Nat.of(1), out);
    }

    @Test (expected = IOException.class)
    public void throwIfNonExistingSource() throws IOException {
        Path nonExistingSource = pathInTempDir("x");
        OutputStream out = new ByteArrayOutputStream();

        FileOps.transfer(nonExistingSource, Nat.of(1), out);
    }

    @Test (expected = NullPointerException.class)
    public void throwIfNullSource() throws Exception {
        FileOps.transfer(null, Nat.of(0), new ByteArrayOutputStream());
    }

    @Test (expected = NullPointerException.class)
    public void throwIfNullSize() throws Exception {
        FileOps.transfer(pathInTempDir("x"), null, new ByteArrayOutputStream());
    }

    @Test (expected = NullPointerException.class)
    public void throwIfNullDestination() throws Exception {
        FileOps.transfer(pathInTempDir("x"), Nat.of(0), null);
    }

}
