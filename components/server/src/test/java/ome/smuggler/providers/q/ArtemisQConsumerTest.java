package ome.smuggler.providers.q;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

import org.apache.activemq.artemis.api.core.ActiveMQBuffer;
import org.apache.activemq.artemis.api.core.ActiveMQException;
import org.apache.activemq.artemis.api.core.client.ClientConsumer;
import org.apache.activemq.artemis.api.core.client.ClientMessage;
import org.junit.Before;
import org.junit.Test;
import util.lambda.BiConsumerE;

import java.io.ByteArrayInputStream;
import java.io.InputStream;


public class ArtemisQConsumerTest {

    private ClientConsumer mockConsumer;
    private ArtemisQConsumer target;

    private ArtemisMessage receivedMessage;
    private InputStream receivedBody;

    void handleMessage(ArtemisMessage msg, InputStream msgBody) {
        receivedMessage = msg;
        receivedBody = msgBody;
    }

    @Before
    public void setup() throws ActiveMQException {
        mockConsumer = mock(ClientConsumer.class);
        target = new ArtemisQConsumer(mockConsumer, this::handleMessage);
        receivedMessage = null;
        receivedBody = null;
    }

    @Test
    public void ctorSetsMessageHandler() throws ActiveMQException {
        verify(mockConsumer, times(1)).setMessageHandler(target);
    }

    @Test
    public void canUseMessageHandler() {
        BiConsumerE<ArtemisMessage, InputStream> handler =
                target.messageHandler();

        assertNotNull(handler);

        ClientMessage msg = mock(ClientMessage.class);
        ArtemisMessage adapter = new ArtemisMessage(msg);
        InputStream body = new ByteArrayInputStream(new byte[0]);
        handler.accept(adapter, body);

        assertThat(receivedMessage, is(adapter));
        assertThat(receivedBody, is(body));
    }

    @Test
    public void onMessageForwardsToMessageHandler() {
        ClientMessage msg = mock(ClientMessage.class);
        when(msg.getBodyBuffer()).thenReturn(mock(ActiveMQBuffer.class));

        target.onMessage(msg);

        assertNotNull(receivedMessage);
        assertNotNull(receivedBody);
    }

    @Test (expected = NullPointerException.class)
    public void ctorThrowsIfNullConsumer() throws ActiveMQException {
        new ArtemisQConsumer(null, (m, d) -> {});
    }

    @Test (expected = NullPointerException.class)
    public void ctorThrowsIfNullHandler() throws ActiveMQException {
        new ArtemisQConsumer(mockConsumer, null);
    }

}
