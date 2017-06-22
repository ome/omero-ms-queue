package ome.smuggler.providers.q;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import ome.smuggler.core.convert.SourceReader;
import org.hornetq.api.core.HornetQBuffer;
import org.hornetq.api.core.HornetQException;
import org.hornetq.api.core.client.ClientConsumer;
import org.hornetq.api.core.client.ClientMessage;
import org.hornetq.api.core.client.ClientSession;
import org.junit.Test;

import ome.smuggler.config.items.ImportQConfig;
import ome.smuggler.core.msg.ChannelMessage;
import ome.smuggler.core.msg.ChannelSink;
import ome.smuggler.core.msg.MessageSink;

import java.io.InputStream;

public class DequeueTaskTest implements MessageSink<ClientMessage, String> {

    private static SourceReader<InputStream, String> deserializer(String sentData) {
        return in -> sentData;
    }

    private ClientMessage receivedMsg;
    private String receivedData;
    
    private DequeueTask<String> newTask(String sentData) throws HornetQException {
        ImportQConfig q = new ImportQConfig();
        q.setName("q");
        ClientSession sesh = mock(ClientSession.class);
        ClientConsumer receiver = mock(ClientConsumer.class);
        when(sesh.createConsumer(q.getName(), false)).thenReturn(receiver);
        
        QueueConnector connector = new QueueConnector(q, sesh);
        DequeueTask<String> task = 
                new DequeueTask<>(connector, this, deserializer(sentData), true);
        verify(receiver).setMessageHandler(task);
        
        return task;
    }
    
    @Override
    public void consume(ChannelMessage<ClientMessage, String> msg) {
        receivedMsg = msg.metadata().get();
        receivedData = msg.data();
    }

    @Test
    public void receiveMessage() throws HornetQException {
        String msgData = "msg";
        DequeueTask<String> task = newTask(msgData);
        ClientMessage qMsg = mock(ClientMessage.class);
        HornetQBuffer buf = mock(HornetQBuffer.class);
        when(qMsg.getBodyBuffer()).thenReturn(buf);

        task.onMessage(qMsg);
        
        assertThat(receivedMsg, is(qMsg));
        assertThat(receivedData, is(msgData));
    }
    
    @Test (expected = NullPointerException.class)
    public void ctor1ThrowsIfArg1Null() throws HornetQException {
        new DequeueTask<>(null, (ChannelSink<String>) d -> {},
                          deserializer(""), true);
    }
    
    @Test (expected = NullPointerException.class)
    public void ctor1ThrowsIfArg2Null() throws HornetQException {
        new DequeueTask<>(mock(QueueConnector.class), 
                          (ChannelSink<String>)null, deserializer(""), true);
    }

    @Test (expected = NullPointerException.class)
    public void ctor1ThrowsIfArg4Null() throws HornetQException {
        new DequeueTask<>(mock(QueueConnector.class),
                          (ChannelSink<String>) d -> {}, null, true);
    }

    @Test (expected = NullPointerException.class)
    public void ctor2ThrowsIfArg1Null() throws HornetQException {
        new DequeueTask<>(null, (MessageSink<ClientMessage, String>) msg -> {}, 
                          deserializer(""), true);
    }
    
    @Test (expected = NullPointerException.class)
    public void ctor2ThrowsIfArg2Null() throws HornetQException {
        new DequeueTask<>(mock(QueueConnector.class), 
                          (MessageSink<ClientMessage, String>)null,
                          deserializer(""), true);
    }

    @Test (expected = NullPointerException.class)
    public void ctor2ThrowsIfArg4Null() throws HornetQException {
        new DequeueTask<>(mock(QueueConnector.class),
                (MessageSink<ClientMessage, String>) msg -> {},
                (SourceReader<InputStream, String>) null,
                true);
    }

}
