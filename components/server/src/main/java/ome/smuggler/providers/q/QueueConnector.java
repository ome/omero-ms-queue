package ome.smuggler.providers.q;

import static java.util.Objects.requireNonNull;

import org.apache.activemq.artemis.api.core.ActiveMQException;
import org.apache.activemq.artemis.api.core.client.ClientConsumer;
import org.apache.activemq.artemis.api.core.client.ClientMessage;
import org.apache.activemq.artemis.api.core.client.ClientProducer;
import org.apache.activemq.artemis.api.core.client.ClientSession;
import org.apache.activemq.artemis.core.config.CoreQueueConfiguration;

/**
 * Provides access to an Artemis queue.
 */
public class QueueConnector {

    private final CoreQueueConfiguration config;
    private final ClientSession session;
    
    /**
     * Creates a new instance to access the specified queue.
     * @param config the queue to access.
     * @param session the session to use to access the queue.
     * @throws NullPointerException if any argument is {@code null}.
     */
    public QueueConnector(CoreQueueConfiguration config, ClientSession session) {
        requireNonNull(config, "config");
        requireNonNull(session, "session");
        
        this.config = config;
        this.session = session;
    }
    
    /**
     * Creates a new consumer to fetch messages from the queue.
     * @return the consumer.
     * @throws ActiveMQException if the consumer could not be created.
     */
    public ClientConsumer newConsumer() throws ActiveMQException {
        return session.createConsumer(config.getName(), false);
    }
    
    /**
     * Creates a new consumer to receive messages from the queue without 
     * removing them.
     * @return the consumer.
     * @throws ActiveMQException if the consumer could not be created.
     */
    public ClientConsumer newBrowser() throws ActiveMQException {
        return session.createConsumer(config.getName(), true);
    }
    
    /**
     * Creates a new producer to put messages on the queue.
     * @return the producer.
     * @throws ActiveMQException if the producer could not be created.
     */
    public ClientProducer newProducer() throws ActiveMQException {
        return session.createProducer(config.getAddress());
    }
    
    /**
     * Creates a new durable message.
     * @return the message.
     */
    public ClientMessage newDurableMessage() {
        return session.createMessage(true);
    }
    
    /**
     * @return the session in use to access the queue.
     */
    public ClientSession getSession() {
        return session;
    }
    
    /**
     * @return configuration of the queue to access.
     */
    public CoreQueueConfiguration getConfig() {
        return config;
    }
    
}
