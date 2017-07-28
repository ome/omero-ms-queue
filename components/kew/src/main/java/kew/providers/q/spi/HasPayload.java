package kew.providers.q.spi;

/**
 * Methods to read and write message data of the underlying messaging middleware
 * messages. We expect to be able to read and write the underlying message
 * payload as a byte array.
 */
public interface HasPayload {

    /**
     * Writes the message data to the underlying queue message.
     * @param data the message data.
     * @throws NullPointerException if the argument is {@code null}.
     */
    void writePayload(byte[] data);

    /**
     * Reads the message data from the underlying queue message.
     * @return the message data.
     */
    byte[] readPayload();

}
/* NOTE. Large messages.
 * This design is kinda lame cos it basically rules out large messages: you're
 * forced to hold the whole payload into memory when you write and suck the
 * whole thing back into memory when you read. At the moment we're not using
 * large messages and I have no dev cycles to spare that I could use to upgrade
 * this design to stream processing. But bear in mind it isn't an awful
 * complicated thing to do, e.g. we could use a simple array of arrays wire
 * representation and read/write one array at a time...
 */