package kew.core.qchan;

import static java.util.Objects.requireNonNull;
import static kew.core.msg.ChannelMessage.message;
import static kew.core.qchan.MetaProps.getScheduleCount;
import static util.types.FutureTimepoint.now;

import java.util.Optional;

import kew.core.qchan.spi.HasProps;
import kew.core.msg.ChannelMessage;
import kew.core.msg.CountedSchedule;
import kew.core.msg.MessageSink;


/**
 * {@link DequeueTask} consumer to convert raw middleware metadata into a
 * {@link CountedSchedule} and forward it, along with the received data
 * {@code T} to a target message sink.
 *
 * @param <QM> the message type in the underlying middleware.
 * @param <T> the type of the message data.
 * @see CountedScheduleTask
 */
public class CountedScheduleSink<QM extends HasProps,T>
        implements MessageSink<QM, T> {

    private final MessageSink<CountedSchedule, T> consumer;

    /**
     * Creates a new instance.
     * @param consumer the target sink to consume the metadata and data 
     * extracted from queued messages.
     * @throws NullPointerException if the argument is {@code null}.
     */
    public CountedScheduleSink(MessageSink<CountedSchedule, T> consumer) {
        requireNonNull(consumer, "consumer");
        this.consumer = consumer;
    }

    @Override
    public void consume(ChannelMessage<QM, T> queued) {
        Optional<CountedSchedule> current = 
                getScheduleCount(queued.metadata().get())
                .map(count -> new CountedSchedule(now(), count));
        
        consumer.consume(message(current, queued.data()));
    }

}
