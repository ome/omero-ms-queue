package util.serialization.yaml;

import static java.util.Objects.requireNonNull;
import static util.sequence.Streams.asStream;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Stream;

import org.yaml.snakeyaml.Yaml;


/**
 * Simple (de-)serialization of objects (from) to YAML.
 */
public class YamlConverter<T> {

    /**
     * Serializes the given object to a UTF8 stream.
     * @param data the object to serialize.
     * @param output the stream to write to.
     * @throws NullPointerException if any argument is {@code null}.
     */
    public void toYaml(T data, OutputStream output) {
        requireNonNull(data, "data");
        requireNonNull(output, "output");

        Writer writer = new BufferedWriter(
                new OutputStreamWriter(output, StandardCharsets.UTF_8));
        new Yaml().dump(data, writer);
    }

    /**
     * Serializes the given object to a stream.
     * @param data the object to serialize.
     * @param output the stream to write to.
     * @throws NullPointerException if any argument is {@code null}.
     */
    public void toYaml(T data, Writer output) {
        requireNonNull(data, "data");
        requireNonNull(output, "output");
        
        new Yaml().dump(data, output);
    }
    
    /**
     * Serializes the given object to a string.
     * @param data the object to serialize.
     * @return a string containing the serialized object. 
     * @throws NullPointerException if any argument is {@code null}.
     */
    public String toYaml(T data) {
        requireNonNull(data, "data");
        
        return new Yaml().dump(data);
    }

    /**
     * Reads a list of {@code T}'s from the input stream.
     * @param input a stream containing a YAML list of serialized {@code T}'s. 
     * @return the list of objects read from the stream.
     * @throws NullPointerException if any argument is {@code null}.
     */
    @SuppressWarnings("unchecked")
    public Stream<T> fromYamlList(InputStream input) {
        requireNonNull(input, "input");
        
        List<T> ts = new Yaml().loadAs(input, List.class); 
        return asStream(ts);
    }

    /**
     * Reads an instance of the specified class from the stream.
     * @param input a stream containing a serialized {@code T}.
     * @param tClass {@code T}'s class.
     * @return the object read from the stream.
     * @throws NullPointerException if any argument is {@code null}.
     */
    public T fromYaml(InputStream input, Class<T> tClass) {
        requireNonNull(input, "input");
        requireNonNull(tClass, "tClass");
        
        return new Yaml().loadAs(input, tClass);
    }
    
}
