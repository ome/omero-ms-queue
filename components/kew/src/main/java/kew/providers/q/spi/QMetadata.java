package kew.providers.q.spi;

import java.util.Optional;
import java.util.function.Function;

import kew.core.msg.CountedSchedule;
import util.types.FutureTimepoint;
import util.types.PositiveN;

/**
 * Methods to build and query metadata the underlying messaging middleware
 * uses to deliver messages.
 * This interface specifies what we expect of the underlying middleware:
 * <ul>
 *     <li>A simple key-value store associated to a message; and</li>
 *     <li>the ability to schedule message delivery.</li>
 * </ul>
 * @param <M> the type of the underlying metadata store. Typically this is
 *           the queue message's type as metadata is usually stored in the
 *           message itself.
 */
public interface QMetadata<M> {

    /**
     * A setter to specify a message schedule.
     * @param when time at which to deliver the message.
     * @return the setter.
     * @throws NullPointerException if the argument is {@code null}.
     */
    Function<M, M> setScheduledDeliveryTime(FutureTimepoint when);

    /**
     * Sets a key-value pair in the metadata store.
     * @param key the key.
     * @param value the associated value.
     * @return the setter.
     * @throws NullPointerException if any argument is {@code null}.
     */
    Function<M, M> setProp(String key, String value);

    /**
     * Sets a key-value pair in the metadata store.
     * @param key the key.
     * @param value the associated value.
     * @return the setter.
     * @throws NullPointerException if any argument is {@code null}.
     */
    Function<M, M> setProp(String key, long value);

    /**
     * Retrieves the value associated to the specified key.
     * @param store the metadata store.
     * @param key the lookup key.
     * @return the value associated to the specified key or empty if the key
     * is not bound to any value.
     * @throws NullPointerException if any argument is {@code null}.
     */
    Optional<String> getStringProp(M store, String key);

    /**
     * Retrieves the value associated to the specified key.
     * @param store the metadata store.
     * @param key the lookup key.
     * @return the value associated to the specified key or empty if the key
     * is not bound to any value.
     * @throws NullPointerException if any argument is {@code null}.
     */
    Optional<Long> getLongProp(M store, String key);


    // Derived methods. Putting them here out of convenience for now until
    // I find a better home for them!

    String ScheduleCountKey = CountedSchedule.class.getName() + "#count";

    default Function<M, M> setScheduleCount(PositiveN count) {
        return setProp(ScheduleCountKey, count.get());
    }

    default Optional<PositiveN> getScheduleCount(M store) {
        return getLongProp(store, ScheduleCountKey).map(PositiveN::of);
    }

}
