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
/* NOTE. Design debt.
 * If we had composable channel sources, then we wouldn't need to have this
 * class keep a reference to the underlying channel to forward method calls.
 * Notes to self follow, just in case one day I'll find the time to do it.
 * ChannelSource could be a contravariant functor CS from types to effectful
 * computations (a Kleisli category for some monad E)
 *
 *         x                   x → ()      more accurately: x → E ()
 *                  CS                     but Java being Java we can pretend
 *       f ↓   ------------->    ↑         there's no E...
 *
 *         y                   y → ()
 *
 * The basic idea is that if you can send y-data and can transform x's into
 * y's then you can also send x-data. The arrow map of CS could be implemented
 * by a helper class ChannelSourceTransformer similar to this one:
 *
 * - https://github.com/c0c0n3/omero-ms-queue/commit/9904d437361fbe9f05fb2ae5a8041133d45f294e
 *
 * Then it's just a matter of adding a convenience method to ChannelSource to
 * compute: (CS f) (CS y)
 * Now ScheduleTask becomes just a function:
 *
 *   ChannelMessage<FutureTimepoint, T>  →  ChannelMessage<QMsgBuilder<QM>, T>
 *
 *         FutureTimepoint when = msg.metadata().orElse(now());
 *         return message(messageBuilder(when), msg.data());
 *
 *
 * and we can replace the rest of the functionality currently sitting here
 * in ScheduleTask with:
 *
 *     enqueueTask.m(scheduleFunction)
 *
 * where m is the convenience composition method of ChannelSource.
 * For this to work smoothly, we'll have to get rid of the MessageSource
 * interface though cos it's gonna break composability. There's no loss of
 * functionality cos MessageSource is only meant to be a type alias to
 * improve readability. But Java doesn't have proper type aliases (never
 * mind readability!) so I don't think there's any easy way of implementing
 * composability of sources while still keeping the message source alias.
 */
