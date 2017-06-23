package ome.smuggler.core.types;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.time.Duration;

import org.junit.Test;

public class FutureTimepointTest {

    @Test(expected = NullPointerException.class)
    public void throwIfNullArg() {
        new FutureTimepoint(null);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void throwIfNegativeArg() {
        new FutureTimepoint(Duration.ofMillis(-1));
    }
    
    @Test
    public void valueIsInTheFuture() {
        Duration timepoint = new FutureTimepoint(Duration.ofMinutes(1)).get();
        Duration now = Duration.ofMillis(System.currentTimeMillis());
        
        assertThat(timepoint.compareTo(now), greaterThan(0));
    }
    
    @Test
    public void nowWillBeInThePast() {
        Duration earlier = FutureTimepoint.now().get();
        Duration now = Duration.ofMillis(System.currentTimeMillis());
        
        assertThat(now.compareTo(earlier), greaterThanOrEqualTo(0));
    }

    @Test
    public void canTellWhenIsStillInTheFuture() {
        FutureTimepoint later = new FutureTimepoint(Duration.ofMinutes(1));

        assertTrue(later.isStillInTheFuture());
    }

    @Test
    public void canTellWhenIsInThePast() {
        FutureTimepoint earlier = FutureTimepoint.now();

        assertFalse(earlier.isStillInTheFuture());
    }
    
}
