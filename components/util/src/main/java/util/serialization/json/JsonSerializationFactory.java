package util.serialization.json;

import java.io.InputStream;
import java.io.OutputStream;

import util.io.SinkWriter;
import util.io.SourceReader;
import util.serialization.SerializationFactory;


/**
 * Factory to instantiate JSON serializers.
 */
public class JsonSerializationFactory implements SerializationFactory {

    @Override
    public <T> SinkWriter<T, OutputStream> serializer() {
        return new JsonOutputStreamWriter<>();
    }

    @Override
    public <T> SourceReader<InputStream, T> deserializer(Class<T> type) {
        return new JsonInputStreamReader<>(type);
    }

}
