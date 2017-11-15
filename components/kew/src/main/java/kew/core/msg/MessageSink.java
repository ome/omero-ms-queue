package kew.core.msg;

import static java.util.Objects.requireNonNull;
import static kew.core.msg.ChannelMessage.message;

/**
 * Encapsulates the consumption of an asynchronous message received from a 
 * channel.  Messages have data to consume and possibly associated metadata.
 * @see ChannelMessage
 * @see MessageSource
 */
public interface MessageSink<M, D> extends ChannelSink<ChannelMessage<M, D>> {

    /**
     * Builds a message sink that forwards message data to the given consumer,
     * while discarding any message metadata.
     * @param <M> the metadata type.
     * @param <D> the data type.
     * @param target consumes message data.
     * @return a message sink adapter.
     * @throws NullPointerException if the argument is {@code null}.
     */
    static <M, D> MessageSink<M, D> forwardDataTo(ChannelSink<D> target) {
        requireNonNull(target, "target");
        return msg -> target.consume(msg.data());
    }
    
    /**
     * Builds a channel sink that uses this message sink to consume data items
     * {@code D}. The messages forwarded to the underlying consumer will all 
     * have empty metadata as any received metadata is discarded.
     * @return a channel sink adapter.
     */
    default ChannelSink<D> asDataSink() {
        return data -> consume(message(data));
    }
    
}
