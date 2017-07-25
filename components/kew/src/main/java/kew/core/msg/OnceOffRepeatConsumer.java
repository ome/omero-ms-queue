package kew.core.msg;

import static java.util.Objects.requireNonNull;
import static kew.core.msg.RepeatAction.Repeat;

import java.util.function.Consumer;

/**
 * Makes message consumers returning {@link RepeatAction}s work well when used
 * for once-off schedules.
 * @see ReschedulingSink
 * @see MessageRepeater
 * @see OnceOffSchedule
 */
public class OnceOffRepeatConsumer<T> implements Consumer<T> {

    private final RepeatConsumer<T> consumer;
    private final Consumer<T> exceededRedeliveryHandler;

    /**
     * Creates a new instance.
     * @param consumer consumes the message output from the channel and returns
     * an indication of whether the same message should be delivered again. 
     * @param exceededRedeliveryHandler is given the message if the consumer
     * asks to re-deliver.
     * @throws NullPointerException if any argument is {@code null}.
     */
    public OnceOffRepeatConsumer(RepeatConsumer<T> consumer,
                                 Consumer<T> exceededRedeliveryHandler) {
        requireNonNull(consumer, "consumer");
        requireNonNull(exceededRedeliveryHandler, "exceededRedeliveryHandler");
        
        this.consumer = consumer;
        this.exceededRedeliveryHandler = exceededRedeliveryHandler;
    }

    @Override
    public void accept(T data) {
        RepeatAction outcome = consumer.consume(data);
        if (outcome == Repeat) {
            exceededRedeliveryHandler.accept(data);
        }
    }
    
}
