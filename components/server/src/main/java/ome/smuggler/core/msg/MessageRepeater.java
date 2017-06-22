package ome.smuggler.core.msg;

import static java.util.Objects.requireNonNull;
import static ome.smuggler.core.msg.RepeatAction.Repeat;
import static util.sequence.Arrayz.hasNulls;

import java.time.Duration;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Stream;

import ome.smuggler.core.types.FutureTimepoint;
import ome.smuggler.core.types.Schedule;

/**
 * Plugs into a {@link ReschedulingSink} to deliver the same message to sink's
 * consumers multiple times at predefined intervals for a finite number of 
 * times.
 * <p>A repeater is configured with a consumer and a sequence of {@code n > 0} 
 * {@link Duration}s {@code d[0], .., d[n-1]}. When the first message {@code m}
 * comes in at time {@code t0}, it is delivered to the consumer. If the consumer
 * returns {@link RepeatAction#Stop Stop} no further action is taken; if it
 * returns {@link RepeatAction#Repeat Repeat} instead, then {@code m} will be
 * delivered again to the consumer at time {@code t1 = t0 + d[0]}. When given 
 * the message again at time {@code t1}, the consumer can either {@link 
 * RepeatAction#Stop Stop} or {@link RepeatAction#Repeat Repeat}, in which case
 * {@code m} will be delivered again to the consumer at time {@code t2 = t1 + 
 * d[1]}. And so on for at most {@code n} deliveries, past which point {@code m}
 * is given to a configured exceeded re-delivery handler.
 * </p>
 * <p>A typical use case for a repeater is that of retrying failed actions. 
 * The consumer would carry out some task using the message as input. If a
 * transient error occurs, the consumer would ask to {@link RepeatAction#Repeat 
 * repeat} the delivery later; if the error is permanent the consumer would just
 * {@link RepeatAction#Stop stop} instead. The consumer can retry up to {@code 
 * n} times; past that, the message is fed into the exceeded re-delivery handler
 * which would be some kind of permanent failure handler in this scenario.    
 * </p>
 */
public class MessageRepeater<T> implements Reschedulable<T> {

    private static Duration[] collectIntervals(Stream<Duration> xs) {
        Duration[] intervals = xs.toArray(Duration[]::new);
        if (hasNulls(intervals)) {
            throw new IllegalArgumentException("repeat intervals has nulls");
        }
        return intervals;
    }
    
    private final RepeatConsumer<T> consumer;
    private final Duration[] repeatIntervals;
    private final Reschedulable<T> lastDeliveryHandler;

    /**
     * Creates a new instance.
     * @param consumer consumes the message output from the channel and returns
     * an indication of whether the same message should be delivered again. 
     * @param repeatIntervals intervals at which to re-deliver the message. 
     * @param exceededRedeliveryHandler is given the message after {@code n} 
     * re-deliveries, where {@code n} is the number of repeat intervals.
     * @throws NullPointerException if any argument is {@code null}.
     * @throws IllegalArgumentException if the retry intervals stream is empty
     * or contains {@code null}.
     */
    public MessageRepeater(RepeatConsumer<T> consumer,
                           Stream<Duration> repeatIntervals,
                           Consumer<T> exceededRedeliveryHandler) {
        requireNonNull(consumer, "consumer");
        requireNonNull(repeatIntervals, "repeatIntervals");
        requireNonNull(exceededRedeliveryHandler, "exceededRedeliveryHandler");
        
        this.consumer = consumer;
        this.repeatIntervals = collectIntervals(repeatIntervals);
        this.lastDeliveryHandler = ReschedulableFactory.buildOnceOffSchedule(
                                        consumer, exceededRedeliveryHandler);
    }
    
    private Optional<Integer> deliver(CountedSchedule current, T data) {
        int retryCount = current.count().get().intValue() - 1;  // PositiveN is always > 0
        if (retryCount < repeatIntervals.length) {
            RepeatAction outcome = consumer.consume(data);
            return outcome == Repeat ? Optional.of(retryCount) : 
                                       Optional.empty();
        } else {
            lastDeliveryHandler.consume(current, data);
            return Optional.empty();
        }
    }
    
    private FutureTimepoint nextRetryTime(int retryCount) {
        return new FutureTimepoint(repeatIntervals[retryCount]);
    }
    
    @Override
    public Optional<Schedule<T>> consume(CountedSchedule current, T data) {
        return deliver(current, data)
              .map(this::nextRetryTime)
              .map(when -> new Schedule<>(when, data));
    }

}
/* NOTE. Why do this when HornetQ supports delayed re-delivery?!
 * Before you send WTH's flying all over, know that at least I tried it, 
 * but there was some dodginess I couldn't quite figure out, eventually
 * ran out of debug cycles I could use on this, so decided to implement
 * re-delivery myself.
 * 
 * For the record, this is what I initially attempted:
 * 
 * 1. Change ServerConnector to create a transacted session.
 * 2. EnqueueTask commits the session after sending the message.
 * 3. DequeueTask rolls back if an the consumer says so.
 * 
 * The code was basically lifted from the HornetQ "delayed re-delivery" and
 * and "transactional" examples. It all seemed to work fine, except for the
 * fact that rolling back was taking ages (i.e. calling the session's rollback
 * method within onMessage) and HornetQ kept on telling me there was something
 * wrong:
 * 
 * + HQ212002: Timed out waiting for handler to complete processing
 * 
 * Okay, now it's my turn for the WTH...
 * 
 * Another thing I didn't have time to test is what messages are rolled back.
 * What happens if producers and consumers share the *same* session---i.e. our
 * set up? Say messages m1 and m2 are on the queue, m1 is delivered to a 
 * consumer which rolls back while m2 still sits on the queue. What is the fate
 * of m2?
 */