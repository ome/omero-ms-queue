package kew.providers.artemis.qchan;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.apache.activemq.artemis.api.core.ActiveMQBuffer;
import org.apache.activemq.artemis.api.core.ActiveMQException;
import org.apache.activemq.artemis.api.core.client.ClientMessage;
import org.apache.activemq.artemis.api.core.client.ClientProducer;
import org.apache.activemq.artemis.api.core.client.ClientSession;
import org.apache.activemq.artemis.core.config.CoreQueueConfiguration;

import kew.core.qchan.spi.QConnector;

public class BaseSendTest {

    protected ClientProducer producer;
    protected ClientMessage msgToQueue;
    protected ActiveMQBuffer msgBody;
    protected QConnector<ArtemisMessage> connector;
    
    protected void initMocks() throws ActiveMQException {
        CoreQueueConfiguration q = new CoreQueueConfiguration();
        q.setName("q");
        ClientSession sesh = mock(ClientSession.class);
        
        producer = mock(ClientProducer.class);
        when(sesh.createProducer(q.getAddress())).thenReturn(producer);
        
        msgToQueue = mock(ClientMessage.class);
        boolean durable = true;
        when(sesh.createMessage(durable)).thenReturn(msgToQueue);
        
        msgBody = mock(ActiveMQBuffer.class);
        when(msgToQueue.getBodyBuffer()).thenReturn(msgBody);
        
        connector = new ArtemisQConnector(q, sesh);
    }
    
}
