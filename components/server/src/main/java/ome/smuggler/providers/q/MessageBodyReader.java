package ome.smuggler.providers.q;

import static java.util.Objects.requireNonNull;

import ome.smuggler.core.convert.SourceReader;
import org.hornetq.api.core.client.ClientMessage;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * Reads the body of a message from the underlying HornetQ buffer.
 * It uses a de-serializer to convert the underlying byte buffer into the body
 * value.
 * @see MessageBodyWriter
 */
public class MessageBodyReader<T> implements SourceReader<ClientMessage, T> {

    private final SourceReader<InputStream, T> deserializer;

    /**
     * Creates a new instance.
     * @param deserializer the deserializer to use.
     * @throws NullPointerException if the argument is {@code null}.
     */
    public MessageBodyReader(SourceReader<InputStream, T> deserializer) {
        requireNonNull(deserializer, "deserializer");
        this.deserializer = deserializer;
    }

    @Override
    public T read(ClientMessage source) throws Exception {
        requireNonNull(source, "source");

        int length = source.getBodyBuffer().readInt();            // (*)
        byte[] buf = new byte[length];
        source.getBodyBuffer().readBytes(buf);

        ByteArrayInputStream in = new ByteArrayInputStream(buf);  // (*)
        return deserializer.read(in);
    }
    /* NOTE. Large messages.
     * If we ever going to need large messages (I doubt it!) we could easily
     * give this code an upgrade using NIO channels and byte buffers. (In
     * that case, don't forget to use a long instead of an int for the
     * serialised buffer size!)
     * However, bear in mind that for large messages (think GBs), slurping
     * the whole thing into memory (as we do here) can cause severe indigestion
     * and possibly send Smuggler to the ER...So rather switch to stream
     * processing in that case!
     */
}
