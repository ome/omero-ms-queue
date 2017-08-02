package kew.core.qchan.spi;

/**
 * Specifies the types of messages we expect the underlying messaging
 * middleware can provide.
 * @param <QM> the message type in the underlying middleware.
 */
public interface QMsgFactory<QM> {

    /**
     * Creates a new queue message of the specified type.
     * If the implementation doesn't support durable messages then it should
     * fall back to creating a non-durable message.
     * @param t the message type.
     * @return a new message.
     * @throws NullPointerException if the argument is {@code null}.
     */
    QM queueMessage(QMessageType t);

    /**
     * Creates a new durable message.
     * If the implementation doesn't support durable messages then it should
     * fall back to creating a non-durable message.
     * @return the message.
     */
    default QM durableMessage() {
        return queueMessage(QMessageType.Durable);
    }

    /**
     * Creates a new non-durable message.
     * @return the message.
     */
    default QM nonDurableMessage() {
        return queueMessage(QMessageType.NonDurable);
    }

}
