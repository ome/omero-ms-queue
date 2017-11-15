package kew.core.qchan.spi;

import java.io.InputStream;

import util.lambda.BiConsumerE;


/**
 * Fetches messages from a queue and dispatches them to a message handler.
 * An interface behind which to hide the actual queue consumer provided by
 * the underlying messaging middleware.
 * @param <QM> the message type in the underlying middleware.
 */
public interface QConsumer<QM> {

    /**
     * Returns the handler that processes queued messages received by the
     * underlying messaging middleware consumer.
     * @return the handler.
     * @see QConnector#newConsumer(BiConsumerE)
     * @see QConnector#newBrowser(BiConsumerE)
     */
    BiConsumerE<QM, InputStream> messageHandler();

}
