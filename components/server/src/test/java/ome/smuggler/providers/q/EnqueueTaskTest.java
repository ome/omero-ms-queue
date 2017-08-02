package ome.smuggler.providers.q;

import static org.mockito.Mockito.*;

import kew.core.qchan.EnqueueTask;
import util.io.SinkWriter;
import org.apache.activemq.artemis.api.core.ActiveMQException;
import org.junit.Test;

import kew.core.msg.ChannelSource;

import java.io.OutputStream;


public class EnqueueTaskTest extends BaseSendTest {

    private static SinkWriter<String, OutputStream> serializer() {
        return (v, s) -> {};
    }

    private ChannelSource<String> newTask() throws Exception {
        initMocks();
        return new EnqueueTask<>(connector.newProducer(),
                                 serializer()).asDataSource();
    }
    
    @Test
    public void sendMessage() throws Exception {
        newTask().send("msg");

        verify(msgBody).writeBytes((byte[]) any());
        verify(producer).send(msgToQueue);
    }
    
    @Test (expected = NullPointerException.class)
    public void ctorThrowsIfNullQ() throws ActiveMQException {
        new EnqueueTask<>(null, serializer());
    }

    @Test (expected = NullPointerException.class)
    public void ctorThrowsIfNullWriter() throws Exception {
        new EnqueueTask<>(connector.newProducer(), null);
    }
}
