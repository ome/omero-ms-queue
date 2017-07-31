package kew.core.qchan.spi;

import java.io.InputStream;

import util.lambda.BiConsumerE;

/**
 * Provides access to a queue in the underlying messaging middleware.
 * @param <QM> the message type in the underlying middleware.
 */
public interface QConnector<QM extends HasReceiptAck> {  // (1)

    /**
     * Creates a new consumer to fetch messages from this queue.
     * The message is kept in the queue until the provided message handler
     * explicitly {@link HasReceiptAck#removeFromQueue() removes} it. If the
     * handler doesn't remove it, the message will be redelivered the next
     * time a connector binds to this queue.
     * @param messageHandler handles a queued messaged received by the
     *                underlying messaging middleware consumer. The actual
     *                message content is passed to the handler in an input
     *                stream.
     * @throws NullPointerException if the argument is {@code null}.
     * @throws Exception if the underlying middleware consumer could not be
     * created.
     */
    QConsumer<QM> newConsumer(BiConsumerE<QM, InputStream> messageHandler)
            throws Exception;

    /**
     * Creates a new consumer to browse messages on this queue.
     * Messages processed by the provided message handler won't be removed
     * from the queue.
     * @param messageHandler handles a queued messaged received by the
     *                underlying messaging middleware consumer. The actual
     *                message content is passed to the handler in an input
     *                stream.
     * @throws NullPointerException if the argument is {@code null}.
     * @throws Exception if the underlying middleware consumer could not be
     * created.
     */
    QConsumer<QM> newBrowser(BiConsumerE<QM, InputStream> messageHandler)
        throws Exception;

    /**
     * Creates a new producer to put messages on this queue.
     * @return the producer.
     * @throws Exception if the producer could not be created.
     */
    QProducer<QM> newProducer() throws Exception;

}
/* NOTES.
 * 1. HasReceiptAck. It's only applicable to newConsumer method, but couldn't
 * find an easy way to enforce it. I tried scoping the type param at the
 * method level:
 *
 *     interface QConnector {  // NB no type param here
 *         <QM> QConsumer<QM extends HasReceiptAck> newConsumer(...
 *         <QM> QConsumer<QM> newBrowser(...
 *
 * but I failed miserably. In fact, when I then tried to implement the
 * interface, the compiler moaned: both methods have same erasure but
 * neither overrides the other.
 */