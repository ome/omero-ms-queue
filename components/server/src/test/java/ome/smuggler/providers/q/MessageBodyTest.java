package ome.smuggler.providers.q;

import static org.junit.Assert.*;

import util.io.SinkWriter;
import util.io.SourceReader;

import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

@RunWith(Theories.class)
public class MessageBodyTest extends BaseMessageBodyTest<byte[]> {

    @DataPoints
    public static byte[][] bodies = new byte[][] {
        new byte[0],
        new byte[] { 65 },         // ASCII for A
        new byte[] { 65, 66 },     // A, B
        new byte[] { 65, 66, 67 }  // A, B, C
    };

    // copy input bytes into output stream
    private static SinkWriter<byte[], OutputStream> serializer() {
        return (out, buf) -> {
            out.write(buf);
            out.flush();
        };
    }


    // read input stream into byte array
    private static SourceReader<InputStream, byte[]> deserializer() {
        return in -> {
            ByteArrayOutputStream buf = new ByteArrayOutputStream();
            int b;
            while ((b = in.read()) != -1) {
                buf.write(b);
            }
            return buf.toByteArray();
        };
    }

    @Override
    protected MessageBodyWriter<byte[]> writer() {
        return new MessageBodyWriter<>(serializer());
    }

    @Override
    protected MessageBodyReader<byte[]> reader() {
        return new MessageBodyReader<>(deserializer());
    }

    @Theory
    public void writeThenReadIsIdentity(byte[] bodyValue) {
        byte[] deserialized = writeThenReadValue(bodyValue, bodyValue.length);
        assertArrayEquals(bodyValue, deserialized);
    }

}
