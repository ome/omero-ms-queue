package kew.core.qchan.impl;

import static java.util.Objects.requireNonNull;
import static kew.core.msg.ChannelMessage.message;
import static kew.core.qchan.impl.MetaProps.scheduleCount;
import static kew.core.qchan.impl.MetaProps.scheduledDelivery;

import java.io.OutputStream;

import kew.core.qchan.spi.*;
import kew.core.msg.ChannelMessage;
import kew.core.msg.CountedSchedule;
import kew.core.msg.MessageSource;
import util.io.SinkWriter;

/**
 * Enqueues a message that will only be delivered to consumers at a specified
 * time in the future and makes a sender-specified delivery count available in
 * the metadata.
 * @see CountedScheduleSink
 * @param <QM> the message type in the underlying middleware.
 * @param <T> the type of the message data.
 */
public class CountedScheduleTask<QM extends HasSchedule & HasProps, T>
        implements MessageSource<CountedSchedule, T> {

    private final EnqueueTask<QM, T> channel;
    
    /**
     * Creates a new instance.
     * @param producer provides access to the queue on which to put messages.
     * @param serializer serialises the message data, a {@code T}-value.
     * @throws NullPointerException if any argument is {@code null}.
     */
    public CountedScheduleTask(QProducer<QM> producer,
                               SinkWriter<T, OutputStream> serializer) {
        this.channel = new EnqueueTask<>(producer, serializer);
    }

    private QMsgBuilder<QM> messageBuilder(CountedSchedule metadata) {
        QMsgBuilder<QM> dm = QMsgFactory::durableMessage;
        return dm.with(scheduledDelivery(metadata.when()))
                 .with(scheduleCount(metadata.count()));
    }

    @Override
    public void send(ChannelMessage<CountedSchedule, T> msg) throws Exception {
        requireNonNull(msg, "msg");
        
        CountedSchedule metadata = msg.metadata()
                                      .orElse(CountedSchedule.first());
        channel.send(message(messageBuilder(metadata), msg.data()));
    }

}
/* NOTE. Design debt.
 * Same considerations as note in ScheduleTask.
 */
