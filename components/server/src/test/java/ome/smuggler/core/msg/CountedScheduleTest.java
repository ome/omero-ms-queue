package ome.smuggler.core.msg;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static ome.smuggler.core.types.FutureTimepoint.now;

import java.time.Duration;

import org.junit.Test;

import ome.smuggler.core.types.FutureTimepoint;
import util.types.PositiveN;

public class CountedScheduleTest {

    @Test(expected = NullPointerException.class)
    public void ctorThrowsIfArg1Null() {
        new CountedSchedule(null, PositiveN.of(1));
    }
    
    @Test(expected = NullPointerException.class)
    public void ctorThrowsIfArg2Null() {
        new CountedSchedule(now(), null);
    }
    
    @Test
    public void initialStartsNowAtCountOfZero() {
        CountedSchedule actual = CountedSchedule.first();
        
        assertThat(actual.count(), is(1L));
        assertThat(now().get().compareTo(actual.when().get()), 
                   greaterThanOrEqualTo(0));
    }
    
    @Test
    public void nextBumpsCounterByOne() {
        FutureTimepoint when = new FutureTimepoint(Duration.ofMinutes(1));
        CountedSchedule actual = CountedSchedule.first().next(when);
        
        assertThat(actual.count(), is(2L));
        assertThat(actual.when(), is(when));
    }
    
    @Test
    public void equalsReturnsFalseOnNullArg() {
        assertFalse(CountedSchedule.first().equals(null));
    }
    
    @Test
    public void equalsReturnsFalseOnDifferentType() {
        assertFalse(CountedSchedule.first().equals(""));
    }
    
    @Test
    public void equalsReturnsFalseOnDifferentValues() {
        CountedSchedule second = new CountedSchedule(now(), PositiveN.of(2));
        assertFalse(CountedSchedule.first().equals(second));
    }
    
    @Test
    public void equalsReturnsTrueOnEquivalentValues() {
        FutureTimepoint when = new FutureTimepoint(Duration.ofMinutes(1));
        CountedSchedule cs1 = new CountedSchedule(when, PositiveN.of(2));
        CountedSchedule cs2 = new CountedSchedule(when, PositiveN.of(2));
        assertTrue(cs1.equals(cs2));
    }
    
}
