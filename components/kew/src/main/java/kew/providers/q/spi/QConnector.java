package kew.providers.q.spi;

/**
 * Provides access to a queue in the underlying messaging middleware.
 */
public interface QConnector {

    /**
     * Creates a new consumer to fetch messages from this queue.
     * The message is kept in the queue until the provided message handler
     * explicitly {@link HasReceiptAck#removeFromQueue() removes} it. If the
     * handler doesn't remove it, the message will be redelivered the next
     * time a connector binds to this queue.
     * @throws Exception if the underlying middleware consumer could not be
     * created.
     */
    <QM extends HasReceiptAck>
    QConsumer<QM> newConsumer() throws Exception;

    /**
     * Creates a new consumer to browse messages on this queue.
     * Messages processed by the provided message handler won't be removed
     * from the queue.
     * @throws Exception if the underlying middleware consumer could not be
     * created.
     */
    <QM> QConsumer<QM> newBrowser() throws Exception;

    /**
     * Creates a new producer to put messages on this queue.
     * @return the producer.
     * @throws Exception if the producer could not be created.
     */
    <QM> QProducer<QM> newProducer() throws Exception;

}
