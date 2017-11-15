package kew.core.qchan.impl;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import kew.core.qchan.spi.HasProps;
import kew.core.qchan.spi.HasSchedule;
import org.junit.Test;
import util.types.FutureTimepoint;
import util.types.PositiveN;

import java.util.Optional;

public class MetaPropsTest {

    @Test
    public void scheduledDelivery() {
        FutureTimepoint when = FutureTimepoint.now();
        HasSchedule qm = mock(HasSchedule.class);
        MetaProps.scheduledDelivery(when).accept(qm);

        verify(qm, times(1)).setScheduledDeliveryTime(when);
    }

    @Test
    public void scheduleCount() {
        long count = 2;
        HasProps qm = mock(HasProps.class);
        MetaProps.scheduleCount(PositiveN.of(count)).accept(qm);

        verify(qm, times(1)).putProp(MetaProps.ScheduleCountKey, count);
    }

    @Test
    public void getScheduleCount() {
        long count = 2;
        HasProps qm = mock(HasProps.class);
        when(qm.lookupLongValue(MetaProps.ScheduleCountKey))
                .thenReturn(Optional.of(count));

        Optional<PositiveN> actual = MetaProps.getScheduleCount(qm);

        assertNotNull(actual);
        assertTrue(actual.isPresent());
        assertThat(actual.get(), is(PositiveN.of(count)));
    }

    @Test
    public void ctor() {
        new MetaProps();  // just to make coverage 100%...
    }

}
