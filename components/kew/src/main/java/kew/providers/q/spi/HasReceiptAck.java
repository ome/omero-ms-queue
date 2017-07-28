package kew.providers.q.spi;

/**
 * Message receipt acknowledgement.
 * We expect the underlying messaging middleware to make available a facility
 * whereby consumers can acknowledge the receipt of a message. The middleware
 * delivers the message to the consumer but keeps it on the queue until the
 * consumer explicitly asks to {@link #removeFromQueue() remove} it. If the
 * consumer disconnects from the queue before removing the message, the
 * middleware will deliver it again to the next consumer that connects to
 * the queue.
 */
public interface HasReceiptAck {

    /**
     * Removes a consumed message from the queue.
     * @throws Exception if removal fails.
     */
    void removeFromQueue() throws Exception;

}
