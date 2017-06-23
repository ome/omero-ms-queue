package ome.smuggler.providers.q;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.hornetq.api.core.HornetQBuffer;
import org.hornetq.api.core.HornetQException;
import org.hornetq.api.core.client.ClientMessage;
import org.hornetq.api.core.client.ClientProducer;
import org.hornetq.api.core.client.ClientSession;

import ome.smuggler.config.items.ImportQConfig;

public class BaseSendTest {

    protected ClientProducer producer;
    protected ClientMessage msgToQueue;
    protected HornetQBuffer msgBody;
    protected QueueConnector connector;
    
    protected void initMocks() throws HornetQException {
        ImportQConfig q = new ImportQConfig();
        q.setName("q");
        ClientSession sesh = mock(ClientSession.class);
        
        producer = mock(ClientProducer.class);
        when(sesh.createProducer(q.getAddress())).thenReturn(producer);
        
        msgToQueue = mock(ClientMessage.class);
        boolean durable = true;
        when(sesh.createMessage(durable)).thenReturn(msgToQueue);
        
        msgBody = mock(HornetQBuffer.class);
        when(msgToQueue.getBodyBuffer()).thenReturn(msgBody);
        
        connector = new QueueConnector(q, sesh);
    }
    
}
