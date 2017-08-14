package kew.providers.artemis.qchan;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

import kew.core.qchan.spi.QConsumer;
import kew.core.qchan.spi.QProducer;
import org.apache.activemq.artemis.api.core.ActiveMQException;
import org.apache.activemq.artemis.api.core.client.ClientConsumer;
import org.apache.activemq.artemis.api.core.client.ClientMessage;
import org.apache.activemq.artemis.api.core.client.ClientProducer;
import org.apache.activemq.artemis.api.core.client.ClientSession;
import org.apache.activemq.artemis.core.config.CoreQueueConfiguration;
import org.junit.Before;
import org.junit.Test;

public class ArtemisQConnectorTest {

    private CoreQueueConfiguration mockConfig;
    private ClientSession mockSession;
    private ArtemisQConnector target;

    @Before
    public void setup() throws ActiveMQException {
        mockConfig = mock(CoreQueueConfiguration.class);
        mockSession = mock(ClientSession.class);
        target = new ArtemisQConnector(mockConfig, mockSession);

        when(mockSession.createConsumer(anyString(), anyBoolean()))
                .thenReturn(mock(ClientConsumer.class));
        when(mockSession.createProducer(anyString()))
                .thenReturn(mock(ClientProducer.class));
    }

    @Test
    public void newConsumerNeverReturnsNull() throws ActiveMQException {
        QConsumer<ArtemisMessage> consumer = target.newConsumer((m, d) -> {});
        assertNotNull(consumer);
    }

    @Test
    public void newBrowserNeverReturnsNull() throws ActiveMQException {
        QConsumer<ArtemisMessage> consumer = target.newBrowser((m, d) -> {});
        assertNotNull(consumer);
    }

    @Test
    public void newProducerNeverReturnsNull() throws ActiveMQException {
        QProducer<ArtemisMessage> producer = target.newProducer();
        assertNotNull(producer);
    }

    @Test
    public void buildDurableMessage() {
        ClientMessage msg = mock(ClientMessage.class);
        when(mockSession.createMessage(true)).thenReturn(msg);

        ArtemisMessage adapter = target.durableMessage();
        assertNotNull(adapter);
        assertThat(adapter.message(), is(msg));
    }

    @Test
    public void buildNonDurableMessage() {
        ClientMessage msg = mock(ClientMessage.class);
        when(mockSession.createMessage(false)).thenReturn(msg);

        ArtemisMessage adapter = target.nonDurableMessage();
        assertNotNull(adapter);
        assertThat(adapter.message(), is(msg));
    }

    @Test (expected = NullPointerException.class)
    public void ctorThrowsIfNullConfig() {
        new ArtemisQConnector(null, mockSession);
    }

    @Test (expected = NullPointerException.class)
    public void ctorThrowsIfNullSession() {
        new ArtemisQConnector(null, mockSession);
    }

    @Test (expected = NullPointerException.class)
    public void newConsumerThrowsIfNullHandler() throws ActiveMQException {
        target.newConsumer(null);
    }

    @Test (expected = NullPointerException.class)
    public void newBrowserThrowsIfNullHandler() throws ActiveMQException {
        target.newBrowser(null);
    }

    @Test (expected = NullPointerException.class)
    public void queueMessageThrowsIfNullMsgType() throws ActiveMQException {
        target.queueMessage(null);
    }

}
