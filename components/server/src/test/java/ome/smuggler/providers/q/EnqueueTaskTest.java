package ome.smuggler.providers.q;

import static org.mockito.Mockito.*;

import ome.smuggler.core.convert.SinkWriter;
import org.apache.activemq.artemis.api.core.ActiveMQException;
import org.junit.Test;

import ome.smuggler.core.msg.ChannelSource;

import java.io.OutputStream;


public class EnqueueTaskTest extends BaseSendTest {

    private static SinkWriter<String, OutputStream> serializer() {
        return (v, s) -> {};
    }

    private ChannelSource<String> newTask() throws ActiveMQException {
        initMocks();
        return new EnqueueTask<>(connector, serializer()).asDataSource();
    }
    
    @Test
    public void sendMessage() throws ActiveMQException {
        newTask().uncheckedSend("msg");

        verify(msgBody).writeBytes((byte[]) any());
        verify(producer).send(msgToQueue);
    }
    
    @Test (expected = NullPointerException.class)
    public void ctorThrowsIfNullQ() throws ActiveMQException {
        new EnqueueTask<>(null, serializer());
    }

    @Test (expected = NullPointerException.class)
    public void ctorThrowsIfNullWriter() throws ActiveMQException {
        new EnqueueTask<>(connector, null);
    }
}
