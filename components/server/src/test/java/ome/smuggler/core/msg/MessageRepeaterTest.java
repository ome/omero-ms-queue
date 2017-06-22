package ome.smuggler.core.msg;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.junit.Assume.*;
import static ome.smuggler.core.msg.RepeatAction.Repeat;
import static ome.smuggler.core.msg.RepeatAction.Stop;
import static ome.smuggler.core.types.FutureTimepoint.now;
import static util.sequence.Arrayz.array;
import static util.sequence.Arrayz.asStream;

import java.time.Duration;
import java.util.Optional;
import java.util.stream.Stream;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import ome.smuggler.core.types.FutureTimepoint;
import ome.smuggler.core.types.PositiveN;
import ome.smuggler.core.types.Schedule;

@RunWith(Theories.class)
public class MessageRepeaterTest {

    @DataPoints
    public static Duration[][] intervalsSupply = new Duration[][] {
        array(), array(Duration.ZERO), array(Duration.ofDays(1), Duration.ZERO),
        array(Duration.ofDays(1), Duration.ZERO, Duration.ofDays(3))
    };
    
    @DataPoints
    public static Integer[] deliveryCountsSupply = array(1, 2, 3, 4);
    

    private static void assertNear(Duration offset, FutureTimepoint actual) {
        FutureTimepoint expected = new FutureTimepoint(offset);
        assertThat(expected.get().minus(actual.get()), 
                   lessThan(Duration.ofSeconds(1)));
    }
    
    
    private Optional<Integer> consumedData;
    private Optional<Integer> exceededRedeliveryData;
    
    private MessageRepeater<Integer> newRepeater(Duration[] intervals,
            RepeatAction stopOrRepeat) {
        return new MessageRepeater<>(
                d -> {
                    consumedData = Optional.of(d);
                    return stopOrRepeat;
                }, 
                asStream(intervals), 
                d -> exceededRedeliveryData = Optional.of(d));
    }
    
    private CountedSchedule newCountedSchedule(Integer deliveryCount) {
        return new CountedSchedule(now(), PositiveN.of(deliveryCount));
    }
    
    @Before
    public void setup() {
        consumedData = Optional.empty();
        exceededRedeliveryData = Optional.empty();
    }
    
    @Theory
    public void stopWhenConsumerSaysSo(Duration[] intervals, Integer deliveryCount) {
        assumeThat(deliveryCount, lessThanOrEqualTo(intervals.length));
        
        Integer sentData = 1;
        MessageRepeater<Integer> target = newRepeater(intervals, Stop);
        Optional<Schedule<Integer>> schedule = 
                target.consume(newCountedSchedule(deliveryCount), sentData);
        
        assertFalse(schedule.isPresent());
        
        assertTrue(consumedData.isPresent());
        assertThat(consumedData.get(), is(sentData));
        
        assertFalse(exceededRedeliveryData.isPresent());
    }
    
    @Theory
    public void repeatWhenConsumerSaysSo(Duration[] intervals, Integer deliveryCount) {
        assumeThat(deliveryCount, lessThanOrEqualTo(intervals.length));
        
        Integer sentData = 2;
        MessageRepeater<Integer> target = newRepeater(intervals, Repeat);
        Optional<Schedule<Integer>> schedule = 
                target.consume(newCountedSchedule(deliveryCount), sentData);
        
        assertTrue(schedule.isPresent());
        assertNear(intervals[deliveryCount - 1], schedule.get().when());
        assertThat(schedule.get().what(), is(sentData));
        
        assertTrue(consumedData.isPresent());
        assertThat(consumedData.get(), is(sentData));
        
        assertFalse(exceededRedeliveryData.isPresent());
    }
    
    @Theory
    public void hanlderNeverCalledOnLastDeliveryIfConsumerStops(
            Duration[] intervals, Integer deliveryCount) {
        assumeThat(deliveryCount, greaterThan(intervals.length));
        
        Integer sentData = 3;
        MessageRepeater<Integer> target = newRepeater(intervals, Stop);
        Optional<Schedule<Integer>> schedule = 
                target.consume(newCountedSchedule(deliveryCount), sentData);
        
        assertFalse(schedule.isPresent());
        
        assertTrue(consumedData.isPresent());
        assertThat(consumedData.get(), is(sentData));
        assertFalse(exceededRedeliveryData.isPresent());
    }
    
    @Theory
    public void bothConsumerAndHanlderCalledOnLastDeliveryIfConsumerRepeats(
            Duration[] intervals, Integer deliveryCount) {
        assumeThat(deliveryCount, greaterThan(intervals.length));
        
        Integer sentData = 4;
        MessageRepeater<Integer> target = newRepeater(intervals, Repeat);
        Optional<Schedule<Integer>> schedule = 
                target.consume(newCountedSchedule(deliveryCount), sentData);
        
        assertFalse(schedule.isPresent());
        
        assertTrue(consumedData.isPresent());
        assertThat(consumedData.get(), is(sentData));
        assertTrue(exceededRedeliveryData.isPresent());
        assertThat(exceededRedeliveryData.get(), is(sentData));
    }
    
    @Test (expected = NullPointerException.class)
    public void ctorThrowsIfArg1Null() {
        new MessageRepeater<>(null, Stream.of(now().get()), x -> {});
    }

    @Test (expected = NullPointerException.class)
    public void ctorThrowsIfArg2Null() {
        new MessageRepeater<>(x -> Stop, null, x -> {});
    }
    
    @Test (expected = NullPointerException.class)
    public void ctorThrowsIfArg3Null() {
        new MessageRepeater<>(x -> Stop, Stream.of(now().get()), null);
    }
    
    @Test
    public void ctorAcceptsEmptyDurations() {
        new MessageRepeater<>(x -> Stop, Stream.empty(), x -> {});
    }
    
    @Test (expected = IllegalArgumentException.class)
    public void ctorThrowsIfAnyNullDuration() {
        new MessageRepeater<>(x -> Stop, Stream.of(now().get(), null), x -> {});
    }
    
}
