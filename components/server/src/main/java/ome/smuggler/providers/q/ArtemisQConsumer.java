package ome.smuggler.providers.q;

import static java.util.Objects.requireNonNull;
import static ome.smuggler.providers.q.MessageBodyReader.readBody;

import java.io.InputStream;

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

    private final BiConsumerE<ArtemisMessage, InputStream> messageHandler;

    /**
     *
     * @param handler
     * @throws NullPointerException if the argument is {@code null}.
     */
    public ArtemisQConsumer(BiConsumerE<ArtemisMessage, InputStream> handler) {
        requireNonNull(handler, "handler");
        this.messageHandler = handler;
    }

    @Override
    public BiConsumerE<ArtemisMessage, InputStream> messageHandler() {
        return messageHandler;
    }

    @Override
    public void onMessage(ClientMessage source) {
        ArtemisMessage msg = new ArtemisMessage(source);
        InputStream body = readBody(source);
        messageHandler.accept(msg, body);
    }

}
