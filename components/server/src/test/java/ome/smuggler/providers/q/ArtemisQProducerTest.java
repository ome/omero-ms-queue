package ome.smuggler.providers.q;

import static org.mockito.Mockito.*;

import kew.core.qchan.spi.QMsgFactory;
import org.apache.activemq.artemis.api.core.ActiveMQBuffer;
import org.apache.activemq.artemis.api.core.ActiveMQException;
import org.apache.activemq.artemis.api.core.client.ClientMessage;
import org.apache.activemq.artemis.api.core.client.ClientProducer;
import org.apache.activemq.artemis.api.core.client.ClientSession;
import org.junit.Before;
import org.junit.Test;


public class ArtemisQProducerTest {

    private ClientSession mockSession;
    private ClientProducer mockProducer;
    private QMsgFactory<ArtemisMessage> mockMsgFactory;
    private ArtemisQProducer target;

    @Before
    public void setUp() throws Exception {
        mockSession = mock(ClientSession.class);
        mockProducer = mock(ClientProducer.class);
        mockMsgFactory = mock(QMsgFactory.class);

        when(mockSession.createProducer()).thenReturn(mockProducer);
        target = new ArtemisQProducer(mockSession, mockMsgFactory);
    }

    @Test
    public void onlyEverUseOneQueueProducer() throws Exception {
        ClientMessage msg = mock(ClientMessage.class);
        ArtemisMessage adapter = new ArtemisMessage(msg);
        when(mockMsgFactory.durableMessage()).thenReturn(adapter);
        when(msg.getBodyBuffer()).thenReturn(mock(ActiveMQBuffer.class));

        target.sendMessage(QMsgFactory::durableMessage, out -> {});
        target.sendMessage(QMsgFactory::durableMessage, out -> {});
        verify(mockSession, times(1)).createProducer();
    }

    @Test (expected = NullPointerException.class)
    public void ctorThrowsIfNullSession() throws ActiveMQException {
        new ArtemisQProducer(null, mock(QMsgFactory.class));
    }

    @Test (expected = NullPointerException.class)
    public void ctorThrowsIfNullMsgFactory() throws ActiveMQException {
        new ArtemisQProducer(mock(ClientSession.class), null);
    }

    @Test (expected = NullPointerException.class)
    public void sendMessageThrowsIfNullMetaBuilder() throws Exception {
        target.sendMessage(null, out -> {});
    }

    @Test (expected = NullPointerException.class)
    public void sendMessageThrowsIfNullConsumer() throws Exception {
        target.sendMessage(QMsgFactory::durableMessage, null);
    }

}
