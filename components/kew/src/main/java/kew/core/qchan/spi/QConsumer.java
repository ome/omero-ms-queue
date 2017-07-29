package kew.core.qchan.spi;

import util.lambda.ConsumerE;

/**
 * We expect the underlying messaging middleware to support the receiving of
 * messages on a queue as specified by this interface.
 * @param <QM> the message type in the underlying middleware.
 */
public interface QConsumer<QM> {

    /**
     * Makes the consumer pass the received message to the specified handler
     * for processing.
     * @param handler handles a queued messaged received by the underlying
     *                messaging middleware consumer.
     * @throws NullPointerException if the argument is {@code null}.
     * @return reference to self, just out of convenience.
     * @see QConnector
     */
    QConsumer<QM> withMessageHandler(ConsumerE<QM> handler);

}
