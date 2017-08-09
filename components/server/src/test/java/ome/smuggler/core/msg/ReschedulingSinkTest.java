package ome.smuggler.core.msg;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static ome.smuggler.core.msg.ChannelMessage.message;
import static util.types.FutureTimepoint.now;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

import util.types.FutureTimepoint;
import util.types.PositiveN;
import util.types.Schedule;


public class ReschedulingSinkTest implements Reschedulable<CountedSchedule> {

    private SourceQueue<CountedSchedule, CountedSchedule> loopback;
    private ReschedulingSink<CountedSchedule> target;
    private int messageCap;
    
    @Before
    public void setup() {
        loopback = new SourceQueue<>();
        target = new ReschedulingSink<>(this, loopback);
    }
    
    @Override
    public Optional<Schedule<CountedSchedule>> consume(
            CountedSchedule current, CountedSchedule data) {
        Schedule<CountedSchedule> next = new Schedule<>(data.when(), data);
        return current.count().get() < messageCap ? Optional.of(next)
                                                  : Optional.empty();
    }
    /* NB
     * current  data       next              loopback (expected if sink works correctly)  
     * (t1, 1) (t2, 2) -> (t2, (t2, 2)) ==>  <m:(t2, 2), d:(t2, 2)>
     * (t2, 2) (t3, 3) -> (t3, (t3, 3)) ==>  <m:(t2, 2), d:(t2, 2)> <m:(t3, 3), d:(t3, 3)>
     * (t3, 3) (t4, 4) -> empty         ***** messageCap = 3 *****
     * ...
     */
    
    private CountedSchedule schedule(int ix) {
        FutureTimepoint when = new FutureTimepoint(Duration.ofDays(ix));
        PositiveN count = PositiveN.of(ix);
        return new CountedSchedule(when, count);
    }
    
    private void deliver(int messageCap) {
        this.messageCap = messageCap;
        
        target.consume(message(schedule(2)));
        // no metadata, so sink should call: consume(meta=s(1), data=s(2))
        
        if (messageCap > 1) {    
            for (int k = 2; k <= messageCap; ++k) {
                CountedSchedule current = schedule(k);
                CountedSchedule next = schedule(k + 1);
                target.consume(message(current, next));
                // sink should call: consume(meta=s(k), data=s(k + 1))
            }
        }
    }
    
    private void assertEmptyLoopbackOrScheduledMsgMetaIsSameAsMsgData() {
        List<ChannelMessage<CountedSchedule, CountedSchedule>>  
        actualMessagesSentOnLoopback= loopback.dequeue();
    
        assertThat(actualMessagesSentOnLoopback.size(), is(messageCap - 1));
        actualMessagesSentOnLoopback.forEach(msg -> assertThat(msg.metadata().get(), is(msg.data())));
    }
    
    private void verifyScheduling(int messageCap) {
        deliver(messageCap);
        assertEmptyLoopbackOrScheduledMsgMetaIsSameAsMsgData();
    }
    
    @Test
    public void noReschedulingWhenReschedulableReturnsEmpty() {
        verifyScheduling(1);
    }
    
    @Test
    public void evenWithScheduleNoReschedulingWhenReschedulableReturnsEmpty() {
        messageCap = 1;
        target.consume(message(schedule(1), schedule(1)));
        
        assertEmptyLoopbackOrScheduledMsgMetaIsSameAsMsgData();
    }
    
    @Test
    public void scheduleOne() {
        verifyScheduling(2);
    }
    
    @Test
    public void scheduleMany() {
        verifyScheduling(3);
    }
    
    @Test (expected = NullPointerException.class)
    public void ctorThrowsIfArg1Null() {
        new ReschedulingSink<>(null, loopback);
    }
    
    @Test (expected = NullPointerException.class)
    public void ctorThrowsIfArg2Null() {
        new ReschedulingSink<>(
                (cs, d) -> Optional.of(new Schedule<>(now(), "")), 
                null);
    }
    
}
