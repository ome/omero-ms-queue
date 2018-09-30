package kew.providers.artemis.qchan;

import static org.mockito.Mockito.*;

import kew.core.qchan.spi.QMsgFactory;
import kew.core.qchan.spi.QProducer;
import org.apache.activemq.artemis.api.core.ActiveMQBuffer;
import org.apache.activemq.artemis.api.core.ActiveMQException;
import org.apache.activemq.artemis.api.core.client.ClientMessage;
import org.apache.activemq.artemis.api.core.client.ClientProducer;
import org.apache.activemq.artemis.api.core.client.ClientSession;
import org.apache.activemq.artemis.core.config.CoreQueueConfiguration;
import org.junit.Before;
import org.junit.Test;


public class ArtemisQProducerTest {

    private ClientSession mockSession;
    private ClientProducer mockProducer;
    private ArtemisQConnector msgFactory;
    private QProducer<ArtemisMessage> target;

    @Before
    public void setUp() throws Exception {
        CoreQueueConfiguration mockConfig = mock(CoreQueueConfiguration.class);
        mockSession = mock(ClientSession.class);
        msgFactory = new ArtemisQConnector(mockConfig, mockSession);

        mockProducer = mock(ClientProducer.class);
        when(mockSession.createProducer(anyString())).thenReturn(mockProducer);
    }

    @Test
    public void passMessageToClientProducer() throws Exception {
        ClientMessage msg = mock(ClientMessage.class);
        when(mockSession.createMessage(anyBoolean())).thenReturn(msg);
        when(msg.getBodyBuffer()).thenReturn(mock(ActiveMQBuffer.class));

        target = msgFactory.newProducer();
        target.sendMessage(QMsgFactory::durableMessage, out -> {});
        verify(mockProducer, times(1)).send(any());
    }

    @Test (expected = NullPointerException.class)
    public void ctorThrowsIfNullSession() throws ActiveMQException {
        new ArtemisQProducer(null, msgFactory);
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
