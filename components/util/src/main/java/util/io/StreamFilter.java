package util.io;

import static util.error.Exceptions.throwAsIfUnchecked;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Represents the process of reading from an {@link InputStream} to produce the
 * content of an {@link OutputStream}.
 */
@FunctionalInterface
public interface StreamFilter {

    /**
     * Same as {@link #processE(InputStream, OutputStream) processE} but masks
     * any exception into a runtime exception so we can use this interface as
     * a functional interface.
     * @param in data to read.
     * @param out where to write the output data.
     */
    default void process(InputStream in, OutputStream out) {
        try {
            processE(in, out);
        } catch (Exception e) {
            throwAsIfUnchecked(e);
        }
    }

    /**
     * Reads from the input stream and writes to the output stream.
     * The caller is responsible for creating suitable streams and for closing
     * them after this method returns.
     * @param in the data to read.
     * @param out where this method will write its output data.
     * @throws NullPointerException if any argument is {@code null}.
     * @throws Exception If any other error occurs.
     */
    void processE(InputStream in, OutputStream out) throws Exception;

}
