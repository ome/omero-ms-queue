package kew.core.msg;

import static java.util.Objects.requireNonNull;

import java.time.Duration;
import java.util.List;
import java.util.function.Consumer;

/**
 * Builds {@link Reschedulable}s.
 */
public class ReschedulableFactory {

    /**
     * Assembles a {@link Reschedulable} that works with repeat messages.
     * This will be a {@link MessageRepeater} if the given list of repeat 
     * intervals is not empty; otherwise a {@link OnceOffSchedule} with a
     * {@link OnceOffRepeatConsumer} as a consumer.
     * @param <T> the reschedulable type.
     * @param consumer consumes the message output from the channel and returns
     * an indication of whether the same message should be delivered again. 
     * @param repeatIntervals intervals at which to re-deliver the message. 
     * @param exceededRedeliveryHandler is given the message after {@code n} 
     * re-deliveries, where {@code n} is the number of repeat intervals.
     * @return the assembled {@link Reschedulable}.
     * @throws NullPointerException if any argument is {@code null}.
     * @throws IllegalArgumentException if the retry intervals list is not empty
     * but contains {@code null}s.
     */
    public static <T> Reschedulable<T> buildForRepeatConsumer(
            RepeatConsumer<T> consumer,
            List<Duration> repeatIntervals,
            Consumer<T> exceededRedeliveryHandler) {
        requireNonNull(repeatIntervals, "repeatIntervals");
        
        if (repeatIntervals.isEmpty()) {
            return buildOnceOffSchedule(consumer, exceededRedeliveryHandler);
        } else {
            return new MessageRepeater<>(consumer, repeatIntervals.stream(), 
                                         exceededRedeliveryHandler);
        }
    }
    /* NOTE. MessageRepeater works with empty intervals too, but as we have  
     * OnceOffSchedule why not take advantage of it...
     */
    
    /**
     * Assembles a {@link Reschedulable} to deliver a channel message to the 
     * specified {@link RepeatConsumer} exactly once. If the consumer asks to
     * deliver the message again, the message is given to the exceeded delivery
     * handler instead of being put back on the channel.
     * @param <T> the reschedulable type.
     * @param consumer consumes the message output from the channel and returns
     * an indication of whether the same message should be delivered again. 
     * @param exceededRedeliveryHandler is given the message if the consumer
     * asks to re-deliver.
     * @return the assembled {@link Reschedulable}.
     * @throws NullPointerException if any argument is {@code null}.
     */
    public static <T> Reschedulable<T> buildOnceOffSchedule(
            RepeatConsumer<T> consumer, Consumer<T> exceededRedeliveryHandler) {
        return new OnceOffSchedule<>(
                    new OnceOffRepeatConsumer<>(consumer, 
                                                exceededRedeliveryHandler));
    }
    
}
