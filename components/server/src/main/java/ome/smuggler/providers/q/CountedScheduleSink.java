package ome.smuggler.providers.q;

import static java.util.Objects.requireNonNull;
import static ome.smuggler.core.msg.ChannelMessage.message;
import static ome.smuggler.providers.q.Messages.getScheduleCount;
import static util.types.FutureTimepoint.now;

import java.util.Optional;

import org.apache.activemq.artemis.api.core.client.ClientMessage;

import ome.smuggler.core.msg.ChannelMessage;
import ome.smuggler.core.msg.CountedSchedule;
import ome.smuggler.core.msg.MessageSink;


/**
 * {@link DequeueTask} consumer to convert raw Artemis metadata into a
 * {@link CountedSchedule} and forward it, along with the received data
 * {@code T} to a target message sink.
 * @see CountedScheduleTask
 */
public class CountedScheduleSink<T> implements MessageSink<ClientMessage, T> {

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
    public void consume(ChannelMessage<ClientMessage, T> queued) {
        Optional<CountedSchedule> current = 
                getScheduleCount(queued.metadata().get())
                .map(count -> new CountedSchedule(now(), count));
        
        consumer.consume(message(current, queued.data()));
    }

}
