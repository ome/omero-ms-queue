package kew.core.msg;

import static kew.core.msg.ChannelMessage.message;

/**
 * Encapsulates the sending of an asynchronous message along a channel that
 * supports metadata.
 * This channel source imposes a minimal structure to the data being sent over 
 * the channel, namely that of a message containing data meant to be consumed 
 * by the receiving end and optionally metadata, typically used to configure 
 * the sending of the message.
 * @see ChannelMessage
 * @see MessageSink
 */
public interface MessageSource<M, D> 
    extends ChannelSource<ChannelMessage<M, D>> {
    
    /**
     * Builds a channel source that uses this message source to send data items
     * {@code D}. The underlying messages will all have empty metadata.
     * @return a channel source adapter.
     */
    default ChannelSource<D> asDataSource() {
        return new ChannelSourceAdapter<>(this);
    }
    
}
