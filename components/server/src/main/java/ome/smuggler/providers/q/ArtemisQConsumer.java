package ome.smuggler.providers.q;

import static java.util.Objects.requireNonNull;
import static ome.smuggler.providers.q.MessageBodyReader.readBody;

import java.io.InputStream;

import org.apache.activemq.artemis.api.core.ActiveMQException;
import org.apache.activemq.artemis.api.core.client.ClientConsumer;
import org.apache.activemq.artemis.api.core.client.ClientMessage;
import org.apache.activemq.artemis.api.core.client.MessageHandler;

import kew.core.qchan.spi.QConsumer;
import util.lambda.BiConsumerE;

/**
 * Fetches messages from an Artemis queue and dispatches them to a message
 * handler.
 */
public class ArtemisQConsumer
        implements QConsumer<ArtemisMessage>, MessageHandler {

    private final ClientConsumer consumer;
    private final BiConsumerE<ArtemisMessage, InputStream> messageHandler;

    private ArtemisQConsumer(ClientConsumer consumer,
                             BiConsumerE<ArtemisMessage, InputStream> handler) {
        this.consumer = consumer;
        this.messageHandler = handler;
    }

    /**
     * Helper constructor called to instantiate a temp object from which to
     * get the actual instance with a proper handler set.
     * @param consumer the underlying Artemis consumer.
     * @throws NullPointerException if the argument is {@code null}.
     */
    ArtemisQConsumer(ClientConsumer consumer) {
        requireNonNull(consumer, "consumer");

        this.consumer = consumer;
        messageHandler = (m, d) -> {};
    }

    @Override
    public QConsumer<ArtemisMessage> withMessageHandler(
            BiConsumerE<ArtemisMessage, InputStream> handler)
            throws ActiveMQException {
        requireNonNull(handler, "handler");

        ArtemisQConsumer qConsumer = new ArtemisQConsumer(consumer, handler);
        consumer.setMessageHandler(qConsumer);
        return qConsumer;
    }

    @Override
    public void onMessage(ClientMessage source) {
        ArtemisMessage msg = new ArtemisMessage(source);
        InputStream body = readBody(source);
        messageHandler.accept(msg, body);
    }

}
