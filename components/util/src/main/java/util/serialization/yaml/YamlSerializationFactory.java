package util.serialization.yaml;

import java.io.InputStream;
import java.io.OutputStream;

import util.io.SinkWriter;
import util.io.SourceReader;
import util.serialization.SerializationFactory;

/**
 * Factory to instantiate YAML serializers.
 * This factory uses SnakeYaml, so the serialized values should be instances
 * of a Java Bean (i.e. getters/setters, no args ctor) to be (de-)serialized
 * painlessly by SnakeYaml.
 */
public class YamlSerializationFactory implements SerializationFactory {

    @Override
    public <T> SinkWriter<T, OutputStream> serializer() {
        return (sink, value) -> new YamlConverter<>().toYaml(value, sink);
    }

    @Override
    public <T> SourceReader<InputStream, T> deserializer(Class<T> type) {
        YamlConverter<T> converter = new YamlConverter<>();
        return source -> converter.fromYaml(source, type);
    }

}
