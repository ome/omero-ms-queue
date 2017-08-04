package kew.providers.artemis;

import static org.mockito.Mockito.*;

import kew.core.qchan.impl.EnqueueTask;
import util.io.SinkWriter;
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

}
