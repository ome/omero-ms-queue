package kew.core.qchan.impl;

import static java.util.Objects.requireNonNull;
import static kew.core.msg.ChannelMessage.message;
import static kew.core.qchan.impl.MetaProps.scheduledDelivery;
import static util.types.FutureTimepoint.now;

import java.io.OutputStream;

import kew.core.qchan.spi.HasSchedule;
import kew.core.qchan.spi.QMsgBuilder;
import kew.core.qchan.spi.QMsgFactory;
import kew.core.qchan.spi.QProducer;
import kew.core.msg.ChannelMessage;
import kew.core.msg.SchedulingSource;
import util.types.FutureTimepoint;
import util.io.SinkWriter;

/**
 * Enqueues a message that will only be delivered to consumers at a specified
 * time in the future.
 * @param <QM> the message type in the underlying middleware.
 * @param <T> the type of the message data.
 */
public class ScheduleTask<QM extends HasSchedule, T>
        implements SchedulingSource<T> {

    private final EnqueueTask<QM, T> channel;

    /**
     * Creates a new instance.
     * @param producer provides access to the queue on which to put messages.
     * @param serializer serialises the message data, a {@code T}-value.
     * @throws NullPointerException if any argument is {@code null}.
     */
    public ScheduleTask(QProducer<QM> producer,
                        SinkWriter<T, OutputStream> serializer) {
        this.channel = new EnqueueTask<>(producer, serializer);
    }

    private QMsgBuilder<QM> messageBuilder(FutureTimepoint when) {
        QMsgBuilder<QM> dm = QMsgFactory::durableMessage;
        return dm.with(scheduledDelivery(when));
    }

    /**
     * Sends the message so that the channel will only deliver it to consumers
     * at the specified time in the future.
     * @param msg amount of time from now to specify when in the
     * future the message should be delivered.
     */
    @Override
    public void send(ChannelMessage<FutureTimepoint, T> msg) throws Exception {
        requireNonNull(msg, "msg");
        
        FutureTimepoint when = msg.metadata().orElse(now());
        channel.send(message(messageBuilder(when), msg.data()));
    }
    
}
