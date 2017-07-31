package ome.smuggler.providers.q;

import static java.util.Objects.requireNonNull;

import kew.core.qchan.spi.*;
import org.apache.activemq.artemis.api.core.client.ClientConsumer;
import org.apache.activemq.artemis.api.core.client.ClientMessage;
import org.apache.activemq.artemis.api.core.client.ClientProducer;
import org.apache.activemq.artemis.api.core.client.ClientSession;
import org.apache.activemq.artemis.core.config.CoreQueueConfiguration;

import util.lambda.BiConsumerE;

import java.io.InputStream;
import java.util.function.Function;


/**
 * Provides access to an Artemis queue as specified by the {@link QConnector}
 * interface.
 */
public class ArtemisQConnector
        implements QConnector<ArtemisMessage>, QMsgFactory<ArtemisMessage> {

    private final CoreQueueConfiguration config;
    private final ClientSession session;

    /**
     * Creates a new instance to access the specified queue.
     * @param config the queue to access.
     * @param session the session to use to access the queue.
     * @throws NullPointerException if any argument is {@code null}.
     */
    public ArtemisQConnector(CoreQueueConfiguration config,
                             ClientSession session) {
        requireNonNull(config, "config");
        requireNonNull(session, "session");

        this.config = config;
        this.session = session;
    }

    @Override
    public QConsumer<ArtemisMessage> newConsumer(
            BiConsumerE<ArtemisMessage, InputStream> messageHandler)
            throws Exception {
        ArtemisQConsumer adapter = new ArtemisQConsumer(messageHandler);
        ClientConsumer consumer =
                session.createConsumer(config.getName(), false);
        consumer.setMessageHandler(adapter);

        return adapter;
    }
    // TODO what happens when consumer gets out of scope?!
    // you definitely don't want it to be garbage-collected!
    @Override
    public QConsumer<ArtemisMessage> newBrowser(
            BiConsumerE<ArtemisMessage, InputStream> messageHandler)
            throws Exception {
        ArtemisQConsumer adapter = new ArtemisQConsumer(messageHandler);
        ClientConsumer consumer =
                session.createConsumer(config.getName(), true);
        consumer.setMessageHandler(adapter);

        return adapter;
    }

    @Override
    public QProducer<ArtemisMessage> newProducer() throws Exception {
        ClientProducer producer = session.createProducer(config.getAddress());
        return new ArtemisQProducer(producer, this);
    }

    @Override
    public ArtemisMessage queueMessage(MessageType t) {
        Function<Boolean, ClientMessage> create = session::createMessage;
        Function<Boolean, ArtemisMessage> builder =
                create.andThen(ArtemisMessage::new);  // (2)
        switch (t) {
            case Durable:
                return builder.apply(true);
            case NonDurable:
                return builder.apply(false);
            default:  // (1)
                throw new IllegalArgumentException("unsupported message type");
        }
    }
    /* NOTES
     * 1. Future proofing. Just in case we add a new enum constant and
     * forget about implementing the corresponding message builder here...
     * If only I could encode that (easily) in the type system! Pigs might
     * fly...
     * 2. Function composition. Couldn't be easier in Java, the best
     * (dis-)functional language in the West! Maybe I'm being a dumbass but
     * just couldn't find a one-liner for the above fun-comp without an
     * explicit cast. Never a dull moment when coding in Java!
     */
}
