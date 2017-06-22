package ome.smuggler.providers.q;

import static org.mockito.Mockito.*;

import org.hornetq.api.core.HornetQBuffer;
import org.hornetq.api.core.client.ClientMessage;
import org.junit.Before;
import org.mockito.ArgumentCaptor;


public abstract class BaseMessageBodyTest<T> {

    private ClientMessage msgMock;

    @Before
    public void setup() {
        msgMock = mock(ClientMessage.class);
        HornetQBuffer buf = mock(HornetQBuffer.class);
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
        HornetQBuffer buf = msgMock.getBodyBuffer();
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
        writer().uncheckedWrite(msgMock, value);
        simulateSendReceive(serializedValueLen);
        return reader().uncheckedRead(msgMock);
    }

    protected abstract MessageBodyWriter<T> writer();
    protected abstract MessageBodyReader<T> reader();
}
