package ome.smuggler.core.service.file;

import static util.string.Strings.write;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.stream.Stream;

import ome.smuggler.core.io.StreamFilter;
import ome.smuggler.core.io.StringFilter;
import util.lambda.ConsumerE;
import util.lambda.FunctionE;
import util.object.Identifiable;


/**
 * Manages a directory containing files associated to {@link Identifiable} task 
 * ID's.
 */
public interface TaskFileStore<T extends Identifiable> {

    /**
     * Builds the path to the file associated to the given task ID.
     * @param taskId the task ID.
     * @return the path associated to the ID.
     * @throws NullPointerException if the argument is {@code null}.
     */
    Path pathFor(T taskId);

    /**
     * @return the task ID's of all files currently stored in the directory.
     * <p>The following checked exceptions are rethrown as unchecked (i.e.
     * the exception is masked as a runtime exception and thrown as is without
     * wrapping it in a {@code RuntimeException}):
     * <br>{@link IOException} if an I/O occurs.
     * </p>
     */
    Stream<T> listTaskIds();
    
    /**
     * Deletes the file associated to the specified task ID.
     * @param taskId the task ID.
     * @throws NullPointerException if the argument is {@code null}.
     * <p>The following checked exceptions are rethrown as unchecked (i.e.
     * the exception is masked as a runtime exception and thrown as is without
     * wrapping it in a {@code RuntimeException}):
     * <br>{@link IOException} if an I/O occurs.
     * </p>
     */
    void remove(T taskId);

    /**
     * Writes a file associated to the specified task.
     * If the file already exists, it will be overwritten.
     * @param taskId identifies the task.
     * @param contentWriter writes the file contents into an output stream.
     * @throws NullPointerException if any argument is {@code null}.
     * <p>The following checked exceptions are rethrown as unchecked (i.e.
     * the exception is masked as a runtime exception and thrown as is without
     * wrapping it in a {@code RuntimeException}):
     * <br>{@link IOException} if an I/O occurs.
     * </p>
     */
    void add(T taskId, ConsumerE<OutputStream> contentWriter);
    
    /**
     * Writes a file associated to the specified task.
     * If the file already exists, it will be overwritten.
     * @param taskId identifies the task.
     * @param content the file contents.
     * @throws NullPointerException if any argument is {@code null}.
     * <p>The following checked exceptions are rethrown as unchecked (i.e.
     * the exception is masked as a runtime exception and thrown as is without
     * wrapping it in a {@code RuntimeException}):
     * <br>{@link IOException} if an I/O occurs.
     * </p>
     */
    default void add(T taskId, String content) {
        add(taskId, out -> write(out, content));
    }
    
    /**
     * Writes a file associated to the specified task by taking the contents
     * from a source file.
     * If the file already exists in the store, it will be overwritten.
     * (The source file is never modified though.)
     * @param taskId identifies the task.
     * @param contentSource the file contents to use.
     * @throws NullPointerException if any argument is {@code null}.
     * <p>The following checked exceptions are rethrown as unchecked (i.e.
     * the exception is masked as a runtime exception and thrown as is without
     * wrapping it in a {@code RuntimeException}):
     * <br>{@link IOException} if an I/O occurs.
     * </p>
     */
    void add(T taskId, Path contentSource);

    /**
     * Uses a {@code filter} function to replace the content of the file
     * associated to the specified task.
     * @param taskId identifies the task.
     * @param filter gets passed the current content of the file and produces
     *               the new file content.
     * @throws NullPointerException if any argument is {@code null}.
     * @throws IllegalArgumentException if there's no file associated to the
     * specified task.
     * <p>The following checked exceptions are rethrown as unchecked (i.e.
     * the exception is masked as a runtime exception and thrown as is without
     * wrapping it in a {@code RuntimeException}):
     * <br>{@link Exception} if any other kind of error occurs.
     * </p>
     * @see #replace(Identifiable, FunctionE)
     */
    void replace(T taskId, StreamFilter filter);

    /**
     * Uses a {@code setter} function to replace the content of the file
     * associated to the specified task.
     * This method reads the entire file content into memory as a string {@code
     * s} using the default character encoding, calls {@code setter} with {@code
     * s} as an argument, and overrides the old file contents with the returned
     * string.
     * @param taskId identifies the task.
     * @param setter gets passed the current content of the file and produces
     *               the new file content.
     * @throws NullPointerException if any argument is {@code null}.
     * @throws IllegalArgumentException if there's no file associated to the
     * specified task.
     * <p>The following checked exceptions are rethrown as unchecked (i.e.
     * the exception is masked as a runtime exception and thrown as is without
     * wrapping it in a {@code RuntimeException}):
     * <br>{@link Exception} if any other kind of error occurs.
     * </p>
     * @see #replace(Identifiable, StreamFilter)
     */
    default void replace(T taskId, FunctionE<String, String> setter) {
        replace(taskId, new StringFilter(setter));
    }

}
