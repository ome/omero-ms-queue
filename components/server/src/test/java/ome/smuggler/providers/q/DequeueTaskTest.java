package ome.smuggler.providers.q;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import kew.core.qchan.spi.QConnector;
import util.io.SourceReader;
import org.apache.activemq.artemis.api.core.ActiveMQBuffer;
import org.apache.activemq.artemis.api.core.client.ClientConsumer;
import org.apache.activemq.artemis.api.core.client.ClientMessage;
import org.apache.activemq.artemis.api.core.client.ClientSession;
import org.junit.Test;

import ome.smuggler.config.items.ImportQConfig;
import kew.core.msg.ChannelMessage;
import kew.core.msg.ChannelSink;
import kew.core.msg.MessageSink;

import java.io.InputStream;

public class DequeueTaskTest implements MessageSink<ArtemisMessage, String> {

    private static SourceReader<InputStream, String> deserializer(String sentData) {
        return in -> sentData;
    }

    private ArtemisMessage receivedMsg;
    private String receivedData;
    
    private DequeueTask<ArtemisMessage, String> newTask(String sentData)
            throws Exception {
        ImportQConfig q = new ImportQConfig();
        q.setName("q");
        ClientSession sesh = mock(ClientSession.class);
        ClientConsumer receiver = mock(ClientConsumer.class);
        when(sesh.createConsumer(q.getName(), false)).thenReturn(receiver);
        
        QConnector<ArtemisMessage> connector = new ArtemisQConnector(q, sesh);
        DequeueTask<ArtemisMessage, String> task =
                new DequeueTask<>(connector, this, deserializer(sentData), true);

        verify(receiver).setMessageHandler(any());
        
        return task;
    }
    
    @Override
    public void consume(ChannelMessage<ArtemisMessage, String> msg) {
        receivedMsg = msg.metadata().get();
        receivedData = msg.data();
    }

    @Test
    public void hasReceiver() throws Exception {
        DequeueTask<ArtemisMessage, String> task = newTask("");
        assertNotNull(task.receiver());
    }

    @Test
    public void receiveMessage() throws Exception {
        String msgData = "msg";
        DequeueTask<ArtemisMessage, String> task = newTask(msgData);
        ClientMessage qMsg = mock(ClientMessage.class);
        ActiveMQBuffer buf = mock(ActiveMQBuffer.class);
        when(qMsg.getBodyBuffer()).thenReturn(buf);

        ArtemisQConsumer qConsumer = (ArtemisQConsumer) task.receiver();
        qConsumer.onMessage(qMsg);
        
        assertThat(receivedMsg.message(), is(qMsg));
        assertThat(receivedData, is(msgData));
    }
    
    @Test (expected = NullPointerException.class)
    public void ctor1ThrowsIfArg1Null() throws Exception {
        new DequeueTask<>(null, (ChannelSink<String>) d -> {},
                          deserializer(""), true);
    }
    
    @Test (expected = NullPointerException.class)
    @SuppressWarnings("unchecked")
    public void ctor1ThrowsIfArg2Null() throws Exception {
        new DequeueTask<>(mock(QConnector.class),
                          (ChannelSink<String>)null, deserializer(""), true);
    }

    @Test (expected = NullPointerException.class)
    @SuppressWarnings("unchecked")
    public void ctor1ThrowsIfArg4Null() throws Exception {
        new DequeueTask<>(mock(QConnector.class),
                          (ChannelSink<String>) d -> {}, null, true);
    }

    @Test (expected = NullPointerException.class)
    public void ctor2ThrowsIfArg1Null() throws Exception {
        new DequeueTask<>(null, (MessageSink<ArtemisMessage, String>) msg -> {},
                          deserializer(""), true);
    }
    
    @Test (expected = NullPointerException.class)
    @SuppressWarnings("unchecked")
    public void ctor2ThrowsIfArg2Null() throws Exception {
        new DequeueTask<>(mock(QConnector.class),
                          (MessageSink<ArtemisMessage, String>)null,
                          deserializer(""), true);
    }

    @Test (expected = NullPointerException.class)
    @SuppressWarnings("unchecked")
    public void ctor2ThrowsIfArg4Null() throws Exception {
        new DequeueTask<>(mock(QConnector.class),
                (MessageSink<ArtemisMessage, String>) msg -> {},
                (SourceReader<InputStream, String>) null,
                true);
    }

}
