package ome.smuggler.providers.q;

import static java.util.Objects.requireNonNull;

import ome.smuggler.core.convert.SinkWriter;
import org.hornetq.api.core.client.ClientMessage;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

/**
 * Writes the body of a message to the underlying HornetQ buffer.
 * It uses a serializer to convert the body into a byte stream.
 * @see MessageBodyReader
 */
public class MessageBodyWriter<T> implements SinkWriter<T, ClientMessage> {

    private final SinkWriter<T, OutputStream> serializer;

    /**
     * Creates a new instance.
     * @param serializer the serializer to use.
     * @throws NullPointerException if the argument is {@code null}.
     */
    public MessageBodyWriter(SinkWriter<T, OutputStream> serializer) {
        requireNonNull(serializer, "serializer");
        this.serializer = serializer;
    }

    @Override
    public void write(ClientMessage sink, T body) throws Exception {
        requireNonNull(sink, "sink");
        requireNonNull(body, "body");

        ByteArrayOutputStream out = new ByteArrayOutputStream(4*1024);  // (*)
        serializer.write(out, body);
        byte[] serialized = out.toByteArray();

        sink.getBodyBuffer().writeInt(serialized.length);               // (*)
        sink.getBodyBuffer().writeBytes(serialized);
    }
    /* NOTE. Large messages.
     * If we ever going to need large messages (I doubt it!) we could easily
     * give this code an upgrade using NIO channels and byte buffers. (In
     * that case, don't forget to use a long instead of an int for the
     * serialised buffer size!)
     * However, bear in mind that for large messages (think GBs), holding
     * the whole thing into memory (as we do here) can cause severe indigestion
     * and possibly send Smuggler to the ER...So rather switch to stream
     * processing in that case!
     */
}
