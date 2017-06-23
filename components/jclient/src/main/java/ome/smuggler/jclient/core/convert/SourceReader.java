package ome.smuggler.jclient.core.convert;

/**
 * Reads a value from a data source, typically the body of a message.
 * @see SinkWriter
 */
public interface SourceReader<T> {

    /**
     * Reads {@code T}-value from the underlying data source.
     * @return the value read from the source.
     * @throws Exception if the value could not be read.
     */
    T read() throws Exception;

}
