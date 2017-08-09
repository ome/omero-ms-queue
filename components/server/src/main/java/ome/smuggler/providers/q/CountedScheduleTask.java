package ome.smuggler.providers.q;

import static java.util.Objects.requireNonNull;
import static ome.smuggler.core.msg.ChannelMessage.message;
import static ome.smuggler.providers.q.Messages.durableMessage;
import static ome.smuggler.providers.q.Messages.setScheduleCount;
import static ome.smuggler.providers.q.Messages.setScheduledDeliveryTime;

import java.io.OutputStream;

import org.apache.activemq.artemis.api.core.ActiveMQException;

import ome.smuggler.core.msg.ChannelMessage;
import ome.smuggler.core.msg.CountedSchedule;
import ome.smuggler.core.msg.MessageSource;
import util.io.SinkWriter;

/**
 * Enqueues a message that will only be delivered to consumers at a specified
 * time in the future and makes a sender-specified delivery count available in
 * the metadata.
 * @see CountedScheduleSink
 */
public class CountedScheduleTask<T> implements MessageSource<CountedSchedule, T> {

    private final EnqueueTask<T> channel;
    
    /**
     * Creates a new instance.
     * @param queue provides access to the queue on which to put messages.
     * @param serializer serialises the message data, a {@code T}-value.
     * @throws ActiveMQException if a queue producer could not be created.
     * @throws NullPointerException if any argument is {@code null}.
     */
    public CountedScheduleTask(QueueConnector queue,
                               SinkWriter<T, OutputStream> serializer)
            throws ActiveMQException {
        this.channel = new EnqueueTask<>(queue, serializer);
    }
    
    @Override
    public void send(ChannelMessage<CountedSchedule, T> msg) throws Exception {
        requireNonNull(msg, "msg");
        
        CountedSchedule metadata = msg.metadata()
                                      .orElse(CountedSchedule.first());
        channel.send(
                message(durableMessage().andThen(
                             setScheduledDeliveryTime(metadata.when())).andThen(
                             setScheduleCount(metadata.count())), 
                        msg.data()));
    }

}
