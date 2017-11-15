package kew.core.msg;

/**
 * Encapsulates the consumption of an asynchronous message received from a 
 * channel.
 */
public interface ChannelSink<T> {

    /**
     * Consumes a message.
     * @param data the message data output from the channel.
     * @throws NullPointerException if the argument is {@code null}.
     */
    void consume(T data);
    
}
