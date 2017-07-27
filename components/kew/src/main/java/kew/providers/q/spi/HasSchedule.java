package kew.providers.q.spi;

import util.types.FutureTimepoint;

/**
 * This interface basically says we expect the underlying messaging middleware
 * has the ability to schedule message delivery. Typically this is metadata
 * that's stored in the queue message itself.
 */
public interface HasSchedule {

    /**
     * Specifies a message schedule.
     * @param when time at which to deliver the message.
     * @throws NullPointerException if the argument is {@code null}.
     */
    void setScheduledDeliveryTime(FutureTimepoint when);

}
