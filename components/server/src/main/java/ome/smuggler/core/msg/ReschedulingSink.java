package ome.smuggler.core.msg;

import static java.util.Objects.requireNonNull;
import static ome.smuggler.core.msg.ChannelMessage.message;

import ome.smuggler.core.types.Schedule;

/**
 * A channel sink feeding the channel output to a task that may produce more 
 * input to be delivered on the channel at a later point in time.
 */
public class ReschedulingSink<T> implements MessageSink<CountedSchedule, T> {

    private final Reschedulable<T> task;
    private final MessageSource<CountedSchedule, T> loopback;
    
    /**
     * Creates a new instance.
     * @param task consumes the channel output and optionally produces more
     * data to input into the channel with an indication of when the data 
     * should be delivered to channel's sinks.
     * @param loopback where to put data (if any) produced by the task; normally
     * this would be the same channel this sink is bound to, i.e. the same
     * channel from which the data was received.
     * @throws NullPointerException if any argument is {@code null}.
     */
    public ReschedulingSink(Reschedulable<T> task,
                            MessageSource<CountedSchedule, T> loopback) {
        requireNonNull(loopback, "loopback");
        requireNonNull(task, "task");
        
        this.loopback = loopback;
        this.task = task;
    }
    
    private void reschedule(Schedule<T> nextDelivery, CountedSchedule last) {
        CountedSchedule delivery = last.next(nextDelivery.when());
        T data = nextDelivery.what();
        loopback.uncheckedSend(message(delivery, data));
    }

    @Override
    public void consume(ChannelMessage<CountedSchedule, T> msg) {
        CountedSchedule current = msg.metadata()
                                     .orElse(CountedSchedule.first());
        task.consume(current, msg.data())
            .ifPresent(next -> reschedule(next, current));
    }
    
}
