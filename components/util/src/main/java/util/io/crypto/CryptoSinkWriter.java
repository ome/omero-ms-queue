package util.io.crypto;

import static java.util.Objects.requireNonNull;

import java.io.OutputStream;
import javax.crypto.CipherOutputStream;

import util.io.SinkWriter;

/**
 * An encryption filter that wraps an underlying {@link SinkWriter} {@code w}
 * to encrypt whatever {@code w} writes to the sink {@link OutputStream}.
 */
public class CryptoSinkWriter<T> implements SinkWriter<T, OutputStream> {

    private final CipherFactory crypto;
    private final SinkWriter<T, OutputStream> writer;

    /**
     * Creates a new instance.
     * @param crypto factory to create encryption ciphers.
     * @param writer the underlying writer that actually outputs the
     *               {@code T-}value to the sink {@link OutputStream}.
     * @throws NullPointerException if any argument is {@code null}.
     */
    public CryptoSinkWriter(CipherFactory crypto,
                            SinkWriter<T, OutputStream> writer) {
        requireNonNull(crypto, "crypto");
        requireNonNull(writer, "writer");

        this.crypto = crypto;
        this.writer = writer;
    }

    @Override
    public void write(OutputStream sink, T value) throws Exception {
        requireNonNull(sink, "sink");
        requireNonNull(value, "value");

        try (CipherOutputStream cos = new CipherOutputStream(
                sink, crypto.encryptionChipher())) {
            writer.write(cos, value);
        }
    }

}
