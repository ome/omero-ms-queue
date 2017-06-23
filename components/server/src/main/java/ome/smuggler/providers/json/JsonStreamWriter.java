package ome.smuggler.providers.json;

import static java.util.Objects.requireNonNull;

import java.io.Writer;
import java.util.function.Function;

import ome.smuggler.core.convert.SinkWriter;


/**
 * Serialises {@code T}-values as JSON into a stream of type {@code S}.
 * @see JsonStreamReader
 */
public class JsonStreamWriter<T, S> implements SinkWriter<T, S> {

    private final Function<S, Writer> toWriter;

    /**
     * Creates a new instance.
     * @param toWriter converts a stream of type {@code S} to a {@link Writer}.
     * @throws NullPointerException if the argument is {@code null}.
     */
    public JsonStreamWriter(Function<S, Writer> toWriter) {
        requireNonNull(toWriter, "toWriter");
        this.toWriter = toWriter;
    }

    /**
     * Writes the given value, as JSON, to the specified stream.
     * @param out the stream.
     * @param value the object to serialise.
     * @throws Exception if an error occurs while serialising and writing the
     * output to the stream.
     */
    @Override
    public void write(S out, T value) throws Exception {
        requireNonNull(out, "out");
        requireNonNull(value, "value");

        Writer sink = toWriter.apply(out);
        JsonSinkWriter<T> writer = new JsonSinkWriter<>();
        writer.write(sink, value);
        sink.flush();
    }

}
