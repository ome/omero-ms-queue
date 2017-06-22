package ome.smuggler.jclient.core.convert;

/**
 * Writes a value to a data sink, typically the body of a message.
 * @see SourceReader
 */
public interface SinkWriter<T> {

    /**
     * Writes a value to the underlying data sink.
     * @param value the data to write.
     * @throws NullPointerException if the argument is {@code null}.
     * @throws Exception if the data could not be written to the sink.
     */
    void write(T value) throws Exception;

}
