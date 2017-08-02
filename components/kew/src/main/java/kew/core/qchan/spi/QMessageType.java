package kew.core.qchan.spi;

/**
 * Kinds of messages we expect the underlying middleware to support.
 */
public enum QMessageType {

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
