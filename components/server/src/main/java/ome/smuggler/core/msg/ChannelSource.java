package ome.smuggler.core.msg;

import static util.error.Exceptions.unchecked;

/**
 * Encapsulates the sending of an asynchronous message along a channel. 
 */
public interface ChannelSource<T> {

    /**
     * Inputs data into the channel and returns immediately.
     * This is an asynchronous operation, so that the sender never waits for
     * the receiver to get the data.
     * @param data the message to send.
     * @throws NullPointerException if the input is {@code null}.
     * @throws Exception if the data could not be input into the channel.
     */
    void send(T data) throws Exception;
    
    /**
     * Same as {@link #send(Object) send} but masks any exception as a runtime 
     * (unchecked) exception and throws it as such without any wrapping.
     * @param data the message to send.
     */
    default void uncheckedSend(T data) {
        unchecked(this::send).accept(data);
    }
    
}
