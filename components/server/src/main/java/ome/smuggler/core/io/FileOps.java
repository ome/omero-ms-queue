package ome.smuggler.core.io;

import static java.util.Objects.requireNonNull;
import static util.error.Exceptions.runUnchecked;
import static util.error.Exceptions.throwAsIfUnchecked;
import static util.error.Exceptions.unchecked;

import java.io.*;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import java.util.stream.Stream;

import ome.smuggler.core.types.Nat;
import util.lambda.ConsumerE;


/**
 * Utility methods for file operations.
 */
public class FileOps {

    /**
     * Deletes the specified file if it exists; does nothing otherwise.
     * @param file path to the file to delete.
     * @throws NullPointerException if the argument is {@code null}.
     * <p>The following checked exceptions are rethrown as unchecked (i.e.
     * the exception is masked as a runtime exception and thrown as is without
     * wrapping it in a {@code RuntimeException}):
     * <br>{@link IOException} if an I/O error occurs.
     * </p>
     */
    public static void delete(Path file) {
        requireNonNull(file, "file");
        runUnchecked(() -> Files.deleteIfExists(file));
    }
    
    /**
     * Copies the source file to the destination path, overriding it if it 
     * exists already. If the source file does not exist, this method does
     * nothing.
     * @param source path to the file to copy.
     * @param destination path to the copied file.
     * @throws NullPointerException if any argument is {@code null}.
     * <p>The following checked exceptions are rethrown as unchecked (i.e.
     * the exception is masked as a runtime exception and thrown as is without
     * wrapping it in a {@code RuntimeException}):
     * <br>{@link IOException} if an I/O error occurs.
     * </p>
     */
    public static void copy(Path source, Path destination) {
        requireNonNull(source, "source");
        requireNonNull(destination, "destination");
        
        if (Files.exists(source)) {
            runUnchecked(() -> Files.copy(source, destination, 
                                          StandardCopyOption.REPLACE_EXISTING));
        }
    }
    
    /**
     * Efficiently copies an initial segment of a file into a target stream.
     * @param from path to the file from which to get the data.
     * @param howManyBytes number of bytes to take, starting at the beginning
     * of the file.
     * @param to where to copy the data.
     * @return the number of bytes actually copied.
     * @throws NullPointerException if any argument is {@code null}.
     * @throws IOException if an I/O error occurs.
     */
    public static long transfer(Path from, Nat howManyBytes, OutputStream to) 
            throws IOException {
        requireNonNull(from, "from");
        requireNonNull(howManyBytes, "howManyBytes");
        requireNonNull(to, "to");
        
        try (FileChannel source = FileChannel.open(from);
             WritableByteChannel target = Channels.newChannel(to)) {
            long limit = howManyBytes.get();
            long bytesRead = 0;
            while (bytesRead < limit && bytesRead < source.size()) {  // (1)
                long count = limit - bytesRead;
                bytesRead += source.transferTo(bytesRead, count, target); // (2)
            }
            return bytesRead;
        }
    }
    /* NOTES
     * 1. Don't assume the file won't change as we read it. It may be written to
     * or being truncated; the latter justifies querying the size each time.
     * 2. If the JavaDoc doesn't lie, then transferTo "is potentially much more 
     * efficient than a simple loop that reads from this channel and writes to
     * the target channel. Many operating systems can transfer bytes directly 
     * from the filesystem cache to the target channel without actually copying
     * them."
     */
        
    /**
     * Queries the size of the given file.
     * @param file the file to query.
     * @return the number of bytes in the specified file.
     * @throws NullPointerException if the argument is {@code null}.
     * <p>The following checked exceptions are rethrown as unchecked (i.e.
     * the exception is masked as a runtime exception and thrown as is without
     * wrapping it in a {@code RuntimeException}):
     * <br>{@link IOException} if an I/O error occurs.
     * </p>
     */
    public static Nat byteLength(Path file) {
        requireNonNull(file, "file");
        return unchecked(Files::size)
               .andThen(Nat::from)
               .andThen(x -> x.orElse(Nat.of(0)))  // (*)
               .apply(file);
    }
    /* (*) returned file size should always be non-negative, I'm just being
     * paranoid here I guess...
     */
    
