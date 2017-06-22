package ome.smuggler.core.msg;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static ome.smuggler.core.msg.RepeatAction.Repeat;
import static ome.smuggler.core.msg.RepeatAction.Stop;
import static ome.smuggler.core.types.FutureTimepoint.now;
import static util.sequence.Arrayz.array;

import java.util.Optional;

import org.junit.Before;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import ome.smuggler.core.types.PositiveN;
import ome.smuggler.core.types.Schedule;

@RunWith(Theories.class)
public class OnceOffDeliveryTest {
    
    @DataPoints
    public static RepeatAction[] actionsSupply = array(Repeat, Stop);
    
    @DataPoints
    public static Integer[] deliveryCountsSupply = array(1, 2, 3, 4);
    
    
    private Optional<Integer> consumedData;
    private Optional<Integer> exceededRedeliveryData;
    
    private OnceOffSchedule<Integer> newOnceOffSchedule(RepeatAction stopOrRepeat) {
        OnceOffRepeatConsumer<Integer> consumer = new OnceOffRepeatConsumer<>(
                d -> {
                    consumedData = Optional.of(d);
                    return stopOrRepeat;
                }, 
                d -> exceededRedeliveryData = Optional.of(d));
        return new OnceOffSchedule<>(consumer);
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
    public void redeliveryHanlderNeverCalledIfConsumerStops(Integer deliveryCount) {
        Integer sentData = 1;
        OnceOffSchedule<Integer> target = newOnceOffSchedule(Stop);
        target.consume(newCountedSchedule(deliveryCount), sentData);
        
        assertTrue(consumedData.isPresent());
        assertThat(consumedData.get(), is(sentData));
        assertFalse(exceededRedeliveryData.isPresent());
    }
    
    @Theory
    public void redeliveryHanlderCalledTooIfConsumerRepeats(Integer deliveryCount) {
        Integer sentData = 2;
        OnceOffSchedule<Integer> target = newOnceOffSchedule(Repeat);
        target.consume(newCountedSchedule(deliveryCount), sentData);
        
        assertTrue(consumedData.isPresent());
        assertThat(consumedData.get(), is(sentData));
        assertTrue(exceededRedeliveryData.isPresent());
        assertThat(exceededRedeliveryData.get(), is(sentData));
    }
    
    @Theory
    public void neverReschedule(Integer deliveryCount, RepeatAction stopOrRepeat) {
        Integer sentData = 3;
        OnceOffSchedule<Integer> target = newOnceOffSchedule(stopOrRepeat);
        Optional<Schedule<Integer>> schedule = 
                target.consume(newCountedSchedule(deliveryCount), sentData);
        
        assertFalse(schedule.isPresent());
        
        assertTrue(consumedData.isPresent());
        assertThat(consumedData.get(), is(sentData));
    }
    
}
