package kew.core.msg;

import static org.junit.Assert.*;

import org.junit.Test;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;


public class ReschedulableFactoryTest {

    @Test
    public void buildForRepeatConsumerWithNoIntervals() {
        Reschedulable<?> actual = ReschedulableFactory.buildForRepeatConsumer(
                t -> RepeatAction.Stop, new ArrayList<>(), t -> {});
        assertNotNull(actual);
    }

    @Test
    public void buildForRepeatConsumerWithIntervals() {
        List<Duration> intervals = new ArrayList<>();
        intervals.add(Duration.ofDays(1));
        Reschedulable<?> actual = ReschedulableFactory.buildForRepeatConsumer(
                t -> RepeatAction.Stop, intervals, t -> {});
        assertNotNull(actual);
    }

    @Test (expected = NullPointerException.class)
    public void buildForRepeatConsumerThrowsIfNullConsumer() {
        ReschedulableFactory.buildForRepeatConsumer(
                null, new ArrayList<>(), t -> {});
    }

    @Test (expected = NullPointerException.class)
    public void buildForRepeatConsumerThrowsIfNullIntervals() {
        ReschedulableFactory.buildForRepeatConsumer(
                t -> RepeatAction.Stop, null, t -> {});
    }

    @Test (expected = NullPointerException.class)
    public void buildForRepeatConsumerThrowsIfNullHandler() {
        ReschedulableFactory.buildForRepeatConsumer(
                t -> RepeatAction.Stop, new ArrayList<>(), null);
    }

    @Test
    public void ctor() {
        new ReschedulableFactory();  // just to make coverage 100%...
    }

}
