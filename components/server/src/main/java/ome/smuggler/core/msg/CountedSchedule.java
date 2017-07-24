package ome.smuggler.core.msg;

import static util.types.FutureTimepoint.now;

import util.types.FutureTimepoint;
import util.types.PositiveN;
import util.types.Schedule;

/**
 * Channel metadata to request the scheduling of a message and the sending of
 * a counter to indicate how many times the message has been scheduled for
 * delivery.
 */
public class CountedSchedule extends Schedule<PositiveN> {

    /**
     * @return an initial schedule for the current time point with a {@link 
     * #count() counter} initialized to {@code 1}.
     */
    public static CountedSchedule first() {
        return new CountedSchedule(now(), PositiveN.of(1));
    }
    
    /**
     * Creates a new instance.
     * @param when at which future point in time deliver the message.
     * @param count how many times the delivery has been requested.
     * @throws NullPointerException if any argument is {@code null}.
     */
    public CountedSchedule(FutureTimepoint when, PositiveN count) {
        super(when, count);
    }
   
    /**
     * @return how many times the delivery has been requested.
     */
    public PositiveN count() {
        return what();
    }
    
    /**
     * Creates a new schedule for the requested time with a counter increased
     * by one with respect to the current {@link #count() counter}. 
     * @param when at which future point in time deliver the message.
     * @return the new schedule.
     * @throws NullPointerException if the argument is {@code null}.
     */
    public CountedSchedule next(FutureTimepoint when) {
        return new CountedSchedule(when, PositiveN.of(count().get() + 1));
    }
    
    @Override
    public boolean equals(Object x) {
        if (x == this) return true;
        return x instanceof CountedSchedule && super.equals(x);
    }
    
}
