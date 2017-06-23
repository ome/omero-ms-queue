package ome.smuggler.providers.q;

import static org.mockito.Mockito.*;

import ome.smuggler.core.convert.SinkWriter;
import org.hornetq.api.core.HornetQException;
import org.junit.Test;

import ome.smuggler.core.msg.ChannelSource;

import java.io.OutputStream;


public class EnqueueTaskTest extends BaseSendTest {

    private static SinkWriter<String, OutputStream> serializer() {
        return (v, s) -> {};
    }

    private ChannelSource<String> newTask() throws HornetQException {
        initMocks();
        return new EnqueueTask<>(connector, serializer()).asDataSource();
    }
    
    @Test
    public void sendMessage() throws HornetQException {
        newTask().uncheckedSend("msg");

        verify(msgBody).writeBytes((byte[]) any());
        verify(producer).send(msgToQueue);
    }
    
    @Test (expected = NullPointerException.class)
    public void ctorThrowsIfNullQ() throws HornetQException {
        new EnqueueTask<>(null, serializer());
    }

    @Test (expected = NullPointerException.class)
    public void ctorThrowsIfNullWriter() throws HornetQException {
        new EnqueueTask<>(connector, null);
    }
}
