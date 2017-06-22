package ome.smuggler.core.convert;

import static util.error.Exceptions.unchecked;

/**
 * Reads a value {@code V} from a data source {@code S}.
 * For example, {@code S} could be an input stream and the value {@code V}
 * could be de-serialised from the stream. Or {@code V} could be the body of
 * a message fetched from a queue {@code S}.
 * @see SinkWriter
 */
public interface SourceReader<S, T> {

    /**
     * Reads a {@code T}-value from the data source.
     * @param source the data source.
     * @return the value read from the source.
     * @throws NullPointerException if the argument is {@code null}.
     * @throws Exception if the value could not be read.
     */
    T read(S source) throws Exception;

    /**
     * Calls the {@link #read(Object) read} method converting any checked
     * exception into an unchecked one that will bubble up without requiring
     * a {@code throws} clause on this method.
     * @param source the data source.
     * @return the value read from the source.
     */
    default T uncheckedRead(S source) {
        return unchecked(this::read).apply(source);
    }

}
