package kew.core.msg;

import static java.util.Objects.requireNonNull;
import static kew.core.msg.ChannelMessage.message;

/**
 * A channel source that uses an underlying message source to send data items
 * {@code D}. The underlying messages will all have empty metadata.
 * @see MessageSource
 */
public class ChannelSourceAdapter<M, D> implements ChannelSource<D> {

    private final MessageSource<M, D> channel;

    /**
     * Creates a new instance.
     * @param channel the underlying message source.
     * @throws NullPointerException if the argument is {@code null}.
     */
    public ChannelSourceAdapter(MessageSource<M, D> channel) {
        requireNonNull(channel, "channel");
        this.channel = channel;
    }

    @Override
    public void send(D data) throws Exception {
        channel.send(message(data));
    }

}
