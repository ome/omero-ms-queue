package ome.smuggler.core.msg;

import static java.util.Objects.requireNonNull;

import java.util.Objects;
import java.util.Optional;

/**
 * Imposes a minimal structure to a message exchanged on a channel.
 * This represents a common use case where the data to be consumed by the 
 * receiving end is possibly accompanied by metadata. Typically metadata is
 * used to configure the sending of a message and/or to specify additional 
 * message properties. 
 */
public class ChannelMessage<M, D> {

    /**
     * Syntactic sugar for the {@link #ChannelMessage(Optional, Object)  
     * two-argument constructor}.
     * @param <M> the metadata type.
     * @param <D> the data type.
     * @param metadata the optional metadata.
     * @param data the data.
     * @return a new instance.
     */
    public static <M, D> ChannelMessage<M, D> message(Optional<M> metadata, D data) { 
        return new ChannelMessage<>(metadata, data);
    }

    /**
     * Syntactic sugar for the {@link #ChannelMessage(Object, Object)  
     * two-argument constructor}.
     * @param <M> the metadata type.
     * @param <D> the data type.
     * @param metadata the metadata.
     * @param data the data.
     * @return a new instance.
     */
    public static <M, D> ChannelMessage<M, D> message(M metadata, D data) { 
        return new ChannelMessage<>(metadata, data);
    }
    
    /**
     * Syntactic sugar for the {@link #ChannelMessage(Object) one-argument 
     * constructor}.
     * @param <M> the metadata type.
     * @param <D> the data type.
     * @param data the data.
     * @return a new instance.
     */
    public static <M, D> ChannelMessage<M, D> message(D data) { 
        return new ChannelMessage<>(data);
    }
    
    
    private final Optional<M> metadata;
    private final D data;

    /**
     * Creates a new instance.
     * @param metadata any metadata accompanying the data to consume.
     * @param data the data to consume.
     * @throws NullPointerException if any argument is {@code null}.
     */
    public ChannelMessage(Optional<M> metadata, D data) {
        requireNonNull(metadata, "metadata");
        requireNonNull(data, "data");
        
        this.metadata = metadata;
        this.data = data;
    }
    
    /**
     * Creates a new instance.
     * @param metadata any metadata accompanying the data to consume.
     * @param data the data to consume.
     * @throws NullPointerException if any argument is {@code null}.
     */
    public ChannelMessage(M metadata, D data) {
        requireNonNull(metadata, "metadata");
        requireNonNull(data, "data");
        
        this.metadata = Optional.of(metadata);
        this.data = data;
    }

    /**
     * Creates a new instance with no metadata.
     * @param data the data to consume.
     * @throws NullPointerException if the argument is {@code null}.
     */
    public ChannelMessage(D data) {
        this(Optional.empty(), data);
    }
    
    /**
     * @return any metadata accompanying the {@link #data() data} to consume or
     * empty if the sender specified no metadata.
     */
    public Optional<M> metadata() {
        return metadata;
    }
    
    /**
     * @return the data to consume.
     */
    public D data() {
        return data;
    }
    
    @Override
    public boolean equals(Object x) {
        if (x == this) {  // avoid unnecessary work
            return true;
        }
        if (x instanceof ChannelMessage) {  // false if x == null or type differs
            ChannelMessage<?, ?> other = (ChannelMessage<?, ?>) x;  // best we can do, courtesy of type erasure
            return Objects.equals(this.metadata, other.metadata)    // crossing fingers their equals methods check
                && Objects.equals(this.data, other.data);           // the type is the same
        }
        return false;
    }
    /* NOTE. See also:
     * - http://www.angelikalanger.com/GenericsFAQ/FAQSections/ProgrammingIdioms.html#FAQ501
     */
    
    @Override
    public int hashCode() {
        return Objects.hash(metadata, data);
    }
    
}
/* NOTE. Design debt.
 * Need to put more thought into this metadata thingie. Typically it would 
 * relate to channel capabilities, e.g. being able to schedule messages, being
 * able to specify int props for metadata, etc. 
 * Because MessageSource ties a channel implementation to the kind of metadata 
 * it supports, it's not possible for senders to accidentally request features
 * not supported by the channel---as it can easily happen with JMS when setting
 * message props. 
 * Encoding this logic in the type system makes the code more robust. (Yay!) 
 * But, as it stands now my approach here is not composable, lending itself to 
 * tons of boilerplate. (Oh crap!)
 * In fact, consider an implementation that supports both scheduling and setting
 * int props in the metadata. We have three possible usage scenarios: client
 * only needs scheduling, or only int props, or both. Clark the bright spark
 * (erm, me!) decided we need a metadata class for each combination (e.g. look
 * at CountedSchedule) and a corresponding MessageSource. 
 * As the number of possible combinations grows, surely will also increase the 
 * frequency at which you're cursing me out.
 * A better approach would be to come up with a way to represent channel 
 * capabilities and a way to compose them while still using the type system
 * to make sure senders cannot use a channel that doesn't support the requested
 * features. Applicative functors and monads come to mind. Bingo!
 */