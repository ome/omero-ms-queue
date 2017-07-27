package kew.providers.q.spi;

import java.util.function.Consumer;

/**
 * Provides access to a queue in the underlying messaging middleware.
 * @param <QM> the type of the queued message.
 */
public interface QueueConnector<QM> {

    /**
     * Creates a new consumer to fetch messages from this queue.
     * The message is kept in the queue until the provided message handler
     * explicitly {@link #removeFromQueue(Object) removes} it. If the handler
     * doesn't remove it, the message will be redelivered the next time a
     * connector binds to this queue.
     * @param messageHandler handles a queued messaged received by the
     *                       underlying messaging middleware consumer.
     * @throws NullPointerException if the argument is {@code null}.
     * @throws Exception if the underlying middleware consumer could not be
     * created.
     */
    void newReceiver(Consumer<QM> messageHandler) throws Exception;

    /**
     * Creates a new consumer to browse messages on this queue.
     * Messages processed by the provided message handler won't be removed
     * from the queue.
     * @param messageHandler handles a queued messaged received by the
     *                       underlying messaging middleware consumer.
     * @throws NullPointerException if the argument is {@code null}.
     * @throws Exception if the underlying middleware consumer could not be
     * created.
     */
    void newBrowser(Consumer<QM> messageHandler) throws Exception;

    /**
     * Creates a new producer to put messages on this queue.
     * @return the producer.
     * @throws Exception if the producer could not be created.
     */
    Consumer<QM> newSender() throws Exception;

    /**
     * Creates a new durable message that a producer can put on this queue.
     * @return the message.
     */
    QM newDurableMessage();

    /**
     * Removes a consumed message from the queue.
     * @param consumedMessage the message to remove.
     * @throws NullPointerException if the argument is {@code null}.
     * @throws Exception if removal fails.
     */
    void removeFromQueue(QM consumedMessage) throws Exception;

}
