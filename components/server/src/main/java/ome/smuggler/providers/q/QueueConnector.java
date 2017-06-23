package ome.smuggler.providers.q;

import static java.util.Objects.requireNonNull;

import org.hornetq.api.core.HornetQException;
import org.hornetq.api.core.client.ClientConsumer;
import org.hornetq.api.core.client.ClientMessage;
import org.hornetq.api.core.client.ClientProducer;
import org.hornetq.api.core.client.ClientSession;
import org.hornetq.core.config.CoreQueueConfiguration;

/**
 * Provides access to a HornetQ queue.
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
     * @throws HornetQException if the consumer could not be created.
     */
    public ClientConsumer newConsumer() throws HornetQException {
        return session.createConsumer(config.getName(), false);
    }
    
    /**
     * Creates a new consumer to receive messages from the queue without 
     * removing them.
     * @return the consumer.
     * @throws HornetQException if the consumer could not be created.
     */
    public ClientConsumer newBrowser() throws HornetQException {
        return session.createConsumer(config.getName(), true);
    }
    
    /**
     * Creates a new producer to put messages on the queue.
     * @return the producer.
     * @throws HornetQException if the producer could not be created.
     */
    public ClientProducer newProducer() throws HornetQException {
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
