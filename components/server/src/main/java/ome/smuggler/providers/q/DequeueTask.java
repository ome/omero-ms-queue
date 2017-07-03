package ome.smuggler.providers.q;

import static java.util.Objects.requireNonNull;
import static ome.smuggler.core.msg.ChannelMessage.message;
import static util.error.Exceptions.throwAsIfUnchecked;

import java.io.InputStream;

import org.apache.activemq.artemis.api.core.ActiveMQException;
import org.apache.activemq.artemis.api.core.client.ClientConsumer;
import org.apache.activemq.artemis.api.core.client.ClientMessage;
import org.apache.activemq.artemis.api.core.client.MessageHandler;

import ome.smuggler.core.convert.SourceReader;
import ome.smuggler.core.msg.ChannelSink;
import ome.smuggler.core.msg.MessageSink;

/**
 * Receives messages asynchronously from a queue and dispatches them to a 
 * consumer.
 */
public class DequeueTask<T> implements MessageHandler {
    
    private final ClientConsumer receiver;
    private final MessageSink<ClientMessage, T> sink;
    private final boolean redeliverOnCrash;
    private final MessageBodyReader<T> bodyReader;
    
    /**
     * Creates a new instance.
     * @param queue provides access to the queue from which to fetch messages.
     * @param consumer consumes message data fetched from the queue.
     * @param deserializer de-serialises the message data, a {@code T}-value.
     * @param redeliverOnCrash if {@code true} and the process terminates 
     * abnormally (e.g. segfault, power failure) while the consumer is busy 
     * processing a message, the message will be delivered again once the
     * process is rebooted. If {@code false}, a message will only ever be 
     * delivered once to the consumer.
     * @throws ActiveMQException if an error occurs while setting up Artemis to
     * receive messages on the specified queue.
     * @throws NullPointerException if any argument is {@code null}.
     */
    public DequeueTask(QueueConnector queue,
                       ChannelSink<T> consumer,
                       SourceReader<InputStream, T> deserializer,
                       boolean redeliverOnCrash)
            throws ActiveMQException {
        this(queue, MessageSink.forwardDataTo(consumer), deserializer,
             redeliverOnCrash);
    }
    
    /**
     * Creates a new instance.
     * @param queue provides access to the queue from which to fetch messages.
     * @param consumer consumes message data and metadata fetched from the queue.
     * @param redeliverOnCrash if {@code true} and the process terminates 
     * abnormally (e.g. segfault, power failure) while the consumer is busy 
     * processing a message, the message will be delivered again once the
     * process is rebooted. If {@code false}, a message will only ever be 
     * delivered once to the consumer.
     * @param deserializer de-serialises the message data, a {@code T}-value.
     * @throws ActiveMQException if an error occurs while setting up Artemis to
     * receive messages on the specified queue.
     * @throws NullPointerException if any argument is {@code null}.
     */
    public DequeueTask(QueueConnector queue,
                       MessageSink<ClientMessage, T> consumer,
                       SourceReader<InputStream, T> deserializer,
                       boolean redeliverOnCrash)
            throws ActiveMQException {
        requireNonNull(queue, "queue");
        requireNonNull(consumer, "consumer");

        this.sink = consumer;
        this.receiver = queue.newConsumer();
        this.receiver.setMessageHandler(this);
        this.redeliverOnCrash = redeliverOnCrash;
        this.bodyReader = new MessageBodyReader<>(deserializer);
    }
    
    private void removeFromQueue(ClientMessage msg) {
        try {
            msg.acknowledge();
        } catch (ActiveMQException e) {
            throwAsIfUnchecked(e);
        }
    }
    
    private void consumeThenRemove(ClientMessage msg, T messageData) {
        try {
            sink.consume(message(msg, messageData));  // (*)
        } finally {
            removeFromQueue(msg);
        }
    }
    /* NOTE. If the process dies here, the message is still in the queue as it
     * hasn't been acknowledged yet. Artemis will deliver it again on reboot.
     * In fact, this works exactly the same as it used to in HornetQ.
     * See:
     * - http://stackoverflow.com/questions/15243991/what-happen-if-client-acknowledgment-not-done
     */
    
    private void removeThenConsume(ClientMessage msg, T messageData) {
        removeFromQueue(msg);
        sink.consume(message(msg, messageData));
    }
    
    @Override
    public void onMessage(ClientMessage msg) {
        T messageData = bodyReader.uncheckedRead(msg);
        if (redeliverOnCrash) {
            consumeThenRemove(msg, messageData);
        } else {
            removeThenConsume(msg, messageData);
        }
    }
    
}
