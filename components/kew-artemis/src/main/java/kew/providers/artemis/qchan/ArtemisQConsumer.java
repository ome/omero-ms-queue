package kew.providers.artemis.qchan;

import static java.util.Objects.requireNonNull;
import static kew.providers.artemis.qchan.MessageBodyReader.readBody;

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

    private final ClientConsumer consumer;  // keep ref to avoid GC nuking it.
    private final BiConsumerE<ArtemisMessage, InputStream> messageHandler;
    private final ArtemisSessionSynchronizer sessionSynchronizer;

    /**
     * Creates a new instance.
     * @param consumer the Artemis queue consumer.
     * @param messageHandler processes messages received by the consumer.
     * @param sessionSynchronizer serial access to the Artemis session.
     * @throws NullPointerException if any argument is {@code null}.
     * @throws ActiveMQException if the message handler couldn't be connected
     * to the consumer.
     */
    public ArtemisQConsumer(
            ClientConsumer consumer,
            BiConsumerE<ArtemisMessage, InputStream> messageHandler,
            ArtemisSessionSynchronizer sessionSynchronizer)
            throws ActiveMQException {
        requireNonNull(consumer, "consumer");
        requireNonNull(messageHandler, "messageHandler");
        requireNonNull(sessionSynchronizer, "sessionSynchronizer");

        this.consumer = consumer;
        this.messageHandler = messageHandler;
        this.sessionSynchronizer = sessionSynchronizer;

        consumer.setMessageHandler(this);
    }

    @Override
    public BiConsumerE<ArtemisMessage, InputStream> messageHandler() {
        return messageHandler;
    }

    @Override
    public void onMessage(ClientMessage source) {
        ArtemisMessage msg = new ArtemisMessage(source, sessionSynchronizer);
        InputStream body = readBody(source);
        messageHandler.accept(msg, body);
    }

}
