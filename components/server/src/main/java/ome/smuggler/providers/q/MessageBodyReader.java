package ome.smuggler.providers.q;

import static java.util.Objects.requireNonNull;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.function.Function;

import org.apache.activemq.artemis.api.core.client.ClientMessage;

import util.io.SourceReader;
import util.lambda.FunctionE;


/**
 * Reads the body of a message from the underlying Artemis buffer into an
 * input stream.
 * @see MessageBodyWriter
 */
public class MessageBodyReader
        implements SourceReader<ClientMessage, InputStream> {

    /**
     * Convenience method to compose the {@link #read(ClientMessage) method}
     * with a deserialiser.
     * @param deserialiser reads the message body from an input stream and
     *                     converts it to a {@code T}-value.
     * @param <T> the type of the value in the message body.
     * @return the composite function.
     */
    public static <T> Function<ClientMessage, T> bodyReader(
            FunctionE<InputStream, T> deserialiser) {
        requireNonNull(deserialiser, "deserialiser");

        Function<ClientMessage, InputStream> reader =
                new MessageBodyReader()::read;
        return reader.andThen(deserialiser);
    }

    @Override
    public InputStream read(ClientMessage source) {
        requireNonNull(source, "source");

        int length = source.getBodyBuffer().readInt();            // (*)
        byte[] buf = new byte[length];
        source.getBodyBuffer().readBytes(buf);

        return new ByteArrayInputStream(buf);  // (*)
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