    /**
     * Collects all the regular files directly under the specified directory.
     * Sub-directories are not recursed.
     * @param dir the target directory.
     * @return a list of the files found in the directory.
     * @throws NullPointerException if the argument is {@code null}.
     * <p>The following checked exceptions are rethrown as unchecked (i.e.
     * the exception is masked as a runtime exception and thrown as is without
     * wrapping it in a {@code RuntimeException}):
     * <br>{@link IOException} if an I/O error occurs.
     * </p>
     */
    public static Stream<Path> listChildFiles(Path dir) {
        requireNonNull(dir, "dir");
        if (!Files.exists(dir)) {
            return Stream.empty();
        }
        return unchecked(() -> Files.walk(dir, 1).filter(Files::isRegularFile))
                .get();
    }
    
    /**
     * Calls {@link Files#createDirectories(Path, java.nio.file.attribute.FileAttribute...)
     * Files.createDirectories()} re-throwing any exception without wrapping. 
     * @param p the path containing the directories to create.
     * @throws NullPointerException if the argument is {@code null}.
     */
    public static void ensureDirectory(Path p) {
        requireNonNull(p, "p");
        
        Path dir = p.toAbsolutePath();
        runUnchecked(() -> Files.createDirectories(dir));
    }
    
    /**
     * Creates a new file and has the given consumer write its contents.
     * If the file already exists, it will be overwritten. 
     * @param file path to the file to create and write.
     * @param writer writes the file contents.
     * @throws NullPointerException if any argument is {@code null}.
     * <p>The following checked exceptions are rethrown as unchecked (i.e.
     * the exception is masked as a runtime exception and thrown as is without
     * wrapping it in a {@code RuntimeException}):
     * <br>{@link IOException} if an I/O error occurs.
     * </p>
     */
    public static void writeNew(Path file, ConsumerE<OutputStream> writer) {
        requireNonNull(file, "file");
        requireNonNull(writer, "writer");
        
        try {
            OutputStream out = Files.newOutputStream(file);  // (*)
            BufferedOutputStream buf = new BufferedOutputStream(out);
            
            writer.accept(buf);
            
            StreamOps.close(buf);
        } catch (IOException e) {
            throwAsIfUnchecked(e);
        }
    }
    /* (*) The JavaDoc of the method states that it will truncate and overwrite 
     * an existing file, or create the file if it doesn't initially exist.
     */

    /**
     * Uses a filter to rewrite the content of the specified file.
     * @param target the file to rewrite.
     * @param f the filter to use.
     * @throws NullPointerException if any argument is {@code null}.
     * <p>The following checked exceptions are rethrown as unchecked (i.e.
     * the exception is masked as a runtime exception and thrown as is without
     * wrapping it in a {@code RuntimeException}):
     * <br>{@link Exception} if an I/O or any other kind of error occurs.
     * </p>
     */
    public static void rewrite(Path target, StreamFilter f) {
        Path output = null;
        try {
            output = Files.createTempFile(UUID.randomUUID().toString(), null);
            filter(target, output, f);
            copy(output, target);
        } catch (Exception e) {
            throwAsIfUnchecked(e);
        } finally {
            if (output != null) {
                delete(output);
            }
        }
    }

    /**
     * Uses a filter to read data from a source file and write a new destination
     * file. If the destination file exists, it will be overwritten with the
     * filter's output.
     * @param from the source file.
     * @param to the destination file.
     * @param f the filter to use.
     * @throws NullPointerException if any argument is {@code null}.
     * <p>The following checked exceptions are rethrown as unchecked (i.e.
     * the exception is masked as a runtime exception and thrown as is without
     * wrapping it in a {@code RuntimeException}):
     * <br>{@link Exception} if an I/O or any other kind of error occurs.
     * </p>
     */
    public static void filter(Path from, Path to, StreamFilter f) {
        requireNonNull(from, "from");
        requireNonNull(to, "to");
        requireNonNull(f, "f");

        try (InputStream in = new BufferedInputStream(
                Files.newInputStream(from));
             OutputStream out = new BufferedOutputStream(
                     Files.newOutputStream(to))) {  // (*)
            f.processE(in, out);
        } catch (Exception e) {
            throwAsIfUnchecked(e);
        }
    }
    /* (*) The JavaDoc of the method states that it will truncate and overwrite
     * an existing file, or create the file if it doesn't initially exist.
     */
}
