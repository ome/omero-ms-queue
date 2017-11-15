package kew.providers.artemis.qchan;

import static org.mockito.Mockito.*;
import static kew.providers.artemis.qchan.MessageBodyReader.readBody;
import static kew.providers.artemis.qchan.MessageBodyWriter.writeBody;

import org.apache.activemq.artemis.api.core.ActiveMQBuffer;
import org.apache.activemq.artemis.api.core.client.ClientMessage;
import org.junit.Before;
import org.mockito.ArgumentCaptor;
import util.io.SinkWriter;
import util.io.SourceReader;

import java.io.InputStream;
import java.io.OutputStream;


public abstract class BaseMessageBodyTest<T> {

    private ClientMessage msgMock;

    @Before
    public void setup() {
        msgMock = mock(ClientMessage.class);
        ActiveMQBuffer buf = mock(ActiveMQBuffer.class);
        when(msgMock.getBodyBuffer()).thenReturn(buf);
    }

    private byte[] verifySerialization(int serializedValueLen) {
        ArgumentCaptor<byte[]> captor = ArgumentCaptor.forClass(byte[].class);

        verify(msgMock.getBodyBuffer()).writeInt(serializedValueLen);
        verify(msgMock.getBodyBuffer()).writeBytes(captor.capture());

        byte[] serialized = captor.getValue();
        return serialized;
    }

    private void mockMsgToRead(int serializedValueLen, byte[] serializedValue) {
        ActiveMQBuffer buf = msgMock.getBodyBuffer();
        when(buf.readInt()).thenReturn(serializedValueLen);
        doAnswer(invocation -> {
            byte[] passedInBuffer = (byte[])invocation.getArguments()[0];
            System.arraycopy(serializedValue, 0,
                             passedInBuffer, 0, passedInBuffer.length);
            return null;
        }).when(buf).readBytes((byte[])any());
    }

    private void simulateSendReceive(int serializedValueLen) {
        byte[] serializedValue = verifySerialization(serializedValueLen);
        mockMsgToRead(serializedValueLen, serializedValue);
    }

    protected T writeThenReadValue(T value, int serializedValueLen) {
        writeBody(msgMock, out -> serializer().write(out, value));
        simulateSendReceive(serializedValueLen);
        return deserializer().uncheckedRead(readBody(msgMock));
    }

    protected abstract SinkWriter<T, OutputStream> serializer();
    protected abstract SourceReader<InputStream, T> deserializer();

}
