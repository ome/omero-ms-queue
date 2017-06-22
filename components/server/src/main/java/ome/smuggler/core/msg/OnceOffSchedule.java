package ome.smuggler.core.msg;

import static java.util.Objects.requireNonNull;

import java.util.Optional;
import java.util.function.Consumer;

import ome.smuggler.core.types.Schedule;

/**
 * Plugs into a {@link ReschedulingSink} to deliver messages to sink's consumers
 * exactly once.
 * @see MessageRepeater
 * @see OnceOffRepeatConsumer
 */
public class OnceOffSchedule<T> implements Reschedulable<T> {

    private final Consumer<T> consumer;

    /**
     * Creates a new instance.
     * @param consumer consumes the message output from the channel. 
     * @throws NullPointerException if the argument is {@code null}.
     */
    public OnceOffSchedule(Consumer<T> consumer) {
        requireNonNull(consumer, "consumer");
        this.consumer = consumer;
    }

    @Override
    public Optional<Schedule<T>> consume(CountedSchedule current, T data) {
        consumer.accept(data);
        return Optional.empty();
    }

}
