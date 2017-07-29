package kew.core.qchan.spi;

import java.io.OutputStream;

import util.lambda.ConsumerE;

/**
 * We expect the underlying messaging middleware to support the sending of
 * messages on a queue as specified by this interface.
 * @param <QM> the message type in the underlying middleware.
 */
public interface QProducer<QM> {

    /**
     * Puts a message on the queue this producer is connected to.
     * @param metadataBuilder builds the message to put on the queue with
     *                        its associated metadata.
     * @param payloadWriter writes the message data, i.e. the actual message
     *                      content, to the given output stream.
     * @throws NullPointerException if any argument is {@code null}.
     * @throws Exception if an error occurs while sending the message.
     * @see QConnector
     */
    void sendMessage(QMsgBuilder<QM> metadataBuilder,
                     ConsumerE<OutputStream> payloadWriter)
            throws Exception;

}
