package util.serialization.json;

import static java.util.Objects.requireNonNull;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;

import util.io.SinkWriter;

/**
 * Serializes an object into JSON and writes the serialized data to a sink.
 */
public class JsonSinkWriter<T> implements SinkWriter<T, Appendable> {

    private final Gson mapper;
    
    /**
     * Creates a new instance.
     */
    public JsonSinkWriter() {
        this.mapper = new Gson();
    }
    
    @Override
    public void write(Appendable sink, T body) throws JsonIOException {
        requireNonNull(sink, "sink");
        requireNonNull(body, "body");

        mapper.toJson(body, sink);
    }

}
