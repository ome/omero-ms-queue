package util.io;

import static util.error.Exceptions.unchecked;

/**
 * Writes a value {@code V} to a data sink {@code S}.
 * For example, {@code S} could be an output stream and the value {@code V}
 * could be serialised into the stream. Or {@code V} could be the body of a
 * message to put on a queue {@code S}.
 * @see SourceReader
 */
public interface SinkWriter<T, S> {

    /**
     * Writes a value to a data sink.
     * @param sink where to write the value to.
     * @param value the data to write.
     * @throws NullPointerException if any of the arguments is {@code null}.
     * @throws Exception if the data could not be written to the sink.
     */
    void write(S sink, T value) throws Exception;
    
    /**
     * Calls the {@link #write(Object, Object)} write} method converting any
     * checked exception into an unchecked one that will bubble up without
     * requiring a {@code throws} clause on this method.
     * @param sink where to write the value to.
     * @param value the data to write.
     */
    default void uncheckedWrite(S sink, T value) {
        unchecked(this::write).accept(sink, value);
    }
    
}
