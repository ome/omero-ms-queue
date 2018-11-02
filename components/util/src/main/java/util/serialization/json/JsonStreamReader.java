package util.serialization.json;

import static java.util.Objects.requireNonNull;

import java.io.Reader;
import java.util.function.Function;

import util.io.SourceReader;

/**
 * Reads JSON from a stream of type {@code S} and de-serialises it as a
 * {@code T}-value.
 * @see JsonStreamWriter
 */
public class JsonStreamReader<S, T> implements SourceReader<S, T> {

    private final Class<T> valueType;
    private final Function<S, Reader> toReader;

    /**
     * Creates a new instance.
     * @param valueType the class of the object to read.
     * @param toReader converts a stream of type {@code S} to a {@link Reader}.
     * @throws NullPointerException if any argument is {@code null}.
     */
    public JsonStreamReader(Class<T> valueType, Function<S, Reader> toReader) {
        requireNonNull(valueType, "valueType");
        requireNonNull(toReader, "toReader");

        this.valueType = valueType;
        this.toReader = toReader;
    }

    /**
     * Reads JSON from the input stream and de-serialises it into a
     * {@code T}-value.
     * @param in the stream to read from.
     * @return the de-serialised object.
     * @throws NullPointerException if the argument is {@code null}.
     * @throws Exception if an error occurs while reading from the stream or
     * converting JSON into an object.
     */
    @Override
    public T read(S in) throws Exception {
        requireNonNull(in, "in");

        Reader source = toReader.apply(in);
        JsonSourceReader<T> reader = new JsonSourceReader<>(valueType);
        return reader.read(source);
    }

}
