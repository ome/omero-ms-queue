package ome.smuggler.config.wiring.crypto;

import static java.util.Objects.requireNonNull;

import java.io.InputStream;
import java.io.OutputStream;

import ome.smuggler.core.io.crypto.*;
import ome.smuggler.core.types.CryptoConfigSource;
import ome.smuggler.providers.json.JsonInputStreamReader;
import ome.smuggler.providers.json.JsonOutputStreamWriter;

import util.io.SinkWriter;
import util.io.SourceReader;

/**
 * Builds serializers depending on the current crypto configuration.
 * If encryption is turned off, then we use plain JSON serialization. On the
 * other hand, when encryption is turned on, the output of JSON serialization
 * is encrypted and when de-serializing, the input is first decrypted and then
 * de-serialized from JSON.
 * Services that read/write sensitive data (e.g. session keys) to disk
 * (directly or indirectly through the queue) use these provided factory
 * methods to make sure sensitive data is protected.
 */
public class SerializationFactory {

    private final CryptoConfigSource config;

    /**
     * Creates a new instance.
     * @param config the crypto configuration.
     * @throws NullPointerException if the argument is {@code null}.
     */
    public SerializationFactory(CryptoConfigSource config) {
        requireNonNull(config, "config");
        this.config = config;
    }

    private CipherFactory crypto() {
        CryptoAlgoSpec algo = CryptoAlgoSpec.AES;
        return new CipherFactory(algo, config.key().get());
    }

    /**
     * Instantiates a serializer for {@code T-}values.
     * @param <T> the value type.
     * @return the serializer.
     */
    public <T> SinkWriter<T, OutputStream> serializer() {
        SinkWriter<T, OutputStream> writer = new JsonOutputStreamWriter<>();
        if (config.encrypt()) {
            writer = new CryptoSinkWriter<>(crypto(), writer);
        }
        return writer;
    }

    /**
     * Instantiates a de-serializer for {@code T-}values.
     * @param <T> the value type.
     * @param type the class of {@code T-}values.
     * @return the de-serializer.
     * @throws NullPointerException if the argument is {@code null}.
     */
    public <T> SourceReader<InputStream, T> deserializer(Class<T> type) {
        SourceReader<InputStream, T> reader = new JsonInputStreamReader<>(type);
        if (config.encrypt()) {
            reader = new CryptoSourceReader<>(crypto(), reader);
        }
        return reader;
    }

}
