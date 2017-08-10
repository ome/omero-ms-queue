package kew.core.msg;

import static java.util.Objects.requireNonNull;
import static kew.core.msg.ChannelMessage.message;

/**
 * A channel sink that uses an underlying message sink to consume data items
 * {@code D}. The messages forwarded to the underlying consumer will all
 * have empty metadata as any received metadata is discarded.
 * @see MessageSink
 */
public class ChannelSinkAdapter<M, D> implements ChannelSink<D> {

    private final MessageSink<M, D> channel;

    /**
     * Creates a new instance.
     * @param channel the underlying message sink.
     * @throws NullPointerException if the argument is {@code null}.
     */
    public ChannelSinkAdapter(MessageSink<M, D> channel) {
        requireNonNull(channel, "channel");
        this.channel = channel;
    }

    @Override
    public void consume(D data) {
        channel.consume(message(data));
    }

}
