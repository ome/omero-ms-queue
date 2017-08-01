package ome.smuggler.providers.q;

import static org.mockito.Mockito.*;

import kew.core.qchan.spi.QMsgFactory;
import org.apache.activemq.artemis.api.core.ActiveMQBuffer;
import org.apache.activemq.artemis.api.core.ActiveMQException;
import org.apache.activemq.artemis.api.core.client.ClientMessage;
import org.apache.activemq.artemis.api.core.client.ClientProducer;
import org.junit.Before;
import org.junit.Test;


public class ArtemisQProducerTest {

    private ClientProducer mockProducer;
    private QMsgFactory<ArtemisMessage> mockMsgFactory;
    private ArtemisQProducer target;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() throws Exception {
        mockProducer = mock(ClientProducer.class);
        mockMsgFactory = mock(QMsgFactory.class);
        target = new ArtemisQProducer(mockProducer, mockMsgFactory);
    }

    @Test
    public void passMessageToClientProducer() throws Exception {
        ClientMessage msg = mock(ClientMessage.class);
        ArtemisMessage adapter = new ArtemisMessage(msg);
        when(mockMsgFactory.durableMessage()).thenReturn(adapter);
        when(msg.getBodyBuffer()).thenReturn(mock(ActiveMQBuffer.class));

        target.sendMessage(QMsgFactory::durableMessage, out -> {});
        verify(mockProducer, times(1)).send(any());
    }

    @Test (expected = NullPointerException.class)
    public void ctorThrowsIfNullSession() throws ActiveMQException {
        new ArtemisQProducer(null, mockMsgFactory);
    }

    @Test (expected = NullPointerException.class)
    public void ctorThrowsIfNullMsgFactory() throws ActiveMQException {
        new ArtemisQProducer(mockProducer, null);
    }

    @Test (expected = NullPointerException.class)
    public void sendMessageThrowsIfNullMetaBuilder() throws Exception {
        target.sendMessage(null, out -> {});
    }

    @Test (expected = NullPointerException.class)
    public void sendMessageThrowsIfNullConsumer() throws Exception {
        target.sendMessage(QMsgFactory::nonDurableMessage, null);
    }

}
