package util.serialization;

import java.io.InputStream;
import java.io.OutputStream;

import util.io.SinkWriter;
import util.io.SourceReader;

/**
 * Instantiates typed serializers.
 */
public interface SerializationFactory {

    /**
     * Instantiates a serializer for {@code T-}values.
     * @param <T> the value type.
     * @return the serializer.
     */
    <T> SinkWriter<T, OutputStream> serializer();

    /**
     * Instantiates a de-serializer for {@code T-}values.
     * @param <T> the value type.
     * @param type the class of {@code T-}values.
     * @return the de-serializer.
     * @throws NullPointerException if the argument is {@code null}.
     */
    <T> SourceReader<InputStream, T> deserializer(Class<T> type);

}
