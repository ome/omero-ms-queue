package kew.core.qchan.spi;

/**
 * Specifies the types of messages we expect the underlying messaging
 * middleware can provide.
 * @param <QM> the message type in the underlying middleware.
 */
public interface QMsgFactory<QM> {

    /**
     * Kinds of messages we expect the underlying middleware to support.
     */
    enum MessageType {
        /**
         * Denotes a message that the underlying middleware will persist on
         * the queue as long as the queue itself is persistent. A durable
         * message is supposed to survive a crash and still be available for
         * delivery when the system comes back online.
         */
        Durable,
        /**
         * Denotes a message that the underlying middleware won't persist on
         * the queue. An undelivered, non-durable message won't survive a
         * crash.
         */
        NonDurable
    }

    /**
     * Creates a new queue message of the specified type.
     * @param t the message type.
     * @return a new message.
     */
    QM queueMessage(MessageType t);

    /**
     * Creates a new durable message.
     * @return the message.
     */
    default QM durableMessage() {
        return queueMessage(MessageType.Durable);
    }

    /**
     * Creates a new non-durable message.
     * @return the message.
     */
    default QM nonDurableMessage() {
        return queueMessage(MessageType.NonDurable);
    }

}
