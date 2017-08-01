package kew.core.qchan;

import java.util.Optional;
import java.util.function.Consumer;

import kew.core.msg.CountedSchedule;
import kew.core.qchan.spi.HasProps;
import kew.core.qchan.spi.HasSchedule;
import util.types.FutureTimepoint;
import util.types.PositiveN;


/**
 * Utility methods to help with message metadata.
 */
public class MetaProps {

    /**
     * Property key for the schedule count of {@link CountedSchedule} metadata.
     */
    public static final String ScheduleCountKey =
            CountedSchedule.class.getName() + "#count";

    /**
     * Convenience consumer to
     * {@link HasSchedule#setScheduledDeliveryTime(FutureTimepoint) set}
     * metadata specifying when a message should be delivered to consumers.
     * @param <QM> the message type in the underlying middleware.
     * @param when when to deliver.
     * @return the consumer.
     */
    public static <QM extends HasSchedule>
    Consumer<QM> scheduledDelivery(FutureTimepoint when) {
        return qm -> qm.setScheduledDeliveryTime(when);
    }

    /**
     * Convenience consumer to set a metadata property with the count from
     * a {@link CountedSchedule}.
     * @param <QM> the message type in the underlying middleware.
     * @param count the value to set.
     * @return the consumer.
     * @see #getScheduleCount(HasProps)
     */
    public static <QM extends HasProps>
    Consumer<QM> scheduleCount(PositiveN count) {
        return qm -> qm.putProp(ScheduleCountKey, count.get());
    }

    /**
     * Convenience getter to retrieve the count of a {@link CountedSchedule}.
     * @param msg the message.
     * @return the count if set.
     * @see #scheduleCount(PositiveN)
     */
    public static Optional<PositiveN> getScheduleCount(HasProps msg) {
        return msg.lookupLongValue(ScheduleCountKey).map(PositiveN::of);
    }

}
