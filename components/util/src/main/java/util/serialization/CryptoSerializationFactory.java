package util.serialization;

import static java.util.Objects.requireNonNull;

import java.io.InputStream;
import java.io.OutputStream;

import util.io.SinkWriter;
import util.io.SourceReader;
import util.io.crypto.CipherFactory;
import util.io.crypto.CryptoSinkWriter;
import util.io.crypto.CryptoSourceReader;


/**
 * Decorates an underlying {@link SerializationFactory} to encrypt/decrypt the
 * serialized data.
 */
public class CryptoSerializationFactory implements SerializationFactory {

    private final CipherFactory crypto;
    private final SerializationFactory factory;

    /**
     * Creates a new instance.
     * @param crypto the cipher factory to use for encryption/decryption.
     * @param factory the underlying serialization factory to decorate.
     * @throws NullPointerException if any argument is {@code null}.
     */
    public CryptoSerializationFactory(CipherFactory crypto,
                                      SerializationFactory factory) {
        requireNonNull(crypto, "crypto");
        requireNonNull(factory, "factory");

        this.crypto = crypto;
        this.factory = factory;
    }

    @Override
    public <T> SinkWriter<T, OutputStream> serializer() {
        return new CryptoSinkWriter<>(crypto, factory.serializer());
    }

    @Override
    public <T> SourceReader<InputStream, T> deserializer(Class<T> type) {
        return new CryptoSourceReader<>(crypto, factory.deserializer(type));
    }

}
